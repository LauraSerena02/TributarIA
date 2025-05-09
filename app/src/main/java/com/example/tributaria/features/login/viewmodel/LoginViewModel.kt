package com.example.tributaria.features.login.viewmodel

import android.content.Intent
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tributaria.features.login.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


// ViewModel que gestiona la lógica de la pantalla de inicio de sesión
@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val db: FirebaseFirestore
) : ViewModel() {

    // Estado mutable para gestionar el estado de la pantalla de inicio de sesión
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    // Variables para almacenar el nombre de usuario (email) y la contraseña
    var username by mutableStateOf("")
    var password by mutableStateOf("")

    // Estado del nombre de usuario
    private val _userName = MutableStateFlow<String>("")
    val userName: StateFlow<String> = _userName

    // Función para actualizar el nombre de usuario
    fun updateUsername(value: String) {
        username = value
        if (_loginState.value is LoginState.EmptyFields || _loginState.value is LoginState.InvalidCredentials) {
            _loginState.value = LoginState.Idle
        }
    }

    fun getGoogleSignInClient(): GoogleSignInClient? {
        return authRepository.getGoogleSignInClient()
    }

    fun loginWithGoogle(resultLauncher: ActivityResultLauncher<Intent>) {
        val signInClient = getGoogleSignInClient()
        signInClient?.signInIntent?.let {
            // Asegúrate de que no hay una cuenta seleccionada previamente
            signInClient.signOut()
            resultLauncher.launch(it)
        }
    }

    fun handleGoogleSignInResult(data: Intent?) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            try {
                val user = authRepository.handleGoogleSignInResult(data)
                if (user != null) {
                    val existsInFirestore = authRepository.isUserFirestore(user.uid)
                    if (existsInFirestore) {
                        loadUsernameFromFirestore(user.uid)
                        _loginState.value = LoginState.Success
                    } else {
                        createUserInFirestore(user)
                        loadUsernameFromFirestore(user.uid)
                        _loginState.value = LoginState.Success
                    }
                } else {
                    _loginState.value = LoginState.Error("Failed to sign in with Google")
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Google sign-in failed", e)
                _loginState.value = LoginState.Error(
                    e.message ?: "An unknown error occurred during Google sign-in"
                )
            }
        }
    }

    private fun createUserInFirestore(user: FirebaseUser) {
        val userData = hashMapOf<String, Any>(
            "username" to (user.displayName ?: "Usuario Google"),
            "email" to (user.email ?: ""),
            "createdAt" to com.google.firebase.Timestamp.now()
        )

        db.collection("users").document(user.uid)
            .set(userData)
            .addOnSuccessListener {
                Log.d("LoginViewModel", "Usuario de Google añadido a Firestore")
            }
            .addOnFailureListener { e ->
                Log.w("LoginViewModel", "Error añadiendo usuario a Firestore", e)
            }
    }
    fun checkUserSession() {
        val currentUser = authRepository.getCurrentUser()
        if (currentUser != null) {
            _loginState.value = LoginState.Success
            loadUsernameFromFirestore(currentUser.uid)
        } else {
            _loginState.value = LoginState.Idle
            _userName.value = ""
        }
    }

    private fun loadUsernameFromFirestore(uid: String) {
        db.collection("users").document(uid)
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
                Log.w("LoginViewModel", "Error cargando nombre de usuario", exception)
            }
    }

    fun logout() {
        authRepository.logout()
        _loginState.value = LoginState.Idle
    }

    val currentUserId: String?
        get() = authRepository.getCurrentUser()?.uid

    fun updatePassword(value: String) {
        password = value
        if (_loginState.value is LoginState.EmptyFields || _loginState.value is LoginState.InvalidCredentials) {
            _loginState.value = LoginState.Idle
        }
    }

    fun login() {
        viewModelScope.launch {
            if (username.isBlank() || password.isBlank()) {
                _loginState.value = LoginState.EmptyFields
                return@launch
            }

            _loginState.value = LoginState.Loading
            val result = authRepository.login(username, password)

            if (result.isSuccess) {
                val user = authRepository.getCurrentUser()
                val uid = user?.uid

                if (uid != null) {
                    val existsInFirestore = authRepository.isUserFirestore(uid)
                    if (existsInFirestore) {
                        loadUsernameFromFirestore(uid)
                        _loginState.value = LoginState.Success
                    } else {
                        authRepository.logout()
                        _loginState.value =
                            LoginState.Error("Tu cuenta ha sido eliminada o no está registrada correctamente.")
                    }
                } else {
                    _loginState.value =
                        LoginState.Error("No se pudo obtener la información del usuario.")
                }
            } else {
                val exception = result.exceptionOrNull()
                _loginState.value = when (exception) {
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
