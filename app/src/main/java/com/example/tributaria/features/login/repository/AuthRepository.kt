package com.example.tributaria.features.login.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()  // Inicialización de FirebaseAuth

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: FirebaseAuthInvalidCredentialsException) {
            Result.failure(e)  // Excepción para credenciales incorrectas
        } catch (e: FirebaseAuthInvalidUserException) {
            Result.failure(e)  // Excepción para usuario no encontrado
        } catch (e: Exception) {
            Result.failure(e)  // Excepciones genéricas (red, etc.)
        }
    }

    // Métodos adicionales
    fun logout() {
        firebaseAuth.signOut()
    }

    fun getCurrentUser() = firebaseAuth.currentUser
}
