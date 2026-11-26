package com.example.appdeviajes.ui.resumen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdeviajes.data.model.CalendarioFeriados
import com.example.appdeviajes.data.model.ConversionPresupuesto
import com.example.appdeviajes.data.model.DetalleDestino
import com.example.appdeviajes.data.model.Moneda
import com.example.appdeviajes.data.repository.ViajeRepository
import com.example.appdeviajes.ui.comun.mensajeParaUsuario
import com.example.appdeviajes.ui.navegacion.Rutas
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Year

sealed class EstadoDestino {
    data object Cargando : EstadoDestino()
    data class Exito(val detalle: DetalleDestino) : EstadoDestino()
    data class Error(val mensaje: String) : EstadoDestino()
}

sealed class EstadoConversion {
    data object Inactiva : EstadoConversion()
    data object Cargando : EstadoConversion()
    data class Exito(val conversion: ConversionPresupuesto) : EstadoConversion()
    data class Error(val mensaje: String) : EstadoConversion()
}

sealed class EstadoFeriados {
    data object Cargando : EstadoFeriados()
    data class Exito(val calendario: CalendarioFeriados) : EstadoFeriados()
    data object SinDatos : EstadoFeriados()
    data class Error(val mensaje: String) : EstadoFeriados()
}

data class ResumenViajeUiState(
    val codigoPais: String,
    val destino: EstadoDestino = EstadoDestino.Cargando,
    val monedaOrigen: Moneda? = null,
    val monto: String = "",
    val conversion: EstadoConversion = EstadoConversion.Inactiva,
    val anio: Int = Year.now().value,
    val feriados: EstadoFeriados = EstadoFeriados.Cargando
) {
    val puedeRetrocederAnio: Boolean get() = anio > ANIO_ACTUAL - RANGO_ANIOS
    val puedeAvanzarAnio: Boolean get() = anio < ANIO_ACTUAL + RANGO_ANIOS

    companion object {
        val ANIO_ACTUAL: Int = Year.now().value
        const val RANGO_ANIOS = 10
    }
}

class ResumenViajeViewModel(
    private val repositorio: ViajeRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val codigoPais: String = savedStateHandle.get<String>(Rutas.ARG_CODIGO_PAIS).orEmpty()

    private val _uiState = MutableStateFlow(ResumenViajeUiState(codigoPais = codigoPais))
    val uiState: StateFlow<ResumenViajeUiState> = _uiState.asStateFlow()

    private var trabajoDestino: Job? = null
    private var trabajoConversion: Job? = null
    private var trabajoFeriados: Job? = null

    init {
        cargarDestino()
        cargarMonedaOrigen()
        cargarFeriados()
    }

    fun cargarDestino() {
        trabajoDestino?.cancel()
        _uiState.update { it.copy(destino = EstadoDestino.Cargando) }
        trabajoDestino = viewModelScope.launch {
            val estado = try {
                EstadoDestino.Exito(repositorio.obtenerDetalleDestino(codigoPais))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                EstadoDestino.Error(e.mensajeParaUsuario())
            }
            _uiState.update { it.copy(destino = estado) }
        }
    }

    fun cambiarMonto(texto: String) {
        val limpio = texto.replace(',', '.')
        if (limpio.isNotEmpty() && !FORMATO_MONTO.matches(limpio)) return
        trabajoConversion?.cancel()
        _uiState.update { it.copy(monto = texto, conversion = EstadoConversion.Inactiva) }
    }

    fun convertir() {
        val monto = _uiState.value.monto.replace(',', '.').toDoubleOrNull()
        if (monto == null || monto <= 0.0) {
            _uiState.update { it.copy(conversion = EstadoConversion.Error("Escribe un monto válido mayor a cero.")) }
            return
        }
        trabajoConversion?.cancel()
        _uiState.update { it.copy(conversion = EstadoConversion.Cargando) }
        trabajoConversion = viewModelScope.launch {
            val estado = try {
                EstadoConversion.Exito(repositorio.convertirPresupuesto(monto, codigoPais))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                EstadoConversion.Error(e.mensajeParaUsuario())
            }
            _uiState.update {
                it.copy(
                    conversion = estado,
                    monedaOrigen = (estado as? EstadoConversion.Exito)?.conversion?.monedaOrigen ?: it.monedaOrigen
                )
            }
        }
    }

    fun anioAnterior() {
        if (_uiState.value.puedeRetrocederAnio) cambiarAnio(_uiState.value.anio - 1)
    }

    fun anioSiguiente() {
        if (_uiState.value.puedeAvanzarAnio) cambiarAnio(_uiState.value.anio + 1)
    }

    fun cargarFeriados() {
        trabajoFeriados?.cancel()
        val anio = _uiState.value.anio
        _uiState.update { it.copy(feriados = EstadoFeriados.Cargando) }
        trabajoFeriados = viewModelScope.launch {
            val estado = try {
                repositorio.obtenerCalendarioFeriados(codigoPais, anio)
                    ?.let { EstadoFeriados.Exito(it) }
                    ?: EstadoFeriados.SinDatos
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                EstadoFeriados.Error(e.mensajeParaUsuario())
            }
            _uiState.update { it.copy(feriados = estado) }
        }
    }

    private fun cambiarAnio(anio: Int) {
        _uiState.update { it.copy(anio = anio) }
        cargarFeriados()
    }

    private fun cargarMonedaOrigen() {
        viewModelScope.launch {
            try {
                val moneda = repositorio.obtenerOrigen().moneda
                _uiState.update { it.copy(monedaOrigen = moneda) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(monedaOrigen = null) }
            }
        }
    }

    private companion object {
        val FORMATO_MONTO = Regex("^\\d{0,12}(\\.\\d{0,2})?$")
    }
}
