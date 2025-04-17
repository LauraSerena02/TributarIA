package com.example.tributaria.features.register.respository

import com.example.tributaria.features.register.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

// Clase encargada de manejar el registro
class UserRepository(
    // Instancia de FirebaseAuth para manejar la autenticación
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),

    // Instancia de Firestore para acceder a la base de datos en la nube
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {

    /**
     * Función suspendida que registra un usuario con correo y contraseña.
     * Además, guarda los datos adicionales del usuario (username, email y uid) en Firestore.
     *
     * @param username Nombre de usuario personalizado que el usuario proporciona.
     * @param email Correo electrónico para crear la cuenta.
     * @param password Contraseña para crear la cuenta.
     * @return Devuelve un objeto Result indicando éxito o fallo.
     */
    suspend fun registerUser(username: String, email: String, password: String): Result<Unit> {
        return try {
            // Se crea un nuevo usuario con correo y contraseña en Firebase Auth
            val result = auth.createUserWithEmailAndPassword(email, password).await()

            // Se obtiene el UID del usuario registrado; si es nulo, lanza una excepción
            val uid = result.user?.uid ?: throw Exception("UID inválido")

            // Se crea una instancia del modelo User con los datos obtenidos
            val user = User(uid = uid, username = username, email = email)

            // Se guarda el objeto user en Firestore bajo el documento con el mismo UID
            db.collection("users").document(uid).set(user).await()

            // Si todo sale bien, se devuelve un resultado exitoso
            Result.success(Unit)
        } catch (e: Exception) {
            // Si ocurre algún error, se devuelve un resultado fallido con la excepción
            Result.failure(e)
        }
    }
}
