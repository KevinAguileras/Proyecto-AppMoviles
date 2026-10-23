package com.Duoc.TecnoApp.example.TecnoApp.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.Duoc.TecnoApp.example.TecnoApp.ui.theme.TecnoGreenPrimary

@Composable
fun LoginScreen(
    onLoginExitoso: () -> Unit,
    onIrARegistro: () -> Unit
) {
    var usuario by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var hasError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Título de la App inspirado en el mockup
        Text(
            text = "TecMundo\nConnect",
            fontSize = 32.sp,
            color = TecnoGreenPrimary,
            lineHeight = 38.sp
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(text = "Login", fontSize = 20.sp, modifier = Modifier.align(Alignment.Start))

        Spacer(modifier = Modifier.height(12.dp))

        // Campo Usuario / Email
        OutlinedTextField(
            value = usuario,
            onValueChange = {
                usuario = it
                hasError = false
            },
            label = { Text("Usuario: productor@tecrmundo.test") },
            isError = hasError,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Campo Contraseña
        OutlinedTextField(
            value = password,
            onValueChange = {
                password = it
                hasError = false
            },
            label = { Text("Password: ••••••••") },
            visualTransformation = PasswordVisualTransformation(),
            isError = hasError,
            modifier = Modifier.fillMaxWidth()
        )

        if (hasError) {
            Text(
                text = "Credenciales incorrectas",
                color = Color.Red,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botón Recuperar contraseña
        TextButton(
            onClick = { /* Lógica de recuperación de contraseña */ },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Recuperar contraseña", color = Color.Gray, fontSize = 12.sp)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón Iniciar Sesión (Verde principal)
        Button(
            onClick = {
                if (usuario.isNotBlank() && password.isNotBlank()) {
                    onLoginExitoso()
                } else {
                    hasError = true
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = TecnoGreenPrimary),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Iniciar Sesión", color = Color.White)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón Secundario para ir al Registro
        OutlinedButton(
            onClick = onIrARegistro,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Crear una cuenta")
        }
    }
}