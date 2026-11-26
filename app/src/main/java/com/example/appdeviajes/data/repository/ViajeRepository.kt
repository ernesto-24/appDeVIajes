package com.example.appdeviajes.data.repository

import com.example.appdeviajes.BuildConfig
import com.example.appdeviajes.data.model.CalendarioFeriados
import com.example.appdeviajes.data.model.ConversionPresupuesto
import com.example.appdeviajes.data.model.Destino
import com.example.appdeviajes.data.model.DetalleDestino
import com.example.appdeviajes.data.model.Feriado
import com.example.appdeviajes.data.model.FinDeSemanaLargo
import com.example.appdeviajes.data.model.Moneda
import com.example.appdeviajes.data.model.OrigenViaje
import com.example.appdeviajes.data.model.Pais
import com.example.appdeviajes.data.model.Personaje
import com.example.appdeviajes.data.model.TasaDeCambio
import com.example.appdeviajes.data.model.UbicacionUsuario
import com.example.appdeviajes.data.model.toDomain
import com.example.appdeviajes.data.remote.RetrofitClient
import com.example.appdeviajes.data.remote.exchangerate.ExchangeRateApiService
import com.example.appdeviajes.data.remote.ipgeolocation.IpGeolocationApiService
import com.example.appdeviajes.data.remote.publicholiday.PublicHolidayApiService
import com.example.appdeviajes.data.remote.restcountries.RestCountriesApiService
import com.example.appdeviajes.data.remote.rickandmorty.RickAndMortyApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.text.Collator
import java.time.Instant
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

