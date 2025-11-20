package com.example.tributaria.features.calendar.worker

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.tributaria.features.calendar.util.NotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import android.util.Log
import com.example.tributaria.features.calendar.repository.ReminderRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class NotificationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notificationHelper: NotificationHelper,
    private val reminderRepository: ReminderRepository
) : CoroutineWorker(context, params) {

    companion object {
        private const val TAG = "NotificationWorker"
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            try {
                val message = inputData.getString("message") ?: run {
                    Log.w(TAG, "No message provided in input data")
                    return@withContext Result.failure()
                }

                val workId = inputData.getString("workId") ?: run {
                    Log.w(TAG, "No workId provided in input data")
                    return@withContext Result.failure()
                }

                Log.d(TAG, "Processing notification for workId: $workId")

                // 1. Verificar si el recordatorio existe y está activo
                val reminder = reminderRepository.getReminderByWorkId(workId)
                if (reminder == null || !reminder.isActive) {
                    Log.d(TAG, "Reminder not found or inactive, workId: $workId")
                    return@withContext Result.success()
                }

                // 2. Mostrar la notificación
                val notificationShown = notificationHelper.showNotification(
                    title = "Recordatorio Tributario",
                    message = message
                )

                if (!notificationShown) {
                    Log.w(TAG, "Failed to show notification, will retry")
                    return@withContext Result.retry()
                }

                // 3. Opcional: Marcar como completado si es un recordatorio de un solo uso
                reminderRepository.markReminderCompleted(workId)
                Log.d(TAG, "Notification shown successfully for workId: $workId")

                Result.success()
            } catch (e: Exception) {
                Log.e(TAG, "Error in NotificationWorker", e)
                Result.failure()
            }
        }
    }
}