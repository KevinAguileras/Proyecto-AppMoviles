package com.Duoc.TecnoApp.example.TecnoApp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.Duoc.TecnoApp.example.TecnoApp.model.Solicitud

@Composable
fun PantallaInicioProductor(
    solicitudes: List<Solicitud>,
    alNuevaSolicitud: () -> Unit,
    alVerSolicitud: (Int) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(17.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            TituloPantalla("Mis solicitudes") {
                Button(onClick = alNuevaSolicitud) { Text("+ Nueva solicitud") }
            }
        }
        if (solicitudes.isEmpty()) {
            item { Text("Aún no tienes solicitudes. Crea la primera para recibir ofertas.") }
        }
        items(solicitudes, key = { it.id }) { solicitud ->
            TarjetaSolicitud(solicitud) { alVerSolicitud(solicitud.id) }
        }
    }
}
