package com.example.tributaria.features.foro.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tributaria.features.foro.repository.Post
import com.example.tributaria.features.foro.repository.PostRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ForoViewModel : ViewModel() {

    private val repository = PostRepository()

    private val _posts = MutableStateFlow<List<Post>>(emptyList())
    val posts: StateFlow<List<Post>> = _posts

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    init {
        loadPosts()
    }
    fun getPostById(postId: String?): Post? {
        return _posts.value.find { it.id == postId }
    }

    fun loadPosts() {
        viewModelScope.launch {
            val result = repository.getAllPosts()
            result.onSuccess { _posts.value = it }
                .onFailure { _error.value = it.localizedMessage }
        }
    }
    fun createPost(title: String, body: String, authorId: String, userName: String) {
        viewModelScope.launch {
            val result = repository.createPost(title, body, authorId, userName)
            result.onSuccess {
                loadPosts()
            }.onFailure {
                _error.value = it.localizedMessage
            }
        }
    }
    fun deletePost(postId: String) {
        viewModelScope.launch {
            repository.deletePost(postId)
            loadPosts()
        }
    }

    fun updatePost(postId: String, title: String, body: String) {
        viewModelScope.launch {
            try {
                repository.updatePost(postId, title, body)
            } catch (e: Exception) {
                // Manejar error (por ejemplo, mostrar Toast o Snackbar)
            }
        }
    }

}

