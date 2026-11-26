package com.example.appdeviajes.ui.inicio

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.appdeviajes.data.model.OrigenViaje
import com.example.appdeviajes.ui.comun.IndicadorCargando
import com.example.appdeviajes.ui.comun.MensajeError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(viewModel: InicioViewModel, onElegirDestino: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = { CenterAlignedTopAppBar(title = { Text("viajaloVos") }) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Tu punto de partida", style = MaterialTheme.typography.headlineSmall)

            when (val estado = uiState) {
                InicioUiState.Cargando -> IndicadorCargando("Detectando tu país de origen…")
                is InicioUiState.Error -> MensajeError(
                    mensaje = "No pudimos detectar tu origen. ${estado.mensaje}",
                    onReintentar = viewModel::detectarOrigen
                )
                is InicioUiState.Exito -> TarjetaOrigen(estado.origen)
            }

            Button(onClick = onElegirDestino, modifier = Modifier.fillMaxWidth()) {
                Text("Elegir destino")
            }
        }
    }
}

@Composable
private fun TarjetaOrigen(origen: OrigenViaje) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "País de origen", style = MaterialTheme.typography.labelLarge)
            Text(text = origen.nombrePais, style = MaterialTheme.typography.titleLarge)
            origen.ciudad?.takeIf { it.isNotBlank() && it != "-" }?.let {
                Text(text = "Ciudad: $it", style = MaterialTheme.typography.bodyMedium)
            }
            Text(text = "Moneda de origen", style = MaterialTheme.typography.labelLarge)
            Text(
                text = buildString {
                    append(origen.moneda.nombre ?: origen.moneda.codigo)
                    append(" (")
                    append(origen.moneda.codigo)
                    append(")")
                    origen.moneda.simbolo?.let { append(" · $it") }
                },
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}
