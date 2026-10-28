package com.Duoc.TecnoApp.example.TecnoApp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.Duoc.TecnoApp.example.TecnoApp.model.*

@Composable
fun PantallaDetalleProductor(
    solicitud: Solicitud,
    alAceptar: (Int) -> Boolean,
    alRegresarInicio: () -> Unit
) {
    var mostrarConfirmacion by remember { mutableStateOf(false) }
    var contacto by remember { mutableStateOf<Oferta?>(null) }

    // Confirmación posterior a guardar el estado finalizado.
    if (mostrarConfirmacion) {
        AlertDialog(
            onDismissRequest = alRegresarInicio,
            title = { Text("Oferta aceptada") },
            text = { Text("La solicitud quedó finalizada. Puedes revisar su estado en Mis solicitudes.") },
            confirmButton = {
                TextButton(onClick = alRegresarInicio) { Text("Volver a mis solicitudes") }
            }
        )
    }
    if (contacto != null) {
        AlertDialog(
            onDismissRequest = { contacto = null },
            title = { Text("Comunicarse con aplicador") },
            text = { Text("Contacto simulado con ${contacto!!.nombreAplicador}. En una próxima versión se podrá abrir el chat o llamar al aplicador.") },
            confirmButton = {
                TextButton(onClick = { contacto = null }) { Text("Cerrar") }
            }
        )
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        TituloPantalla("Detalle de solicitud")
        ElevatedCard(modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(14.dp)) {
                Text("${solicitud.tipoProducto.etiqueta} · ${solicitud.superficieHectareas} ha", fontWeight = FontWeight.Bold)
                FilaDato("Horario", solicitud.horario.etiqueta)
                FilaDato("Agua", if (solicitud.tieneAgua) "Sí" else "No")
                Spacer(Modifier.height(7.dp))
                EtiquetaEstado(solicitud.estado)
            }
        }
        Spacer(Modifier.height(21.dp))
        Text("Ofertas recibidas (${solicitud.ofertas.size})", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(10.dp))
        if (solicitud.ofertas.isEmpty()) {
            Text("Aún no hay ofertas. Cambia a la vista de aplicador desde el perfil para enviar una cotización de prueba.")
        }
        solicitud.ofertas.forEach { oferta ->
            ElevatedCard(modifier = Modifier.fillMaxWidth().padding(bottom = 10.dp)) {
                Column(Modifier.padding(14.dp)) {
                    Text("👤 ${oferta.nombreAplicador}", fontWeight = FontWeight.Bold)
                    FilaDato("Precio por hectárea", "${formatearPesos(oferta.precioPorHectarea)} CLP")
                    FilaDato("Total", formatearPesos(oferta.precioPorHectarea * solicitud.superficieHectareas))
                    Text("Calificación: ${oferta.calificacion} ★★★★★")
                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { if (alAceptar(oferta.id)) mostrarConfirmacion = true },
                            enabled = solicitud.estado != EstadoSolicitud.FINALIZADA,
                            modifier = Modifier.weight(1f)
                        ) { Text("Aceptar oferta") }
                        OutlinedButton(
                            onClick = { contacto = oferta },
                            modifier = Modifier.weight(1f)
                        ) { Text("Comunicarse") }
                    }
                }
            }
        }
        if (solicitud.estado == EstadoSolicitud.FINALIZADA) {
            Text("Esta solicitud ya fue finalizada.", color = MaterialTheme.colorScheme.primary)
        }
    }
}
