package com.Duoc.TecnoApp.Duocuc.TecnoApp.repository

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import com.Duoc.TecnoApp.Duocuc.TecnoApp.model.EstadoSolicitud
import com.Duoc.TecnoApp.Duocuc.TecnoApp.model.HorarioAplicacion
import com.Duoc.TecnoApp.Duocuc.TecnoApp.model.Solicitud
import com.Duoc.TecnoApp.Duocuc.TecnoApp.model.TipoProducto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONTokener
import java.net.HttpURLConnection
import java.net.URL
import kotlin.math.roundToInt

class RepositorioSolicitudes {

    val solicitudes = mutableStateListOf<Solicitud>()
    private var tokenSesion: String? = null

    suspend fun iniciarSesion(correo: String, contrasena: String) {
        val datos = JSONObject()
            .put("email", correo.trim())
            .put("password", contrasena)

        val respuesta = enviarPeticion(
            metodo = "POST",
            direccion = ConfiguracionXano.URL_LOGIN,
            datos = datos
        )

        val token = JSONObject(respuesta).opt("authToken") as? String
        if (token.isNullOrBlank()) {
            throw IllegalStateException("Xano respondió al login sin entregar un token.")
        }

        tokenSesion = token
    }

    fun cerrarSesion() {
        tokenSesion = null
        solicitudes.clear()
    }

    // Las rutas de solicitudes requieren el token obtenido al iniciar sesión.
    private suspend fun solicitarXano(
        metodo: String,
        ruta: String,
        datos: JSONObject? = null
    ): String {
        val token = tokenSesion
            ?: throw IllegalStateException("Inicia sesión para acceder a las solicitudes.")

        val direccion = ConfiguracionXano.URL_BASE.trimEnd('/') + "/" + ruta.trimStart('/')

        return enviarPeticion(
            metodo = metodo,
            direccion = direccion,
            datos = datos,
            token = token
        )
    }

    private suspend fun enviarPeticion(
        metodo: String,
        direccion: String,
        datos: JSONObject? = null,
        token: String? = null
    ): String = withContext(Dispatchers.IO) {
        val conexion = URL(direccion).openConnection() as HttpURLConnection

        try {
            conexion.requestMethod = metodo
            conexion.connectTimeout = 15000
            conexion.readTimeout = 20000
            conexion.setRequestProperty("Accept", "application/json")

            if (token != null) {
                conexion.setRequestProperty("Authorization", "Bearer $token")
            }

            if (datos != null) {
                conexion.doOutput = true
                conexion.setRequestProperty("Content-Type", "application/json; charset=UTF-8")
                conexion.outputStream.use { salida ->
                    salida.write(datos.toString().toByteArray(Charsets.UTF_8))
                }
            }

            val estado = conexion.responseCode
            val flujo = if (estado in 200..299) {
                conexion.inputStream
            } else {
                conexion.errorStream
            }

            val respuesta = flujo?.bufferedReader(Charsets.UTF_8)?.use { lector ->
                lector.readText()
            }.orEmpty()

            if (estado !in 200..299) {
                throw IllegalStateException("Xano respondió HTTP $estado: ${respuesta.take(300)}")
            }

            respuesta
        } finally {
            conexion.disconnect()
        }
    }

    // Xano puede devolver la lista directamente o dentro de "items" o "data".
    suspend fun cargarDesdeXano() {
        val respuesta = solicitarXano("GET", "solicitud")

        val nuevas = withContext(Dispatchers.Default) {
            val contenido = JSONTokener(respuesta).nextValue()

            val arreglo = when (contenido) {
                is JSONArray -> contenido
                is JSONObject -> contenido.optJSONArray("items")
                    ?: contenido.optJSONArray("data")
                    ?: throw IllegalStateException("La API no devolvió una lista de solicitudes.")
                else -> throw IllegalStateException("Xano devolvió una respuesta inesperada.")
            }

            val validas = (0 until arreglo.length()).mapNotNull { posicion ->
                try {
                    convertirSolicitud(arreglo.getJSONObject(posicion))
                } catch (error: Exception) {
                    Log.w(
                        "TecMundoXano",
                        "Se omitió la solicitud en la posición $posicion: ${error.message}"
                    )
                    null
                }
            }

            if (validas.isEmpty() && arreglo.length() > 0) {
                throw IllegalStateException(
                    "Xano devolvió ${arreglo.length()} registros, pero ninguno pudo leerse. " +
                            "Revisa el producto, las hectáreas, el horario y el estado."
                )
            }

            validas.sortedByDescending { it.id }
        }

        solicitudes.clear()
        solicitudes.addAll(nuevas)
    }

