package com.example.tributaria.features.foro.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tributaria.features.foro.repository.Comment
import com.example.tributaria.features.foro.repository.commentsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class commentViewModel : ViewModel() {

    private val repository = commentsRepository()

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>> = _comments


    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error


    fun observeComments(postId: String) {
        repository.observeComments(postId) { updatedComments ->
            _comments.value = updatedComments
        }
    }

    fun loadComments(postId: String) {
        viewModelScope.launch {
            val result = repository.getCommentsForPost(postId)
            result.onSuccess { _comments.value = it }
                .onFailure { _error.value = it.localizedMessage }
        }
    }

    fun createComment(postId: String, body: String, authorId: String, userName: String) {
        viewModelScope.launch {
            val result = repository.addCommentToPost(postId, body, authorId, userName)
            result.onSuccess {
                loadComments(postId)
            }.onFailure {
                _error.value = it.localizedMessage
            }
        }
    }

    fun updateComment(commentId: String, newBody: String, postId: String) {
        viewModelScope.launch {
            val result = repository.updateComment(commentId, newBody)
            result.onSuccess {
                loadComments(postId)
            }.onFailure {
                _error.value = it.localizedMessage
            }
        }
    }


    fun deleteComment(commentId: String, postId: String) {
        viewModelScope.launch {
            val result = repository.deleteComment(commentId, postId)
            result.onSuccess {
                loadComments(postId)
            }.onFailure {
                _error.value = it.localizedMessage
            }
        }
    }

}

