package com.example.appdeviajes.data.remote.restcountries

// =====================================================================================
// DTOs de REST Countries API (v5) - PENDIENTE DE IMPLEMENTAR
// =====================================================================================
//
// PARA QUÉ: representar la respuesta de https://api.restcountries.com/countries/v5/...
// Documentación: https://restcountries.com/docs/countries
//
// Campos que necesita la app: nombre común, códigos ISO, capital, idiomas y monedas.
// Estructura observada en la respuesta de ejemplo de la v5 (demo key, país Canadá):
//   {"data":{"objects":[{
//      "names":{"common":"Canada","official":"Canada", ...},
//      "codes":{"alpha_2":"CA","alpha_3":"CAN","ccn3":"124", ...},
//      "capitals":[{"name":"Ottawa","coordinates":{"lat":45.42,"lng":-75.7}, ...}],
//      "languages":[{"iso639_1":"en","name":"English","native_name":"English", ...}],
//      "currencies":[{"code":"CAD","name":"Canadian dollar","symbol":"$"}],
//      ... }]}}
//
// ESQUELETO (mismo formato que publicholiday/PublicHolidayDtos.kt)
//
// import com.google.gson.annotations.SerializedName
//
// data class RestCountriesResponseDto(
//     @SerializedName("data") val data: RestCountriesDataDto?
// )
//
// data class RestCountriesDataDto(
//     @SerializedName("objects") val objects: List<CountryDto>?
// )
//
// data class CountryDto(
//     @SerializedName("names") val names: CountryNamesDto?,
//     @SerializedName("codes") val codes: CountryCodesDto?,
//     @SerializedName("capitals") val capitals: List<CapitalDto>?,
//     @SerializedName("languages") val languages: List<LanguageDto>?,
//     @SerializedName("currencies") val currencies: List<CurrencyDto>?
// )
//
// data class CountryNamesDto(
//     @SerializedName("common") val common: String,
//     @SerializedName("official") val official: String?
// )
//
// data class CountryCodesDto(
//     @SerializedName("alpha_2") val alpha2: String,
//     @SerializedName("alpha_3") val alpha3: String?
// )
//
// data class CapitalDto(
//     @SerializedName("name") val name: String
// )
//
// data class LanguageDto(
//     @SerializedName("iso639_1") val iso6391: String?,
//     @SerializedName("name") val name: String
// )
//
// data class CurrencyDto(
//     @SerializedName("code") val code: String,
//     @SerializedName("name") val name: String?,
//     @SerializedName("symbol") val symbol: String?
// )
//
// REGLAS
//   1. Se consume a través del RetrofitClient compartido (no crear otro cliente).
//   2. Probar el JSON real con tu API key antes de descomentar: el ejemplo de arriba viene de la
//      demo key; confirmar los campos con un país real (ej. "BO") antes de usarlos.
//   3. Mapear a un modelo de dominio en data/model/ (ej. Pais(nombre, codigoIso2, codigoIso3,
//      capital, idiomas, monedas)).
//   4. Exponerlo desde ViajeRepository; currencies[].code alimenta a Exchange Rate.
