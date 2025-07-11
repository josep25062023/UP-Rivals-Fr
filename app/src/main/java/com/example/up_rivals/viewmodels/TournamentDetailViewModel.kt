package com.example.up_rivals.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_rivals.data.UserPreferencesRepository
import com.example.up_rivals.network.ApiClient
import com.example.up_rivals.network.dto.MatchDto
import com.example.up_rivals.network.dto.StandingDto
import com.example.up_rivals.network.dto.Tournament
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// --- Estados de la UI ---
sealed interface TournamentDetailUiState {
    object Loading : TournamentDetailUiState
    data class Success(val tournament: Tournament) : TournamentDetailUiState
    data class Error(val message: String) : TournamentDetailUiState
}

sealed interface StandingsUiState {
    object Idle : StandingsUiState
    object Loading : StandingsUiState
    data class Success(val standings: List<StandingDto>) : StandingsUiState
    data class Error(val message: String) : StandingsUiState
}

sealed interface MatchesUiState {
    object Idle : MatchesUiState
    object Loading : MatchesUiState
    data class Success(val matches: List<MatchDto>) : MatchesUiState
    data class Error(val message: String) : MatchesUiState
}

// --- Eventos de una sola vez ---
sealed interface DetailScreenEvent {
    data class ShowToast(val message: String) : DetailScreenEvent
    object DeletionSuccess : DetailScreenEvent
}

class TournamentDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow<TournamentDetailUiState>(TournamentDetailUiState.Loading)
    val uiState: StateFlow<TournamentDetailUiState> = _uiState.asStateFlow()

    private val _standingsUiState = MutableStateFlow<StandingsUiState>(StandingsUiState.Idle)
    val standingsUiState: StateFlow<StandingsUiState> = _standingsUiState.asStateFlow()

    private val _matchesUiState = MutableStateFlow<MatchesUiState>(MatchesUiState.Idle)
    val matchesUiState: StateFlow<MatchesUiState> = _matchesUiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<DetailScreenEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    fun loadTournamentDetails(tournamentId: String) {
        if (_uiState.value is TournamentDetailUiState.Success) return
        _uiState.value = TournamentDetailUiState.Loading
        viewModelScope.launch {
            try {
                val token = userPreferencesRepository.authToken.first()
                if (token.isNullOrBlank()) {
                    _uiState.value = TournamentDetailUiState.Error("No autenticado.")
                    return@launch
                }
                val bearerToken = "Bearer $token"
                val response = ApiClient.apiService.getTournamentDetails(bearerToken, tournamentId)

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

    fun loadStandings(tournamentId: String) {
        if (_standingsUiState.value is StandingsUiState.Success) return
        _standingsUiState.value = StandingsUiState.Loading
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getTournamentStandings(tournamentId)
                if (response.isSuccessful && response.body() != null) {
                    _standingsUiState.value = StandingsUiState.Success(response.body()!!)
                } else {
                    _standingsUiState.value = StandingsUiState.Error("Error al cargar la tabla de posiciones.")
                }
            } catch (e: Exception) {
                _standingsUiState.value = StandingsUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun loadMatches(tournamentId: String) {
        // Ya no detenemos la carga. Siempre que se llame, se refrescará.
        _matchesUiState.value = MatchesUiState.Loading
        viewModelScope.launch {
            try {
                // Los partidos son públicos, no necesitan token
                val response = ApiClient.apiService.getTournamentMatches(tournamentId)
                if (response.isSuccessful && response.body() != null) {
                    _matchesUiState.value = MatchesUiState.Success(response.body()!!)
                } else {
                    _matchesUiState.value = MatchesUiState.Error("Error al cargar los partidos.")
                }
            } catch (e: Exception) {
                _matchesUiState.value = MatchesUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun deleteTournament(tournamentId: String) {
        viewModelScope.launch {
            try {
                val token = userPreferencesRepository.authToken.first()
                if (token.isNullOrBlank()) {
                    _eventFlow.emit(DetailScreenEvent.ShowToast("Error de autenticación."))
                    return@launch
                }
                val bearerToken = "Bearer $token"
                val response = ApiClient.apiService.deleteTournament(bearerToken, tournamentId)

                if (response.isSuccessful) {
                    _eventFlow.emit(DetailScreenEvent.ShowToast("Torneo eliminado exitosamente."))
                    _eventFlow.emit(DetailScreenEvent.DeletionSuccess)
                } else {
                    _eventFlow.emit(DetailScreenEvent.ShowToast("Error al eliminar el torneo."))
                }
            } catch (e: Exception) {
                _eventFlow.emit(DetailScreenEvent.ShowToast("Error de conexión."))
            }
        }
    }

    fun generateSchedule(tournamentId: String) {
        viewModelScope.launch {
            try {
                val token = userPreferencesRepository.authToken.first()
                if (token.isNullOrBlank()) {
                    _eventFlow.emit(DetailScreenEvent.ShowToast("Error de autenticación."))
                    return@launch
                }
                val bearerToken = "Bearer $token"
                val response = ApiClient.apiService.generateSchedule(bearerToken, tournamentId)

                if (response.isSuccessful) {
                    _eventFlow.emit(DetailScreenEvent.ShowToast("Partidos generados exitosamente. Actualizando..."))
                    loadMatches(tournamentId)
                } else {
                    _eventFlow.emit(DetailScreenEvent.ShowToast("Error al generar los partidos."))
                }
            } catch (e: Exception) {
                _eventFlow.emit(DetailScreenEvent.ShowToast("Error de conexión: ${e.message}"))
            }
        }
    }
}