
package com.Duoc.TecnoApp.Duocuc.TecnoApp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.Duoc.TecnoApp.Duocuc.TecnoApp.model.Rol
import com.Duoc.TecnoApp.Duocuc.TecnoApp.model.Usuario
import com.Duoc.TecnoApp.Duocuc.TecnoApp.ui.theme.VerdePrincipal

@Composable
fun PantallaRegistro(
    rolActual: Rol,
    alCambiarRol: (Rol) -> Unit,
    alRegistrarse: (Usuario, Rol, String) -> Unit,
    alVolver: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf("") }
    var apellido by rememberSaveable { mutableStateOf("") }
    var rut by rememberSaveable { mutableStateOf("") }
    var correo by rememberSaveable { mutableStateOf("") }
    var telefono by rememberSaveable { mutableStateOf("") }
    var contrasena by rememberSaveable { mutableStateOf("") }
    var repetirContrasena by rememberSaveable { mutableStateOf("") }

    val camposValidos =
        nombre.isNotBlank() &&
                apellido.isNotBlank() &&
                rut.isNotBlank() &&
                telefono.isNotBlank() &&
                correo.contains("@") &&
                correo.isNotBlank() &&
                contrasena.length >= 8 &&
                contrasena == repetirContrasena

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp)
    ) {
        TituloPantalla("Crear tu cuenta")

        Text(
            "Selecciona cómo usarás TecMundo Connect",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(12.dp))

        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(Rol.PRODUCTOR, Rol.APLICADOR).forEach { rol ->
                Button(
                    onClick = { alCambiarRol(rol) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (rolActual == rol)
                            VerdePrincipal else Color.LightGray
                    )
                ) {
                    Text(
                        if (rol == Rol.PRODUCTOR) "Soy productor" else "Soy aplicador",
                        color = if (rolActual == rol) Color.White else Color.Black
                    )
                }
            }
        }

        Spacer(Modifier.height(14.dp))

        CampoFormulario(nombre, { nombre = it }, "Nombre")
        Spacer(Modifier.height(8.dp))

        CampoFormulario(apellido, { apellido = it }, "Apellido")
        Spacer(Modifier.height(8.dp))

        CampoFormulario(rut, { rut = it }, "RUT")
        Spacer(Modifier.height(8.dp))

        CampoFormulario(correo, { correo = it }, "Correo electrónico")
        Spacer(Modifier.height(8.dp))

        CampoFormulario(telefono, { telefono = it }, "Teléfono")
        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = contrasena,
            onValueChange = { contrasena = it },
            label = { Text("Crear contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        Spacer(Modifier.height(8.dp))

        OutlinedTextField(
            value = repetirContrasena,
            onValueChange = { repetirContrasena = it },
            label = { Text("Repetir contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )

        if (contrasena.isNotEmpty() && contrasena.length < 8) {
            Text(
                "La contraseña debe tener al menos 8 caracteres.",
                color = MaterialTheme.colorScheme.error
            )
        }

        if (repetirContrasena.isNotEmpty() && contrasena != repetirContrasena) {
            Text(
                "Las contraseñas no coinciden.",
                color = MaterialTheme.colorScheme.error
            )
        }

        Spacer(Modifier.height(18.dp))

        BotonPrincipal(
            "Registrarse",
            habilitado = camposValidos
        ) {
            val nuevoUsuario = Usuario(
                nombre.trim(),
                apellido.trim(),
                rut.trim(),
                correo.trim().lowercase(),
                telefono.trim()
            )

            alRegistrarse(nuevoUsuario, rolActual, contrasena)
        }

        TextButton(
            onClick = alVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver al acceso")
        }
    }
}
