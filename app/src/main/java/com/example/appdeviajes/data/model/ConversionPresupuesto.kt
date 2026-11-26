package com.example.appdeviajes.data.model

data class ConversionPresupuesto(
    val montoOrigen: Double,
    val monedaOrigen: Moneda,
    val montoDestino: Double,
    val monedaDestino: Moneda,
    val tasa: Double
)
