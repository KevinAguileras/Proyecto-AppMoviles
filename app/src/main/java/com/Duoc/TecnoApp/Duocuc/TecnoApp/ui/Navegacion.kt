package com.Duoc.TecnoApp.Duocuc.TecnoApp.ui

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.Duoc.TecnoApp.Duocuc.TecnoApp.model.Pantalla
import com.Duoc.TecnoApp.Duocuc.TecnoApp.model.Rol
import com.Duoc.TecnoApp.Duocuc.TecnoApp.viewmodel.TecMundoViewModel

@Composable
fun NavegacionAplicacion(vistaModelo: TecMundoViewModel = viewModel()) {
    val pantalla = vistaModelo.pantallaActual
    val mostrarBarra = pantalla != Pantalla.ACCESO && pantalla != Pantalla.REGISTRO
    var mostrarInfo by remember { mutableStateOf(false) }

    BackHandler(enabled = pantalla != Pantalla.ACCESO) {
        vistaModelo.volver()
    }

    vistaModelo.mensajeErrorXano?.let { mensaje ->

        val esRegistroExitoso =
            mensaje.startsWith("¡Cuenta creada exitosamente!")

        val titulo = when {
            esRegistroExitoso ->
                "Registro exitoso"

            mensaje == "Correo o contraseña incorrectos." ->
                "No se pudo iniciar sesión"

            mensaje.startsWith("Xano respondió correctamente al POST") ->
                "Solicitud enviada. Falta actualizar la lista"

            else ->
                "Problema al comunicarse con Xano"
        }

        AlertDialog(
            onDismissRequest = vistaModelo::cerrarErrorXano,
            title = { Text(titulo) },
            text = { Text(mensaje) },
            confirmButton = {
                TextButton(
                    onClick = vistaModelo::cerrarErrorXano
                ) {
                    Text("Entendido")
                }
            }
        )
    }

    if (mostrarInfo) {
        AlertDialog(
            onDismissRequest = { mostrarInfo = false },
            title = { Text("Acerca de TecMundo Connect") },
            text = {
                Text(
                    "Plataforma de demostración que conecta productores agrícolas con " +
                            "aplicadores. Puedes cambiar de vista para probar ambos roles."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        mostrarInfo = false
                        val nuevoRol = if (vistaModelo.rolActivo == Rol.PRODUCTOR) {
                            Rol.APLICADOR
                        } else {
                            Rol.PRODUCTOR
                        }
                        vistaModelo.cambiarRol(nuevoRol)
                    }
                ) {
                    Text("Cambiar de vista")
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarInfo = false }) {
                    Text("Cerrar")
                }
            }
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(margenes)
        ) {
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
                    alVerSolicitud = { id ->
                        vistaModelo.seleccionarSolicitud(id, Pantalla.DETALLE_PRODUCTOR)
                    }
                )

                Pantalla.NUEVA_SOLICITUD -> PantallaNuevaSolicitud(
                    alPublicar = { tipo, hectareas, agua, horario, obstaculos, comentario, fotos ->
                        vistaModelo.crearSolicitud(
                            tipo, hectareas, agua, horario, obstaculos, comentario, fotos
                        )
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
                    alCotizar = { id ->
                        vistaModelo.seleccionarSolicitud(id, Pantalla.COTIZACION)
                    }
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
                        val nuevoRol = if (vistaModelo.rolActivo == Rol.PRODUCTOR) {
                            Rol.APLICADOR
                        } else {
                            Rol.PRODUCTOR
                        }
                        vistaModelo.cambiarRol(nuevoRol)
                    },
                    alCerrarSesion = vistaModelo::cerrarSesion
                )
            }
        }
    }
}