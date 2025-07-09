// En: viewmodels/ProfileViewModel.kt
package com.example.up_rivals.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_rivals.data.UserPreferencesRepository
import com.example.up_rivals.network.ApiClient
import com.example.up_rivals.network.dto.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Estado para la pantalla de Perfil
sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(val user: User) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            try {
                // 1. Obtenemos el token guardado
                val token = userPreferencesRepository.authToken.first()
                if (token.isNullOrBlank()) {
                    _uiState.value = ProfileUiState.Error("No se encontró sesión. Por favor, inicia sesión de nuevo.")
                    return@launch
                }
                val bearerToken = "Bearer $token"

                // 2. Llamamos a la API para obtener el perfil
                val response = ApiClient.apiService.getProfile(bearerToken)

                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = ProfileUiState.Success(response.body()!!)
                } else {
                    _uiState.value = ProfileUiState.Error("Error al cargar el perfil.")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}