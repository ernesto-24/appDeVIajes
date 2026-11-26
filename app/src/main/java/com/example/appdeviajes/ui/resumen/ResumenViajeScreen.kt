package com.example.appdeviajes.ui.resumen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.example.appdeviajes.data.model.ConversionPresupuesto
import com.example.appdeviajes.data.model.DetalleDestino
import com.example.appdeviajes.data.model.Feriado
import com.example.appdeviajes.data.model.FinDeSemanaLargo
import com.example.appdeviajes.data.model.Moneda
import com.example.appdeviajes.ui.comun.IndicadorCargando
import com.example.appdeviajes.ui.comun.MensajeError
import com.example.appdeviajes.ui.comun.formatearFecha
import com.example.appdeviajes.ui.comun.formatearMonto
import com.example.appdeviajes.ui.comun.formatearRango
import com.example.appdeviajes.ui.comun.formatearTasa

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResumenViajeScreen(viewModel: ResumenViajeViewModel, onVolver: () -> Unit) {
    val uiState by viewModel.uiState.collectAsState()
    val nombreDestino = (uiState.destino as? EstadoDestino.Exito)?.detalle?.nombre

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(nombreDestino ?: "Resumen del viaje") },
                navigationIcon = {
                    IconButton(onClick = onVolver) { Text("←", style = MaterialTheme.typography.titleLarge) }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(16.dp)
        ) {
            item(key = "destino") {
                SeccionDestino(estado = uiState.destino, onReintentar = viewModel::cargarDestino)
            }
            item(key = "conversor") {
                SeccionConversor(
                    monto = uiState.monto,
                    monedaOrigen = uiState.monedaOrigen,
                    estado = uiState.conversion,
                    onMontoCambiado = viewModel::cambiarMonto,
                    onConvertir = viewModel::convertir
                )
            }
            item(key = "selector_anio") {
                SelectorAnio(
                    anio = uiState.anio,
                    puedeRetroceder = uiState.puedeRetrocederAnio,
                    puedeAvanzar = uiState.puedeAvanzarAnio,
                    onAnterior = viewModel::anioAnterior,
                    onSiguiente = viewModel::anioSiguiente
                )
            }
            seccionFeriados(
                estado = uiState.feriados,
                anio = uiState.anio,
                nombreDestino = nombreDestino ?: uiState.codigoPais,
                onReintentar = viewModel::cargarFeriados
            )
        }
    }
}

@Composable
private fun SeccionDestino(estado: EstadoDestino, onReintentar: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        when (estado) {
            EstadoDestino.Cargando -> IndicadorCargando("Cargando datos del destino…")
            is EstadoDestino.Error -> MensajeError(
                mensaje = "No pudimos cargar los datos del destino. ${estado.mensaje}",
                onReintentar = onReintentar
            )
            is EstadoDestino.Exito -> DatosDestino(estado.detalle)
        }
    }
}

@Composable
private fun DatosDestino(detalle: DetalleDestino) {
    val pais = detalle.pais
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AsyncImage(
            model = detalle.urlBandera,
            contentDescription = "Bandera de ${detalle.nombre}",
            contentScale = ContentScale.Fit,
            modifier = Modifier.width(160.dp).height(100.dp)
        )
        Text(text = detalle.nombre, style = MaterialTheme.typography.headlineSmall)
        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            DatoDestino("Capital", pais.capital ?: "Sin capital registrada")
            DatoDestino("Idiomas", pais.idiomas.joinToString(", ").ifBlank { "Sin datos" })
            DatoDestino(
                "Moneda",
                pais.monedas.joinToString(", ") { textoMoneda(it) }.ifBlank { "Sin datos" }
            )
        }
    }
}

@Composable
private fun DatoDestino(etiqueta: String, valor: String) {
    Text(
        text = "$etiqueta: $valor",
        style = MaterialTheme.typography.bodyLarge
    )
}

@Composable
private fun SeccionConversor(
    monto: String,
    monedaOrigen: Moneda?,
    estado: EstadoConversion,
    onMontoCambiado: (String) -> Unit,
    onConvertir: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Convertir presupuesto", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = monto,
                onValueChange = onMontoCambiado,
                label = {
                    Text(monedaOrigen?.let { "Monto en ${it.codigo}" } ?: "Monto en tu moneda de origen")
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal, imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { onConvertir() }),
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = onConvertir,
                enabled = estado != EstadoConversion.Cargando,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Convertir")
            }
            when (estado) {
                EstadoConversion.Inactiva -> Unit
                EstadoConversion.Cargando -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                is EstadoConversion.Error -> MensajeError(mensaje = estado.mensaje, onReintentar = onConvertir)
                is EstadoConversion.Exito -> ResultadoConversion(estado.conversion)
            }
        }
    }
}

