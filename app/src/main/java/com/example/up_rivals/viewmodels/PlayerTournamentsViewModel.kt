// En: viewmodels/PlayerTournamentsViewModel.kt
package com.example.up_rivals.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_rivals.data.UserPreferencesRepository
import com.example.up_rivals.network.ApiClient
import com.example.up_rivals.network.dto.Tournament
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

// Un estado para manejar la carga de ambas listas
sealed interface PlayerTournamentsUiState {
    object Loading : PlayerTournamentsUiState
    data class Success(
        val allTournaments: List<Tournament>,
        val registeredTournamentIds: Set<String>
    ) : PlayerTournamentsUiState
    data class Error(val message: String) : PlayerTournamentsUiState
}

class PlayerTournamentsViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow<PlayerTournamentsUiState>(PlayerTournamentsUiState.Loading)
    private val _searchQuery = MutableStateFlow("")
    val uiState: StateFlow<PlayerTournamentsUiState> = _uiState.asStateFlow()
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadAllData()
    }

    private fun loadAllData() {
        _uiState.value = PlayerTournamentsUiState.Loading
        viewModelScope.launch {
            try {
                // Obtenemos el token
                val token = userPreferencesRepository.authToken.first()
                if (token.isNullOrBlank()) {
                    _uiState.value = PlayerTournamentsUiState.Error("No se encontró token de autenticación.")
                    return@launch
                }
                val bearerToken = "Bearer $token"

                // Hacemos las dos llamadas a la API
                val allTournamentsResponse = ApiClient.apiService.getTournaments()
                val myTournamentsResponse = ApiClient.apiService.getPlayerMyTournaments(bearerToken)

                // Verificamos que ambas hayan sido exitosas
                if (allTournamentsResponse.isSuccessful && myTournamentsResponse.isSuccessful) {
                    val allTournaments = allTournamentsResponse.body() ?: emptyList()
                    val myTournaments = myTournamentsResponse.body() ?: emptyList()

                    _uiState.value = PlayerTournamentsUiState.Success(
                        allTournaments = allTournaments,
                        registeredTournamentIds = myTournaments.map { it.id }.toSet()
                    )
                } else {
                    _uiState.value = PlayerTournamentsUiState.Error("Error al cargar los torneos.")
                }
            } catch (e: Exception) {
                _uiState.value = PlayerTournamentsUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    // --- LISTAS FILTRADAS Y SEPARADAS ---
    private val _finalLists = combine(_uiState, searchQuery) { state, query ->
        if (state is PlayerTournamentsUiState.Success) {
            val filteredList = state.allTournaments.filter {
                it.name.contains(query, ignoreCase = true)
            }
            // Separamos la lista en dos: inscritos y disponibles
            val (registered, available) = filteredList.partition {
                it.id in state.registeredTournamentIds
            }
            Pair(registered, available)
        } else {
            Pair(emptyList(), emptyList())
        }
    }

    val registeredTournaments: StateFlow<List<Tournament>> = _finalLists
        .map { it.first }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val availableTournaments: StateFlow<List<Tournament>> = _finalLists
        .map { it.second }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    // Función pública para refrescar los datos
    fun loadTournaments() {
        loadAllData()
    }
}