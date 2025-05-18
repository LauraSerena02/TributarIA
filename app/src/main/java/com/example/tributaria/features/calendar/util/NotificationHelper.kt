package com.example.tributaria.features.calendar.util

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.tributaria.R

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "renta_channel"
        const val CHANNEL_NAME = "Recordatorios de Renta"
    }

    init {
        createNotificationChannel()
    }

    fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH // Cambiado a HIGH para mayor prioridad
            ).apply {
                description = "Notificaciones para recordatorios de declaración de renta"
                enableLights(true)
                enableVibration(true)
            }

            val notificationManager = context.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNotification(title: String, message: String): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (NotificationManagerCompat.from(context).areNotificationsEnabled()) {
                // Mostrar notificación normalmente
                val notification = buildNotification(title, message)
                NotificationManagerCompat.from(context)
                    .notify(System.currentTimeMillis().toInt(), notification)
                true
            } else {
                false
            }
        } else {
            // Para versiones anteriores a Android 13
            val notification = buildNotification(title, message)
            NotificationManagerCompat.from(context)
                .notify(System.currentTimeMillis().toInt(), notification)
            true
        }
    }

    private fun buildNotification(title: String, message: String): Notification {
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_calendar)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()
    }
}