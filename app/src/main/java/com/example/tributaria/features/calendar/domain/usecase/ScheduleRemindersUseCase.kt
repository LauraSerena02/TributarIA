package com.example.tributaria.features.calendar.domain.usecase

import android.os.Build
import android.util.Log
import androidx.work.*
import com.example.tributaria.features.calendar.repository.ReminderEntity
import com.example.tributaria.features.calendar.repository.ReminderRepository
import com.example.tributaria.features.calendar.util.CalendarConstants
import com.example.tributaria.features.calendar.util.await
import com.example.tributaria.features.calendar.worker.NotificationWorker
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class ScheduleRemindersUseCase @Inject constructor(
    private val workManager: WorkManager,
    private val reminderRepository: ReminderRepository
) {
    private val workTag = CalendarConstants.WORK_TAG
    private val notificationTimeHour = 9 // Todas las notificaciones a las 9:00 AM

    suspend operator fun invoke(userType: String, userId: String) {
        try {
            val dates = getImportantDates(userType, userId)
            dates.forEach { (targetDate, message) ->
                scheduleSingleReminder(userId, targetDate, message)
            }
        } catch (e: Exception) {
            Log.e("ScheduleReminders", "Error scheduling reminders", e)
            throw e
        }
    }

    private suspend fun scheduleSingleReminder(userId: String, targetDate: Long, message: String) {
        val delay = targetDate - System.currentTimeMillis()
        if (delay > 0) {
            val workId = UUID.randomUUID().toString()
            val uniqueTag = "$workTag|$userId|$targetDate"

            val existingReminder = reminderRepository.getReminders(userId).firstOrNull {
                it.message == message && it.triggerTime == targetDate
            }

            if (existingReminder == null) {
                val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .addTag(uniqueTag)
                    .addTag("$workTag|$userId")
                    .setInputData(
                        workDataOf(
                            "message" to message,
                            "userId" to userId,
                            "workId" to workId
                        )
                    )
                    .build()

                reminderRepository.saveReminder(
                    ReminderEntity(
                        id = UUID.randomUUID().toString(),
                        userId = userId,
                        message = message,
                        triggerTime = targetDate,
                        workId = workId,
                        isActive = true
                    )
                )

                workManager.enqueue(workRequest)
                Log.d("ScheduleDebug", "Programado: $message para ${formatDate(targetDate)}")
            }
        }
    }
    suspend fun getScheduledReminders(userId: String): List<ReminderEntity> {
        return reminderRepository.getReminders(userId)
    }

    suspend fun cancelReminder(reminderId: String, workId: String) {
        try {
            reminderRepository.deleteReminder(reminderId)
            workManager.cancelWorkById(UUID.fromString(workId))
        } catch (e: Exception) {
            Log.e("ScheduleReminders", "Error canceling reminder", e)
            throw e
        }
    }

    private fun getImportantDates(userType: String, userId: String): List<Pair<Long, String>> {
        return when (userType.lowercase()) {
            "natural" -> getNaturalPersonDates(userId)
            "juridica" -> getLegalPersonDates(userId)
            else -> emptyList()
        }
    }

    private fun getNaturalPersonDates(userId: String): List<Pair<Long, String>> {
        val lastTwoDigits = userId.takeLast(2).toIntOrNull() ?: return emptyList()
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        val deadlineMap = mapOf(
            // Agosto
            1 to "12-08", 2 to "12-08", 3 to "13-08", 4 to "13-08",
            5 to "14-08", 6 to "14-08", 7 to "15-08", 8 to "15-08",
            9 to "19-08", 10 to "19-08", 11 to "20-08", 12 to "20-08",
            13 to "21-08", 14 to "21-08", 15 to "22-08", 16 to "22-08",
            17 to "25-08", 18 to "25-08", 19 to "26-08", 20 to "26-08",
            21 to "27-08", 22 to "27-08", 23 to "28-08", 24 to "28-08",
            25 to "29-08", 26 to "29-08",

            // Septiembre
            27 to "01-09", 28 to "01-09", 29 to "02-09", 30 to "02-09",
            31 to "03-09", 32 to "03-09", 33 to "04-09", 34 to "04-09",
            35 to "05-09", 36 to "05-09", 37 to "08-09", 38 to "08-09",
            39 to "09-09", 40 to "09-09", 41 to "10-09", 42 to "10-09",
            43 to "11-09", 44 to "11-09", 45 to "12-09", 46 to "12-09",
            47 to "15-09", 48 to "15-09",

            // Octubre
            49 to "16-10", 50 to "16-10", 51 to "17-10", 52 to "17-10",
            53 to "18-10", 54 to "18-10", 55 to "19-10", 56 to "19-10",
            57 to "22-10", 58 to "22-10", 59 to "23-10", 60 to "23-10",
            61 to "24-10", 62 to "24-10", 63 to "25-10", 64 to "25-10",
            65 to "26-10", 66 to "26-10", 67 to "01-10", 68 to "01-10",
            69 to "02-10", 70 to "02-10", 71 to "03-10", 72 to "03-10",
            73 to "06-10", 74 to "06-10", 75 to "07-10", 76 to "07-10",
            77 to "08-10", 78 to "08-10", 79 to "09-10", 80 to "09-10",
            81 to "10-10", 82 to "10-10", 83 to "14-10", 84 to "14-10",
            85 to "15-10", 86 to "15-10", 87 to "16-10", 88 to "16-10",
            89 to "17-10", 90 to "17-10", 91 to "20-10", 92 to "20-10",
            93 to "21-10", 94 to "21-10", 95 to "22-10", 96 to "22-10",
            97 to "23-10", 98 to "23-10", 99 to "24-10", 0 to "24-10"
        )

        val deadlineDateStr = deadlineMap[lastTwoDigits] ?: return emptyList()
        val deadlineDate = "$deadlineDateStr-$currentYear"
        val deadlineMillis = getDateMillisAt9AM(deadlineDate)

        return listOf(
            createReminderDate(deadlineMillis, 7) to "Faltan 7 días para declarar renta (Fecha límite: $deadlineDate)",
            createReminderDate(deadlineMillis, 3) to "Faltan 3 días para declarar renta (Fecha límite: $deadlineDate)",
            createReminderDate(deadlineMillis, 1) to "¡Último día! Mañana vence la declaración de renta (Fecha límite: $deadlineDate)"
        ).filter { (triggerTime, _) ->
            triggerTime > System.currentTimeMillis() // Solo futuros
        }.also { reminders ->
            reminders.forEach { (date, msg) ->
                Log.d("DateDebug", "Recordatorio: $msg - ${formatDate(date)}")
            }
        }
    }

    private fun createReminderDate(baseMillis: Long, daysBefore: Int): Long {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = baseMillis
            add(Calendar.DAY_OF_YEAR, -daysBefore)
            // Mantener la hora consistente a las 9:00 AM
            set(Calendar.HOUR_OF_DAY, notificationTimeHour)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return calendar.timeInMillis
    }

    private fun getLegalPersonDates(userId: String): List<Pair<Long, String>> {
        val lastDigit = userId.takeLast(1).toIntOrNull() ?: 0
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)

        // Mapeo de fechas según la tabla proporcionada
        val firstQuotaDates = mapOf(
            1 to "12-05", 2 to "13-05", 3 to "14-05", 4 to "15-05",
            5 to "16-05", 6 to "19-05", 7 to "20-05", 8 to "21-05",
            9 to "22-05", 0 to "23-05"
        )

        val secondQuotaDates = mapOf(
            1 to "09-07", 2 to "10-07", 3 to "11-07", 4 to "14-07",
            5 to "15-07", 6 to "16-07", 7 to "17-07", 8 to "18-07",
            9 to "21-07", 0 to "22-07"
        )

        val firstQuotaDateStr = firstQuotaDates[lastDigit] ?: "31-05"
        val secondQuotaDateStr = secondQuotaDates[lastDigit] ?: "31-07"

        val firstDeadline = getDateMillisAt9AM("$firstQuotaDateStr-$currentYear")
        val secondDeadline = getDateMillisAt9AM("$secondQuotaDateStr-$currentYear")

        val now = System.currentTimeMillis()

        return listOfNotNull(
            // Recordatorios para primera cuota
            if (firstDeadline > now) createReminderDate(firstDeadline, 7) to
                    "Faltan 7 días para declarar y pagar 1a. cuota (Fecha límite: $firstQuotaDateStr-$currentYear)" else null,
            if (firstDeadline > now) createReminderDate(firstDeadline, 3) to
                    "Faltan 3 días para declarar y pagar 1a. cuota (Fecha límite: $firstQuotaDateStr-$currentYear)" else null,
            if (firstDeadline > now) createReminderDate(firstDeadline, 1) to // CORRECCIÓN: firstDeadline -> firstDeadline
                    "¡Último día! Mañana vence declaración y pago 1a. cuota (Fecha límite: $firstQuotaDateStr-$currentYear)" else null,

            // Recordatorios para segunda cuota
            if (secondDeadline > now) createReminderDate(secondDeadline, 7) to
                    "Faltan 7 días para pagar 2a. cuota (Fecha límite: $secondQuotaDateStr-$currentYear)" else null,
            if (secondDeadline > now) createReminderDate(secondDeadline, 3) to
                    "Faltan 3 días para pagar 2a. cuota (Fecha límite: $secondQuotaDateStr-$currentYear)" else null,
            if (secondDeadline > now) createReminderDate(secondDeadline, 1) to
                    "¡Último día! Mañana vence pago 2a. cuota (Fecha límite: $secondQuotaDateStr-$currentYear)" else null
        )
    }

    private fun getDateMillisAt9AM(dateStr: String): Long {
        return try {
            val date = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).parse(dateStr)
            Calendar.getInstance().apply {
                time = date
                set(Calendar.HOUR_OF_DAY, notificationTimeHour)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        } catch (e: Exception) {
            0L
        }
    }

    private fun formatDate(timestamp: Long): String {
        return SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date(timestamp))
    }
}