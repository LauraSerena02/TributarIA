package com.example.tributaria.features.moneylend.model

data class LoanCalculationResult(
    val monthlyPayment: Double,
    val totalInterest: Double,
    val amortizationTable: List<AmortizationRow> // Aunque no la usemos aquí
)

data class AmortizationRow(
    val month: Int,
    val payment: Double,
    val principal: Double,
    val interest: Double,
    val remainingBalance: Double
)