package com.example.appdeviajes.ui.destinos

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.appdeviajes.data.model.Destino
import com.example.appdeviajes.ui.comun.IndicadorCargando
import com.example.appdeviajes.ui.comun.MensajeError

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DestinosScreen(
    viewModel: DestinosViewModel,
    onDestinoElegido: (String) -> Unit,
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Elegir destino") },
                navigationIcon = {
                    IconButton(onClick = onVolver) { Text("←", style = MaterialTheme.typography.titleLarge) }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            OutlinedTextField(
                value = uiState.consulta,
                onValueChange = viewModel::buscar,
                label = { Text("Buscar país") },
                placeholder = { Text("Ej.: Perú, Japón, España") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )

            val error = uiState.error
            when {
                uiState.cargando -> IndicadorCargando("Cargando países…")
                error != null -> MensajeError(mensaje = error, onReintentar = viewModel::cargarDestinos)
                uiState.destinos.isEmpty() -> Text(
                    text = "No se encontraron países para “${uiState.consulta}”.",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp)
                )
                else -> LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(uiState.destinos, key = { it.codigoPais }) { destino ->
                        FilaDestino(destino = destino, onClick = { onDestinoElegido(destino.codigoPais) })
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

@Composable
private fun FilaDestino(destino: Destino, onClick: () -> Unit) {
    ListItem(
        headlineContent = { Text(destino.nombre) },
        leadingContent = {
            AsyncImage(
                model = destino.urlBandera,
                contentDescription = "Bandera de ${destino.nombre}",
                contentScale = ContentScale.Fit,
                modifier = Modifier.size(width = 48.dp, height = 32.dp)
            )
        },
        modifier = Modifier.clickable(onClick = onClick)
    )
}
