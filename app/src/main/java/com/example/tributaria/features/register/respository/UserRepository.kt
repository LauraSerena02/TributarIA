package com.example.tributaria.features.register.respository

import android.content.Context
import android.content.Intent
import com.example.tributaria.R
import com.example.tributaria.features.register.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance(),
    @ApplicationContext private val context: Context
) {

    suspend fun registerUser(username: String, email: String, password: String): Result<Unit> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = result.user?.uid ?: throw Exception("UID inválido")
            val user = User(uid = uid, username = username, email = email)
            db.collection("users").document(uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getGoogleSignInClient(): GoogleSignInClient {
        val clientId = context.getString(R.string.default_web_client_id).takeIf { it.isNotEmpty() }
            ?: throw IllegalStateException("default_web_client_id no está definido en strings.xml")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    suspend fun handleGoogleSignInResult(data: Intent?): Result<User> {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val authResult = auth.signInWithCredential(credential).await()

            val user = authResult.user ?: throw Exception("Usuario de Google no disponible")
            val googleUser = User(
                uid = user.uid,
                username = user.displayName ?: "Usuario Google",
                email = user.email ?: ""
            )

            // Guardar el usuario en Firestore
            db.collection("users").document(user.uid).set(googleUser).await()

            Result.success(googleUser)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}