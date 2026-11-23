
package com.Duoc.AgroNova.Duocuc.TecnoApp.ui

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.Duoc.AgroNova.Duocuc.TecnoApp.model.AvisoNotificacion
import com.Duoc.AgroNova.Duocuc.TecnoApp.utils.NotificacionHelper

@Composable
fun GestorNotificaciones(
    aviso: AvisoNotificacion?,
    alConsumirAviso: () -> Unit
) {
    val contexto = LocalContext.current

    val pedirPermiso = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pedirPermiso.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    LaunchedEffect(aviso) {
        if (aviso != null) {
            NotificacionHelper.mostrarNotificacion(
                contexto,
                aviso.titulo,
                aviso.mensaje
            )

            alConsumirAviso()
        }
    }
}
