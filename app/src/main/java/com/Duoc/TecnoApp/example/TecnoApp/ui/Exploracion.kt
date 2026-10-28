package com.Duoc.TecnoApp.example.TecnoApp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.Duoc.TecnoApp.example.TecnoApp.model.*
import com.Duoc.TecnoApp.example.TecnoApp.ui.theme.VerdeAcento

@Composable
fun PantallaExploracion(solicitudes: List<Solicitud>, alCotizar: (Int) -> Unit) {
    val disponibles = solicitudes.filter { it.estado != EstadoSolicitud.FINALIZADA }
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item { TituloPantalla("Solicitudes cerca de ti") }
        if (disponibles.isEmpty()) {
            item { Text("No hay solicitudes disponibles en este momento.") }
        }
        items(disponibles, key = { it.id }) { solicitud ->
            ElevatedCard(onClick = { alCotizar(solicitud.id) }, modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(14.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Producto: ${solicitud.tipoProducto.etiqueta}", fontWeight = FontWeight.Bold)
                        Text("Ofertar ›", color = VerdeAcento, fontWeight = FontWeight.Bold)
                    }
                    Text("Superficie: ${solicitud.superficieHectareas} ha · ${solicitud.horario.etiqueta}")
                    Text("A ${solicitud.distanciaKilometros} km (distancia de ejemplo)", color = VerdeAcento)
                    if (solicitud.estado == EstadoSolicitud.CON_OFERTAS) {
                        Text("También tiene otras ofertas", style = MaterialTheme.typography.labelSmall)
                    }
                }
            }
        }
    }
}
