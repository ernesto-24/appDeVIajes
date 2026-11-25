package com.example.appdeviajes.data.repository

import com.example.appdeviajes.data.model.Feriado
import com.example.appdeviajes.data.model.FinDeSemanaLargo
import com.example.appdeviajes.data.model.TasaDeCambio
import com.example.appdeviajes.data.model.toDomain
import com.example.appdeviajes.data.remote.RetrofitClient
import com.example.appdeviajes.data.remote.exchangerate.ExchangeRateApiService
import com.example.appdeviajes.data.remote.publicholiday.PublicHolidayApiService
import java.time.Instant

class ViajeRepository(
    private val publicHolidayApi: PublicHolidayApiService =
        RetrofitClient.createService(PublicHolidayApiService.BASE_URL),
    private val exchangeRateApi: ExchangeRateApiService =
        RetrofitClient.createService(ExchangeRateApiService.BASE_URL)
) {

    // Las tasas se actualizan una vez al día: se guardan por moneda base hasta la próxima actualización.
    private val tasasEnCache = mutableMapOf<String, TasaDeCambio>()

    suspend fun obtenerFeriados(codigoPais: String, anio: Int): List<Feriado> =
        publicHolidayApi.getPublicHolidays(anio, codigoPais.uppercase()).map { it.toDomain() }

    suspend fun obtenerFinesDeSemanaLargos(codigoPais: String, anio: Int): List<FinDeSemanaLargo> =
        publicHolidayApi.getLongWeekends(anio, codigoPais.uppercase()).map { it.toDomain() }

    suspend fun obtenerTasas(monedaBase: String): TasaDeCambio {
        val codigo = monedaBase.uppercase()
        val guardada = tasasEnCache[codigo]
        val proxima = guardada?.proximaActualizacion
        if (guardada != null && proxima != null && Instant.now().isBefore(proxima)) {
            return guardada
        }
        val nueva = exchangeRateApi.getTasas(codigo).toDomain()
        tasasEnCache[codigo] = nueva
        return nueva
    }

    suspend fun convertir(monto: Double, desde: String, hacia: String): Double {
        val tasaDeCambio = obtenerTasas(desde)
        val tasa = tasaDeCambio.tasas[hacia.uppercase()]
            ?: throw IllegalArgumentException("Moneda no soportada: $hacia")
        return monto * tasa
    }
}
