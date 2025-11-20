package com.example.tributaria.features.calendar.repository

import android.content.Context
import androidx.room.*
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

// Definición de la entidad
@Entity(tableName = "reminders")
data class ReminderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val message: String,
    val triggerTime: Long,
    val workId: String,
    val isActive: Boolean = true
)

// Definición del DAO
@Dao
interface ReminderDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveReminder(reminder: ReminderEntity)

    @Query("SELECT * FROM reminders WHERE userId = :userId")
    suspend fun getReminders(userId: String): List<ReminderEntity>

    @Query("SELECT * FROM reminders")
    suspend fun getAllReminders(): List<ReminderEntity>

    @Query("SELECT * FROM reminders WHERE id = :id")
    suspend fun getReminderById(id: String): ReminderEntity?

    @Query("SELECT * FROM reminders WHERE workId = :workId")
    suspend fun getReminderByWorkId(workId: String): ReminderEntity?

    @Delete
    suspend fun deleteReminder(reminder: ReminderEntity)

    @Query("DELETE FROM reminders WHERE id = :reminderId")
    suspend fun deleteReminderById(reminderId: String)

    @Query("DELETE FROM reminders WHERE workId = :workId")
    suspend fun deleteReminderByWorkId(workId: String)

    @Query("UPDATE reminders SET isActive = :isActive WHERE workId = :workId")
    suspend fun updateReminderStatus(workId: String, isActive: Boolean)

    @Query("UPDATE reminders SET isActive = 0 WHERE workId = :workId")
    suspend fun markReminderCompleted(workId: String)
}

// Interfaz del repositorio
interface ReminderRepository {
    // Configuración (SharedPreferences)
    fun saveReminderConfig(userType: String, userId: String)
    fun getReminderConfig(): Pair<String, String>?

    // Operaciones con recordatorios
    suspend fun saveReminder(reminder: ReminderEntity)
    suspend fun getReminders(userId: String): List<ReminderEntity>
    suspend fun getAllReminders(): List<ReminderEntity>
    suspend fun getReminderById(id: String): ReminderEntity?
    suspend fun getReminderByWorkId(workId: String): ReminderEntity?
    suspend fun deleteReminder(reminderId: String)
    suspend fun deleteReminderByWorkId(workId: String)
    suspend fun updateReminderStatus(workId: String, isActive: Boolean)
    suspend fun markReminderCompleted(workId: String)
}

// Implementación del repositorio
@Singleton
class ReminderRepositoryImpl @Inject constructor(
    private val dao: ReminderDao,
    @ApplicationContext private val context: Context
) : ReminderRepository {

    private val prefs by lazy {
        context.getSharedPreferences("reminder_prefs", Context.MODE_PRIVATE)
    }

    // Configuración del usuario
    override fun saveReminderConfig(userType: String, userId: String) {
        prefs.edit().apply {
            putString("user_type", userType)
            putString("user_id", userId)
            apply()
        }
    }

    override fun getReminderConfig(): Pair<String, String>? {
        val type = prefs.getString("user_type", null)
        val id = prefs.getString("user_id", null)
        return if (type != null && id != null) Pair(type, id) else null
    }

    // Operaciones con recordatorios
    override suspend fun saveReminder(reminder: ReminderEntity) {
        dao.saveReminder(reminder)
    }

    override suspend fun getReminders(userId: String): List<ReminderEntity> {
        return dao.getReminders(userId)
    }

    override suspend fun getAllReminders(): List<ReminderEntity> {
        return dao.getAllReminders()
    }

    override suspend fun getReminderById(id: String): ReminderEntity? {
        return dao.getReminderById(id)
    }

    override suspend fun getReminderByWorkId(workId: String): ReminderEntity? {
        return dao.getReminderByWorkId(workId)
    }

    override suspend fun deleteReminder(reminderId: String) {
        dao.deleteReminderById(reminderId)
    }

    override suspend fun deleteReminderByWorkId(workId: String) {
        dao.deleteReminderByWorkId(workId)
    }

    override suspend fun updateReminderStatus(workId: String, isActive: Boolean) {
        dao.updateReminderStatus(workId, isActive)
    }

    override suspend fun markReminderCompleted(workId: String) {
        dao.markReminderCompleted(workId)
    }
}

// Definición de la base de datos
@Database(
    entities = [ReminderEntity::class],
    version = 1,
    exportSchema = false
)
abstract class ReminderDatabase : RoomDatabase() {
    abstract fun reminderDao(): ReminderDao
}