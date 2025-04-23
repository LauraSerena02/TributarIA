package com.example.tributaria.features.news.provider

import com.example.tributaria.features.news.model.NewsApiResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

private const val API_KEY = "132d3a3aede942dc89bba25b6ad1e5dc"

interface NewsProvider {
    @GET("top-headlines")
    suspend fun topHeadLines(
        @Query("country") country: String,
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<NewsApiResponse>

    @GET("everything")
    suspend fun searchEverything(
        @Query("q") keyword: String,
        @Query("sortBy") sortBy: String = "publishedAt",
        @Query("language") language: String = "es",
        @Query("apiKey") apiKey: String = API_KEY
    ): Response<NewsApiResponse>
}

