package com.Duoc.TecnoApp.example.TecnoApp.repository

import androidx.compose.runtime.mutableStateListOf
import com.Duoc.TecnoApp.example.TecnoApp.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.net.HttpURLConnection
import java.net.URL

// Repositorio de solicitudes conectado a los endpoints existentes de Xano.
class RepositorioSolicitudes {

    val solicitudes = mutableStateListOf<Solicitud>()

    private suspend fun solicitarXano(
        metodo: String,
        ruta: String,
        datos: JSONObject? = null
    ): String = withContext(Dispatchers.IO) {
        val direccion = ConfiguracionXano.URL_BASE.trimEnd('/') + "/" + ruta.trimStart('/')
        val conexion = URL(direccion).openConnection() as HttpURLConnection
        try {
            conexion.requestMethod = metodo
            conexion.connectTimeout = 15000
            conexion.readTimeout = 20000
            conexion.setRequestProperty("Accept", "application/json")
            if (datos != null) {
                conexion.doOutput = true
                conexion.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                conexion.outputStream.use { salida ->
                    salida.write(datos.toString().toByteArray(Charsets.UTF_8))
                }
            }
            val estado = conexion.responseCode
            val flujo = if (estado in 200..299) conexion.inputStream else conexion.errorStream
            val respuesta = flujo?.bufferedReader(Charsets.UTF_8)?.use { it.readText() }.orEmpty()
            if (estado !in 200..299) {
                throw IllegalStateException("Xano respondió HTTP $estado: ${respuesta.take(300)}")
            }
            respuesta
        } finally {
            conexion.disconnect()
        }
    }

    // GET /solicitud. Xano puede responder una lista directa o un objeto con items.
    suspend fun cargarDesdeXano() {
        val nuevas = withContext(Dispatchers.IO) {
            val respuesta = solicitarXano("GET", "solicitud")
            val contenido = JSONTokener(respuesta).nextValue()
            val arreglo = when (contenido) {
                is JSONArray -> contenido
                is JSONObject -> contenido.optJSONArray("items")
                    ?: contenido.optJSONArray("data")
                    ?: throw IllegalStateException("La API no devolvió una lista de solicitudes")
                else -> throw IllegalStateException("Xano devolvió una respuesta inesperada")
            }
            (0 until arreglo.length()).map { posicion ->
                convertirSolicitud(arreglo.getJSONObject(posicion))
            }
        }
        solicitudes.clear()
        solicitudes.addAll(nuevas.sortedByDescending { it.id })
    }

    // POST /solicitud. Los nombres deben coincidir con los campos de la tabla Xano.
    suspend fun publicarEnXano(solicitud: Solicitud) {
        val datos = JSONObject()
            .put("tipo_producto", solicitud.tipoProducto.name)
            .put("superficie_hectareas", solicitud.superficieHectareas)
            .put("tiene_agua", solicitud.tieneAgua)
            .put("horario", solicitud.horario.name)
            .put("obstaculos", JSONArray(solicitud.obstaculos))
            .put("comentarios", solicitud.comentarios)
            .put("estado", "PENDIENTE")
        solicitarXano("POST", "solicitud", datos)
        cargarDesdeXano()
    }

    fun buscar(id: Int): Solicitud? = solicitudes.firstOrNull { it.id == id }

    // Se conserva solamente para las ofertas demostrativas locales.
    // Cuando exista el endpoint /oferta habrá que sustituir esta actualización.
    fun actualizar(solicitud: Solicitud) {
        val posicion = solicitudes.indexOfFirst { it.id == solicitud.id }
        if (posicion >= 0) solicitudes[posicion] = solicitud
    }

    private fun convertirSolicitud(registro: JSONObject): Solicitud {
        val producto = registro.optString("tipo_producto")
            .ifBlank { registro.optString("producto") }
            .ifBlank { registro.optString("tipo") }
        val tipo = when {
            producto.contains("GRAN", ignoreCase = true) -> TipoProducto.GRANULADA
            producto.contains("LIQ", ignoreCase = true) -> TipoProducto.LIQUIDA
            else -> throw IllegalStateException(
                "La solicitud ${registro.optInt("id")} no contiene un tipo de producto reconocido. " +
                        "Revisa los campos que devuelve GET /solicitud."
            )
        }
        val superficie = when {
            registro.has("superficie_hectareas") -> registro.optInt("superficie_hectareas")
            registro.has("superficie") -> registro.optInt("superficie")
            registro.has("hectareas") -> registro.optInt("hectareas")
            else -> throw IllegalStateException(
                "Falta superficie_hectareas en la solicitud ${registro.optInt("id")}."
            )
        }
        if (superficie <= 0) throw IllegalStateException("La superficie de Xano debe ser mayor que cero")
        val horarioTexto = registro.optString("horario")
            .ifBlank { registro.optString("aplicacion") }
        val horario = if (horarioTexto.contains("NOCT", ignoreCase = true)) {
            HorarioAplicacion.NOCTURNA
        } else HorarioAplicacion.DIURNA
        val estadoTexto = registro.optString("estado")
            .uppercase().replace(" ", "_")
        val estado = when (estadoTexto) {
            "CON_OFERTAS" -> EstadoSolicitud.CON_OFERTAS
            "FINALIZADA" -> EstadoSolicitud.FINALIZADA
            else -> EstadoSolicitud.PENDIENTE
        }
        val lista = registro.optJSONArray("obstaculos") ?: JSONArray()
        val obstaculos = (0 until lista.length()).map { lista.optString(it) }
        val agua = if (registro.has("tiene_agua")) registro.optBoolean("tiene_agua")
        else registro.optBoolean("tieneAgua")

        return Solicitud(
            id = registro.getInt("id"),
            tipoProducto = tipo,
            superficieHectareas = superficie,
            tieneAgua = agua,
            horario = horario,
            obstaculos = obstaculos,
            comentarios = registro.optString("comentarios"),
            distanciaKilometros = registro.optInt("distancia_km", 20),
            estado = estado
        )
    }
}
