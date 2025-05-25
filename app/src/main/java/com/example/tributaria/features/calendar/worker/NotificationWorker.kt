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

            if (notificationHelper.showNotification("Recordatorio", message)) {
                Result.success()
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            Result.failure()
        }
    }
}