class ViajeRepository(
    private val publicHolidayApi: PublicHolidayApiService =
        RetrofitClient.createService(PublicHolidayApiService.BASE_URL),
    private val restCountriesApi: RestCountriesApiService =
        RetrofitClient.createService(RestCountriesApiService.BASE_URL),
    private val exchangeRateApi: ExchangeRateApiService =
        RetrofitClient.createService(ExchangeRateApiService.BASE_URL),
    private val ipGeolocationApi: IpGeolocationApiService =
        RetrofitClient.createService(IpGeolocationApiService.BASE_URL),
    private val rickAndMortyApi: RickAndMortyApiService =
        RetrofitClient.createService(RickAndMortyApiService.BASE_URL),
    private val restCountriesApiKey: String = BuildConfig.RESTCOUNTRIES_API_KEY
) {

    private val mutexOrigen = Mutex()

    @Volatile
    private var origenGuardado: OrigenViaje? = null

    suspend fun obtenerFeriados(codigoPais: String, anio: Int): List<Feriado> =
        publicHolidayApi.getPublicHolidays(anio, codigoPais.uppercase()).map { it.toDomain() }

    suspend fun obtenerFinesDeSemanaLargos(codigoPais: String, anio: Int): List<FinDeSemanaLargo> =
        publicHolidayApi.getLongWeekends(anio, codigoPais.uppercase()).map { it.toDomain() }

    suspend fun obtenerCalendarioFeriados(codigoPais: String, anio: Int): CalendarioFeriados? =
        coroutineScope {
            val feriados = async { nullSiNoHayDatos { obtenerFeriados(codigoPais, anio) } }
            val finesLargos = async { nullSiNoHayDatos { obtenerFinesDeSemanaLargos(codigoPais, anio) } }
            val listaFeriados = feriados.await()
            if (listaFeriados.isNullOrEmpty()) {
                finesLargos.cancel()
                null
            } else {
                CalendarioFeriados(
                    codigoPais = codigoPais.uppercase(),
                    anio = anio,
                    feriados = listaFeriados.sortedBy { it.fecha },
                    finesDeSemanaLargos = finesLargos.await().orEmpty().sortedBy { it.fechaInicio }
                )
            }
        }

    suspend fun obtenerPais(codigoPais: String): Pais {
        val codigo = codigoPais.uppercase()
        paisesEnCache[codigo]?.let { return it }
        check(restCountriesApiKey.isNotBlank()) { "Falta RESTCOUNTRIES_API_KEY en local.properties" }
        val pais = restCountriesApi.getCountryByCode(codigo, "Bearer $restCountriesApiKey")
            .data?.objects?.firstOrNull()?.toDomain()
            ?: throw NoSuchElementException("No se encontró el país con código $codigoPais")
        paisesEnCache[codigo] = pais
        return pais
    }

    suspend fun obtenerMoneda(codigoPais: String): String = monedaPrincipal(codigoPais).codigo

    suspend fun obtenerDestinos(): List<Destino> = withContext(Dispatchers.Default) {
        val orden = Collator.getInstance(ESPANOL)
        Locale.getISOCountries()
            .map { Destino(codigoPais = it, nombre = nombreEnEspanol(it), urlBandera = urlBandera(it)) }
            .sortedWith(compareBy(orden) { it.nombre })
    }

    suspend fun obtenerDetalleDestino(codigoPais: String): DetalleDestino {
        val pais = obtenerPais(codigoPais)
        return DetalleDestino(
            nombre = nombreEnEspanol(pais.codigoIso2).ifBlank { pais.nombre },
            urlBandera = urlBandera(pais.codigoIso2),
            pais = pais
        )
    }

    suspend fun obtenerOrigen(): OrigenViaje {
        origenGuardado?.let { return it }
        return mutexOrigen.withLock {
            origenGuardado ?: run {
                val ubicacion = obtenerUbicacionUsuario()
                val moneda = monedaPrincipal(ubicacion.codigoPais)
                OrigenViaje(
                    codigoPais = ubicacion.codigoPais.uppercase(),
                    nombrePais = nombreEnEspanol(ubicacion.codigoPais).ifBlank { ubicacion.nombrePais.orEmpty() },
                    ciudad = ubicacion.ciudad,
                    moneda = moneda
                ).also { origenGuardado = it }
            }
        }
    }

    suspend fun obtenerTasas(monedaBase: String): TasaDeCambio {
        val codigo = monedaBase.uppercase()
        val guardada = tasasEnCache[codigo]
        val proxima = guardada?.proximaActualizacion
        if (guardada != null && proxima != null && Instant.now().isBefore(proxima)) {
            return guardada
        }
        val nueva = exchangeRateApi.getLatestRates(codigo).toDomain()
        tasasEnCache[codigo] = nueva
        return nueva
    }

    suspend fun convertir(monto: Double, desde: String, hacia: String): Double {
        val tasaDeCambio = obtenerTasas(desde)
        val tasa = tasaDeCambio.tasas[hacia.uppercase()]
            ?: throw IllegalArgumentException("Moneda no soportada: $hacia")
        return monto * tasa
    }

    suspend fun convertirPresupuesto(monto: Double, codigoPaisDestino: String): ConversionPresupuesto {
        val origen = obtenerOrigen()
        val monedaDestino = monedaPrincipal(codigoPaisDestino)
        val tasa = obtenerTasas(origen.moneda.codigo).tasas[monedaDestino.codigo.uppercase()]
            ?: throw IllegalArgumentException(
                "Exchange Rate no tiene tasa de ${origen.moneda.codigo} a ${monedaDestino.codigo}"
            )
        return ConversionPresupuesto(
            montoOrigen = monto,
            monedaOrigen = origen.moneda,
            montoDestino = monto * tasa,
            monedaDestino = monedaDestino,
            tasa = tasa
        )
    }

    suspend fun obtenerUbicacionUsuario(): UbicacionUsuario =
        ipGeolocationApi.getCurrentLocation().toDomain()

    suspend fun obtenerUbicacionPorIp(ip: String): UbicacionUsuario =
        ipGeolocationApi.getLocationByIp(ip).toDomain()

    suspend fun obtenerPersonajes(): List<Personaje> =
        rickAndMortyApi.getCharacters().results.orEmpty().map { it.toDomain() }

    private suspend fun monedaPrincipal(codigoPais: String): Moneda =
        obtenerPais(codigoPais).monedas.firstOrNull()
            ?: throw NoSuchElementException("El país $codigoPais no tiene moneda registrada")

    private suspend fun <T> nullSiNoHayDatos(consulta: suspend () -> List<T>): List<T>? =
        try {
            consulta()
        } catch (e: HttpException) {
            if (e.code() == 404) null else throw e
        } catch (e: NullPointerException) {
            null
        }

    private fun nombreEnEspanol(codigoPais: String): String =
        runCatching {
            Locale.Builder().setRegion(codigoPais.uppercase()).build().getDisplayCountry(ESPANOL)
        }.getOrDefault("")

    private fun urlBandera(codigoPais: String): String =
        "https://flagcdn.com/w160/${codigoPais.lowercase()}.png"

    private companion object {
        val ESPANOL: Locale = Locale.forLanguageTag("es")
        val tasasEnCache = ConcurrentHashMap<String, TasaDeCambio>()
        val paisesEnCache = ConcurrentHashMap<String, Pais>()
    }
}
