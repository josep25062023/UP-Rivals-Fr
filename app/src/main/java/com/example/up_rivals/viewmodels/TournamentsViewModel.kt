// En: viewmodels/TournamentsViewModel.kt
package com.example.up_rivals.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_rivals.network.ApiClient
import com.example.up_rivals.network.dto.Tournament
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter



class TournamentsViewModel : ViewModel() {

    // --- ESTADO 1: La lista COMPLETA de torneos desde la API ---
    private val _uiState = MutableStateFlow<TournamentsUiState>(TournamentsUiState.Loading)
    val uiState: StateFlow<TournamentsUiState> = _uiState.asStateFlow()

    // --- ESTADO 2: El texto actual en la barra de búsqueda ---
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // --- LÓGICA PRINCIPAL ---
    init {
        // Carga los torneos tan pronto como el ViewModel se crea
        loadTournaments()
    }

    // --- LISTAS FILTRADAS Y SEPARADAS (La Magia de 'combine') ---
    // Este 'combine' escucha cambios en la lista de torneos Y en el texto de búsqueda
    // y crea un nuevo objeto que contiene ambas listas separadas.
    private val _filteredTournaments = combine(uiState, searchQuery) { state, query ->
        if (state is TournamentsUiState.Success) {
            val filteredList = state.tournaments.filter {
                it.name.contains(query, ignoreCase = true)
            }
            // Separamos la lista filtrada en dos: en curso y próximos
            val now = ZonedDateTime.now()
            filteredList.partition { tournament ->
                try {
                    val startDate = ZonedDateTime.parse(tournament.startDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    startDate.isBefore(now)
                } catch (e: Exception) {
                    false // Si la fecha está mal formateada, no lo contamos como "en curso"
                }
            }
        } else {
            // Si no hay éxito, devolvemos listas vacías
            Pair(emptyList(), emptyList())
        }
    }

    // Exponemos la lista de torneos "En curso" para que la UI la observe
    val inProgressTournaments: StateFlow<List<Tournament>> = _filteredTournaments
        .map { it.first } // .first se refiere a la primera lista del Pair (los que cumplen la condición)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Exponemos la lista de torneos "Próximos" para que la UI la observe
    val upcomingTournaments: StateFlow<List<Tournament>> = _filteredTournaments
        .map { it.second } // .second se refiere a la segunda lista del Pair (los que no la cumplen)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())


    // --- FUNCIONES PÚBLICAS ---
    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun loadTournaments() {
        _uiState.value = TournamentsUiState.Loading
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getTournaments()
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = TournamentsUiState.Success(response.body()!!)
                } else {
                    _uiState.value = TournamentsUiState.Error("Error al cargar los torneos.")
                }
            } catch (e: Exception) {
                _uiState.value = TournamentsUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}