package com.example.appdeviajes.data.remote.restcountries

// =====================================================================================
// REST Countries API - PENDIENTE DE IMPLEMENTAR
// =====================================================================================
//
// PARA QUÉ LA USA LA APP
//   Obtener la ficha del país DESTINO (y también la moneda del país de origen). Datos:
//     - names.common                 -> nombre del país
//     - codes.alpha_2 / alpha_3      -> códigos ISO ("BO" / "BOL")
//     - capitals[].name              -> capital
//     - languages[].name             -> idiomas
//     - currencies[].code / symbol   -> moneda (ej. "BOB", "Bs.")
//
// URLS
//   Documentación: https://restcountries.com/docs/countries
//   URL base:      https://api.restcountries.com/countries/v5/
//   Ejemplo:       https://api.restcountries.com/countries/v5/codes.alpha_2/BO
//
// IMPORTANTE (verificado el 2026-11-24)
//   - La versión v3.1 (restcountries.com/v3.1/...) que lista freepublicapis.com está DEPRECADA:
//     responde {"success":false,"errors":[{"message":"This API version has been deprecated..."}]}.
//   - La v5 EXIGE API key (plan Free: 1.000 consultas/mes) enviada como header
//     "Authorization: Bearer TU_KEY". Registro: https://restcountries.com/sign-up
//   - La v5 envuelve la respuesta en {"data":{"objects":[ ... ]}}.
//   - Con "response_fields" se piden solo los campos necesarios, ej.:
//     ?response_fields=names.common,codes.alpha_2,codes.alpha_3,capitals,languages,currencies
//   - No subir la API key al repositorio: guardarla en local.properties y exponerla vía BuildConfig.
//
// QUÉ DATO LE PASA A LAS OTRAS APIs
//   codes.alpha_2   -> Public Holiday API (feriados del país destino)
//   currencies.code -> Exchange Rate API (moneda destino; y moneda origen si se consulta el país
//                      que devolvió IP Geolocation)
//   Recibe: country_code desde IP Geolocation o el código del destino elegido por el usuario.
//
// ESQUELETO (mismo formato que publicholiday/PublicHolidayApiService.kt)
//
// import retrofit2.http.GET
// import retrofit2.http.Header
// import retrofit2.http.Path
// import retrofit2.http.Query
//
// interface RestCountriesApiService {
//
//     @GET("codes.alpha_2/{codigo}")
//     suspend fun getPaisPorCodigo(
//         @Path("codigo") codigo: String,
//         @Header("Authorization") autorizacion: String,
//         @Query("response_fields") campos: String = CAMPOS
//     ): RestCountriesResponseDto
//
//     companion object {
//         const val BASE_URL = "https://api.restcountries.com/countries/v5/"
//         const val CAMPOS = "names.common,codes.alpha_2,codes.alpha_3,capitals,languages,currencies"
//     }
// }
//
// REGLAS
//   1. Usar el RetrofitClient compartido: RetrofitClient.createService<RestCountriesApiService>(BASE_URL).
//      No crear otro Retrofit ni otro OkHttpClient.
//   2. Probar el JSON real con tu API key ANTES de definir los campos del DTO. No inventar campos.
//   3. Mapear el DTO a un modelo de dominio en data/model/ (ej. Pais.kt) con su mapper toDomain().
//   4. Agregar el servicio como parámetro del constructor de ViajeRepository y una función
//      suspend, por ejemplo: suspend fun obtenerPais(codigoPais: String): Pais
