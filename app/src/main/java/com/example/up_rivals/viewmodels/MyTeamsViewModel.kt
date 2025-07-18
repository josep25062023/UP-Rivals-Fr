// En: viewmodels/MyTeamsViewModel.kt
package com.example.up_rivals.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_rivals.data.UserPreferencesRepository
import com.example.up_rivals.network.ApiClient
import com.example.up_rivals.network.dto.PlayerTeamDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Estado para la pantalla de "Mis Equipos"
sealed interface MyTeamsUiState {
    object Loading : MyTeamsUiState
    data class Success(val teams: List<PlayerTeamDto>) : MyTeamsUiState
    data class Error(val message: String) : MyTeamsUiState
}

class MyTeamsViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow<MyTeamsUiState>(MyTeamsUiState.Loading)
    val uiState: StateFlow<MyTeamsUiState> = _uiState.asStateFlow()

    init {
        loadPlayerTeams()
    }

    // ✅ NUEVA FUNCIÓN: Para refrescar manualmente
    fun refreshTeams() {
        loadPlayerTeams()
    }

    // ✅ HACER PÚBLICA: Para poder llamarla desde otras pantallas
    fun loadPlayerTeams() {
        _uiState.value = MyTeamsUiState.Loading
        viewModelScope.launch {
            try {
                val token = userPreferencesRepository.authToken.first()
                if (token.isNullOrBlank()) {
                    _uiState.value = MyTeamsUiState.Error("No se encontró token de autenticación.")
                    return@launch
                }
                val bearerToken = "Bearer $token"

                val response = ApiClient.apiService.getPlayerTeams(bearerToken)

                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = MyTeamsUiState.Success(response.body()!!)
                } else {
                    _uiState.value = MyTeamsUiState.Error("Error al cargar tus equipos.")
                }
            } catch (e: Exception) {
                _uiState.value = MyTeamsUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}