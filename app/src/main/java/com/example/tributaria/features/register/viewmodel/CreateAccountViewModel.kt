package com.example.tributaria.features.register.viewmodel

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import android.util.Patterns
import com.example.tributaria.features.register.respository.UserRepository
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class CreateAccountViewModel @Inject constructor(
    private val repository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(CreateAccountState())
    val state: StateFlow<CreateAccountState> = _state

    fun onUsernameChange(value: String) {
        _state.value = _state.value.copy(username = value)
    }

    fun onEmailChange(value: String) {
        _state.value = _state.value.copy(
            email = value,
            isEmailValid = Patterns.EMAIL_ADDRESS.matcher(value).matches()
        )
    }

    fun onPasswordChange(value: String) {
        _state.value = _state.value.copy(password = value)
    }

    fun onConfirmPasswordChange(value: String) {
        _state.value = _state.value.copy(
            confirmPassword = value,
            passwordMismatch = value != _state.value.password
        )
    }

    fun onTermsAcceptedChange(value: Boolean) {
        _state.value = _state.value.copy(termsAccepted = value)
    }

    fun register() {
        val current = _state.value

        if (
            !current.termsAccepted ||
            current.username.isBlank() ||
            !current.isEmailValid ||
            current.passwordMismatch
        ) return

        _state.value = current.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result = repository.registerUser(current.username, current.email, current.password)

            if (result.isSuccess) {
                _state.value = _state.value.copy(success = true, isLoading = false)
            } else {
                _state.value = _state.value.copy(
                    error = result.exceptionOrNull()?.message,
                    isLoading = false
                )
            }
        }
    }

    fun getGoogleSignInClient(): GoogleSignInClient {
        return repository.getGoogleSignInClient()
    }

    fun registerWithGoogle(resultLauncher: ActivityResultLauncher<Intent>) {
        val signInClient = getGoogleSignInClient()
        signInClient.signOut() // Limpiar sesión previa
        resultLauncher.launch(signInClient.signInIntent)
    }

    fun handleGoogleSignInResult(data: Intent?) {
        _state.value = _state.value.copy(isLoading = true, error = null)

        viewModelScope.launch {
            val result = repository.handleGoogleSignInResult(data)

            if (result.isSuccess) {
                _state.value = _state.value.copy(
                    success = true,
                    isLoading = false,
                    username = result.getOrNull()?.username ?: ""
                )
            } else {
                _state.value = _state.value.copy(
                    error = result.exceptionOrNull()?.message ?: "Error desconocido",
                    isLoading = false
                )
            }
        }
    }

    fun resetSuccess() {
        _state.value = _state.value.copy(success = false)
    }
}