
package com.Duoc.AgroNova.Duocuc.TecnoApp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.Duoc.AgroNova.Duocuc.TecnoApp.model.*
import com.Duoc.AgroNova.Duocuc.TecnoApp.repository.RepositorioSolicitudes

class TecMundoViewModel : ViewModel() {

    private val repositorio = RepositorioSolicitudes()
    private val historial = mutableStateListOf<Pantalla>()

    private var ingresando = false

    val solicitudes get() = repositorio.solicitudes

    var mensajeErrorXano by mutableStateOf<String?>(null)
        private set

    var guardandoSolicitud by mutableStateOf(false)
        private set

    // Aviso pendiente para mostrar una notificación.
    var avisoPendiente by mutableStateOf<AvisoNotificacion?>(null)
        private set

    fun consumirAviso() {
        avisoPendiente = null
    }

    fun cerrarErrorXano() {
        mensajeErrorXano = null
    }

    fun cargarSolicitudesXano() {
        viewModelScope.launch {
            try {
                repositorio.cargarDesdeXano()
            } catch (error: Exception) {
                mensajeErrorXano =
                    error.message ?: "Error al consultar la base de datos Xano"
            }
        }
    }

    var pantallaActual by mutableStateOf(Pantalla.ACCESO)
        private set

    var rolActivo by mutableStateOf(Rol.PRODUCTOR)
        private set

    var sesionIniciada by mutableStateOf(false)
        private set

    var usuarioActual by mutableStateOf(
        Usuario(
            "Martín",
            "Rojas",
            "99.123.456-K",
            "m.rojas@tecmundo.test",
            "+56 9 0000 1111"
        )
    )
        private set

    var solicitudSeleccionadaId by mutableStateOf(1)
        private set

    val solicitudSeleccionada: Solicitud?
        get() = repositorio.buscar(solicitudSeleccionadaId)

    fun navegar(destino: Pantalla) {
        if (destino == pantallaActual) return
        historial.add(pantallaActual)
        pantallaActual = destino
    }

    fun volver() {
        if (historial.isNotEmpty()) {
            pantallaActual = historial.removeAt(historial.lastIndex)
        } else if (sesionIniciada) {
            irAInicio()
        } else {
            pantallaActual = Pantalla.ACCESO
        }
    }

    fun seleccionarSolicitud(id: Int, destino: Pantalla) {
        if (repositorio.buscar(id) != null) {
            solicitudSeleccionadaId = id
            navegar(destino)
        }
    }

    fun inicioSegunRol(): Pantalla =
        if (rolActivo == Rol.PRODUCTOR)
            Pantalla.INICIO_PRODUCTOR
        else
            Pantalla.INICIO_APLICADOR

    fun irAInicio() {
        historial.clear()
        pantallaActual = inicioSegunRol()
        if (sesionIniciada) cargarSolicitudesXano()
    }

    // Inicio de sesión con Xano.
    fun iniciarSesion(correo: String, contrasena: String) {
        if (ingresando) return

        if (correo.isBlank() || contrasena.isBlank()) {
            mensajeErrorXano = "Ingresa tu correo y contraseña."
            return
        }

        ingresando = true
        mensajeErrorXano = null

        viewModelScope.launch {
            try {
                repositorio.iniciarSesion(correo, contrasena)

                sesionIniciada = true

                avisoPendiente = AvisoNotificacion(
                    titulo = "Sesión iniciada",
                    mensaje = "Bienvenido a AgroNova, ${usuarioActual.nombre}."
                )

                irAInicio()

            } catch (error: kotlinx.coroutines.CancellationException) {
                throw error
            } catch (error: Exception) {
                mensajeErrorXano =
                    error.message ?: "No se pudo iniciar sesión."
            } finally {
                ingresando = false
            }
        }
    }

