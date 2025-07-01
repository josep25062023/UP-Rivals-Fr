// En: viewmodels/CreateTournamentViewModel.kt
package com.example.up_rivals.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_rivals.data.UserPreferencesRepository
import com.example.up_rivals.network.ApiClient
import com.example.up_rivals.network.dto.CreateTournamentRequest
import com.example.up_rivals.network.dto.Tournament
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// --- Estados posibles para la pantalla de Creación de Torneo ---
sealed interface CreateTournamentUiState {
    object Idle : CreateTournamentUiState
    object Loading : CreateTournamentUiState
    data class Success(val tournament: Tournament) : CreateTournamentUiState // Éxito, tenemos el nuevo torneo
    data class Error(val message: String) : CreateTournamentUiState
}

class CreateTournamentViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow<CreateTournamentUiState>(CreateTournamentUiState.Idle)
    val uiState: StateFlow<CreateTournamentUiState> = _uiState.asStateFlow()

    fun createTournament(request: CreateTournamentRequest) {
        _uiState.value = CreateTournamentUiState.Loading

        viewModelScope.launch {
            try {
                // 1. Obtenemos el token guardado en el dispositivo
                val token = userPreferencesRepository.authToken.first()
                if (token.isNullOrBlank()) {
                    _uiState.value = CreateTournamentUiState.Error("No se encontró token de autenticación. Por favor, inicia sesión de nuevo.")
                    return@launch
                }

                val bearerToken = "Bearer $token"

                // 2. Llamamos a la API para crear el torneo
                val response = ApiClient.apiService.createTournament(bearerToken, request)

                if (response.isSuccessful && response.body() != null) {
                    // ¡Éxito!
                    _uiState.value = CreateTournamentUiState.Success(response.body()!!)
                } else {
                    // Error del servidor (ej. datos inválidos)
                    _uiState.value = CreateTournamentUiState.Error("Error al crear el torneo. Revisa los datos.")
                }
            } catch (e: Exception) {
                // Error de red
                _uiState.value = CreateTournamentUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}