package com.example.appdeviajes.data.model

import java.time.LocalDate

data class FinDeSemanaLargo(
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate,
    val cantidadDias: Int,
    val requiereDiaPuente: Boolean,
    val diasPuente: List<LocalDate>
)
