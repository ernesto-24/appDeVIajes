package com.example.appdeviajes

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.appdeviajes.data.repository.ViajeRepository
import com.example.appdeviajes.ui.theme.AppDeViajesTheme
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppDeViajesTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        lifecycleScope.launch {
            val repository = ViajeRepository()
            try {
                val feriados = repository.obtenerFeriados("BO", 2026)
                Log.d("ViajeTest", "Feriados BO 2026: ${feriados.size}")
                feriados.forEach { Log.d("ViajeTest", it.toString()) }

                val finesLargos = repository.obtenerFinesDeSemanaLargos("BO", 2026)
                Log.d("ViajeTest", "Fines de semana largos BO 2026: ${finesLargos.size}")
                finesLargos.forEach { Log.d("ViajeTest", it.toString()) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("ViajeTest", "Error al consultar Public Holiday API", e)
            }

            try {
                val tasas = repository.obtenerTasas("BOB")
                Log.d("ViajeTest", "Tasas BOB (actualizadas ${tasas.ultimaActualizacion}): ${tasas.tasas.size} monedas")

                val enDolares = repository.convertir(1000.0, "BOB", "USD")
                Log.d("ViajeTest", "1000 BOB = $enDolares USD")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("ViajeTest", "Error al consultar Exchange Rate API", e)
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AppDeViajesTheme {
        Greeting("Android")
    }
}