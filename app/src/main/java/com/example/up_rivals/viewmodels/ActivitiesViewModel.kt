package com.example.up_rivals.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_rivals.UserRole
import com.example.up_rivals.network.ApiClient
import com.example.up_rivals.network.dto.PendingMatchDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ActivitiesViewModel : ViewModel() {

    private val _pendingMatches = MutableStateFlow<List<PendingMatchDto>>(emptyList())
    val pendingMatches: StateFlow<List<PendingMatchDto>> = _pendingMatches.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadPendingMatches(token: String, userRole: UserRole) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null

            try {
                val response = when (userRole) {
                    UserRole.ORGANIZER -> {
                        // Usar endpoint para organizadores
                        ApiClient.apiService.getPendingMatches("Bearer $token")
                    }
                    UserRole.PLAYER -> {
                        // Usar endpoint para jugadores
                        ApiClient.apiService.getPlayerPendingMatches("Bearer $token")
                    }
                    else -> {
                        _error.value = "Rol de usuario no válido para ver actividades"
                        return@launch
                    }
                }

                if (response.isSuccessful) {
                    _pendingMatches.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error al cargar los partidos pendientes"
                }
            } catch (e: Exception) {
                _error.value = "Error de conexión: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}