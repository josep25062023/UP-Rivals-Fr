// En: viewmodels/LoginViewModel.kt
package com.example.up_rivals.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_rivals.network.ApiClient
import com.example.up_rivals.network.dto.LoginRequest
import com.example.up_rivals.network.dto.User // Importamos el User DTO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// --- El estado de la UI ahora reflejará el resultado final: el objeto User ---
sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class Success(val user: User) : LoginUiState // Ahora contiene el perfil del usuario
    data class Error(val message: String) : LoginUiState
}

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            try {
                // --- Llamada 1: Iniciar Sesión ---
                val loginRequest = LoginRequest(email = email, password = password)
                val loginResponse = ApiClient.apiService.login(loginRequest)

                if (loginResponse.isSuccessful && loginResponse.body() != null) {
                    val token = loginResponse.body()!!.accessToken
                    // Formateamos el token para el encabezado de autorización
                    val bearerToken = "Bearer $token"

                    // --- Llamada 2: Obtener Perfil con el Token ---
                    val profileResponse = ApiClient.apiService.getProfile(bearerToken)

                    if (profileResponse.isSuccessful && profileResponse.body() != null) {
                        // ¡Éxito final! Tenemos el perfil del usuario.
                        _uiState.value = LoginUiState.Success(profileResponse.body()!!)
                    } else {
                        _uiState.value = LoginUiState.Error("Login exitoso, pero no se pudo obtener el perfil.")
                    }
                } else {
                    _uiState.value = LoginUiState.Error("Credenciales incorrectas o usuario no encontrado.")
                }
            } catch (e: Exception) {
                _uiState.value = LoginUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}