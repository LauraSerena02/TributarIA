package com.example.tributaria.features.success.viewmodel

data class SuccessState(
    val isLoading: Boolean = false,
    val exchangeRate: Double? = null,
    val errorMessage: String? = null
)