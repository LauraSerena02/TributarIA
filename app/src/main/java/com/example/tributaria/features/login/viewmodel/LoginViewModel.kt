package com.example.tributaria.features.login.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tributaria.features.login.model.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel que gestiona la lógica de la pantalla de inicio de sesión
class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()  // Se inyecta el repositorio de autenticación
) : ViewModel() {

    // Estado mutable para gestionar el estado de la pantalla de inicio de sesión
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)  // Inicializa el estado como Idle
    val loginState: StateFlow<LoginState> = _loginState  // Exposición del estado para que la vista lo observe

    // Variables para almacenar el nombre de usuario (email) y la contraseña
    var username by mutableStateOf("") // El valor de 'username' se actualiza cuando cambia el campo en la UI
    var password by mutableStateOf("") // El valor de 'password' se actualiza cuando cambia el campo en la UI

    // Función para actualizar el nombre de usuario
    fun updateUsername(value: String) {
        username = value  // Asigna el nuevo valor al 'username'
    }

    // Función para actualizar la contraseña
    fun updatePassword(value: String) {
        password = value  // Asigna el nuevo valor a 'password'
    }

    // Función para manejar el inicio de sesión
    fun login() {
        // Usamos 'viewModelScope' para ejecutar la lógica de login en una corrutina, asegurándonos de que se ejecute en un hilo adecuado
        viewModelScope.launch {
            // Si los campos están vacíos, actualizamos el estado y salimos de la función
            if (username.isBlank() || password.isBlank()) {
                _loginState.value =
                    LoginState.EmptyFields  // Se actualiza el estado a EmptyFields si hay campos vacíos
                return@launch
            }

            // Cambiamos el estado a Loading para mostrar una indicación de que se está procesando el login
            _loginState.value = LoginState.Loading
            // Intentamos realizar el login utilizando el repositorio de autenticación
            val result = authRepository.login(username, password)

            // Actualizamos el estado según el resultado del login
            _loginState.value = when {
                result.isSuccess -> LoginState.Success  // Si el login es exitoso, cambiamos el estado a Success
                else -> LoginState.InvalidCredentials  // Si falla, actualizamos el estado a InvalidCredentials
            }
        }
    }
}
