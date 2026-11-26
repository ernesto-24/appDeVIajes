package com.example.appdeviajes.data.model

data class OrigenViaje(
    val codigoPais: String,
    val nombrePais: String,
    val ciudad: String?,
    val moneda: Moneda
)
