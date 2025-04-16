package com.example.tributaria.features.login.model

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import kotlinx.coroutines.tasks.await

// Clase que actúa como intermediario con Firebase Authentication
class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()  // Instancia de FirebaseAuth para gestionar la autenticación
) {
    // Función suspendida (se ejecuta dentro de una corrutina). Intenta iniciar sesión con email y contraseña.
    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            // Intentamos iniciar sesión con los parámetros proporcionados
            firebaseAuth.signInWithEmailAndPassword(email, password).await()  // Esperamos la tarea asincrónica
            Result.success(Unit)  // Si el login es exitoso, devolvemos un resultado de éxito
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            // Si las credenciales son inválidas (por ejemplo, contraseña incorrecta), manejamos este caso
            Result.failure(Exception("Credenciales incorrectas"))  // Devolvemos un error con un mensaje adecuado
        } catch (e: FirebaseAuthUserCollisionException) {
            // Si el correo ya está registrado y hay una colisión, manejamos este caso
            Result.failure(Exception("Este correo ya está registrado"))  // Devolvemos un error con un mensaje adecuado
        } catch (e: Exception) {
            // Cualquier otro error que pueda ocurrir durante el login
            Result.failure(e)  // Devolvemos el error genérico
        }
    }

    // Función para cerrar sesión
    fun logout() {
        firebaseAuth.signOut()  // Llama a FirebaseAuth para cerrar sesión
    }

    // Función para obtener el usuario actual que está autenticado
    fun getCurrentUser() = firebaseAuth.currentUser  // Devuelve el usuario actualmente autenticado
}
