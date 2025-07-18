// En: viewmodels/TeamDetailViewModel.kt
package com.example.up_rivals.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.up_rivals.data.UserPreferencesRepository
import com.example.up_rivals.network.ApiClient
import com.example.up_rivals.network.dto.AddMemberRequest
import com.example.up_rivals.network.dto.TeamDetailDto
import com.example.up_rivals.network.dto.UpdateTeamRequest
import com.example.up_rivals.network.dto.UploadResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

// Estado para la pantalla de detalle del equipo
sealed interface TeamDetailUiState {
    object Loading : TeamDetailUiState
    data class Success(val teamDetail: TeamDetailDto, val currentUserId: String? = null) : TeamDetailUiState
    data class Error(val message: String) : TeamDetailUiState
}

class TeamDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val teamId: String = checkNotNull(savedStateHandle["teamId"])
    private val userPreferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow<TeamDetailUiState>(TeamDetailUiState.Loading)
    val uiState: StateFlow<TeamDetailUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<UiEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    init {
        loadTeamDetails()
    }

    fun loadTeamDetails() {
        _uiState.value = TeamDetailUiState.Loading
        viewModelScope.launch {
            try {
                val token = userPreferencesRepository.authToken.first()
                if (token.isNullOrBlank()) {
                    _uiState.value = TeamDetailUiState.Error("No autenticado.")
                    return@launch
                }
                val bearerToken = "Bearer $token"

                // Obtener detalles del equipo y perfil del usuario en paralelo
                val teamResponse = ApiClient.apiService.getTeamDetails(bearerToken, teamId)
                val profileResponse = ApiClient.apiService.getProfile(bearerToken)

                if (teamResponse.isSuccessful && teamResponse.body() != null &&
                    profileResponse.isSuccessful && profileResponse.body() != null) {
                    _uiState.value = TeamDetailUiState.Success(
                        teamDetail = teamResponse.body()!!,
                        currentUserId = profileResponse.body()!!.id
                    )
                } else {
                    _uiState.value = TeamDetailUiState.Error("Error al cargar los detalles del equipo.")
                }
            } catch (e: Exception) {
                _uiState.value = TeamDetailUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun addMember(userId: String) {
        viewModelScope.launch {
            try {
                val token = userPreferencesRepository.authToken.first()
                if (token.isNullOrBlank()) {
                    _eventFlow.emit(UiEvent.ShowToast("Error de autenticación."))
                    return@launch
                }
                val bearerToken = "Bearer $token"
                val request = AddMemberRequest(userId = userId)

                val response = ApiClient.apiService.addTeamMember(bearerToken, teamId, request)
                if (response.isSuccessful) {
                    _eventFlow.emit(UiEvent.ShowToast("¡Integrante añadido!"))
                    // Recargamos los detalles para que la lista de miembros se actualice
                    loadTeamDetails()
                } else {
                    _eventFlow.emit(UiEvent.ShowToast("Error al añadir al integrante."))
                }
            } catch (e: Exception) {
                _eventFlow.emit(UiEvent.ShowToast("Error de conexión: ${e.message}"))
            }
        }
    }

    fun updateTeamLogo(imageUri: android.net.Uri, context: android.content.Context) {
        viewModelScope.launch {
            try {
                // Guardar el estado actual antes de cambiar a Loading
                val currentState = _uiState.value
                if (currentState !is TeamDetailUiState.Success) {
                    _eventFlow.emit(UiEvent.ShowToast("Error: No se pudo obtener la información del equipo."))
                    return@launch
                }

                _uiState.value = TeamDetailUiState.Loading

                // 1. Preparar el archivo para upload
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes == null) {
                    _uiState.value = TeamDetailUiState.Error("Error al leer la imagen seleccionada.")
                    return@launch
                }

                // 2. Crear archivo temporal
                val tempFile = File(context.cacheDir, "temp_team_logo.jpg")
                val fos = FileOutputStream(tempFile)
                fos.write(bytes)
                fos.close()

                // 3. Crear MultipartBody.Part
                val requestFile = tempFile.asRequestBody("image/jpeg".toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData("file", tempFile.name, requestFile)

                // 4. Subir archivo
                val uploadResponse = ApiClient.apiService.uploadFile(body)
                tempFile.delete() // Limpiar archivo temporal

                if (!uploadResponse.isSuccessful || uploadResponse.body() == null) {
                    _uiState.value = TeamDetailUiState.Error("Error al subir la imagen.")
                    return@launch
                }

                val uploadResult: UploadResponse = uploadResponse.body()!!
                val logoUrl = uploadResult.secure_url

                // 5. Actualizar el equipo con la nueva URL del logo
                val token = userPreferencesRepository.authToken.first()
                if (token.isNullOrBlank()) {
                    _uiState.value = TeamDetailUiState.Error("Error de autenticación.")
                    return@launch
                }
                val bearerToken = "Bearer $token"
                val updateRequest = UpdateTeamRequest(logo = logoUrl)

                val updateResponse = ApiClient.apiService.updateTeam(bearerToken, teamId, updateRequest)
                if (updateResponse.isSuccessful && updateResponse.body() != null) {
                    // Mantener el currentUserId del estado anterior
                    val currentUserId = (currentState as? TeamDetailUiState.Success)?.currentUserId
                    _uiState.value = TeamDetailUiState.Success(
                        teamDetail = updateResponse.body()!!,
                        currentUserId = currentUserId
                    )
                    _eventFlow.emit(UiEvent.ShowToast("¡Logo del equipo actualizado!"))
                } else {
                    _uiState.value = TeamDetailUiState.Error("Error al actualizar el logo del equipo.")
                }
            } catch (e: Exception) {
                _uiState.value = TeamDetailUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }
}