@Composable
private fun ResultadoConversion(conversion: ConversionPresupuesto) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "${formatearMonto(conversion.montoOrigen)} ${conversion.monedaOrigen.codigo} equivalen a",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "${formatearMonto(conversion.montoDestino)} ${conversion.monedaDestino.codigo}",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Tasa: 1 ${conversion.monedaOrigen.codigo} = ${formatearTasa(conversion.tasa)} ${conversion.monedaDestino.codigo}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun SelectorAnio(
    anio: Int,
    puedeRetroceder: Boolean,
    puedeAvanzar: Boolean,
    onAnterior: () -> Unit,
    onSiguiente: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = "Feriados del destino", style = MaterialTheme.typography.titleMedium)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = onAnterior, enabled = puedeRetroceder) { Text("‹ Anterior") }
            Text(text = anio.toString(), style = MaterialTheme.typography.titleLarge)
            OutlinedButton(onClick = onSiguiente, enabled = puedeAvanzar) { Text("Siguiente ›") }
        }
    }
}

private fun LazyListScope.seccionFeriados(
    estado: EstadoFeriados,
    anio: Int,
    nombreDestino: String,
    onReintentar: () -> Unit
) {
    when (estado) {
        EstadoFeriados.Cargando -> item(key = "feriados_cargando") {
            IndicadorCargando("Cargando feriados de $anio…")
        }
        is EstadoFeriados.Error -> item(key = "feriados_error") {
            MensajeError(
                mensaje = "No pudimos cargar los feriados. ${estado.mensaje}",
                onReintentar = onReintentar
            )
        }
        EstadoFeriados.SinDatos -> item(key = "feriados_sin_datos") {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Public Holiday no tiene datos de feriados para $nombreDestino en $anio.",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(24.dp)
                )
            }
        }
        is EstadoFeriados.Exito -> {
            val calendario = estado.calendario
            item(key = "titulo_fines") {
                Subtitulo("Fines de semana largos (${calendario.finesDeSemanaLargos.size})")
            }
            if (calendario.finesDeSemanaLargos.isEmpty()) {
                item(key = "fines_vacio") {
                    Text("No hay fines de semana largos en $anio.", style = MaterialTheme.typography.bodyMedium)
                }
            } else {
                itemsIndexed(calendario.finesDeSemanaLargos, key = { i, _ -> "fin_$i" }) { _, fin ->
                    FilaFinDeSemanaLargo(fin)
                }
            }
            item(key = "titulo_feriados") {
                Subtitulo("Feriados (${calendario.feriados.size})")
            }
            itemsIndexed(calendario.feriados, key = { i, _ -> "feriado_$i" }) { _, feriado ->
                FilaFeriado(feriado)
            }
        }
    }
}

@Composable
private fun Subtitulo(texto: String) {
    Column {
        Text(text = texto, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
        HorizontalDivider(modifier = Modifier.padding(top = 4.dp))
    }
}

@Composable
private fun FilaFinDeSemanaLargo(fin: FinDeSemanaLargo) {
    ListItem(
        headlineContent = { Text(formatearRango(fin.fechaInicio, fin.fechaFin)) },
        supportingContent = {
            Text(
                if (fin.requiereDiaPuente) {
                    "${fin.cantidadDias} días · requiere día puente: " +
                        fin.diasPuente.joinToString(", ") { formatearFecha(it) }
                } else {
                    "${fin.cantidadDias} días"
                }
            )
        }
    )
}

@Composable
private fun FilaFeriado(feriado: Feriado) {
    ListItem(
        headlineContent = { Text(feriado.nombreLocal) },
        overlineContent = { Text(formatearFecha(feriado.fecha)) },
        supportingContent = {
            Text(
                buildString {
                    if (feriado.nombreLocal != feriado.nombre) append(feriado.nombre).append(" · ")
                    append(if (feriado.esNacional) "Nacional" else "Regional")
                }
            )
        }
    )
}

private fun textoMoneda(moneda: Moneda): String = buildString {
    append(moneda.nombre ?: moneda.codigo)
    append(" (").append(moneda.codigo).append(")")
    moneda.simbolo?.let { append(" · ").append(it) }
}
