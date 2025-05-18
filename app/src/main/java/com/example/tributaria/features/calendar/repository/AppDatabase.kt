package com.example.tributaria.features.calendar.repository

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [ReminderEntity::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
}