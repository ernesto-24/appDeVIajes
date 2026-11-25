package com.example.appdeviajes.data.repository

import com.example.appdeviajes.BuildConfig
import com.example.appdeviajes.data.model.Feriado
import com.example.appdeviajes.data.model.FinDeSemanaLargo
import com.example.appdeviajes.data.model.Pais
import com.example.appdeviajes.data.model.toDomain
import com.example.appdeviajes.data.remote.RetrofitClient
import com.example.appdeviajes.data.remote.publicholiday.PublicHolidayApiService
import com.example.appdeviajes.data.remote.restcountries.RestCountriesApiService

class ViajeRepository(
    private val publicHolidayApi: PublicHolidayApiService =
        RetrofitClient.createService(PublicHolidayApiService.BASE_URL),
    private val restCountriesApi: RestCountriesApiService =
        RetrofitClient.createService(RestCountriesApiService.BASE_URL)
) {

    suspend fun obtenerFeriados(codigoPais: String, anio: Int): List<Feriado> =
        publicHolidayApi.getPublicHolidays(anio, codigoPais.uppercase()).map { it.toDomain() }

    suspend fun obtenerFinesDeSemanaLargos(codigoPais: String, anio: Int): List<FinDeSemanaLargo> =
        publicHolidayApi.getLongWeekends(anio, codigoPais.uppercase()).map { it.toDomain() }

    suspend fun obtenerPais(codigoPais: String): Pais {
        val respuesta = restCountriesApi.getPaisPorCodigo(
            codigo = codigoPais.uppercase(),
            autorizacion = "Bearer ${BuildConfig.RESTCOUNTRIES_API_KEY}"
        )
        val pais = respuesta.data?.objects.orEmpty().firstOrNull()
            ?: throw NoSuchElementException("No se encontró el país con código $codigoPais")
        return pais.toDomain()
    }
}
