package com.example.tributaria.features.news.model

data class News(
    var title: String,
    var content: String?,
    var author: String?,
    var url: String,
    val urlToImage: String?,
    var publishedAt: String? = null,
)
