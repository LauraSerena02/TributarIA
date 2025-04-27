package com.example.tributaria.features.success.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tributaria.features.homepage.repository.ExchangeRateRepository
import com.example.tributaria.features.news.model.News
import com.example.tributaria.features.news.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SuccessViewModel @Inject constructor(
    private val exchangeRateRepository: ExchangeRateRepository,
    private val newsRepository: NewsRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SuccessState())
    val state: StateFlow<SuccessState> = _state

    private val _newsList = MutableStateFlow<List<News>>(emptyList())
    val newsList: StateFlow<List<News>> = _newsList

    init {
        getExchangeRate()
        getNews()
    }

    private fun getExchangeRate() {
        viewModelScope.launch {
            _state.value = SuccessState(isLoading = true)
            try {
                val rate = exchangeRateRepository.getUsdToCopRate()
                _state.value = SuccessState(exchangeRate = rate)
            } catch (e: Exception) {
                _state.value = SuccessState(errorMessage = "Error: ${e.message}")
            }
        }
    }

    fun getNews(query: String = "finanzas") {
        viewModelScope.launch {
            try {
                // Llamada al repositorio para obtener noticias por palabra clave
                val news = newsRepository.searchNewsByKeyword(query)
                Log.d("SuccessScreen", "Noticias obtenidas: $news") // Usando Log.d para depuración
                _newsList.value = news
            } catch (e: Exception) {
                _newsList.value = emptyList()
                Log.d(
                    "SuccessScreen",
                    "Error al obtener noticias: ${e.localizedMessage}"
                ) // Usando Log.d para mostrar el error
            }
        }
    }
}