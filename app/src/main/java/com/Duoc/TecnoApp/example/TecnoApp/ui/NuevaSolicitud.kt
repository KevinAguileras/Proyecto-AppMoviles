package com.Duoc.TecnoApp.example.TecnoApp.ui

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import com.Duoc.TecnoApp.example.TecnoApp.model.*

private val opcionesObstaculos = listOf("Antenas", "Cables", "Torres de alta tensión", "Árboles nativos")

@Composable
fun PantallaNuevaSolicitud(
    alPublicar: (TipoProducto, Int, Boolean, HorarioAplicacion, List<String>, String, List<String>) -> Unit
) {
    var tipo by remember { mutableStateOf(TipoProducto.GRANULADA) }
    var menuAbierto by remember { mutableStateOf(false) }
    var hectareasTexto by rememberSaveable { mutableStateOf("") }
    var tieneAgua by remember { mutableStateOf(true) }
    var horario by remember { mutableStateOf(HorarioAplicacion.DIURNA) }
    val obstaculos = remember { mutableStateListOf<String>() }
    var comentarios by rememberSaveable { mutableStateOf("") }
    val fotos = remember { mutableStateListOf<String>() }
    val selectorFoto = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { foto ->
        if (foto != null && fotos.size < 4) fotos.add(foto.toString())
    }
    // Una solicitud necesita una superficie numérica mayor que cero.
    val hectareas = hectareasTexto.toIntOrNull()
    val superficieValida = hectareas != null && hectareas in 1..100000

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(18.dp)
    ) {
        TituloPantalla("Nueva solicitud")
        Text("Tipo de producto", style = MaterialTheme.typography.labelLarge)
        Box {
            OutlinedButton(onClick = { menuAbierto = true }, modifier = Modifier.fillMaxWidth()) {
                Text("${tipo.etiqueta} ▼")
            }
            DropdownMenu(expanded = menuAbierto, onDismissRequest = { menuAbierto = false }) {
                TipoProducto.entries.forEach { opcion ->
                    DropdownMenuItem(
                        text = { Text(opcion.etiqueta) },
                        onClick = { tipo = opcion; menuAbierto = false }
                    )
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = hectareasTexto,
            onValueChange = { hectareasTexto = it.filter(Char::isDigit).take(6) },
            label = { Text("Superficie (hectáreas)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            singleLine = true,
            isError = hectareasTexto.isNotEmpty() && !superficieValida,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(14.dp))
        Text("¿Tiene agua?", style = MaterialTheme.typography.labelLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = tieneAgua, onClick = { tieneAgua = true })
            Text("Sí")
            Spacer(Modifier.width(18.dp))
            RadioButton(selected = !tieneAgua, onClick = { tieneAgua = false })
            Text("No")
        }
        Text("Aplicación", style = MaterialTheme.typography.labelLarge)
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = horario == HorarioAplicacion.DIURNA, onClick = { horario = HorarioAplicacion.DIURNA })
            Text("Diurna")
            Spacer(Modifier.width(12.dp))
            RadioButton(selected = horario == HorarioAplicacion.NOCTURNA, onClick = { horario = HorarioAplicacion.NOCTURNA })
            Text("Nocturna")
        }
        Text("Obstáculos", style = MaterialTheme.typography.labelLarge)
        opcionesObstaculos.forEach { opcion ->
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = opcion in obstaculos,
                    onCheckedChange = { marcado -> if (marcado) obstaculos.add(opcion) else obstaculos.remove(opcion) }
                )
                Text(opcion)
            }
        }
        CampoFormulario(comentarios, { comentarios = it }, "Comentarios adicionales", unaLinea = false)
        Spacer(Modifier.height(14.dp))
        Text("Fotos de referencia (${fotos.size}/4)", style = MaterialTheme.typography.labelLarge)
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
            fotos.forEach { foto ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ImagenReferencia(foto, Modifier.size(62.dp))
                    TextButton(onClick = { fotos.remove(foto) }) { Text("Quitar") }
                }
            }
            if (fotos.size < 4) {
                OutlinedButton(onClick = { selectorFoto.launch("image/*") }) { Text("+ Foto") }
            }
        }
        Spacer(Modifier.height(18.dp))
        BotonPrincipal("Publicar solicitud", habilitado = superficieValida) {
            alPublicar(tipo, hectareas!!, tieneAgua, horario, obstaculos.toList(), comentarios, fotos.toList())
        }
        Spacer(Modifier.height(12.dp))
    }
}
