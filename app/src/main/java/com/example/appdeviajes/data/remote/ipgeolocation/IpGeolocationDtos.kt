package com.example.appdeviajes.data.remote.ipgeolocation

// =====================================================================================
// DTOs de IP Geolocation API (ip2location.io) - PENDIENTE DE IMPLEMENTAR
// =====================================================================================
//
// PARA QUÉ: representar la respuesta JSON tal cual llega de https://api.ip2location.io/
// Documentación: https://www.ip2location.io/ip2location-documentation
//
// Campos que necesita la app: country_code (obligatorio), country_name, city_name, time_zone.
// La respuesta gratuita NO trae moneda: se obtiene después con REST Countries usando country_code.
//
// ESQUELETO (mismo formato que publicholiday/PublicHolidayDtos.kt)
// Campos tomados de una respuesta real. Los que pueden venir null o faltar van con "?".
//
// import com.google.gson.annotations.SerializedName
//
// data class IpGeolocationDto(
//     @SerializedName("ip") val ip: String,
//     @SerializedName("country_code") val countryCode: String,
//     @SerializedName("country_name") val countryName: String?,
//     @SerializedName("region_name") val regionName: String?,
//     @SerializedName("city_name") val cityName: String?,
//     @SerializedName("latitude") val latitude: Double?,
//     @SerializedName("longitude") val longitude: Double?,
//     @SerializedName("zip_code") val zipCode: String?,
//     @SerializedName("time_zone") val timeZone: String?,
//     @SerializedName("is_proxy") val isProxy: Boolean?
// )
//
// REGLAS
//   1. Se consume a través del RetrofitClient compartido (no crear otro cliente).
//   2. Volver a probar el JSON real antes de descomentar: si un campo no aparece, no lo agregues.
//   3. Mapear a un modelo de dominio en data/model/ (ej. UbicacionUsuario(codigoPais, nombrePais, ciudad)).
//   4. Exponerlo desde ViajeRepository; codigoPais alimenta a Public Holiday y a REST Countries.
