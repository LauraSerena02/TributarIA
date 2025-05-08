package com.example.tributaria.features.foro.repository

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LikesRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getLikedPosts(): Set<String> {
        val userId = "userId"
        val likesSnapshot = db.collection("likesPosts")
            .document(userId)
            .get()
            .await()

        return likesSnapshot.data?.keys?.toSet() ?: emptySet()
    }

    // Obtener los "likes" de los comentarios del usuario
    suspend fun getLikedComments(): Set<String> {
        val userId = "userId"  // Aquí debes obtener el ID del usuario actual
        val likesSnapshot = db.collection("likesComments")
            .document(userId)
            .get()
            .await()

        return likesSnapshot.data?.keys?.toSet() ?: emptySet()
    }

    // Agregar un like al post
    suspend fun addPostLike(postId: String) {
        val userId = "userId"  // Aquí debes obtener el ID del usuario actual
        db.collection("likesPosts")
            .document(userId)
            .update(postId, true)
            .await()
    }

    // Eliminar un like del post
    suspend fun removePostLike(postId: String) {
        val userId = "userId"  // Aquí debes obtener el ID del usuario actual
        db.collection("likesPosts")
            .document(userId)
            .update(postId, null)
            .await()
    }

    // Agregar un like al comentario
    suspend fun addCommentLike(commentId: String) {
        val userId = "userId"  // Aquí debes obtener el ID del usuario actual
        db.collection("likesComments")
            .document(userId)
            .update(commentId, true)
            .await()
    }

    // Eliminar un like del comentario
    suspend fun removeCommentLike(commentId: String) {
        val userId = "userId"  // Aquí debes obtener el ID del usuario actual
        db.collection("likesComments")
            .document(userId)
            .update(commentId, null)
            .await()
    }
}