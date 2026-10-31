package com.Duoc.TecnoApp.example.TecnoApp.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import com.Duoc.TecnoApp.example.TecnoApp.model.*
import com.Duoc.TecnoApp.example.TecnoApp.repository.RepositorioSolicitudes

class TecMundoViewModel : ViewModel() {
    private val repositorio = RepositorioSolicitudes()
    private val historial = mutableStateListOf<Pantalla>()
    val solicitudes get() = repositorio.solicitudes

    var mensajeErrorXano by mutableStateOf<String?>(null)
        private set
    var guardandoSolicitud by mutableStateOf(false)
        private set

    fun cerrarErrorXano() { mensajeErrorXano = null }

    fun cargarSolicitudesXano() {
        viewModelScope.launch {
            try {
                repositorio.cargarDesdeXano()
            } catch (error: Exception) {
                mensajeErrorXano = error.message ?: "Error al consultar la base de datos Xano"
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
        Usuario("Martín", "Rojas", "99.123.456-K", "m.rojas@tecmundo.test", "+56 9 0000 1111")
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
        if (rolActivo == Rol.PRODUCTOR) Pantalla.INICIO_PRODUCTOR else Pantalla.INICIO_APLICADOR

    fun irAInicio() {
        historial.clear()
        pantallaActual = inicioSegunRol()
        if (sesionIniciada) cargarSolicitudesXano()
    }

    // La autenticación es demostrativa: no se envían contraseñas a un servidor.
    fun iniciarSesion(correo: String, contrasena: String) {
        if (correo.isBlank() || contrasena.isBlank()) return
        sesionIniciada = true
        irAInicio()
    }

    fun registrarUsuario(usuario: Usuario, rol: Rol) {
        usuarioActual = usuario
        rolActivo = rol
        sesionIniciada = true
        irAInicio()
    }

    fun cambiarRol(rol: Rol) {
        rolActivo = rol
        if (sesionIniciada) irAInicio()
    }

    fun cerrarSesion() {
        sesionIniciada = false
        historial.clear()
        pantallaActual = Pantalla.ACCESO
    }

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
                        id = 0, tipoProducto = tipo, superficieHectareas = hectareas,
                        tieneAgua = tieneAgua, horario = horario,
                        obstaculos = obstaculos, comentarios = comentarios, fotos = fotos
                    )
                )
                historial.clear()
                pantallaActual = Pantalla.INICIO_PRODUCTOR

                // No volver a publicar si falla la lectura de registros anteriores.
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

    // Una nueva oferta actualiza la solicitud y su estado en el repositorio.
    fun enviarOferta(precioPorHectarea: Long): Boolean {
        val solicitud = solicitudSeleccionada ?: return false
        if (solicitud.estado == EstadoSolicitud.FINALIZADA || precioPorHectarea <= 0L ||
            precioPorHectarea > 1000000000L) return false
        val proximoId = (solicitudes.flatMap { it.ofertas }.maxOfOrNull { it.id } ?: 0) + 1
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
        if (solicitud.estado == EstadoSolicitud.FINALIZADA ||
            solicitud.ofertas.none { it.id == idOferta }) return false
        repositorio.actualizar(solicitud.copy(estado = EstadoSolicitud.FINALIZADA))
        return true
    }
}
