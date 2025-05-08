package com.example.tributaria.features.foro.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await
import kotlin.Int

data class Post(
    val id: String = "",
    val Title: String = "",
    val body: String = "",
    val userName: String = "",
    val authorId: String = "",
    val countComment: Int = 0,
    val totalLikes: Int = 0,
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

    fun updatePost(postId: String, title: String, body: String) {
        val postRef = firestore.collection("post").document(postId)
        postRef.update(mapOf(
            "Title" to title,
            "body" to body
        ))
    }

    fun observePosts(onPostsChanged: (List<Post>) -> Unit) {
        firestore.collection("post")
            .addSnapshotListener { snapshot, error ->
                if (error != null || snapshot == null) {
                    return@addSnapshotListener
                }

                val posts = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Post::class.java)?.copy(id = doc.id)
                }
                onPostsChanged(posts)
            }
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
                    countComment = doc.get("countComment") as Int,
                    totalLikes = doc.get("totalLikes") as Int,
                    authorId = doc.getString("authorId") ?: "",
                    timestamp = doc.getTimestamp("timestamp") ?: Timestamp.now()
                )
            }
            Result.success(posts)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
