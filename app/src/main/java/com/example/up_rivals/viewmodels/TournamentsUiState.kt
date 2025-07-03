// En: viewmodels/TournamentsUiState.kt
package com.example.up_rivals.viewmodels

import com.example.up_rivals.network.dto.Tournament

sealed interface TournamentsUiState {
    object Loading : TournamentsUiState
    data class Success(val tournaments: List<Tournament>) : TournamentsUiState
    data class Error(val message: String) : TournamentsUiState
}