package com.example.appdeviajes.data.model

import com.example.appdeviajes.data.remote.publicholiday.LongWeekendDto
import com.example.appdeviajes.data.remote.publicholiday.PublicHolidayDto
import java.time.LocalDate

fun PublicHolidayDto.toDomain(): Feriado = Feriado(
    fecha = LocalDate.parse(date),
    nombreLocal = localName,
    nombre = name,
    codigoPais = countryCode,
    esNacional = global,
    regiones = counties.orEmpty(),
    tipos = types.orEmpty()
)

fun LongWeekendDto.toDomain(): FinDeSemanaLargo = FinDeSemanaLargo(
    fechaInicio = LocalDate.parse(startDate),
    fechaFin = LocalDate.parse(endDate),
    cantidadDias = dayCount,
    requiereDiaPuente = needBridgeDay,
    diasPuente = bridgeDays.orEmpty().map(LocalDate::parse)
)
