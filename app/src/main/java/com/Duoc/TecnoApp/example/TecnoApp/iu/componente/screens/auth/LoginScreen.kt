package com.Duoc.TecnoApp.example.TecnoApp.iu.componente.screens.auth // <-- Si renombraste la carpeta a ui.screens.auth, ajusta esta línea

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// Definimos el verde aquí mismo para evitar errores de importación
val VerdeTecMundo = Color(0xFF1E4620)

@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit = {},
    onIrARegistro: () -> Unit = {}
) {
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var recordarSesion by remember { mutableStateOf(false) }
    var aceptaTerminos by remember { mutableStateOf(false) }

    // Verifica si ambos campos tienen algo escrito
    val hayTextoEscrito = usuario.isNotBlank() && password.isNotBlank()

    // Condición final para que el botón Iniciar Sesión se active
    val puedeIniciarSesion = hayTextoEscrito && aceptaTerminos

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título de la App
        Text(
            text = "TecMundo\nConnect",
            fontSize = 32.sp,
            color = VerdeTecMundo,
            lineHeight = 38.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Login",
            fontSize = 20.sp,
            modifier = Modifier.align(Alignment.Start)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Campo Usuario
        OutlinedTextField(
            value = usuario,
            onValueChange = { nuevoTexto ->
                usuario = nuevoTexto
                // Si borra el texto, se desmarcan solos los checkboxes
                if (nuevoTexto.isBlank()) {
                    recordarSesion = false
                    aceptaTerminos = false
                }
            },
            label = { Text("Usuario: productor@tecmundo.test") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { nuevoTexto ->
                password = nuevoTexto
                // Si borra la contraseña, se desmarcan solos los checkboxes
                if (nuevoTexto.isBlank()) {
                    recordarSesion = false
                    aceptaTerminos = false
                }
            },
            label = { Text("Password: ••••••••") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Checkbox 1: Recordar sesión (solo se puede marcar si hay texto escrito)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = hayTextoEscrito) { recordarSesion = !recordarSesion },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = recordarSesion,
                onCheckedChange = { recordarSesion = it },
                enabled = hayTextoEscrito,
                colors = CheckboxDefaults.colors(checkedColor = VerdeTecMundo)
            )
            Text(
                text = "Recordar sesión",
                fontSize = 14.sp,
                color = if (hayTextoEscrito) Color.Black else Color.Gray
            )
        }

        // Checkbox 2: Aceptar términos y condiciones (obligatorio para activar el botón)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(enabled = hayTextoEscrito) { aceptaTerminos = !aceptaTerminos },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = aceptaTerminos,
                onCheckedChange = { aceptaTerminos = it },
                enabled = hayTextoEscrito,
                colors = CheckboxDefaults.colors(checkedColor = VerdeTecMundo)
            )
            Text(
                text = "Acepto los términos y condiciones",
                fontSize = 14.sp,
                color = if (hayTextoEscrito) Color.Black else Color.Gray
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Iniciar Sesión (Se activa SOLO cuando hay texto + términos aceptados)
        Button(
            onClick = onLoginExitoso,
            enabled = puedeIniciarSesion,
            colors = ButtonDefaults.buttonColors(
                containerColor = VerdeTecMundo,
                disabledContainerColor = Color.LightGray
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Iniciar Sesión",
                color = if (puedeIniciarSesion) Color.White else Color.DarkGray
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Botón Crear una cuenta
        OutlinedButton(
            onClick = onIrARegistro,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear una cuenta", color = VerdeTecMundo)
        }
    }
}