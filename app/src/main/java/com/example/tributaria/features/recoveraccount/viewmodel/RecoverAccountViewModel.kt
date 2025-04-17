package com.example.tributaria.features.recoveraccount.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class RecoverAccountState(
    val email: String = "",
    val isEmailValid: Boolean = true,
    val errorMessage: String? = null,
    val showSuccessDialog: Boolean = false
)

class RecoverAccountViewModel : ViewModel() {
    private val _state = MutableStateFlow(RecoverAccountState())
    val state = _state.asStateFlow()

    private val auth = FirebaseAuth.getInstance()

    fun onEmailChange(newEmail: String) {
        val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(newEmail).matches()
        _state.update {
            it.copy(email = newEmail, isEmailValid = isValid, errorMessage = null)
        }
    }

    fun sendPasswordReset(onSuccess: () -> Unit) {
        val currentEmail = _state.value.email
        val isValid = android.util.Patterns.EMAIL_ADDRESS.matcher(currentEmail).matches()

        if (currentEmail.isBlank() || !isValid) {
            _state.update { it.copy(isEmailValid = false) }
            return
        }

        auth.sendPasswordResetEmail(currentEmail)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _state.update { it.copy(showSuccessDialog = true, errorMessage = null) }
                } else {
                    _state.update { it.copy(errorMessage = "Ocurrió un error. Intenta nuevamente.") }
                }
            }
            .addOnFailureListener { exception ->
                _state.update { it.copy(errorMessage = exception.localizedMessage ?: "Error desconocido") }
            }
    }

    fun dismissDialog() {
        _state.update { it.copy(showSuccessDialog = false) }
    }
}
