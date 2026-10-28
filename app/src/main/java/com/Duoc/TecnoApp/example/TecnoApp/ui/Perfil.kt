package com.Duoc.TecnoApp.example.TecnoApp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Duoc.TecnoApp.example.TecnoApp.model.Rol
import com.Duoc.TecnoApp.example.TecnoApp.model.Usuario
import com.Duoc.TecnoApp.example.TecnoApp.ui.theme.VerdeClaro

@Composable
fun PantallaPerfil(usuario: Usuario, rol: Rol, alCambiarRol: () -> Unit, alCerrarSesion: () -> Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)) {
        TituloPantalla("Mi perfil")
        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            Surface(shape = CircleShape, color = VerdeClaro) {
                Text("👤", modifier = Modifier.padding(18.dp), fontSize = 40.sp)
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
