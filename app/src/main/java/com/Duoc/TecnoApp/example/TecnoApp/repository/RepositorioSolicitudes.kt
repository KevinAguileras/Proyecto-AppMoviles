package com.Duoc.TecnoApp.example.TecnoApp.repository

import androidx.compose.runtime.mutableStateListOf
import android.util.Log
import kotlin.math.roundToInt
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
            // Un registro de prueba inválido no debe bloquear las demás solicitudes.
            val validas = (0 until arreglo.length()).mapNotNull { posicion ->
                val registro = arreglo.getJSONObject(posicion)
                try {
                    convertirSolicitud(registro)
                } catch (error: Exception) {
                    Log.w(
                        "TecMundoXano",
                        "Se omitió la solicitud ${registro.optInt("id")}: ${error.message}"
                    )
                    null
                }
            }
            if (validas.isEmpty() && arreglo.length() > 0) {
                throw IllegalStateException(
                    "Xano devolvió ${arreglo.length()} registros, pero ninguno tiene " +
                            "producto, hectáreas y horario válidos. Corrige los datos de prueba " +
                            "en Xano y vuelve a cargar."
                )
            }
            validas
        }
        solicitudes.clear()
        solicitudes.addAll(nuevas.sortedByDescending { it.id })
    }

    // POST /solicitud. Los nombres deben coincidir con los campos de la tabla Xano.
    suspend fun publicarEnXano(solicitud: Solicitud) {
        // Contrato verificado en la tabla real de Xano (nombres en singular).
        // El propietario debe asignarse en Xano desde el usuario autenticado
        // cuando se integre el login real; no inventar un ID en Android.
        val datos = JSONObject()
            .put("producto", solicitud.tipoProducto.name)
            .put("hectareas", solicitud.superficieHectareas)
            .put("agua", solicitud.tieneAgua)
            .put("horario", solicitud.horario.name)
            .put("obstaculos", solicitud.obstaculos.joinToString(", "))
            .put("comentarios", solicitud.comentarios)
            .put("estado", "pendiente")
        // El POST y el GET se verifican por separado para no confundir sus errores.
        solicitarXano("POST", "solicitud", datos)
    }

    fun buscar(id: Int): Solicitud? = solicitudes.firstOrNull { it.id == id }

    // Se conserva solamente para las ofertas demostrativas locales.
    // Cuando exista el endpoint /oferta habrá que sustituir esta actualización.
    fun actualizar(solicitud: Solicitud) {
        val posicion = solicitudes.indexOfFirst { it.id == solicitud.id }
        if (posicion >= 0) solicitudes[posicion] = solicitud
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

        // Xano almacena decimales, mientras que el modelo actual usa Int.
        // Se redondea solo para visualización hasta adaptar el modelo a Double.
        val hectareas = when {
            registro.has("hectareas") -> registro.optDouble("hectareas", Double.NaN)
            registro.has("superficie_hectareas") -> registro.optDouble("superficie_hectareas", Double.NaN)
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
            .trim().uppercase().replace(" ", "_")
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
            tieneAgua = if (registro.has("agua")) registro.optBoolean("agua")
            else registro.optBoolean("tiene_agua"),
            horario = horario,
            obstaculos = obstaculos,
            comentarios = registro.optString("comentarios"),
            distanciaKilometros = registro.optInt("distancia_km", 20),
            estado = estado
        )
    }
}
