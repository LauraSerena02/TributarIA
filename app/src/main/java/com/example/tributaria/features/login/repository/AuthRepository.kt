package com.example.tributaria.features.login.repository

import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.tributaria.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) {


    fun getGoogleSignInClient(): GoogleSignInClient {
        val clientId = context.getString(R.string.default_web_client_id).takeIf { it.isNotEmpty() }
            ?: throw IllegalStateException("default_web_client_id no está definido en strings.xml")

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            // Removed setHostedDomain and setAccountName as they're optional and require non-null strings
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    suspend fun handleGoogleSignInResult(data: Intent?): FirebaseUser? {
        return try {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            val account = task.getResult(ApiException::class.java)
            account?.idToken?.let { signInWithGoogle(it) }
        } catch (e: ApiException) {
            Log.e("AuthRepository", "Google sign in failed", e)
            null
        }
    }

    private suspend fun signInWithGoogle(idToken: String): FirebaseUser? {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            firebaseAuth.signInWithCredential(credential).await().user
        } catch (e: Exception) {
            Log.e("AuthRepository", "Firebase auth with Google failed", e)
            null
        }
    }

    suspend fun login(email: String, password: String): Result<Unit> {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            Result.success(Unit)
        } catch (e: FirebaseAuthException) {
            Log.e("AuthRepository", "Login failed", e)
            Result.failure(e)
        } catch (e: Exception) {
            Log.e("AuthRepository", "Unexpected login error", e)
            Result.failure(e)
        }
    }

    fun logout() {
        try {
            firebaseAuth.signOut()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Logout failed", e)
        }
    }

    fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser?.also {
            Log.d("AuthRepository", "Usuario actual: ${it.displayName ?: it.email}")
        }
    }

    suspend fun isUserFirestore(uid: String): Boolean {
        return try {
            firestore.collection("users").document(uid).get().await().exists()
        } catch (e: Exception) {
            Log.e("AuthRepository", "Firestore user check failed", e)
            false
        }
    }
}