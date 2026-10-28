package com.Duoc.TecnoApp.example.TecnoApp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.Duoc.TecnoApp.example.TecnoApp.model.*
import com.Duoc.TecnoApp.example.TecnoApp.ui.theme.VerdeAcento

@Composable
fun PantallaCotizacion(solicitud: Solicitud, alOfertar: (Long) -> Boolean) {
    var precioTexto by rememberSaveable(solicitud.id) { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    val precio = precioTexto.toLongOrNull()
    val precioValido = precio != null && precio in 1L..1000000000L
    // El total cambia mientras se escribe el precio, sin incluir IVA.
    val total = if (precioValido) precio!! * solicitud.superficieHectareas else 0L

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        TituloPantalla("Detalle de solicitud")
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                Text("${solicitud.tipoProducto.etiqueta} · ${solicitud.superficieHectareas} ha · ${solicitud.horario.etiqueta}", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                EtiquetaEstado(solicitud.estado)
            }
        }
        Spacer(Modifier.height(18.dp))
        FilaDato("¿Tiene agua?", if (solicitud.tieneAgua) "Sí" else "No")
        FilaDato("Obstáculos", if (solicitud.obstaculos.isEmpty()) "No informados" else solicitud.obstaculos.joinToString(", "))
        FilaDato("Comentarios", solicitud.comentarios.ifBlank { "Sin comentarios" })
        Spacer(Modifier.height(14.dp))
        Text("Fotografías de referencia", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        if (solicitud.fotos.isEmpty()) {
            Text("El productor no adjuntó fotografías.", style = MaterialTheme.typography.bodySmall)
        } else {
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                solicitud.fotos.forEach { ruta -> ImagenReferencia(ruta, Modifier.size(74.dp)) }
            }
        }
        Spacer(Modifier.height(18.dp))
        OutlinedTextField(
            value = precioTexto,
            onValueChange = { precioTexto = it.filter(Char::isDigit).take(10); error = false },
            label = { Text("Ingresar precio por hectárea (CLP)") },
            prefix = { Text("$") },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            isError = precioTexto.isNotBlank() && !precioValido
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "El precio total será de ${formatearPesos(total)} + IVA",
            fontWeight = FontWeight.SemiBold,
            color = VerdeAcento
        )
        if (error) Text("No se pudo registrar esta oferta.", color = MaterialTheme.colorScheme.error)
        Spacer(Modifier.height(16.dp))
        BotonPrincipal("¡Ofertar!", habilitado = precioValido && solicitud.estado != EstadoSolicitud.FINALIZADA) {
            error = !alOfertar(precio!!)
        }
    }
}
