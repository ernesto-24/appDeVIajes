package com.example.appdeviajes.data.model

import com.example.appdeviajes.data.remote.restcountries.CapitalDto
import com.example.appdeviajes.data.remote.restcountries.CountryDto
import com.example.appdeviajes.data.remote.restcountries.CurrencyDto
import com.example.appdeviajes.data.remote.restcountries.LanguageDto
import java.util.Currency
import java.util.Locale

private val ESPANOL: Locale = Locale.forLanguageTag("es")

private fun String.conMayuscula(): String = replaceFirstChar { it.titlecase(ESPANOL) }

private fun nombreMonedaEnEspanol(codigo: String): String? =
    runCatching { Currency.getInstance(codigo).getDisplayName(ESPANOL) }.getOrNull()
        ?.takeIf { !it.equals(codigo, ignoreCase = true) }
        ?.conMayuscula()

fun CurrencyDto.toDomain(): Moneda? = code?.let {
    Moneda(codigo = it, nombre = nombreMonedaEnEspanol(it) ?: name, simbolo = symbol)
}

private fun LanguageDto.nombreEnEspanol(): String? {
    val traducido = iso6391?.let { Locale.forLanguageTag(it).getDisplayLanguage(ESPANOL) }
        ?.takeIf { it.isNotBlank() && !it.equals(iso6391, ignoreCase = true) }
        ?.conMayuscula()
    return traducido ?: name
}

private fun List<CapitalDto>.capitalPrincipal(): String? =
    (firstOrNull { it.attributes?.primary == true } ?: firstOrNull())?.name

fun CountryDto.toDomain(): Pais = Pais(
    nombre = requireNotNull(names?.common) { "REST Countries no devolvió el nombre del país" },
    codigoIso2 = requireNotNull(codes?.alpha2) { "REST Countries no devolvió el código del país" },
    codigoIso3 = codes?.alpha3,
    capital = capitals.orEmpty().capitalPrincipal(),
    idiomas = languages.orEmpty().mapNotNull { it.nombreEnEspanol() },
    monedas = currencies.orEmpty().mapNotNull { it.toDomain() }
)
