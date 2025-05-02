package com.example.tributaria.features.foro.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

data class Post(
    val id: String = "",
    val Title: String = "",
    val body: String = "",
    val userName: String = "",
    val authorId: String = "",
    val countComment: Int = 0,
    val totalLikes: Int = 0,
    val timestamp: Timestamp = Timestamp.now()
);

data class Comment(
    val id: String = "",
    val body: String = "",
    val authorId: String = "",
    val postId: String = "",
    val timestamp: Timestamp = Timestamp.now()
)

class PostRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun createPost(title: String, body: String, authorId: String, userName: String): Result<Unit> {
        return try {
            val data = hashMapOf(
                "Title" to title,
                "body" to body,
                "authorId" to authorId,
                "userName" to userName,
                "timestamp" to Timestamp.now()
            )
            firestore.collection("post").add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePost(postId: String) {
        try {
            firestore.collection("post")
                .document(postId)
                .delete()
                .await()
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun updatePost(postId: String, title: String, body: String) {
        val postRef = firestore.collection("post").document(postId)
        postRef.update(mapOf(
            "Title" to title,
            "body" to body
        ))
    }


    suspend fun getAllPosts(): Result<List<Post>> {
        return try {
            val snapshot = firestore.collection("post")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val posts = snapshot.documents.map { doc ->
                Post(
                    id = doc.id,
                    Title = doc.getString("Title") ?: "",
                    body = doc.getString("body") ?: "",
                    userName = doc.getString("userName") ?: "",
                    authorId = doc.getString("authorId") ?: "",
                    timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()
                )
            }
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Crear un comentario en un post
    suspend fun addCommentToPost(postId: String, body: String, authorId: String): Result<Unit> {
        return try {
            val data = hashMapOf(
                "postId" to postId,
                "body" to body,
                "authorId" to authorId,
                "timestamp" to Timestamp.now()
            )
            firestore.collection("comments").add(data).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtener los comentarios de un post
    suspend fun getCommentsForPost(postId: String): Result<List<Comment>> {
        return try {
            val snapshot = firestore.collection("comments")
                .whereEqualTo("postId", postId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .await()

            val comments = snapshot.documents.map { doc ->
                Comment(
                    id = doc.id,
                    body = doc.getString("body") ?: "",
                    authorId = doc.getString("authorId") ?: "",
                    postId = doc.getString("postId") ?: "",
                    timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()
                )
            }
            Result.success(comments)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
