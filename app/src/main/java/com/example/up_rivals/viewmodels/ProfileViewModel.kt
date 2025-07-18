// En: viewmodels/ProfileViewModel.kt
package com.example.up_rivals.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.up_rivals.data.UserPreferencesRepository
import com.example.up_rivals.network.ApiClient
import com.example.up_rivals.network.dto.User
import com.example.up_rivals.network.dto.UpdateProfileRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody

// Estado para la pantalla de Perfil
sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class Success(val user: User) : ProfileUiState
    data class Error(val message: String) : ProfileUiState
}

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val userPreferencesRepository = UserPreferencesRepository(application)

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    fun loadProfile() {
        _uiState.value = ProfileUiState.Loading
        viewModelScope.launch {
            try {
                // 1. Obtenemos el token guardado
                val token = userPreferencesRepository.authToken.first()
                if (token.isNullOrBlank()) {
                    _uiState.value = ProfileUiState.Error("No se encontró sesión. Por favor, inicia sesión de nuevo.")
                    return@launch
                }
                val bearerToken = "Bearer $token"

                // 2. Llamamos a la API para obtener el perfil
                val response = ApiClient.apiService.getProfile(bearerToken)

                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = ProfileUiState.Success(response.body()!!)
                } else {
                    _uiState.value = ProfileUiState.Error("Error al cargar el perfil.")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun updateProfile(name: String, phone: String) {
        viewModelScope.launch {
            try {
                // 1. Obtenemos el token guardado
                val token = userPreferencesRepository.authToken.first()
                if (token.isNullOrBlank()) {
                    _uiState.value = ProfileUiState.Error("No se encontró sesión. Por favor, inicia sesión de nuevo.")
                    return@launch
                }
                val bearerToken = "Bearer $token"

                // 2. Obtenemos el usuario actual para obtener su ID
                val currentState = _uiState.value
                if (currentState !is ProfileUiState.Success) {
                    _uiState.value = ProfileUiState.Error("Error: No se pudo obtener la información del usuario.")
                    return@launch
                }
                val userId = currentState.user.id

                // 3. Creamos el request de actualización
                val updateRequest = UpdateProfileRequest(
                    name = name.takeIf { it.isNotBlank() },
                    phone = phone.takeIf { it.isNotBlank() },
                    profilePicture = null // No cambiamos la foto en esta función
                )

                // 4. Llamamos a la API para actualizar el perfil
                val response = ApiClient.apiService.updateProfile(bearerToken, userId, updateRequest)

                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = ProfileUiState.Success(response.body()!!)
                } else {
                    _uiState.value = ProfileUiState.Error("Error al actualizar el perfil.")
                }
            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Error de conexión: ${e.message}")
            }
        }
    }

    fun updateProfilePicture(imageUri: android.net.Uri, context: android.content.Context) {
        viewModelScope.launch {
            try {
                // 0. Guardar el estado actual antes de cambiar a Loading
                val currentState = _uiState.value
                if (currentState !is ProfileUiState.Success) {
                    _uiState.value = ProfileUiState.Error("Error: No se pudo obtener la información del usuario.")
                    return@launch
                }
                val userId = currentState.user.id

                _uiState.value = ProfileUiState.Loading

                // 1. Preparar el archivo para upload
                val inputStream = context.contentResolver.openInputStream(imageUri)
                val bytes = inputStream?.readBytes()
                inputStream?.close()

                if (bytes == null) {
                    _uiState.value = ProfileUiState.Error("Error al leer la imagen seleccionada.")
                    return@launch
                }

                val requestFile = RequestBody.create(
                    "image/*".toMediaType(),
                    bytes
                )
                val body = MultipartBody.Part.createFormData("file", "profile_image.jpg", requestFile)

                // 2. Subir la imagen
                val uploadResponse = ApiClient.apiService.uploadFile(body)
                if (!uploadResponse.isSuccessful || uploadResponse.body() == null) {
                    _uiState.value = ProfileUiState.Error("Error al subir la imagen.")
                    return@launch
                }

                val imageUrl = uploadResponse.body()!!.secure_url

                // 3. Obtener token
                val token = userPreferencesRepository.authToken.first()
                if (token.isNullOrBlank()) {
                    _uiState.value = ProfileUiState.Error("No se encontró sesión.")
                    return@launch
                }
                val bearerToken = "Bearer $token"

                // 4. Actualizar el perfil con la nueva imagen
                val updateRequest = UpdateProfileRequest(
                    name = null,
                    phone = null,
                    profilePicture = imageUrl
                )

                val response = ApiClient.apiService.updateProfile(bearerToken, userId, updateRequest)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.value = ProfileUiState.Success(response.body()!!)
                } else {
                    _uiState.value = ProfileUiState.Error("Error al actualizar la foto de perfil.")
                }

            } catch (e: Exception) {
                _uiState.value = ProfileUiState.Error("Error: ${e.message}")
            }
        }
    }

    // Función pública para refrescar el perfil
    fun refreshProfile() {
        loadProfile()
    }
}
