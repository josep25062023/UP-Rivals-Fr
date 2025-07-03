// En: viewmodels/InscriptionsViewModel.kt
package com.example.up_rivals.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_rivals.data.UserPreferencesRepository
import com.example.up_rivals.network.ApiClient
import com.example.up_rivals.network.dto.InscriptionRequestDto // Importamos el nuevo DTO
import com.example.up_rivals.network.dto.UpdateInscriptionRequest
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// El estado de éxito ahora contendrá la lista de nuestro nuevo DTO
sealed interface InscriptionsUiState {
    object Idle : InscriptionsUiState
    object Loading : InscriptionsUiState
    data class Success(val requests: List<InscriptionRequestDto>) : InscriptionsUiState
    data class Error(val message: String) : InscriptionsUiState
}

sealed interface UiEvent {
    data class ShowToast(val message: String) : UiEvent
}

class InscriptionsViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow<InscriptionsUiState>(InscriptionsUiState.Idle)
    val uiState: StateFlow<InscriptionsUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        // Cargamos las inscripciones tan pronto como se crea el ViewModel
        loadInscriptions()
    }

    // MODIFICADO: La función ya no necesita un tournamentId
    fun loadInscriptions() {
        _uiState.value = InscriptionsUiState.Loading
        viewModelScope.launch {
            try {
                val token = userPreferencesRepository.authToken.first()
                if (token.isNullOrBlank()) {
                    _uiState.value = InscriptionsUiState.Error("No se encontró token de autenticación.")
                    return@launch
                }

                val bearerToken = "Bearer $token"

                // Llamamos a la nueva función del ApiService
                val response = ApiClient.apiService.getOrganizerInscriptions(bearerToken)

                if (response.isSuccessful && response.body() != null) {
                    // Filtramos para mostrar solo las pendientes
                    val pendingRequests = response.body()!!.filter { it.status == "pending" }
                    _uiState.value = InscriptionsUiState.Success(pendingRequests)
                } else {
                    _uiState.value = InscriptionsUiState.Error("Error al obtener las solicitudes.")
                }
            } catch (e: Exception) {
                _uiState.value = InscriptionsUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun approveInscription(tournamentId: String, teamId: String) {
        updateStatus(tournamentId, teamId, "approved", "Equipo aceptado correctamente.")
    }

    fun rejectInscription(tournamentId: String, teamId: String) {
        updateStatus(tournamentId, teamId, "rejected", "Equipo rechazado correctamente.")
    }

    private fun updateStatus(tournamentId: String, teamId: String, status: String, successMessage: String) {
        viewModelScope.launch {
            try {
                val token = userPreferencesRepository.authToken.first()
                if (token.isNullOrBlank()) {
                    _eventFlow.emit(UiEvent.ShowToast("No se encontró token de autenticación."))
                    return@launch
                }

                val bearerToken = "Bearer $token"
                val request = UpdateInscriptionRequest(status = status)
                val response = ApiClient.apiService.updateInscriptionStatus(bearerToken, tournamentId, teamId, request)

                if (response.isSuccessful) {
                    _eventFlow.emit(UiEvent.ShowToast(successMessage))
                    // MODIFICADO: Recargamos la lista completa
                    loadInscriptions()
                } else {
                    _eventFlow.emit(UiEvent.ShowToast("Error al actualizar el estado."))
                }
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowToast("Error de conexión: ${e.message}"))
            }
        }
    }
}