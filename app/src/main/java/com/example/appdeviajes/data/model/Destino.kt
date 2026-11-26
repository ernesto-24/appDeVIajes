package com.example.appdeviajes.data.model

data class Destino(
    val codigoPais: String,
    val nombre: String,
    val urlBandera: String
)

data class DetalleDestino(
    val nombre: String,
    val urlBandera: String,
    val pais: Pais
)
