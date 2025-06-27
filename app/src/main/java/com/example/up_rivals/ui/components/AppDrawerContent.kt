// En: ui/components/AppDrawerContent.kt
package com.example.up_rivals.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.up_rivals.UserRole

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDrawerContent(
    userRole: UserRole, // <-- 1. AHORA RECIBE EL ROL
    onProfileClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onLoginClick: () -> Unit,   // <-- 2. NUEVA ACCIÓN para Iniciar Sesión
    onLogoutClick: () -> Unit
) {
    ModalDrawerSheet {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "UP-Rivals",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 20.dp)
            )
            Divider()
            Spacer(modifier = Modifier.height(16.dp))

            // --- 3. LÓGICA CONDICIONAL ---
            // Usamos un 'if' para decidir qué botones mostrar
            if (userRole == UserRole.VISITOR) {
                // Opciones para el Visitante
                NavigationDrawerItem(
                    label = { Text("Iniciar Sesión") },
                    icon = { Icon(Icons.AutoMirrored.Filled.Login, contentDescription = "Iniciar Sesión") },
                    selected = false,
                    onClick = onLoginClick
                )
            } else {
                // Opciones para Jugador y Organizador (usuarios con sesión iniciada)
                NavigationDrawerItem(
                    label = { Text("Mi Perfil") },
                    icon = { Icon(Icons.Outlined.AccountCircle, contentDescription = "Mi Perfil") },
                    selected = false,
                    onClick = onProfileClick
                )
                NavigationDrawerItem(
                    label = { Text("Configuración") },
                    icon = { Icon(Icons.Outlined.Settings, contentDescription = "Configuración") },
                    selected = false,
                    onClick = onSettingsClick
                )
                NavigationDrawerItem(
                    label = { Text("Cerrar Sesión") },
                    icon = { Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = "Cerrar Sesión") },
                    selected = false,
                    onClick = onLogoutClick
                )
            }
        }
    }
}