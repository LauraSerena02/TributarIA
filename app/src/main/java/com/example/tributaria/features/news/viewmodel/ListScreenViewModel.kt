package com.example.tributaria.features.news.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tributaria.features.news.model.News
import com.example.tributaria.features.news.repository.NewsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@HiltViewModel
class ListScreenViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {
    // LiveData para la lista de noticias
    private val _news = MutableLiveData<List<News>>()
    val news: LiveData<List<News>> = _news

    // LiveData para el estado de carga
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData para el estado de error
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    // Función para obtener noticias según la palabra clave y el país
    fun fetchNews(keyword: String, country: String) {
        _isLoading.value = true
        _errorMessage.value = null

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = if (keyword.isEmpty()) {
                    repository.getNews(country)  // Obtiene noticias por país si no hay palabra clave
                } else {
                    repository.searchNewsByKeyword(keyword)  // Obtiene noticias por palabra clave
                }
                _news.postValue(result)
                _isLoading.postValue(false) // Fin de la carga
            } catch (e: Exception) {
                _news.postValue(emptyList()) // Lista vacía si ocurre un error
                _isLoading.postValue(false) // Fin de la carga
                _errorMessage.postValue("Error al cargar las noticias: ${e.localizedMessage}")
            }
        }
    }
}
