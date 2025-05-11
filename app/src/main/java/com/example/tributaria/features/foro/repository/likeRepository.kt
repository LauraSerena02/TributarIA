package com.example.tributaria.features.foro.repository

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class LikesRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun hasUserLikedPost(postId: String, userId: String): Boolean {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("likesPosts")
            .whereEqualTo("postId", postId)
            .whereEqualTo("authorId", userId)
            .get()
            .await()

        return !snapshot.isEmpty
    }

    suspend fun hasUserLikedComment(commentId: String, userId: String): Boolean {
        val snapshot = FirebaseFirestore.getInstance()
            .collection("likesComments")
            .whereEqualTo("idComment", commentId)
            .whereEqualTo("authorId", userId)
            .get()
            .await()

        return !snapshot.isEmpty
    }

    suspend fun toggleLikeOnPost(postId: String, currentUserId: String): Result<Unit> {
        return try {
            val likeSnapshot = db.collection("likesPosts")
                .whereEqualTo("authorId", currentUserId)
                .whereEqualTo("postId", postId)
                .get()
                .await()

            val isLiked = !likeSnapshot.isEmpty

            if (isLiked) {
                likeSnapshot.documents.forEach { it.reference.delete().await() }

                db.collection("post")
                    .document(postId)
                    .update("totalLikes", FieldValue.increment(-1))
                    .await()
            } else {
                val likeData = hashMapOf(
                    "authorId" to currentUserId,
                    "postId" to postId,
                )

                db.collection("likesPosts")
                    .add(likeData)
                    .await()

                db.collection("post")
                    .document(postId)
                    .update("totalLikes", FieldValue.increment(1))
                    .await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun toggleLikeOnComment(idComment: String, currentUserId: String): Result<Unit> {
        return try {
            val likeSnapshot = db.collection("likesComments")
                .whereEqualTo("authorId", currentUserId)
                .whereEqualTo("idComment", idComment)
                .get()
                .await()

            val isLiked = !likeSnapshot.isEmpty

            if (isLiked) {
                likeSnapshot.documents.forEach { it.reference.delete().await() }

                db.collection("comments")
                    .document(idComment)
                    .update("totalLikes", FieldValue.increment(-1))
                    .await()
            } else {
                val likeData = hashMapOf(
                    "authorId" to currentUserId,
                    "idComment" to idComment
                )

                db.collection("likesComments")
                    .add(likeData)
                    .await()

                db.collection("comments")
                    .document(idComment)
                    .update("totalLikes", FieldValue.increment(1))
                    .await()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}