
package com.Duoc.AgroNova.Duocuc.TecnoApp.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

object NotificacionHelper {

    private const val CANAL_ID = "agronova_canal_principal"

    fun mostrarNotificacion(context: Context, titulo: String, mensaje: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(
                NotificationChannel(
                    CANAL_ID,
                    "Notificaciones AgroNova",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            )
        }

        val idIcono = context.resources.getIdentifier(
            "ic_notificacion",
            "drawable",
            context.packageName
        )

        val icono = if (idIcono != 0) {
            idIcono
        } else {
            android.R.drawable.ic_dialog_info
        }

        val abrirApp = context.packageManager
            .getLaunchIntentForPackage(context.packageName)?.let { intent ->
                PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE or
                            PendingIntent.FLAG_UPDATE_CURRENT
                )
            }

        val notificacion = NotificationCompat.Builder(context, CANAL_ID)
            .setSmallIcon(icono)
            .setContentTitle(titulo)
            .setContentText(mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(mensaje))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(abrirApp)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(
            System.currentTimeMillis().toInt(),
            notificacion
        )
    }
}
