# Arquitectura de viajaloVos

## 1. Resumen

**viajaloVos** es una app Android (Kotlin + Jetpack Compose + Material 3) para planificar un viaje. Al abrirla detecta el país y la moneda de origen del usuario. Después el usuario elige un país destino de una lista con buscador y ve un resumen del viaje: datos del país (bandera, capital, idiomas y moneda), un conversor de presupuesto de su moneda a la del destino, y los feriados y fines de semana largos del destino en el año que elija.

La app sigue el patrón **MVVM** con una única fuente de datos, `ViajeRepository`, que combina cuatro APIs públicas.

## 2. Arquitectura modular

### Capas

| Capa | Paquete | Responsabilidad |
|------|---------|-----------------|
| **UI** | `ui/` | Pantallas Compose (`*Screen`), sus ViewModels (`*ViewModel`) y sus `UiState`. Navegación (`ui/navegacion`), componentes y formatos compartidos (`ui/comun`) y el `ViajaloVosViewModelFactory`. |
| **Repositorio** | `data/repository/` | `ViajeRepository`: el único punto de acceso a datos. Llama a los ApiService, convierte DTOs en modelos, combina APIs y guarda en memoria el origen, los países y las tasas de cambio. |
| **Modelo** | `data/model/` | Modelos de dominio (`Pais`, `Moneda`, `OrigenViaje`, `Destino`, `DetalleDestino`, `ConversionPresupuesto`, `CalendarioFeriados`, `Feriado`, `FinDeSemanaLargo`, `TasaDeCambio`, `UbicacionUsuario`) y los mappers `toDomain()` desde los DTOs. |
| **Remoto** | `data/remote/` | `RetrofitClient` y, por cada API, su interfaz `*ApiService` (Retrofit) y sus `*Dtos` (JSON tal como llega). |

### Reglas

- Cada pantalla habla **solo** con su ViewModel. Observa el `StateFlow<UiState>` con `collectAsState()` y le envía las acciones del usuario.
- Cada ViewModel llama **solo** a `ViajeRepository`. Nunca usa un ApiService ni `RetrofitClient`.
- Toda la lógica que combina varias APIs (por ejemplo moneda de origen + moneda de destino + tasa) vive en el repositorio.
- Los ViewModels se crean con `ViajaloVosViewModelFactory`, que recibe la única instancia de `ViajeRepository` guardada en `ViajaloVosApp`. No se usa Hilt.
- Si una llamada falla, el ViewModel pasa el `UiState` a error y la pantalla muestra un mensaje con el botón "Reintentar".

### Flujo de datos

```
 ┌───────────────────────┐   acciones (clic, texto)   ┌──────────────────────┐
 │  Pantalla (Compose)   │ ─────────────────────────▶ │      ViewModel       │
 │  InicioScreen         │                            │  InicioViewModel     │
 │  DestinosScreen       │ ◀───────────────────────── │  DestinosViewModel   │
 │  ResumenViajeScreen   │  StateFlow<UiState>        │  ResumenViajeVM      │
 └───────────────────────┘  (collectAsState)          └──────────┬───────────┘
                                                                 │ suspend fun
                                                                 ▼
                                                    ┌──────────────────────────┐
                                                    │     ViajeRepository      │
                                                    │  combina APIs + caché    │
                                                    │  DTO ──toDomain()──▶ model│
                                                    └──────────┬───────────────┘
                                                               │ Retrofit
                          ┌──────────────────┬─────────────────┼──────────────────┐
                          ▼                  ▼                 ▼                  ▼
                 IpGeolocationApi   RestCountriesApi   ExchangeRateApi   PublicHolidayApi
                          │                  │                 │                  │
                          ▼                  ▼                 ▼                  ▼
                   ip2location.io   restcountries.com    open.er-api.com    date.nager.at
```

### Navegación

```
InicioScreen ──"Elegir destino"──▶ DestinosScreen ──toca un país──▶ ResumenViajeScreen
  "inicio"                           "destinos"                      "resumen/{codigoPais}"
```

`ResumenViajeViewModel` lee el argumento `codigoPais` desde su `SavedStateHandle`.

## 3. Dónde se usa cada API

| API | Función básica | Pantalla | ViewModel | Funciones de `ViajeRepository` | Dato que entrega a otras APIs |
|-----|----------------|----------|-----------|--------------------------------|-------------------------------|
| **IP Geolocation** (`ip2location.io`) | 1. Detectar mi origen | `InicioScreen` (y la etiqueta del conversor en `ResumenViajeScreen`) | `InicioViewModel`, `ResumenViajeViewModel` | `obtenerOrigen()` → `obtenerUbicacionUsuario()` | El **código ISO del país de origen** → REST Countries, para saber la moneda de origen. |
| **REST Countries** (`restcountries.com`) | 2. Elegir destino (datos del país) y apoyo a 1 y 3 | `ResumenViajeScreen` (datos del destino) e `InicioScreen` (moneda de origen) | `ResumenViajeViewModel`, `InicioViewModel` | `obtenerDetalleDestino()`, `obtenerPais()`, `obtenerOrigen()`, `convertirPresupuesto()` | La **moneda del país de origen y la del destino** → Exchange Rate. |
| **Exchange Rate** (`open.er-api.com`) | 3. Convertir presupuesto | `ResumenViajeScreen` (conversor) | `ResumenViajeViewModel` | `convertirPresupuesto()` → `obtenerTasas()` | Ninguno. Usa las monedas que entrega REST Countries. |
| **Public Holiday** (`date.nager.at`) | 4. Feriados del destino | `ResumenViajeScreen` (feriados por año) | `ResumenViajeViewModel` | `obtenerCalendarioFeriados()` → `obtenerFeriados()` y `obtenerFinesDeSemanaLargos()` | Ninguno. Recibe el código del país destino elegido en `DestinosScreen`. |

### Detalles

- **Origen en memoria:** `obtenerOrigen()` combina IP Geolocation y REST Countries una sola vez y guarda el resultado. Inicio y Resumen lo reutilizan sin repetir llamadas.
- **Lista de destinos:** `obtenerDestinos()` arma la lista con los códigos ISO 3166 de `java.util.Locale` y sus nombres en español, porque `RestCountriesApiService` solo permite consultar un país por código. La bandera se carga con Coil desde `flagcdn.com` usando ese código. Al tocar un país, REST Countries trae sus datos completos.
- **Sin datos de feriados:** si Public Holiday responde 204 (sin contenido) o 404 para un país, `obtenerCalendarioFeriados()` devuelve `null` y la pantalla muestra "Public Holiday no tiene datos de feriados para … en …".
- **Caché:** las tasas se guardan hasta su `time_next_update` y los países consultados se guardan durante la sesión.
