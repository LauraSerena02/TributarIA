package com.example.tributaria.features.balancepoint.repository

class BalancePointRepository {

    fun calcularPuntoEquilibrio(costoVariable: Float, costoFijo: Float, precioUnitario: Float): Float? {
        return try {
            costoFijo / (precioUnitario - costoVariable)
        } catch (e: Exception) {
            null
        }
    }
}