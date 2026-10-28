package com.Duoc.TecnoApp.example.TecnoApp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Duoc.TecnoApp.example.TecnoApp.ui.theme.VerdePrincipal

@Composable
fun PantallaLogin(alIniciarSesion: (String, String) -> Unit, alCrearCuenta: () -> Unit) {
    var usuario by rememberSaveable { mutableStateOf("productor@tecmundo.test") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var recordarSesion by rememberSaveable { mutableStateOf(false) }
    var aceptaTerminos by rememberSaveable { mutableStateOf(false) }
    var mostrarRecuperacion by remember { mutableStateOf(false) }

    // Si falta alguno de los campos, se bloquean los controles de sesión.
    val camposCompletos = usuario.isNotBlank() && contrasena.isNotBlank()
    val puedeIngresar = camposCompletos && aceptaTerminos

    if (mostrarRecuperacion) {
        AlertDialog(
            onDismissRequest = { mostrarRecuperacion = false },
            title = { Text("Recuperar contraseña") },
            text = { Text("Esta versión de demostración no envía correos. Puedes ingresar una contraseña de prueba para acceder.") },
            confirmButton = { TextButton(onClick = { mostrarRecuperacion = false }) { Text("Entendido") } }
        )
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(shape = RoundedCornerShape(15.dp), color = VerdePrincipal) {
            Text("TM", modifier = Modifier.padding(18.dp), color = Color.White, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(14.dp))
        Text("TecMundo", fontWeight = FontWeight.ExtraBold, fontSize = 29.sp)
        Text("Connect", color = VerdePrincipal, fontWeight = FontWeight.Bold, fontSize = 27.sp)
        Spacer(Modifier.height(24.dp))
        Text("Iniciar sesión", fontWeight = FontWeight.Bold, fontSize = 19.sp)
        Spacer(Modifier.height(16.dp))

        CampoFormulario(
            texto = usuario,
            alCambiar = {
                usuario = it
                if (usuario.isBlank() || contrasena.isBlank()) {
                    recordarSesion = false
                    aceptaTerminos = false
                }
            },
            etiqueta = "Correo o usuario"
        )
        Spacer(Modifier.height(9.dp))
        OutlinedTextField(
            value = contrasena,
            onValueChange = {
                contrasena = it
                if (usuario.isBlank() || contrasena.isBlank()) {
                    recordarSesion = false
                    aceptaTerminos = false
                }
            },
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = recordarSesion,
                onCheckedChange = { recordarSesion = it },
                enabled = camposCompletos
            )
            Text("Recordar sesión", color = if (camposCompletos) Color.DarkGray else Color.Gray, fontSize = 13.sp)
        }
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = aceptaTerminos,
                onCheckedChange = { aceptaTerminos = it },
                enabled = camposCompletos
            )
            Text("Acepto los términos y condiciones", color = if (camposCompletos) Color.DarkGray else Color.Gray, fontSize = 13.sp)
        }
        TextButton(
            onClick = { mostrarRecuperacion = true },
            modifier = Modifier.align(Alignment.End)
        ) { Text("Recuperar contraseña") }
        Spacer(Modifier.height(7.dp))
        BotonPrincipal("Iniciar sesión", habilitado = puedeIngresar) {
            alIniciarSesion(usuario, contrasena)
        }
        Spacer(Modifier.height(15.dp))
        Text("o", color = Color.Gray)
        TextButton(onClick = alCrearCuenta) { Text("Crear una cuenta", fontWeight = FontWeight.SemiBold) }
    }
}
