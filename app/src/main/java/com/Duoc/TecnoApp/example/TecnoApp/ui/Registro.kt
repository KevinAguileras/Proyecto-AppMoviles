package com.Duoc.TecnoApp.example.TecnoApp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.Duoc.TecnoApp.example.TecnoApp.model.Rol
import com.Duoc.TecnoApp.example.TecnoApp.model.Usuario
import com.Duoc.TecnoApp.example.TecnoApp.ui.theme.VerdePrincipal

@Composable
fun PantallaRegistro(
    rolActual: Rol,
    alCambiarRol: (Rol) -> Unit,
    alRegistrarse: (Usuario, Rol) -> Unit,
    alVolver: () -> Unit
) {
    var nombre by rememberSaveable { mutableStateOf("Martín") }
    var apellido by rememberSaveable { mutableStateOf("Rojas") }
    var rut by rememberSaveable { mutableStateOf("99.123.456-K") }
    var correo by rememberSaveable { mutableStateOf("m.rojas@tecmundo.test") }
    var telefono by rememberSaveable { mutableStateOf("+56 9 0000 1111") }
    // El registro requiere todos los datos y un correo con arroba.
    val camposValidos = listOf(nombre, apellido, rut, correo, telefono).all { it.isNotBlank() } && "@" in correo

    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)) {
        TituloPantalla("Crear tu cuenta")
        Text("Selecciona cómo usarás TecMundo Connect", style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(12.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(Rol.PRODUCTOR, Rol.APLICADOR).forEach { rol ->
                Button(
                    onClick = { alCambiarRol(rol) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = if (rolActual == rol) VerdePrincipal else Color.LightGray)
                ) { Text(if (rol == Rol.PRODUCTOR) "Soy productor" else "Soy aplicador", color = if (rolActual == rol) Color.White else Color.Black) }
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
        if (!camposValidos) {
            Text("Completa todos los datos e ingresa un correo válido.", color = MaterialTheme.colorScheme.error)
        }
        Spacer(Modifier.height(18.dp))
        BotonPrincipal("Registrarse", habilitado = camposValidos) {
            alRegistrarse(Usuario(nombre.trim(), apellido.trim(), rut.trim(), correo.trim(), telefono.trim()), rolActual)
        }
        TextButton(onClick = alVolver, modifier = Modifier.fillMaxWidth()) { Text("Volver al acceso") }
    }
}
