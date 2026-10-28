package com.Duoc.TecnoApp.example.TecnoApp.model

import java.text.NumberFormat
import java.util.Locale

enum class Rol(val etiqueta: String) {
    PRODUCTOR("Productor"),
    APLICADOR("Aplicador")
}

enum class TipoProducto(val etiqueta: String) {
    GRANULADA("Granulada"),
    LIQUIDA("Líquida")
}

enum class HorarioAplicacion(val etiqueta: String) {
    DIURNA("Diurna"),
    NOCTURNA("Nocturna")
}

enum class EstadoSolicitud(val etiqueta: String) {
    PENDIENTE("Pendiente"),
    CON_OFERTAS("Con ofertas"),
    FINALIZADA("Finalizada")
}

enum class Pantalla {
    ACCESO, REGISTRO, INICIO_PRODUCTOR, NUEVA_SOLICITUD,
    DETALLE_PRODUCTOR, INICIO_APLICADOR, COTIZACION, PERFIL
}

data class Usuario(
    val nombre: String,
    val apellido: String,
    val rut: String,
    val correo: String,
    val telefono: String,
    val calificacion: Double = 4.6
)

data class Oferta(
    val id: Int,
    val nombreAplicador: String,
    val precioPorHectarea: Long,
    val calificacion: Double
)

data class Solicitud(
    val id: Int,
    val tipoProducto: TipoProducto,
    val superficieHectareas: Int,
    val tieneAgua: Boolean,
    val horario: HorarioAplicacion,
    val obstaculos: List<String> = emptyList(),
    val comentarios: String = "",
    val fotos: List<String> = emptyList(),
    val distanciaKilometros: Int = 20,
    val estado: EstadoSolicitud = EstadoSolicitud.PENDIENTE,
    val ofertas: List<Oferta> = emptyList()
)

// Presentación de precios en pesos chilenos.
fun formatearPesos(valor: Long): String =
    "$" + NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-CL")).format(valor)
