package com.example.appdeviajes.data.remote.ipgeolocation

// =====================================================================================
// IP Geolocation API (ip2location.io) - PENDIENTE DE IMPLEMENTAR
// =====================================================================================
//
// PARA QUÉ LA USA LA APP
//   Detectar automáticamente desde qué país viaja el usuario (país de ORIGEN), sin pedirle
//   que lo elija a mano. Datos que necesitamos:
//     - country_code (ej. "BO")  -> dato principal
//     - country_name             -> para mostrarlo en pantalla
//     - city_name / time_zone    -> opcionales
//
// URLS
//   Documentación: https://www.ip2location.io/ip2location-documentation
//   URL base:      https://api.ip2location.io/
//   Ejemplo:       https://api.ip2location.io/            (sin "ip" = IP del propio dispositivo)
//                  https://api.ip2location.io/?ip=8.8.8.8
//
// IMPORTANTE (verificado con una petición real el 2026-11-24)
//   - Sin API key funciona, pero con límite de 1.000 consultas por día.
//   - La respuesta gratuita NO incluye la moneda. El código de moneda del usuario se obtiene
//     pasando country_code a REST Countries (campo currencies[].code).
//   - Respuesta real sin key:
//     {"ip":"189.28.85.108","country_code":"BO","country_name":"Bolivia (Plurinational State of)",
//      "region_name":"Santa Cruz","city_name":"Santa Cruz de la Sierra","latitude":-17.79981,
//      "longitude":-63.16686,"zip_code":null,"time_zone":"-04:00","asn":"27882",
//      "as":"Telefonica Celular de Bolivia S.A.","is_proxy":false,"message":"Limit to 1,000 ..."}
//
// QUÉ DATO LE PASA A LAS OTRAS APIs
//   country_code -> Public Holiday API (feriados del país de origen)
//   country_code -> REST Countries     (de ahí sale el código de moneda del origen)
//   moneda origen (vía REST Countries) -> Exchange Rate API (moneda base de la conversión)
//
// ESQUELETO (mismo formato que publicholiday/PublicHolidayApiService.kt)
//
// import retrofit2.http.GET
// import retrofit2.http.Query
//
// interface IpGeolocationApiService {
//
//     @GET("./")
//     suspend fun getUbicacionActual(
//         @Query("key") apiKey: String? = null
//     ): IpGeolocationDto
//
//     @GET("./")
//     suspend fun getUbicacionPorIp(
//         @Query("ip") ip: String,
//         @Query("key") apiKey: String? = null
//     ): IpGeolocationDto
//
//     companion object {
//         const val BASE_URL = "https://api.ip2location.io/"
//     }
// }
//
// REGLAS
//   1. Usar el RetrofitClient compartido: RetrofitClient.createService<IpGeolocationApiService>(BASE_URL).
//      No crear otro Retrofit ni otro OkHttpClient.
//   2. Probar el JSON real (curl o navegador) ANTES de definir los campos del DTO. No inventar campos.
//   3. Mapear el DTO a un modelo de dominio en data/model/ (ej. UbicacionUsuario.kt) con su mapper
//      toDomain() en data/model/ (ej. IpGeolocationMappers.kt).
//   4. Agregar el servicio como parámetro del constructor de ViajeRepository y una función
//      suspend, por ejemplo: suspend fun obtenerUbicacionUsuario(): UbicacionUsuario
