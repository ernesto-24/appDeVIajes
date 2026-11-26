package com.example.appdeviajes.data.model

import java.time.ZoneOffset

data class UbicacionUsuario(
    val codigoPais: String,
    val nombrePais: String?,
    val ciudad: String?,
    val desfaseHorario: ZoneOffset?
)
