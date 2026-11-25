package com.example.appdeviajes.data.model

data class Pais(
    val nombre: String,
    val codigoIso2: String,
    val codigoIso3: String?,
    val capital: String?,
    val idiomas: List<String>,
    val monedas: List<Moneda>
)

data class Moneda(
    val codigo: String,
    val nombre: String?,
    val simbolo: String?
)
