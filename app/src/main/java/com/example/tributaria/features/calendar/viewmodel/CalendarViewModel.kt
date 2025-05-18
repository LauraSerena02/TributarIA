package com.example.tributaria.features.calendar.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.tributaria.features.calendar.domain.usecase.ScheduleRemindersUseCase
import com.example.tributaria.features.calendar.repository.ReminderRepository
import com.example.tributaria.features.calendar.util.CalendarConstants
import com.example.tributaria.features.calendar.util.await
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val scheduleReminders: ScheduleRemindersUseCase,
    private val reminderRepository: ReminderRepository,
    private val workManager: WorkManager
) : ViewModel() {

    // Estados de la UI
    private val _scheduledReminders = MutableLiveData<List<ReminderUI>>(emptyList())
    val scheduledReminders: LiveData<List<ReminderUI>> = _scheduledReminders

    private val _showConfirmation = MutableLiveData(false)
    val showConfirmation: LiveData<Boolean> = _showConfirmation

    private val _showError = MutableLiveData(false)
    val showError: LiveData<Boolean> = _showError

    private val _userConfig = MutableLiveData<Pair<String, String>?>(null)
    val userConfig: LiveData<Pair<String, String>?> = _userConfig

    var scheduledRemindersCount by mutableStateOf(0)
        private set

    init {
        loadUserConfig()
    }

    // Carga la configuración del usuario
    fun loadUserConfig() {
        viewModelScope.launch {
            try {
                _userConfig.value = reminderRepository.getReminderConfig()
                loadAllReminders() // Cambiamos a cargar todos los recordatorios
            } catch (e: Exception) {
                Log.e("CalendarViewModel", "Error loading user config", e)
                _showError.value = true
            }
        }
    }

    // Carga todos los recordatorios
    fun loadAllReminders() {
        viewModelScope.launch {
            try {
                val allReminders = reminderRepository.getAllReminders()
                    .map { reminder ->
                        ReminderUI(
                            id = reminder.id,
                            message = reminder.message,
                            date = reminder.triggerTime.toFormattedDate(),
                            workId = reminder.workId,
                            isActive = isReminderActive(reminder.workId)
                        )
                    }

                _scheduledReminders.value = allReminders
                scheduledRemindersCount = allReminders.size
            } catch (e: Exception) {
                Log.e("CalendarViewModel", "Error loading all reminders", e)
                _showError.value = true
            }
        }
    }

    private suspend fun isReminderActive(workId: String): Boolean {
        return try {
            val workInfo = workManager.getWorkInfoById(UUID.fromString(workId)).await()
            when (workInfo?.state) {
                WorkInfo.State.ENQUEUED,
                WorkInfo.State.RUNNING,
                WorkInfo.State.BLOCKED -> true
                else -> false
            }
        } catch (e: Exception) {
            Log.e("CalendarViewModel", "Error checking work status", e)
            false
        }
    }

    // Configura nuevos recordatorios
    fun setReminders(userType: String, userId: String) {
        viewModelScope.launch {
            try {
                // Guardar configuración
                reminderRepository.saveReminderConfig(userType, userId)
                _userConfig.value = Pair(userType, userId)

                // Programar nuevos recordatorios
                scheduleReminders(userType, userId)

                // Pequeña espera para asegurar que todo se ha guardado
                delay(500)

                // Recargar todos los recordatorios
                loadAllReminders()

                _showConfirmation.value = true
            } catch (e: Exception) {
                Log.e("CalendarViewModel", "Error setting reminders", e)
                _showError.value = true
            }
        }
    }

    // Cancela un recordatorio
    fun cancelReminder(reminderId: String, workId: String) {
        viewModelScope.launch {
            try {
                // Eliminar de la base de datos
                reminderRepository.deleteReminder(reminderId)

                // Cancelar el trabajo en WorkManager
                workManager.cancelWorkById(UUID.fromString(workId))

                // Recargar la lista
                loadAllReminders()
            } catch (e: Exception) {
                Log.e("CalendarViewModel", "Error canceling reminder", e)
                _showError.value = true
            }
        }
    }

    // Cierra los diálogos
    fun dismissConfirmation() {
        _showConfirmation.value = false
    }

    fun dismissError() {
        _showError.value = false
    }
}

data class ReminderUI(
    val id: String,
    val message: String,
    val date: String,
    val workId: String,
    val isActive: Boolean = true
)

private fun Long.toFormattedDate(): String {
    return try {
        val sdf = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        sdf.format(Date(this))
    } catch (e: Exception) {
        "Fecha no disponible"
    }
}