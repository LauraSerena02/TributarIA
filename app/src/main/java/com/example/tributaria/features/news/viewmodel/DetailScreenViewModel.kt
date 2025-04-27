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
class DetailsScreenViewModel @Inject constructor(
    private val repository: NewsRepository
) : ViewModel() {

    private val _news = MutableLiveData<News>()

    fun getNewsByTitle(title:String): LiveData<News> {
        viewModelScope.launch(Dispatchers.IO) {
            val news = repository.getNew(title)
            _news.postValue(news)
        }
        return _news
    }
}