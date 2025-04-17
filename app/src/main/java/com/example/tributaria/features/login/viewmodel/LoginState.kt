package com.example.tributaria.features.login.viewmodel

// Clase sellada que representa los diferentes estados posibles de la pantalla de inicio de sesión.
sealed class LoginState {

    // Estado inicial cuando no se está realizando ninguna acción de login.
    object Idle : LoginState()

    // Estado cuando el inicio de sesión está en proceso (por ejemplo, mientras se espera la respuesta de Firebase).
    object Loading : LoginState()

    // Estado que indica que el inicio de sesión fue exitoso.
    object Success : LoginState()

    // Estado que indica que las credenciales ingresadas son incorrectas.
    data class InvalidCredentials(val errorMessage: String) : LoginState()

    // Estado que indica que hay campos vacíos en el formulario de login.
    object EmptyFields : LoginState()

    // Estado que contiene un mensaje de error genérico, utilizado cuando hay un fallo específico durante el proceso.
    data class Error(val errorMessage: String) : LoginState()

}
