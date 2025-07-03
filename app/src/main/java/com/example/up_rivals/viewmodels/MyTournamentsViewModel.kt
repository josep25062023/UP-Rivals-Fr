// En: viewmodels/MyTournamentsViewModel.kt
package com.example.up_rivals.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_rivals.data.UserPreferencesRepository
import com.example.up_rivals.network.ApiClient
import com.example.up_rivals.network.dto.Tournament
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MyTournamentsViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow<TournamentsUiState>(TournamentsUiState.Loading)
    val uiState: StateFlow<TournamentsUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadMyTournaments()
    }

    private val _filteredTournaments = combine(uiState, searchQuery) { state, query ->
        if (state is TournamentsUiState.Success) {
            val filteredList = state.tournaments.filter {
                it.name.contains(query, ignoreCase = true)
            }
            val now = ZonedDateTime.now()
            filteredList.partition { tournament ->
                try {
                    val startDate = ZonedDateTime.parse(tournament.startDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    startDate.isBefore(now)
                } catch (e: Exception) {
                    false
                }
            }
        } else {
            Pair(emptyList(), emptyList())
        }
    }

    val inProgressTournaments: StateFlow<List<Tournament>> = _filteredTournaments
        .map { it.first }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val upcomingTournaments: StateFlow<List<Tournament>> = _filteredTournaments
        .map { it.second }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun loadMyTournaments() {
        _uiState.value = TournamentsUiState.Loading
        viewModelScope.launch {
            try {
                // 1. Obtenemos el token del organizador
                val token = userPreferencesRepository.authToken.first()
                if (token.isNullOrBlank()) {
                    _uiState.value = TournamentsUiState.Error("No se encontró token de autenticación.")
                    return@launch
                }
                val bearerToken = "Bearer $token"

                // 2. Llamamos a la nueva función de la API
                val response = ApiClient.apiService.getMyTournaments(bearerToken)

                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = TournamentsUiState.Success(response.body()!!)
                } else {
                    _uiState.value = TournamentsUiState.Error("Error al cargar tus torneos.")
                }
            } catch (e: Exception) {
                _uiState.value = TournamentsUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}