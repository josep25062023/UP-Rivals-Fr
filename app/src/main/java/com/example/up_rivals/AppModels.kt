package com.example.up_rivals

import androidx.compose.ui.graphics.vector.ImageVector

// 1. Definimos los posibles roles de usuario con un enum.
enum class UserRole {
    VISITOR,
    PLAYER,
    ORGANIZER
}

// 2. Definimos la estructura de un item del menú de navegación.
// Asegúrate de que este 'data class' esté aquí y no en otro archivo.
data class BottomNavItem(
    val label: String,          // El texto que se muestra (ej. "Torneos")
    val icon: ImageVector,      // El ícono que se muestra
    val route: String           // La ruta a la que navega (ej. "home_screen")

)

data class TeamMember(val id: Int, val name: String, val email: String)
data class Match(val id: Int, val sportName: String, val teams: String, val time: String)