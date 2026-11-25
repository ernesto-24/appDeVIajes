package com.example.appdeviajes.data.model

import com.example.appdeviajes.data.remote.exchangerate.ExchangeRateDto
import java.time.Instant

fun ExchangeRateDto.toDomain(): TasaDeCambio {
    if (result != "success" || baseCode == null || rates == null) {
        throw IllegalArgumentException("Exchange Rate API respondió error: ${errorType ?: "desconocido"}")
    }
    return TasaDeCambio(
        monedaBase = baseCode,
        tasas = rates,
        ultimaActualizacion = timeLastUpdateUnix?.let(Instant::ofEpochSecond),
        proximaActualizacion = timeNextUpdateUnix?.let(Instant::ofEpochSecond)
    )
}
