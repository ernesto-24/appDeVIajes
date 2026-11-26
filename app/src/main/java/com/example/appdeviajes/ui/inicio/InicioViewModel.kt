package com.example.appdeviajes.ui.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.appdeviajes.data.model.OrigenViaje
import com.example.appdeviajes.data.repository.ViajeRepository
import com.example.appdeviajes.ui.comun.mensajeParaUsuario
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class InicioUiState {
    data object Cargando : InicioUiState()
    data class Exito(val origen: OrigenViaje) : InicioUiState()
    data class Error(val mensaje: String) : InicioUiState()
}

class InicioViewModel(private val repositorio: ViajeRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<InicioUiState>(InicioUiState.Cargando)
    val uiState: StateFlow<InicioUiState> = _uiState.asStateFlow()

    private var trabajo: Job? = null

    init {
        detectarOrigen()
    }

    fun detectarOrigen() {
        trabajo?.cancel()
        _uiState.value = InicioUiState.Cargando
        trabajo = viewModelScope.launch {
            _uiState.value = try {
                InicioUiState.Exito(repositorio.obtenerOrigen())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                InicioUiState.Error(e.mensajeParaUsuario())
            }
        }
    }
}
