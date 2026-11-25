package com.example.appdeviajes.data.model

import java.time.Instant

data class TasaDeCambio(
    val monedaBase: String,
    val tasas: Map<String, Double>,
    val ultimaActualizacion: Instant?,
    val proximaActualizacion: Instant?
)
