package com.example.tributaria.features.balancepoint.viewmodel

import androidx.lifecycle.ViewModel
import com.example.tributaria.features.balancepoint.repository.BalancePointRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class BalancePointViewModel(
    private val repository: BalancePointRepository = BalancePointRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(BalancePointUiState())
    val uiState: StateFlow<BalancePointUiState> = _uiState

    fun onCostoVariableChanged(value: String) {
        _uiState.update { it.copy(costoVariable = value) }
        validar()
    }

    fun onCostoFijoChanged(value: String) {
        _uiState.update { it.copy(costoFijo = value) }
        validar()
    }

    fun onPrecioUnitarioChanged(value: String) {
        _uiState.update { it.copy(precioUnitario = value) }
        validar()
    }

    fun onCalcularClick() {
        validar()

        // Si hay error → NO calcular
        if (_uiState.value.errorMessage != null) return

        val costoVariable = _uiState.value.costoVariable.toFloat()
        val costoFijo = _uiState.value.costoFijo.toFloat()
        val precioUnitario = _uiState.value.precioUnitario.toFloat()

        val resultado = repository.calcularPuntoEquilibrio(costoVariable, costoFijo, precioUnitario)
        _uiState.update { it.copy(puntoEquilibrio = resultado) }
    }

    /** VALIDACIONES COMPLETAS **/
    private fun validar() {
        val state = _uiState.value

        // 1. Validar campos vacíos
        if (state.costoVariable.isBlank() ||
            state.costoFijo.isBlank() ||
            state.precioUnitario.isBlank()
        ) {
            _uiState.update { it.copy(errorMessage = "Ningún campo puede estar vacío.") }
            return
        }

        // 2. Convertir a número
        val costoVariable = state.costoVariable.toFloatOrNull()
        val costoFijo = state.costoFijo.toFloatOrNull()
        val precioUnitario = state.precioUnitario.toFloatOrNull()

        if (costoVariable == null || costoFijo == null || precioUnitario == null) {
            _uiState.update { it.copy(errorMessage = "Todos los valores deben ser números válidos.") }
            return
        }

        // 3. Validar negativos
        if (costoVariable < 0 || costoFijo < 0 || precioUnitario < 0) {
            _uiState.update { it.copy(errorMessage = "Los valores no pueden ser negativos.") }
            return
        }

        // 4. Validar relación costo variable < precio unitario
        if (costoVariable >= precioUnitario) {
            _uiState.update {
                it.copy(errorMessage = "El costo variable debe ser menor que el precio unitario.")
            }
            return
        }

        // Si todo está bien, quitar error
        _uiState.update { it.copy(errorMessage = null) }
    }
}

data class BalancePointUiState(
    val costoVariable: String = "",
    val costoFijo: String = "",
    val precioUnitario: String = "",
    val puntoEquilibrio: Float? = null,
    val errorMessage: String? = null
)
