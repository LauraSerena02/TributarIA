package com.example.tributaria.features.login.viewmodel

import androidx.annotation.OptIn
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.example.tributaria.features.login.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ViewModel que gestiona la lógica de la pantalla de inicio de sesión
class LoginViewModel(
    private val authRepository: AuthRepository = AuthRepository()  // Se inyecta el repositorio de autenticación
) : ViewModel() {

    // Estado mutable para gestionar el estado de la pantalla de inicio de sesión
    private val _loginState =
        MutableStateFlow<LoginState>(LoginState.Idle)  // Inicializa el estado como Idle
    val loginState: StateFlow<LoginState> =
        _loginState  // Exposición del estado para que la vista lo observe

    // Variables para almacenar el nombre de usuario (email) y la contraseña
    var username by mutableStateOf("") // El valor de 'username' se actualiza cuando cambia el campo en la UI
    var password by mutableStateOf("") // El valor de 'password' se actualiza cuando cambia el campo en la UI

    // Agregar el estado del nombre de usuario
    private val _userName = MutableStateFlow<String>("") // Variable para almacenar el nombre de usuario
    val userName: StateFlow<String> = _userName

    // Función para actualizar el nombre de usuario
    fun updateUsername(value: String) {
        username = value
        if (_loginState.value is LoginState.EmptyFields || _loginState.value is LoginState.InvalidCredentials) {
            _loginState.value = LoginState.Idle
        }
    }

    @OptIn(UnstableApi::class)
    fun checkUserSession() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            _loginState.value = LoginState.Success
            loadUsernameFromFirestore(currentUser.uid)

        } else {
            _loginState.value = LoginState.Idle
            _userName.value = ""  // Resetear el nombre si no hay usuario autenticado
        }
    }

    // Función para cargar el nombre de usuario desde Firestore
    @OptIn(UnstableApi::class)
    private fun loadUsernameFromFirestore(uid: String) {
        val firestore = FirebaseFirestore.getInstance()
        firestore.collection("users").document(uid)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val name = document.getString("username") ?: "Usuario"
                    _userName.value = name
                } else {
                    _userName.value = "Usuario desconocido"
                }
            }
            .addOnFailureListener { exception ->
                _userName.value = "Error al cargar el nombre"
            }
    }


    fun logout() {
        authRepository.logout()
        _loginState.value = LoginState.Idle
    }

    val currentUserId: String?
        get() = authRepository.getCurrentUser()?.uid

    val currentUserName: String?
        get() = authRepository.getCurrentUser()?.displayName

    // Función para actualizar la contraseña
    fun updatePassword(value: String) {
        password = value
        if (_loginState.value is LoginState.EmptyFields || _loginState.value is LoginState.InvalidCredentials) {
            _loginState.value = LoginState.Idle
        }
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
                result.isSuccess -> {
                    // Recupera el nombre de usuario desde Firestore
                    val user = authRepository.getCurrentUser()
                    user?.uid?.let { loadUsernameFromFirestore(it) }
                    LoginState.Success
                }
                else -> {
                    val exception = result.exceptionOrNull()
                    when (exception) {
                        is FirebaseAuthInvalidCredentialsException,
                        is FirebaseAuthInvalidUserException -> {
                            LoginState.InvalidCredentials("Correo o contraseña inválidos.")
                        }

                        else -> {
                            LoginState.Error("Error de red. Intenta más tarde.")
                        }
                    }
                }
            }
        }
    }
}
