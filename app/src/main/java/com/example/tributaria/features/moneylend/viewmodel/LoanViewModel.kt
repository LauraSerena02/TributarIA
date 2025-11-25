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
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Locale

class LoanViewModel(private val repository: LoanRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(LoanUiState())
    val uiState: StateFlow<LoanUiState> = _uiState.asStateFlow()

    /** Función de formato para separadores de miles **/
    private fun formatNumber(input: String): String {
        val digits = input.filter { it.isDigit() }
        if (digits.isEmpty()) return ""

        val number = digits.toLong()
        val formatter = NumberFormat.getInstance(Locale("es", "CO")) as DecimalFormat
        formatter.applyPattern("#,###")

        return formatter.format(number)
    }

    // ------------------------------
    //  MANEJO DEL MONTO DEL PRÉSTAMO (con formato)
    // ------------------------------
    fun onLoanAmountChanged(value: String) {
        _uiState.update { it.copy(loanAmount = formatNumber(value), errorMessage = null) }
    }

    fun onInterestRateChanged(value: String) {
        _uiState.update { it.copy(interestRate = value, errorMessage = null) }
    }

    fun onMonthsChanged(value: String) {
        _uiState.update { it.copy(months = value, errorMessage = null) }
    }

    // ------------------------------
    // CÁLCULO DEL PRÉSTAMO
    // ------------------------------
    fun calculateLoan() {
        try {
            val loanAmount = _uiState.value.loanAmount.replace(".", "").toDoubleOrNull()
                ?: throw IllegalArgumentException("Ingrese un monto válido")

            val interestRate = _uiState.value.interestRate.toDoubleOrNull()
                ?: throw IllegalArgumentException("Ingrese una tasa válida")

            val months = _uiState.value.months.toIntOrNull()
                ?: throw IllegalArgumentException("Ingrese un número válido de meses")

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
