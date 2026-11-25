package com.example.appdeviajes.data.remote.exchangerate

import com.google.gson.annotations.SerializedName

// Respuesta de https://open.er-api.com/v6/latest/{baseCode} (campos verificados con una petición real).
// En error solo llegan "result" y "error-type", por eso el resto es nullable.
data class ExchangeRateDto(
    @SerializedName("result") val result: String,
    @SerializedName("error-type") val errorType: String?,
    @SerializedName("base_code") val baseCode: String?,
    @SerializedName("time_last_update_unix") val timeLastUpdateUnix: Long?,
    @SerializedName("time_last_update_utc") val timeLastUpdateUtc: String?,
    @SerializedName("time_next_update_unix") val timeNextUpdateUnix: Long?,
    @SerializedName("time_next_update_utc") val timeNextUpdateUtc: String?,
    @SerializedName("rates") val rates: Map<String, Double>?
)
