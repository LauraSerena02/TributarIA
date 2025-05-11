package com.example.tributaria.features.moneylend.repository

import com.example.tributaria.features.moneylend.model.LoanCalculationResult


class LoanRepository {
    fun calculateLoan(
        loanAmount: Double,
        monthlyInterestRate: Double,
        months: Int
    ): LoanCalculationResult {
        require(loanAmount > 0) { "El monto del crédito debe ser mayor que cero" }
        require(monthlyInterestRate > 0) { "La tasa de interés debe ser mayor que cero" }
        require(months > 0) { "El plazo debe ser mayor que cero meses" }

        val rate = monthlyInterestRate / 100

        val monthlyPayment = if (rate == 0.0) {
            loanAmount / months
        } else {
            loanAmount * rate * Math.pow(1 + rate, months.toDouble()) / (Math.pow(1 + rate, months.toDouble()) - 1)
        }

        var totalInterest = 0.0
        var remainingBalance = loanAmount

        for (month in 1..months) {
            val interest = remainingBalance * rate
            val principal = monthlyPayment - interest
            remainingBalance -= principal
            totalInterest += interest
        }

        return LoanCalculationResult(
            monthlyPayment = monthlyPayment,
            totalInterest = totalInterest,
            amortizationTable = emptyList() // No usamos la tabla en esta versión
        )
    }
}