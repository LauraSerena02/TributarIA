package com.example.tributaria.features.register.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Patterns

// Importación del estado de la pantalla de registro

// Importación del repositorio que maneja la lógica de registro con Firebase
import com.example.tributaria.features.register.model.UserRepository

// ViewModel responsable de manejar el estado y la lógica de la pantalla de creación de cuenta
class CreateAccountViewModel : ViewModel() {

    // Estado mutable interno que contiene todos los datos del formulario
    private val _state = MutableStateFlow(CreateAccountState())

    // Exposición pública del estado como solo lectura (para que la UI lo observe sin modificarlo)
    val state: StateFlow<CreateAccountState> = _state

    // Actualiza el campo de nombre de usuario en el estado
    fun onUsernameChange(value: String) {
        _state.value = _state.value.copy(username = value)
    }

    // Actualiza el correo electrónico y su validez
    fun onEmailChange(value: String) {
        _state.value = _state.value.copy(
            email = value,
            isEmailValid = Patterns.EMAIL_ADDRESS.matcher(value).matches() // Valida el formato del email
        )
    }

    // Actualiza la contraseña ingresada
    fun onPasswordChange(value: String) {
        _state.value = _state.value.copy(password = value)
    }

    // Actualiza la confirmación de la contraseña y verifica si coincide con la anterior
    fun onConfirmPasswordChange(value: String) {
        _state.value = _state.value.copy(
            confirmPassword = value,
            passwordMismatch = value != _state.value.password
        )
    }

    // Actualiza si el usuario ha aceptado los términos y condiciones
    fun onTermsAcceptedChange(value: Boolean) {
        _state.value = _state.value.copy(termsAccepted = value)
    }

    // Lógica para registrar al usuario
    fun register() {
        val current = _state.value

        // Validaciones básicas antes de intentar registrar
        if (
            !current.termsAccepted || // Si no aceptó los términos
            current.username.isBlank() || // Si el nombre de usuario está vacío
            !current.isEmailValid || // Si el email no es válido
            current.passwordMismatch // Si las contraseñas no coinciden
        ) return

        // Indica que se está procesando el registro y limpia errores anteriores
        _state.value = current.copy(isLoading = true, error = null)

        // Inicia una corrutina para manejar la operación asincrónica
        viewModelScope.launch {
            // Llama al repositorio para registrar al usuario en Firebase
            val result = UserRepository().registerUser(current.username, current.email, current.password)

            // Si la operación fue exitosa, actualiza el estado como exitoso
            if (result.isSuccess) {
                _state.value = _state.value.copy(success = true, isLoading = false)
            } else {
                // Si hubo un error, lo almacena en el estado
                _state.value = _state.value.copy(
                    error = result.exceptionOrNull()?.message,
                    isLoading = false
                )
            }
        }
    }

    // Restablece el indicador de éxito (por ejemplo, después de navegar a otra pantalla)
    fun resetSuccess() {
        _state.value = _state.value.copy(success = false)
    }
}
