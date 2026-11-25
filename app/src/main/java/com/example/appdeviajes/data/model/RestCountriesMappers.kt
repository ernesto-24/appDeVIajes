package com.example.appdeviajes.data.model

import com.example.appdeviajes.data.remote.restcountries.CountryDto
import com.example.appdeviajes.data.remote.restcountries.CurrencyDto

fun CurrencyDto.toDomain(): Moneda? = code?.let { Moneda(codigo = it, nombre = name, simbolo = symbol) }

fun CountryDto.toDomain(): Pais = Pais(
    nombre = names?.common.orEmpty(),
    codigoIso2 = codes?.alpha2.orEmpty(),
    codigoIso3 = codes?.alpha3,
    capital = capitals.orEmpty().firstNotNullOfOrNull { it.name },
    idiomas = languages.orEmpty().mapNotNull { it.name },
    monedas = currencies.orEmpty().mapNotNull { it.toDomain() }
)
