package com.Duoc.TecnoApp.example.TecnoApp.ui

import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Duoc.TecnoApp.example.TecnoApp.model.*
import com.Duoc.TecnoApp.example.TecnoApp.ui.theme.*

@Composable
fun TituloPantalla(texto: String, accion: (@Composable () -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(bottom = 18.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(texto, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        accion?.invoke()
    }
}

@Composable
fun BotonPrincipal(texto: String, habilitado: Boolean = true, alPulsar: () -> Unit) {
    Button(
        onClick = alPulsar,
        enabled = habilitado,
        shape = RoundedCornerShape(9.dp),
        modifier = Modifier.fillMaxWidth().height(48.dp),
        colors = ButtonDefaults.buttonColors(containerColor = VerdePrincipal)
    ) {
        Text(texto, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun CampoFormulario(
    texto: String,
    alCambiar: (String) -> Unit,
    etiqueta: String,
    modificador: Modifier = Modifier,
    unaLinea: Boolean = true,
    soloLectura: Boolean = false
) {
    OutlinedTextField(
        value = texto,
        onValueChange = alCambiar,
        label = { Text(etiqueta) },
        readOnly = soloLectura,
        singleLine = unaLinea,
        modifier = modificador.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
fun EtiquetaEstado(estado: EstadoSolicitud) {
    val fondo = when (estado) {
        EstadoSolicitud.PENDIENTE -> AmarilloSuave
        EstadoSolicitud.CON_OFERTAS -> AzulSuave
        EstadoSolicitud.FINALIZADA -> VerdeClaro
    }
    val textoColor = when (estado) {
        EstadoSolicitud.PENDIENTE -> Color(0xFF8D4B00)
        EstadoSolicitud.CON_OFERTAS -> Color(0xFF005E99)
        EstadoSolicitud.FINALIZADA -> VerdePrincipal
    }
    Box(
        modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(fondo)
            .padding(horizontal = 10.dp, vertical = 5.dp)
    ) {
        Text(estado.etiqueta, color = textoColor, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TarjetaSolicitud(solicitud: Solicitud, alAbrir: () -> Unit) {
    ElevatedCard(
        onClick = alAbrir,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = Color.White),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f)) {
                Text("Producto: ${solicitud.tipoProducto.etiqueta}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Spacer(Modifier.height(3.dp))
                Text("Superficie: ${solicitud.superficieHectareas} ha · ${solicitud.horario.etiqueta}", fontSize = 12.sp, color = Color.Gray)
            }
            EtiquetaEstado(solicitud.estado)
        }
    }
}

@Composable
fun BarraNavegacionInferior(
    seleccionado: Pantalla,
    alInicio: () -> Unit,
    alInfo: () -> Unit,
    alPerfil: () -> Unit
) {
    NavigationBar(containerColor = Color.White, tonalElevation = 2.dp) {
        NavigationBarItem(
            selected = seleccionado == Pantalla.INICIO_PRODUCTOR || seleccionado == Pantalla.INICIO_APLICADOR,
            onClick = alInicio,
            icon = { Text("🏠") },
            label = { Text("Inicio") }
        )
        NavigationBarItem(
            selected = false,
            onClick = alInfo,
            icon = { Text("ℹ️") },
            label = { Text("Info") }
        )
        NavigationBarItem(
            selected = seleccionado == Pantalla.PERFIL,
            onClick = alPerfil,
            icon = { Text("👤") },
            label = { Text("Perfil") }
        )
    }
}

@Composable
fun ImagenReferencia(ruta: String, modificador: Modifier = Modifier) {
    val contexto = LocalContext.current
    val imagen = remember(ruta) {
        runCatching {
            contexto.contentResolver.openInputStream(android.net.Uri.parse(ruta))?.use { flujo ->
                BitmapFactory.decodeStream(flujo, null, BitmapFactory.Options().apply {
                    inSampleSize = 4
                })?.asImageBitmap()
            }
        }.getOrNull()
    }
    if (imagen != null) {
        Image(
            bitmap = imagen,
            contentDescription = "Fotografía de referencia",
            modifier = modificador.clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
    } else {
        Box(
            modifier = modificador.clip(RoundedCornerShape(8.dp))
                .background(FondoAplicacion).border(1.dp, BordeSuave, RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) { Text("Foto", color = Color.Gray) }
    }
}

@Composable
fun FilaDato(etiqueta: String, valor: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
        Text("$etiqueta: ", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
        Text(valor, fontSize = 13.sp)
    }
}
