package com.example.appdeviajes.data.model

import java.time.LocalDate

data class Feriado(
    val fecha: LocalDate,
    val nombreLocal: String,
    val nombre: String,
    val codigoPais: String,
    val esNacional: Boolean,
    val regiones: List<String>,
    val tipos: List<String>
)