    suspend fun publicarEnXano(solicitud: Solicitud) {
        val datos = JSONObject()
            .put("producto", solicitud.tipoProducto.name)
            .put("hectareas", solicitud.superficieHectareas)
            .put("agua", solicitud.tieneAgua)
            .put("horario", solicitud.horario.name)
            .put("obstaculos", solicitud.obstaculos.joinToString(", "))
            .put("comentarios", solicitud.comentarios)
            .put("estado", "pendiente")

        solicitarXano("POST", "solicitud", datos)
    }

    fun buscar(id: Int): Solicitud? = solicitudes.firstOrNull { it.id == id }

    // Las pantallas de ofertas todavía usan esta actualización local.
    fun actualizar(solicitud: Solicitud) {
        val posicion = solicitudes.indexOfFirst { it.id == solicitud.id }
        if (posicion >= 0) {
            solicitudes[posicion] = solicitud
        }
    }

    private fun convertirSolicitud(registro: JSONObject): Solicitud {
        val id = registro.getInt("id")

        val producto = registro.optString("producto")
            .ifBlank { registro.optString("tipo_producto") }

        val tipo = when {
            producto.contains("GRAN", ignoreCase = true) -> TipoProducto.GRANULADA
            producto.contains("LIQ", ignoreCase = true) -> TipoProducto.LIQUIDA
            else -> throw IllegalStateException("Producto no reconocido: '$producto'")
        }

        val hectareas = when {
            registro.has("hectareas") -> registro.optDouble("hectareas", Double.NaN)
            registro.has("superficie_hectareas") ->
                registro.optDouble("superficie_hectareas", Double.NaN)
            else -> Double.NaN
        }

        if (!hectareas.isFinite() || hectareas <= 0.0 || hectareas > 100000.0) {
            throw IllegalStateException("Hectáreas inválidas: $hectareas")
        }

        val horarioTexto = registro.optString("horario")
        val horario = when {
            horarioTexto.contains("NOCT", ignoreCase = true) -> HorarioAplicacion.NOCTURNA
            horarioTexto.contains("DIUR", ignoreCase = true) -> HorarioAplicacion.DIURNA
            else -> throw IllegalStateException("Horario no reconocido: '$horarioTexto'")
        }

        val estadoTexto = registro.optString("estado")
            .trim()
            .uppercase()
            .replace(" ", "_")

        val estado = when (estadoTexto) {
            "PENDIENTE" -> EstadoSolicitud.PENDIENTE
            "CON_OFERTAS" -> EstadoSolicitud.CON_OFERTAS
            "FINALIZADA", "COMPLETADA", "ACEPTADA" -> EstadoSolicitud.FINALIZADA
            else -> throw IllegalStateException("Estado no reconocido: '$estadoTexto'")
        }

        val listaObstaculos = registro.optJSONArray("obstaculos")
        val obstaculos = if (listaObstaculos != null) {
            (0 until listaObstaculos.length())
                .map { listaObstaculos.optString(it) }
                .filter { it.isNotBlank() }
        } else {
            registro.optString("obstaculos")
                .split(',', ';', '\n')
                .map { it.trim() }
                .filter { it.isNotBlank() }
        }

        return Solicitud(
            id = id,
            tipoProducto = tipo,
            superficieHectareas = hectareas.roundToInt(),
            tieneAgua = if (registro.has("agua")) {
                registro.optBoolean("agua")
            } else {
                registro.optBoolean("tiene_agua")
            },
            horario = horario,
            obstaculos = obstaculos,
            comentarios = registro.optString("comentarios"),
            distanciaKilometros = registro.optInt("distancia_km", 20),
            estado = estado
        )
    }
}