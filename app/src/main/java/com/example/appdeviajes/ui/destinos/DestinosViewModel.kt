package com.example.appdeviajes.ui.destinos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdeviajes.data.model.Destino
import com.example.appdeviajes.data.repository.ViajeRepository
import com.example.appdeviajes.ui.comun.mensajeParaUsuario
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.Normalizer

data class DestinosUiState(
    val cargando: Boolean = true,
    val consulta: String = "",
    val destinos: List<Destino> = emptyList(),
    val error: String? = null
)

class DestinosViewModel(private val repositorio: ViajeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(DestinosUiState())
    val uiState: StateFlow<DestinosUiState> = _uiState.asStateFlow()

    private var todos: List<Destino> = emptyList()

    init {
        cargarDestinos()
    }

    fun cargarDestinos() {
        _uiState.update { it.copy(cargando = true, error = null) }
        viewModelScope.launch {
            try {
                todos = repositorio.obtenerDestinos()
                _uiState.update { it.copy(cargando = false, destinos = filtrar(it.consulta)) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update { it.copy(cargando = false, error = e.mensajeParaUsuario()) }
            }
        }
    }

    fun buscar(consulta: String) {
        _uiState.update { it.copy(consulta = consulta, destinos = filtrar(consulta)) }
    }

    private fun filtrar(consulta: String): List<Destino> {
        val buscado = normalizar(consulta)
        if (buscado.isEmpty()) return todos
        return todos.filter { normalizar(it.nombre).contains(buscado) }
    }

    private fun normalizar(texto: String): String =
        Normalizer.normalize(texto.trim().lowercase(), Normalizer.Form.NFD)
            .replace(DIACRITICOS, "")

    private companion object {
        val DIACRITICOS = Regex("\\p{Mn}+")
    }
}
