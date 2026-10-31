package com.Duoc.TecnoApp.example.TecnoApp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Duoc.TecnoApp.example.TecnoApp.model.*
import com.Duoc.TecnoApp.example.TecnoApp.viewmodel.TecMundoViewModel

// Controlador central de las ocho pantallas y de la barra inferior.
@Composable
fun NavegacionAplicacion(vistaModelo: TecMundoViewModel = viewModel()) {
    val pantalla = vistaModelo.pantallaActual
    val mostrarBarra = pantalla != Pantalla.ACCESO && pantalla != Pantalla.REGISTRO
    var mostrarInfo by remember { mutableStateOf(false) }

    BackHandler(enabled = pantalla != Pantalla.ACCESO) {
        vistaModelo.volver()
    }

    vistaModelo.mensajeErrorXano?.let { mensaje ->
        AlertDialog(
            onDismissRequest = vistaModelo::cerrarErrorXano,
            title = {
                Text(
                    if (mensaje.startsWith("Xano respondió correctamente al POST"))
                        "Solicitud enviada; falta actualizar la lista"
                    else "Problema al comunicarse con Xano"
                )
            },
            text = { Text(mensaje) },
            confirmButton = {
                TextButton(onClick = vistaModelo::cerrarErrorXano) { Text("Entendido") }
            }
        )
    }

    if (mostrarInfo) {
        AlertDialog(
            onDismissRequest = { mostrarInfo = false },
            title = { Text("Acerca de TecMundo Connect") },
            text = { Text("Plataforma de demostración que conecta productores agrícolas con aplicadores. Puedes cambiar de vista para probar ambos roles.") },
            confirmButton = {
                TextButton(onClick = {
                    mostrarInfo = false
                    vistaModelo.cambiarRol(if (vistaModelo.rolActivo == Rol.PRODUCTOR) Rol.APLICADOR else Rol.PRODUCTOR)
                }) { Text("Cambiar de vista") }
            },
            dismissButton = { TextButton(onClick = { mostrarInfo = false }) { Text("Cerrar") } }
        )
    }

    Scaffold(
        bottomBar = {
            if (mostrarBarra) {
                BarraNavegacionInferior(
                    seleccionado = pantalla,
                    alInicio = { vistaModelo.irAInicio() },
                    alInfo = { mostrarInfo = true },
                    alPerfil = { vistaModelo.navegar(Pantalla.PERFIL) }
                )
            }
        }
    ) { margenes ->
        Box(Modifier.fillMaxSize().padding(margenes)) {
            when (pantalla) {
                Pantalla.ACCESO -> PantallaLogin(
                    alIniciarSesion = vistaModelo::iniciarSesion,
                    alCrearCuenta = { vistaModelo.navegar(Pantalla.REGISTRO) }
                )
                Pantalla.REGISTRO -> PantallaRegistro(
                    rolActual = vistaModelo.rolActivo,
                    alCambiarRol = vistaModelo::cambiarRol,
                    alRegistrarse = vistaModelo::registrarUsuario,
                    alVolver = { vistaModelo.volver() }
                )
                Pantalla.INICIO_PRODUCTOR -> PantallaInicioProductor(
                    solicitudes = vistaModelo.solicitudes,
                    alNuevaSolicitud = { vistaModelo.navegar(Pantalla.NUEVA_SOLICITUD) },
                    alVerSolicitud = { vistaModelo.seleccionarSolicitud(it, Pantalla.DETALLE_PRODUCTOR) }
                )
                Pantalla.NUEVA_SOLICITUD -> PantallaNuevaSolicitud(
                    alPublicar = { tipo, hectareas, agua, horario, obstaculos, comentario, fotos ->
                        vistaModelo.crearSolicitud(tipo, hectareas, agua, horario, obstaculos, comentario, fotos)
                    }
                )
                Pantalla.DETALLE_PRODUCTOR -> {
                    val solicitud = vistaModelo.solicitudSeleccionada
                    if (solicitud != null) {
                        PantallaDetalleProductor(
                            solicitud = solicitud,
                            alAceptar = vistaModelo::aceptarOferta,
                            alRegresarInicio = vistaModelo::irAInicio
                        )
                    } else {
                        Text("Solicitud no encontrada")
                    }
                }
                Pantalla.INICIO_APLICADOR -> PantallaExploracion(
                    solicitudes = vistaModelo.solicitudes,
                    alCotizar = { vistaModelo.seleccionarSolicitud(it, Pantalla.COTIZACION) }
                )
                Pantalla.COTIZACION -> {
                    val solicitud = vistaModelo.solicitudSeleccionada
                    if (solicitud != null) {
                        PantallaCotizacion(solicitud, vistaModelo::enviarOferta)
                    } else {
                        Text("Solicitud no encontrada")
                    }
                }
                Pantalla.PERFIL -> PantallaPerfil(
                    usuario = vistaModelo.usuarioActual,
                    rol = vistaModelo.rolActivo,
                    alCambiarRol = {
                        vistaModelo.cambiarRol(if (vistaModelo.rolActivo == Rol.PRODUCTOR) Rol.APLICADOR else Rol.PRODUCTOR)
                    },
                    alCerrarSesion = vistaModelo::cerrarSesion
                )
            }
        }
    }
}
