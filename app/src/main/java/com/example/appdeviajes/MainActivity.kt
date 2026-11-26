package com.example.appdeviajes

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.lifecycleScope
import com.example.appdeviajes.data.repository.ViajeRepository
import com.example.appdeviajes.ui.navegacion.AppNavegacion
import com.example.appdeviajes.ui.theme.AppDeViajesTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppDeViajesTheme {
                AppNavegacion()
            }
        }

        lifecycleScope.launch {
            val repository = ViajeRepository()
            try {
                val personajes = repository.obtenerPersonajes()
                Log.d("ViajeTest", "Personajes Rick and Morty: ${personajes.size}")
                personajes.forEach { Log.d("ViajeTest", it.toString()) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("ViajeTest", "Error al consultar Rick and Morty API", e)
            }
        }
    }
}
