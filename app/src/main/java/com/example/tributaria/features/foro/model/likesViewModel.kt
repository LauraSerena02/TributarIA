package com.example.tributaria.features.foro.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tributaria.features.foro.repository.Comment
import com.example.tributaria.features.foro.repository.LikesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class likesViewModel : ViewModel() {

    private val repository = LikesRepository()
    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    private val _Likes = MutableStateFlow(false)

    suspend fun loadReactionComment(commentId: String, userId: String): Boolean {
        return try {
            repository.hasUserLikedComment(commentId, userId)
        } catch (e: Exception) {
            _error.value = e.localizedMessage ?: "Error al verificar reacción"
            false
        }
    }

    suspend fun loadReactionPost(postId: String, userId: String): Boolean {
        return try {
            repository.hasUserLikedPost(postId, userId)
        } catch (e: Exception) {
            _error.value = e.localizedMessage ?: "Error al verificar reacción"
            false
        }
    }

    fun reactionPost(postId: String, currentUserId: String) {
        viewModelScope.launch {
            val result = repository.toggleLikeOnPost(postId, currentUserId)
            result.onFailure {
                _error.value = it.localizedMessage ?: "Error al actualizar la reacción"
            }
        }
    }

    fun reactionComment(commentId: String, currentUserId: String) {
        viewModelScope.launch {
            val result = repository.toggleLikeOnComment(commentId, currentUserId)
            result.onFailure {
                _error.value = it.localizedMessage ?: "Error al actualizar la reacción"
            }
        }
    }
}

