package com.example.tributaria.features.calendar.util


import java.util.*

object DateUtils {
    fun calculateReminderDate(baseDate: Calendar, daysBefore: Int): Long {
        val reminderDate = baseDate.clone() as Calendar
        reminderDate.add(Calendar.DAY_OF_YEAR, -daysBefore)
        return reminderDate.timeInMillis - System.currentTimeMillis()
    }
}