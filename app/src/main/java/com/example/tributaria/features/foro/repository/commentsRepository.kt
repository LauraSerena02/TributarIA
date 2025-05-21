package com.example.tributaria.features.foro.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

data class Comment(
    val id: String = "",
    val body: String = "",
    val authorId: String = "",
    val postId: String = "",
    val userName: String = "",
    var totalLikes: Int = 0,
    val timestamp: Timestamp = Timestamp.now()
)

class commentsRepository {

    private val firestore = FirebaseFirestore.getInstance()

    fun observeComments(postId: String, onCommentsChanged: (List<Comment>) -> Unit) {
        firestore.collection("comments")
            .whereEqualTo("postId", postId) // Filtrar por el ID del post
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }
                val comments = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Comment::class.java)?.copy(id = doc.id)
                }
                onCommentsChanged(comments)
            }
    }


    suspend fun addCommentToPost(postId: String, body: String, authorId: String, userName: String): Result<Unit> {
        return try {
            val data: Map<String, Any> = mapOf(
                "userName" to userName,
                "postId" to postId,
                "body" to body,
                "authorId" to authorId,
                "timestamp" to Timestamp.now()
            )

            val postRef = firestore.collection("post").document(postId)
            val commentRef = firestore.collection("comments").document()

            firestore.runTransaction { transaction: com.google.firebase.firestore.Transaction ->
                transaction.set(commentRef, data)
                transaction.update(postRef, "countComment", FieldValue.increment(1))
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }

    suspend fun getCommentsForPost(postId: String): Result<List<Comment>> {
        return try {
            val snapshot = firestore.collection("comments")
                .whereEqualTo("postId", postId)
                .get()
                .await()

            val comments = snapshot.documents.map { doc ->
                Comment(
                    id = doc.id,
                    body = doc.getString("body") ?: "",
                    authorId = doc.getString("authorId") ?: "",
                    postId = doc.getString("postId") ?: "",
                    totalLikes = (doc.getLong("totalLikes") ?: 0L).toInt(),
                    userName = doc.getString("userName") ?: "",
                    timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()
                )
            }
            Result.success(comments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateComment(commentId: String, newBody: String): Result<Unit> {
        return try {
            firestore.collection("comments").document(commentId)
                .update("body", newBody, "timestamp", Timestamp.now())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteComment(commentId: String, postId: String): Result<Unit> {
        return try {
            val commentRef = firestore.collection("comments").document(commentId)
            val postRef = firestore.collection("post").document(postId)

            firestore.runTransaction { transaction ->
                transaction.delete(commentRef)
                transaction.update(postRef, "countComment", FieldValue.increment(-1))
            }.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
