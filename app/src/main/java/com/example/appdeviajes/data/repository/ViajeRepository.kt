package com.example.appdeviajes.data.repository

import com.example.appdeviajes.data.model.Feriado
import com.example.appdeviajes.data.model.FinDeSemanaLargo
import com.example.appdeviajes.data.model.toDomain
import com.example.appdeviajes.data.remote.RetrofitClient
import com.example.appdeviajes.data.remote.publicholiday.PublicHolidayApiService

class ViajeRepository(
    private val publicHolidayApi: PublicHolidayApiService =
        RetrofitClient.createService(PublicHolidayApiService.BASE_URL)
) {

    suspend fun obtenerFeriados(codigoPais: String, anio: Int): List<Feriado> =
        publicHolidayApi.getPublicHolidays(anio, codigoPais.uppercase()).map { it.toDomain() }

    suspend fun obtenerFinesDeSemanaLargos(codigoPais: String, anio: Int): List<FinDeSemanaLargo> =
        publicHolidayApi.getLongWeekends(anio, codigoPais.uppercase()).map { it.toDomain() }
}
