package com.Duoc.TecnoApp.example.TecnoApp.repository

import androidx.compose.runtime.mutableStateListOf
import com.Duoc.TecnoApp.example.TecnoApp.model.*

// Repositorio demostrativo: mantiene los datos reactivos solo mientras vive la aplicación.
class RepositorioSolicitudes {
    val solicitudes = mutableStateListOf(
        Solicitud(
            id = 1,
            tipoProducto = TipoProducto.GRANULADA,
            superficieHectareas = 10,
            tieneAgua = true,
            horario = HorarioAplicacion.DIURNA,
            obstaculos = listOf("Cables", "Torres de alta tensión"),
            comentarios = "Acceso por camino norte"
        ),
        Solicitud(
            id = 2,
            tipoProducto = TipoProducto.LIQUIDA,
            superficieHectareas = 50,
            tieneAgua = true,
            horario = HorarioAplicacion.DIURNA,
            estado = EstadoSolicitud.CON_OFERTAS,
            ofertas = listOf(Oferta(1, "John Doe", 1000, 5.0))
        ),
        Solicitud(
            id = 3,
            tipoProducto = TipoProducto.LIQUIDA,
            superficieHectareas = 14,
            tieneAgua = false,
            horario = HorarioAplicacion.NOCTURNA,
            estado = EstadoSolicitud.FINALIZADA
        )
    )

    fun buscar(id: Int): Solicitud? = solicitudes.firstOrNull { it.id == id }

    fun agregar(solicitud: Solicitud) {
        solicitudes.add(0, solicitud)
    }

    fun actualizar(solicitud: Solicitud) {
        val posicion = solicitudes.indexOfFirst { it.id == solicitud.id }
        if (posicion >= 0) solicitudes[posicion] = solicitud
    }
}
