// En: viewmodels/CreateTeamViewModel.kt
package com.example.up_rivals.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_rivals.data.UserPreferencesRepository
import com.example.up_rivals.network.ApiClient
import com.example.up_rivals.network.dto.CreateTeamRequest
import com.example.up_rivals.network.dto.Team
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Estados para la pantalla de Creación de Equipo
sealed interface CreateTeamUiState {
    object Idle : CreateTeamUiState
    object Loading : CreateTeamUiState
    data class Success(val team: Team) : CreateTeamUiState // Éxito, tenemos el nuevo equipo
    data class Error(val message: String) : CreateTeamUiState
}

class CreateTeamViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow<CreateTeamUiState>(CreateTeamUiState.Idle)
    val uiState: StateFlow<CreateTeamUiState> = _uiState.asStateFlow()

    fun createAndInscribeTeam(teamName: String, tournamentId: String) {
        _uiState.value = CreateTeamUiState.Loading

        viewModelScope.launch {
            try {
                // 1. Obtenemos el token del jugador
                val token = userPreferencesRepository.authToken.first()
                if (token.isNullOrBlank()) {
                    _uiState.value = CreateTeamUiState.Error("No se encontró token de autenticación.")
                    return@launch
                }
                val bearerToken = "Bearer $token"

                // 2. LLAMADA 1: Creamos el equipo
                val createTeamRequest = CreateTeamRequest(name = teamName)
                val teamResponse = ApiClient.apiService.createTeam(bearerToken, createTeamRequest)

                if (teamResponse.isSuccessful && teamResponse.body() != null) {
                    val newTeam = teamResponse.body()!!

                    // 3. LLAMADA 2: Inscribimos el equipo recién creado al torneo
                    val inscribeResponse = ApiClient.apiService.inscribeTeam(
                        token = bearerToken,
                        tournamentId = tournamentId,
                        teamId = newTeam.id
                    )

                    if (inscribeResponse.isSuccessful) {
                        // ¡Éxito en ambos pasos!
                        _uiState.value = CreateTeamUiState.Success(newTeam)
                    } else {
                        _uiState.value = CreateTeamUiState.Error("Se creó el equipo, pero falló la inscripción al torneo.")
                    }
                } else {
                    _uiState.value = CreateTeamUiState.Error("Error al crear el equipo.")
                }
            } catch (e: Exception) {
                _uiState.value = CreateTeamUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}