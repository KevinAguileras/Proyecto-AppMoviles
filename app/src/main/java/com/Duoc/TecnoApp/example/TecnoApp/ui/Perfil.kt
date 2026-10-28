package com.Duoc.TecnoApp.example.TecnoApp.ui

import android.content.ActivityNotFoundException
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.media.ExifInterface
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.Duoc.TecnoApp.example.TecnoApp.model.Rol
import com.Duoc.TecnoApp.example.TecnoApp.model.Usuario
import com.Duoc.TecnoApp.example.TecnoApp.ui.theme.VerdeClaro
import java.io.File

// Lee la foto guardada y corrige la rotación que registra la cámara.
private fun cargarFoto(archivo: File): ImageBitmap? {
    if (!archivo.exists() || archivo.length() == 0L) return null
    return runCatching {
        val bitmap = BitmapFactory.decodeFile(
            archivo.absolutePath,
            BitmapFactory.Options().apply { inSampleSize = 4 }
        ) ?: return@runCatching null
        val grados = when (
            ExifInterface(archivo.absolutePath)
                .getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
        ) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> 0f
        }
        val corregido = if (grados != 0f) {
            Bitmap.createBitmap(
                bitmap, 0, 0, bitmap.width, bitmap.height,
                Matrix().apply { postRotate(grados) }, true
            )
        } else bitmap
        corregido.asImageBitmap()
    }.getOrNull()
}

@Composable
fun PantallaPerfil(usuario: Usuario, rol: Rol, alCambiarRol: () -> Unit, alCerrarSesion: () -> Unit) {
    val contexto = LocalContext.current
    // La foto definitiva queda en la memoria interna de la app y sigue ahí al reabrirla.
    val carpetaFotos = remember { File(contexto.filesDir, "perfil").apply { mkdirs() } }
    val archivoFoto = remember { File(carpetaFotos, "foto_perfil.jpg") }
    val archivoTemporal = remember { File(carpetaFotos, "foto_temporal.jpg") }
    var version by remember { mutableIntStateOf(0) }
    var errorCamara by remember { mutableStateOf(false) }
    val fotoPerfil = remember(version) { cargarFoto(archivoFoto) }

    val camara = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { tomada ->
        if (tomada) {
            runCatching { archivoTemporal.copyTo(archivoFoto, overwrite = true) }
            version++
        }
        archivoTemporal.delete()
    }

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        TituloPantalla("Mi perfil")
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(shape = CircleShape, color = VerdeClaro, modifier = Modifier.size(112.dp)) {
                if (fotoPerfil != null) {
                    Image(
                        bitmap = fotoPerfil,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.fillMaxSize().clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) { Text("👤", fontSize = 48.sp) }
                }
            }
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = {
                errorCamara = false
                try {
                    val uri = FileProvider.getUriForFile(
                        contexto, "${contexto.packageName}.fileprovider", archivoTemporal
                    )
                    camara.launch(uri)
                } catch (e: ActivityNotFoundException) {
                    errorCamara = true
                }
            }) { Text(if (fotoPerfil == null) "📷 Tomar foto de perfil" else "📷 Cambiar foto") }
            if (fotoPerfil != null) {
                TextButton(onClick = { archivoFoto.delete(); version++ }) { Text("Quitar foto") }
            }
            if (errorCamara) {
                Text("No se encontró una aplicación de cámara.", color = MaterialTheme.colorScheme.error)
            }
        }
        Spacer(Modifier.height(14.dp))
        Text("Datos", fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(7.dp))
        CampoFormulario(usuario.nombre, {}, "Nombre", soloLectura = true)
        Spacer(Modifier.height(6.dp))
        CampoFormulario(usuario.apellido, {}, "Apellido", soloLectura = true)
        Spacer(Modifier.height(6.dp))
        CampoFormulario(usuario.rut, {}, "RUT", soloLectura = true)
        Spacer(Modifier.height(6.dp))
        CampoFormulario(usuario.correo, {}, "Correo electrónico", soloLectura = true)
        Spacer(Modifier.height(6.dp))
        CampoFormulario(usuario.telefono, {}, "Teléfono", soloLectura = true)
        Spacer(Modifier.height(12.dp))
        Text("Clasificación: ${usuario.calificacion} ★★★★★", fontWeight = FontWeight.SemiBold)
        Spacer(Modifier.height(7.dp))
        FilaDato("Vista actual", rol.etiqueta)
        Spacer(Modifier.height(16.dp))
        BotonPrincipal(
            if (rol == Rol.PRODUCTOR) "Cambiar a vista Aplicador" else "Cambiar a vista Productor",
            alPulsar = alCambiarRol
        )
        Spacer(Modifier.height(9.dp))
        OutlinedButton(onClick = alCerrarSesion, modifier = Modifier.fillMaxWidth()) {
            Text("Cerrar sesión")
        }
    }
}