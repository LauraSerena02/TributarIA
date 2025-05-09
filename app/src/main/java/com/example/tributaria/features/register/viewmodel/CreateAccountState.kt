package com.example.tributaria.features.register.viewmodel

// Clase de datos que representa el estado de la pantalla de creación de cuenta
data class CreateAccountState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isEmailValid: Boolean = true,
    val passwordMismatch: Boolean = false,
    val termsAccepted: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val success: Boolean = false
)