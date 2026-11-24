package com.example.appdeviajes.data.remote.exchangerate

// =====================================================================================
// DTOs de Exchange Rate API (open.er-api.com) - PENDIENTE DE IMPLEMENTAR
// =====================================================================================
//
// PARA QUÉ: representar la respuesta de https://open.er-api.com/v6/latest/{baseCode}
// Documentación: https://www.exchangerate-api.com/docs/free
//
// Respuesta real (recortada):
//   {"result":"success","provider":"https://www.exchangerate-api.com",
//    "time_last_update_unix":1795478551,"time_last_update_utc":"Tue, 24 Nov 2026 00:02:31 +0000",
//    "time_next_update_unix":1795566061,"time_next_update_utc":"Wed, 25 Nov 2026 00:21:01 +0000",
//    "base_code":"USD","rates":{"USD":1,"BOB":11.736523,"ARS":1575.9572, ...}}
// Respuesta de error real:
//   {"result":"error","error-type":"unsupported-code"}
//
// ESQUELETO (mismo formato que publicholiday/PublicHolidayDtos.kt)
// En error no vienen base_code ni rates, por eso son nullable.
//
// import com.google.gson.annotations.SerializedName
//
// data class ExchangeRateDto(
//     @SerializedName("result") val result: String,
//     @SerializedName("error-type") val errorType: String?,
//     @SerializedName("base_code") val baseCode: String?,
//     @SerializedName("time_last_update_unix") val timeLastUpdateUnix: Long?,
//     @SerializedName("time_last_update_utc") val timeLastUpdateUtc: String?,
//     @SerializedName("time_next_update_utc") val timeNextUpdateUtc: String?,
//     @SerializedName("rates") val rates: Map<String, Double>?
// )
//
// REGLAS
//   1. Se consume a través del RetrofitClient compartido (no crear otro cliente).
//   2. Volver a probar el JSON real antes de descomentar.
//   3. Mapear a un modelo de dominio en data/model/ (ej. TasaDeCambio(monedaBase, tasas,
//      ultimaActualizacion)) y lanzar/devolver un error si result != "success".
//   4. Exponerlo desde ViajeRepository; recibe los códigos de moneda que entrega REST Countries.
