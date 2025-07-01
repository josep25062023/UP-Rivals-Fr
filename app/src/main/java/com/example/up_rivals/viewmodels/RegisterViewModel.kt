// En: viewmodels/RegisterViewModel.kt
package com.example.up_rivals.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_rivals.network.ApiClient
import com.example.up_rivals.network.dto.RegisterRequest
import com.example.up_rivals.network.dto.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// --- Estados posibles para la pantalla de Registro ---
sealed interface RegisterUiState {
    object Idle : RegisterUiState
    object Loading : RegisterUiState
    data class Success(val user: User) : RegisterUiState // Éxito, tenemos el nuevo usuario
    data class Error(val message: String) : RegisterUiState
}

class RegisterViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun register(request: RegisterRequest) {
        // Ponemos la UI en estado de "Cargando"
        _uiState.value = RegisterUiState.Loading

        // Lanzamos una corrutina para no bloquear la app
        viewModelScope.launch {
            try {
                // Llamamos a la API usando nuestro ApiClient
                val response = ApiClient.apiService.register(request)

                // Verificamos si la respuesta fue exitosa
                if (response.isSuccessful && response.body() != null) {
                    // ¡Éxito! Actualizamos la UI con los datos del nuevo usuario
                    _uiState.value = RegisterUiState.Success(response.body()!!)
                } else {
                    // Error, por ejemplo, si el email ya existe
                    // Aquí podrías leer el mensaje de error del body si el servidor lo envía
                    _uiState.value = RegisterUiState.Error("No se pudo completar el registro. El email podría ya estar en uso.")
                }
            } catch (e: Exception) {
                // Error de red (ej. no hay internet)
                _uiState.value = RegisterUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}