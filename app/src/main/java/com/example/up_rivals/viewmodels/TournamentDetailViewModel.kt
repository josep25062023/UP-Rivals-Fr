// En: viewmodels/TournamentDetailViewModel.kt
package com.example.up_rivals.viewmodels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_rivals.network.ApiClient
import com.example.up_rivals.network.dto.Tournament
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// Estado para la pantalla de detalle del torneo
sealed interface TournamentDetailUiState {
    object Loading : TournamentDetailUiState
    data class Success(val tournament: Tournament) : TournamentDetailUiState
    data class Error(val message: String) : TournamentDetailUiState
}

class TournamentDetailViewModel(
    savedStateHandle: SavedStateHandle // Herramienta para leer los argumentos de la navegación
) : ViewModel() {

    // Obtenemos el ID del torneo de la ruta (ej. "tournament_detail_screen/{tournamentId}")
    private val tournamentId: String = checkNotNull(savedStateHandle["tournamentId"])

    private val _uiState = MutableStateFlow<TournamentDetailUiState>(TournamentDetailUiState.Loading)
    val uiState: StateFlow<TournamentDetailUiState> = _uiState.asStateFlow()

    init {
        // Cargamos los detalles tan pronto como el ViewModel se crea
        loadTournamentDetails()
    }

    private fun loadTournamentDetails() {
        _uiState.value = TournamentDetailUiState.Loading
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getTournamentDetails(tournamentId)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = TournamentDetailUiState.Success(response.body()!!)
                } else {
                    _uiState.value = TournamentDetailUiState.Error("Error al cargar los detalles del torneo.")
                }
            } catch (e: Exception) {
                _uiState.value = TournamentDetailUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}