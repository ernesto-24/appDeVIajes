package com.example.appdeviajes.data.remote.exchangerate

// =====================================================================================
// Exchange Rate API (open.er-api.com) - PENDIENTE DE IMPLEMENTAR
// =====================================================================================
//
// PARA QUÉ LA USA LA APP
//   Convertir montos entre la moneda del usuario (origen) y la del país destino, para estimar
//   el presupuesto del viaje. Datos que necesitamos:
//     - base_code             -> moneda base de la consulta (ej. "BOB")
//     - rates                 -> mapa código de moneda -> tasa (ej. "USD": 0.0852)
//     - time_last_update_utc  -> para mostrar desde cuándo es válida la tasa
//
// URLS
//   Documentación: https://www.exchangerate-api.com/docs/free
//   URL base:      https://open.er-api.com/v6/
//   Ejemplo:       https://open.er-api.com/v6/latest/USD
//
// NOTAS (verificado con una petición real el 2026-11-24)
//   - No requiere API key. Las tasas se actualizan una vez al día: conviene cachearlas.
//   - Si la moneda no existe, igual responde JSON: {"result":"error","error-type":"unsupported-code"}.
//     Por eso hay que revisar "result" antes de usar "rates".
//
// QUÉ DATO RECIBE DE LAS OTRAS APIs
//   moneda base   <- currencies[].code de REST Countries para el país de origen
//                    (cuyo código viene de IP Geolocation)
//   moneda destino <- currencies[].code de REST Countries para el país destino
//   Es la última de la cadena: no le pasa datos a otras APIs, devuelve la tasa a la app.
//
// ESQUELETO (mismo formato que publicholiday/PublicHolidayApiService.kt)
//
// import retrofit2.http.GET
// import retrofit2.http.Path
//
// interface ExchangeRateApiService {
//
//     @GET("latest/{baseCode}")
//     suspend fun getTasas(
//         @Path("baseCode") baseCode: String
//     ): ExchangeRateDto
//
//     companion object {
//         const val BASE_URL = "https://open.er-api.com/v6/"
//     }
// }
//
// REGLAS
//   1. Usar el RetrofitClient compartido: RetrofitClient.createService<ExchangeRateApiService>(BASE_URL).
//      No crear otro Retrofit ni otro OkHttpClient.
//   2. Probar el JSON real ANTES de definir los campos del DTO. No inventar campos.
//   3. Mapear el DTO a un modelo de dominio en data/model/ (ej. TasaDeCambio.kt) con su mapper toDomain().
//   4. Agregar el servicio como parámetro del constructor de ViajeRepository y una función
//      suspend, por ejemplo:
//      suspend fun convertir(monto: Double, desde: String, hacia: String): Double
