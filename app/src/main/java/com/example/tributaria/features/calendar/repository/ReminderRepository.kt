package com.example.tributaria.features.calendar.repository

import android.content.Context
import androidx.room.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

// Definiciones de Entity y DAO
@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val message: String,
    val triggerTime: Long,
    val workId: String,
    val isActive: Boolean = true // Nuevo campo
)

// ReminderDao.kt
@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReminder(reminder: ReminderEntity)

    @Query("SELECT * FROM reminders WHERE userId = :userId")
    suspend fun getReminders(userId: String): List<ReminderEntity>

    @Query("SELECT * FROM reminders")
    suspend fun getAllReminders(): List<ReminderEntity>

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE id = :reminderId")
    suspend fun deleteReminderById(reminderId: String)

    @Query("UPDATE reminders SET isActive = 0 WHERE workId = :workId")
    suspend fun markReminderCompleted(workId: String)
}

// ReminderRepository.kt
interface ReminderRepository {
    // Configuración (SharedPreferences)
    fun saveReminderConfig(userType: String, userId: String)
    fun getReminderConfig(): Pair<String, String>?

    // Recordatorios (Room)
    suspend fun saveReminder(reminder: ReminderEntity)
    suspend fun getReminders(userId: String): List<ReminderEntity>
    suspend fun getAllReminders(): List<ReminderEntity>
    suspend fun deleteReminder(reminderId: String)
    suspend fun markReminderCompleted(workId: String)
}

// ReminderRepositoryImpl.kt
class ReminderRepositoryImpl @Inject constructor(
    private val dao: ReminderDao,
    @ApplicationContext private val context: Context
) : ReminderRepository {

    private val prefs by lazy {
        context.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)
    }

    override fun saveReminderConfig(userType: String, userId: String) {
        prefs.edit().apply {
            putString("user_type", userType)
            putString("user_id", userId)
            apply()
        }
    }
    override suspend fun markReminderCompleted(workId: String) {
        dao.markReminderCompleted(workId)
    }

    override fun getReminderConfig(): Pair<String, String>? {
        val type = prefs.getString("user_type", null)
        val id = prefs.getString("user_id", null)
        return if (type != null && id != null) Pair(type, id) else null
    }

    override suspend fun saveReminder(reminder: ReminderEntity) {
        dao.saveReminder(reminder)
    }

    override suspend fun getReminders(userId: String): List<ReminderEntity> {
        return dao.getReminders(userId)
    }

    override suspend fun getAllReminders(): List<ReminderEntity> {
        return dao.getAllReminders()
    }

    override suspend fun deleteReminder(reminderId: String) {
        dao.deleteReminderById(reminderId)
    }
}