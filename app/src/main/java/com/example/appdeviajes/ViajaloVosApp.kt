package com.example.appdeviajes

import android.app.Application
import com.example.appdeviajes.data.repository.ViajeRepository

class ViajaloVosApp : Application() {
    val repositorio: ViajeRepository by lazy { ViajeRepository() }
}
