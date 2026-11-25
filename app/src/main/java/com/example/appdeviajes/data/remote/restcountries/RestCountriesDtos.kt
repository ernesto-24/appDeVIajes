package com.example.appdeviajes.data.remote.restcountries

import com.google.gson.annotations.SerializedName

// DTOs de REST Countries API (v5): https://api.restcountries.com/countries/v5/...
// Documentación: https://restcountries.com/docs/countries
// La respuesta viene envuelta en {"data":{"objects":[ ... ]}}.

data class RestCountriesResponseDto(
    @SerializedName("data") val data: RestCountriesDataDto?
)

data class RestCountriesDataDto(
    @SerializedName("objects") val objects: List<CountryDto>?
)

data class CountryDto(
    @SerializedName("names") val names: CountryNamesDto?,
    @SerializedName("codes") val codes: CountryCodesDto?,
    @SerializedName("capitals") val capitals: List<CapitalDto>?,
    @SerializedName("languages") val languages: List<LanguageDto>?,
    @SerializedName("currencies") val currencies: List<CurrencyDto>?
)

data class CountryNamesDto(
    @SerializedName("common") val common: String?,
    @SerializedName("official") val official: String?
)

data class CountryCodesDto(
    @SerializedName("alpha_2") val alpha2: String?,
    @SerializedName("alpha_3") val alpha3: String?
)

data class CapitalDto(
    @SerializedName("name") val name: String?
)

data class LanguageDto(
    @SerializedName("iso639_1") val iso6391: String?,
    @SerializedName("name") val name: String?
)

data class CurrencyDto(
    @SerializedName("code") val code: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("symbol") val symbol: String?
)
