package com.example.appdeviajes.data.model

data class CalendarioFeriados(
    val codigoPais: String,
    val anio: Int,
    val feriados: List<Feriado>,
    val finesDeSemanaLargos: List<FinDeSemanaLargo>
)
