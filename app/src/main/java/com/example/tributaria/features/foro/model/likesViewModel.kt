package com.example.tributaria.features.foro.model

import androidx.lifecycle.ViewModel
import com.example.tributaria.features.foro.repository.LikesRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class LikesViewModel(private val likesRepository: LikesRepository) : ViewModel() {
    private val _likedPosts = MutableStateFlow<Set<String>>(emptySet())
    val likedPosts: StateFlow<Set<String>> = _likedPosts

    private val _likedComments = MutableStateFlow<Set<String>>(emptySet())
    val likedComments: StateFlow<Set<String>> = _likedComments

    suspend fun addLikeToPost(postId: String) {
        _likedPosts.value = _likedPosts.value + postId
        likesRepository.addPostLike(postId)
    }

    suspend fun removeLikeFromPost(postId: String) {
        _likedPosts.value = _likedPosts.value - postId
        likesRepository.removePostLike(postId)
    }

    suspend fun addLikeToComment(commentId: String) {
        _likedComments.value = _likedComments.value + commentId
        likesRepository.addCommentLike(commentId)
    }

    suspend fun removeLikeFromComment(commentId: String) {
        _likedComments.value = _likedComments.value - commentId
        likesRepository.removeCommentLike(commentId)
    }
}