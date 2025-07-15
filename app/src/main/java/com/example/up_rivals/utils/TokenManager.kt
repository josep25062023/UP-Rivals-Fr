package com.example.up_rivals.utils

import android.content.Context
import com.example.up_rivals.data.UserPreferencesRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object TokenManager {

    /**
     * Obtiene el token de autenticación de forma síncrona
     * @param context El contexto de la aplicación
     * @return El token de autenticación o null si no existe
     */
    fun getToken(context: Context): String? {
        return try {
            val repository = UserPreferencesRepository(context)
            runBlocking {
                repository.authToken.first()
            }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Guarda el token de autenticación
     * @param context El contexto de la aplicación
     * @param token El token a guardar
     */
    suspend fun saveToken(context: Context, token: String) {
        val repository = UserPreferencesRepository(context)
        repository.saveAuthToken(token)
    }

    /**
     * Elimina el token de autenticación
     * @param context El contexto de la aplicación
     */
    suspend fun clearToken(context: Context) {
        val repository = UserPreferencesRepository(context)
        repository.clearAuthToken()
    }
}