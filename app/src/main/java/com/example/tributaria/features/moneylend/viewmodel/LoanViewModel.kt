package com.example.tributaria.features.moneylend.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.update
import com.example.tributaria.features.moneylend.repository.LoanRepository
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.viewModelScope
import com.example.tributaria.features.moneylend.model.LoanCalculationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoanViewModel(private val repository: LoanRepository) : ViewModel() {
    private val _uiState = MutableStateFlow(LoanUiState())
    val uiState: StateFlow<LoanUiState> = _uiState.asStateFlow()

    fun onLoanAmountChanged(value: String) {
        _uiState.update { it.copy(loanAmount = value, errorMessage = null) }
    }

    fun onInterestRateChanged(value: String) {
        _uiState.update { it.copy(interestRate = value, errorMessage = null) }
    }

    fun onMonthsChanged(value: String) {
        _uiState.update { it.copy(months = value, errorMessage = null) }
    }

    fun calculateLoan() {
        try {
            val loanAmount = _uiState.value.loanAmount.toDoubleOrNull() ?: throw IllegalArgumentException("Ingrese un monto válido")
            val interestRate = _uiState.value.interestRate.toDoubleOrNull() ?: throw IllegalArgumentException("Ingrese una tasa de interés válida")
            val months = _uiState.value.months.toIntOrNull() ?: throw IllegalArgumentException("Ingrese un plazo válido")

            val result = repository.calculateLoan(loanAmount, interestRate, months)

            _uiState.update {
                it.copy(
                    calculationResult = result,
                    errorMessage = null
                )
            }
        } catch (e: Exception) {
            _uiState.update { it.copy(errorMessage = e.message) }
        }
    }
}

data class LoanUiState(
    val loanAmount: String = "",
    val interestRate: String = "",
    val months: String = "",
    val calculationResult: LoanCalculationResult? = null,
    val errorMessage: String? = null
)