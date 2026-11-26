package com.example.appdeviajes.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.appdeviajes.data.repository.ViajeRepository
import com.example.appdeviajes.ui.destinos.DestinosViewModel
import com.example.appdeviajes.ui.inicio.InicioViewModel
import com.example.appdeviajes.ui.resumen.ResumenViajeViewModel

class ViajaloVosViewModelFactory(private val repositorio: ViajeRepository) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T = when {
        modelClass.isAssignableFrom(InicioViewModel::class.java) ->
            InicioViewModel(repositorio)
        modelClass.isAssignableFrom(DestinosViewModel::class.java) ->
            DestinosViewModel(repositorio)
        modelClass.isAssignableFrom(ResumenViajeViewModel::class.java) ->
            ResumenViajeViewModel(repositorio, extras.createSavedStateHandle())
        else -> throw IllegalArgumentException("ViewModel desconocido: ${modelClass.name}")
    } as T
}
