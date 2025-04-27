package com.example.tributaria.features.news.model


data class News(
    val title: String,
    val content: String = "Sin contenido disponible", // Valor por defecto
    val author: String? = null, // Autor opcional
    val url: String,
    val urlToImage: String? = null,
    val publishedAt: String? = null,
    )

