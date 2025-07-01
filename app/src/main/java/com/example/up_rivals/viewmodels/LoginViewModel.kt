// En: viewmodels/LoginViewModel.kt
package com.example.up_rivals.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_rivals.data.UserPreferencesRepository
import com.example.up_rivals.network.ApiClient
import com.example.up_rivals.network.dto.LoginRequest
import com.example.up_rivals.network.dto.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed interface LoginUiState {
    object Idle : LoginUiState
    object Loading : LoginUiState
    data class Success(val user: User) : LoginUiState
    data class Error(val message: String) : LoginUiState
}

// --- CAMBIO: Heredamos de AndroidViewModel para tener acceso al Context ---
class LoginViewModel(application: Application) : AndroidViewModel(application) {

    // --- AÑADIDO: Creamos una instancia del repositorio ---
    private val userPreferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        _uiState.value = LoginUiState.Loading
        viewModelScope.launch {
            try {
                val loginRequest = LoginRequest(email = email, password = password)
                val loginResponse = ApiClient.apiService.login(loginRequest)

                if (loginResponse.isSuccessful && loginResponse.body() != null) {
                    val token = loginResponse.body()!!.accessToken

                    // --- AÑADIDO: Guardamos el token ---
                    userPreferencesRepository.saveAuthToken(token)

                    val bearerToken = "Bearer $token"
                    val profileResponse = ApiClient.apiService.getProfile(bearerToken)

                    if (profileResponse.isSuccessful && profileResponse.body() != null) {
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