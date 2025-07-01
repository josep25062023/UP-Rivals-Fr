// En: data/UserPreferencesRepository.kt
package com.example.up_rivals.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Creamos la instancia de DataStore
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_preferences")

class UserPreferencesRepository(
    private val context: Context
) {
    // Creamos una llave para guardar nuestro token de autenticación.
    // Es como la clave en un diccionario.
    private object Keys {
        val AUTH_TOKEN = stringPreferencesKey("auth_token")
    }

    // Un "Flow" es un flujo de datos que emite un valor cada vez que cambia.
    // Aquí, nos dará el token cada vez que se actualice (o null si no hay).
    val authToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[Keys.AUTH_TOKEN]
        }

    // Esta función suspende (se ejecuta en segundo plano) para guardar el token.
    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[Keys.AUTH_TOKEN] = token
        }
    }

    // Esta función borra el token, la usaremos para el "Cerrar Sesión".
    suspend fun clearAuthToken() {
        context.dataStore.edit { preferences ->
            preferences.remove(Keys.AUTH_TOKEN)
        }
    }
}