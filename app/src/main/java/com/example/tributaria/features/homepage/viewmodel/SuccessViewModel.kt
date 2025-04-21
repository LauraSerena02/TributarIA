package com.example.tributaria.features.success.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tributaria.features.homepage.repository.ExchangeRateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SuccessViewModel(
    private val repository: ExchangeRateRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SuccessState())
    val state: StateFlow<SuccessState> = _state

    init {
        getExchangeRate()
    }

    fun getExchangeRate() {
        viewModelScope.launch {
            _state.value = SuccessState(isLoading = true)
            try {
                val rate = repository.getUsdToCopRate()
                _state.value = SuccessState(exchangeRate = rate)
            } catch (e: Exception) {
                _state.value = SuccessState(errorMessage = "Error: ${e.message}")
            }
        }
    }
}