    // Registro de usuario en Xano.
    fun registrarUsuario(
        usuario: Usuario,
        rol: Rol,
        contrasena: String
    ) {
        if (ingresando) return

        ingresando = true
        mensajeErrorXano = null

        viewModelScope.launch {
            try {
                repositorio.registrarEnXano(
                    usuario,
                    rol,
                    contrasena
                )

                usuarioActual = usuario
                rolActivo = rol

                // El usuario deberá iniciar sesión manualmente.
                sesionIniciada = false

                historial.clear()
                pantallaActual = Pantalla.ACCESO

                avisoPendiente = AvisoNotificacion(
                    titulo = "Cuenta creada",
                    mensaje = "¡Bienvenido a AgroNova, ${usuario.nombre}! Ahora inicia sesión."
                )

                mensajeErrorXano =
                    "¡Cuenta creada exitosamente! Ahora inicia sesión."

            } catch (error: kotlinx.coroutines.CancellationException) {
                throw error
            } catch (error: Exception) {
                mensajeErrorXano =
                    error.message ?: "No se pudo registrar la cuenta."
            } finally {
                ingresando = false
            }
        }
    }

    fun cambiarRol(rol: Rol) {
        rolActivo = rol
        if (sesionIniciada) irAInicio()
    }

    fun cerrarSesion() {
        repositorio.cerrarSesion()
        sesionIniciada = false
        mensajeErrorXano = null
        avisoPendiente = null
        historial.clear()
        pantallaActual = Pantalla.ACCESO
    }

    // Publicar una nueva solicitud.
    fun crearSolicitud(
        tipo: TipoProducto,
        hectareas: Int,
        tieneAgua: Boolean,
        horario: HorarioAplicacion,
        obstaculos: List<String>,
        comentarios: String,
        fotos: List<String>
    ) {
        if (hectareas !in 1..100000 || guardandoSolicitud) return

        viewModelScope.launch {
            guardandoSolicitud = true
            mensajeErrorXano = null

            try {
                repositorio.publicarEnXano(
                    Solicitud(
                        id = 0,
                        tipoProducto = tipo,
                        superficieHectareas = hectareas,
                        tieneAgua = tieneAgua,
                        horario = horario,
                        obstaculos = obstaculos,
                        comentarios = comentarios,
                        fotos = fotos
                    )
                )

                historial.clear()
                pantallaActual = Pantalla.INICIO_PRODUCTOR

                avisoPendiente = AvisoNotificacion(
                    titulo = "Solicitud publicada",
                    mensaje = "Tu solicitud se publicó correctamente en AgroNova."
                )

                try {
                    repositorio.cargarDesdeXano()
                } catch (error: Exception) {
                    mensajeErrorXano =
                        "Xano respondió correctamente al POST, pero no fue posible " +
                                "actualizar la lista de solicitudes: ${error.message}. " +
                                "Comprueba el registro en Xano antes de volver a publicar."
                }

            } catch (error: Exception) {
                mensajeErrorXano =
                    "No se confirmó la publicación: ${error.message}. " +
                            "Comprueba Xano antes de volver a intentar."
            } finally {
                guardandoSolicitud = false
            }
        }
    }

    // Enviar una oferta.
    fun enviarOferta(precioPorHectarea: Long): Boolean {
        val solicitud = solicitudSeleccionada ?: return false

        if (
            solicitud.estado == EstadoSolicitud.FINALIZADA ||
            precioPorHectarea <= 0L ||
            precioPorHectarea > 1000000000L
        ) return false

        val proximoId = (
                solicitudes.flatMap { it.ofertas }
                    .maxOfOrNull { it.id } ?: 0
                ) + 1

        val nuevaOferta = Oferta(
            id = proximoId,
            nombreAplicador = "${usuarioActual.nombre} ${usuarioActual.apellido}",
            precioPorHectarea = precioPorHectarea,
            calificacion = usuarioActual.calificacion
        )

        repositorio.actualizar(
            solicitud.copy(
                ofertas = solicitud.ofertas + nuevaOferta,
                estado = EstadoSolicitud.CON_OFERTAS
            )
        )

        irAInicio()
        return true
    }

    fun aceptarOferta(idOferta: Int): Boolean {
        val solicitud = solicitudSeleccionada ?: return false

        if (
            solicitud.estado == EstadoSolicitud.FINALIZADA ||
            solicitud.ofertas.none { it.id == idOferta }
        ) return false

        repositorio.actualizar(
            solicitud.copy(
                estado = EstadoSolicitud.FINALIZADA
            )
        )

        return true
    }
}
