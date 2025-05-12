package com.example.tributaria.features.calendar.worker

import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.example.tributaria.R

// features/calendar/worker/ReminderWorker.kt
class ReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    override fun doWork(): Result {
        val title = inputData.getString("title") ?: ""
        val message = inputData.getString("message") ?: ""

        showNotification(title, message)

        return Result.success()
    }

    private fun showNotification(title: String, message: String) {
        val notificationId = System.currentTimeMillis().toInt()

        val notification = NotificationCompat.Builder(applicationContext, "tax_channel")
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        val notificationManager = applicationContext
            .getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(notificationId, notification)
    }
}