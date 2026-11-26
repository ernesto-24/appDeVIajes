package com.example.appdeviajes.ui.comun

import java.text.NumberFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

private val ESPANOL: Locale = Locale.forLanguageTag("es")

private val formatoFecha: DateTimeFormatter = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", ESPANOL)

private val formatoFechaCorta: DateTimeFormatter = DateTimeFormatter.ofPattern("d MMM", ESPANOL)

fun formatearMonto(valor: Double, decimales: Int = 2): String =
    NumberFormat.getNumberInstance(ESPANOL).apply {
        minimumFractionDigits = decimales
        maximumFractionDigits = decimales
    }.format(valor)

fun formatearTasa(valor: Double): String =
    NumberFormat.getNumberInstance(ESPANOL).apply {
        minimumFractionDigits = 2
        maximumFractionDigits = 6
    }.format(valor)

fun formatearFecha(fecha: LocalDate): String =
    fecha.format(formatoFecha).replaceFirstChar { it.titlecase(ESPANOL) }

fun formatearRango(inicio: LocalDate, fin: LocalDate): String =
    "${inicio.format(formatoFechaCorta)} – ${fin.format(formatoFechaCorta)}"
