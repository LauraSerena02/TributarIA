package com.example.tributaria.features.calendar.worker

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.tributaria.features.calendar.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import android.util.Log
import androidx.annotation.RequiresPermission
import com.example.tributaria.features.calendar.repository.ReminderRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notificationHelper: NotificationHelper,
    private val reminderRepository: ReminderRepository
) : Worker(context, params) {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override fun doWork(): Result {
        return try {
            val message = inputData.getString("message") ?: return Result.failure()
            val workId = inputData.getString("workId") ?: return Result.failure()
            val userId = inputData.getString("userId") ?: return Result.failure()

            // Mostrar notificación
            if (!notificationHelper.showNotification("Recordatorio", message)) {
                Log.w("NotificationWorker", "No se pudo mostrar notificación")
            }

            // NO eliminar el recordatorio de la base de datos aquí
            // Solo registrar que la notificación fue mostrada
            Log.d("NotificationWorker", "Notificación mostrada para workId: $workId")

            CoroutineScope(Dispatchers.IO).launch {
                reminderRepository.markReminderCompleted(workId)
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("NotificationWorker", "Error en doWork", e)
            Result.failure()
        }
    }
}