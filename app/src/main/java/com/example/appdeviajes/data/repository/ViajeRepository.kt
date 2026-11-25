package com.example.appdeviajes.data.repository

import com.example.appdeviajes.BuildConfig
import com.example.appdeviajes.data.model.Feriado
import com.example.appdeviajes.data.model.FinDeSemanaLargo
import com.example.appdeviajes.data.model.Pais
import com.example.appdeviajes.data.model.TasaDeCambio
import com.example.appdeviajes.data.model.toDomain
import com.example.appdeviajes.data.remote.RetrofitClient
import com.example.appdeviajes.data.remote.exchangerate.ExchangeRateApiService
import com.example.appdeviajes.data.remote.publicholiday.PublicHolidayApiService
import com.example.appdeviajes.data.remote.restcountries.RestCountriesApiService
import java.time.Instant

class ViajeRepository(
    private val publicHolidayApi: PublicHolidayApiService =
        RetrofitClient.createService(PublicHolidayApiService.BASE_URL),
    private val restCountriesApi: RestCountriesApiService =
        RetrofitClient.createService(RestCountriesApiService.BASE_URL)
    private val exchangeRateApi: ExchangeRateApiService =
        RetrofitClient.createService(ExchangeRateApiService.BASE_URL)
) {

    // Las tasas se actualizan una vez al día: se guardan por moneda base hasta la próxima actualización.
    private val tasasEnCache = mutableMapOf<String, TasaDeCambio>()

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
