package com.example.up_rivals.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Login
import androidx.compose.material.icons.outlined.MailOutline
import androidx.compose.material.icons.outlined.People
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.up_rivals.BottomNavItem
import com.example.up_rivals.UserRole
import com.example.up_rivals.ui.theme.PrimaryBlue
import com.example.up_rivals.ui.theme.SubtleGrey

@Composable
fun AppBottomNavigationBar(
    navController: NavController,
    userRole: UserRole
) {
    // Definimos las listas de opciones para cada rol
    val visitorItems = listOf(
        BottomNavItem("Torneos", Icons.Outlined.EmojiEvents, "tournaments_screen"),
        BottomNavItem("Iniciar sesión", Icons.Outlined.Login, "login_screen")
    )

    val playerItems = listOf(
        BottomNavItem("Torneos", Icons.Outlined.EmojiEvents, "tournaments_screen"),
        BottomNavItem("Actividades", Icons.Outlined.CalendarMonth, "activities_screen"),
        BottomNavItem("Equipos", Icons.Outlined.People, "teams_screen")
    )

    val organizerItems = listOf(
        BottomNavItem("Torneos", Icons.Outlined.EmojiEvents, "tournaments_screen"),
        BottomNavItem("Actividades", Icons.Outlined.CalendarMonth, "activities_screen"),
        BottomNavItem("Equipos", Icons.Outlined.People, "teams_screen"),
        BottomNavItem("Solicitudes", Icons.Outlined.MailOutline, "requests_screen")
    )

    // Decidimos qué lista de items usar basado en el rol
    val itemsToShow = when (userRole) {
        UserRole.VISITOR -> visitorItems
        UserRole.PLAYER -> playerItems
        UserRole.ORGANIZER -> organizerItems
    }

    // Construimos la barra de navegación
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface // Fondo suave que se adapta al tema
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        itemsToShow.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = { Text(item.label) },
                alwaysShowLabel = true, // Siempre muestra el texto
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue, // Color del ícono seleccionado
                    selectedTextColor = PrimaryBlue, // Color del texto seleccionado
                    unselectedIconColor = SubtleGrey, // Color del ícono no seleccionado
                    unselectedTextColor = SubtleGrey, // Color del texto no seleccionado
                    indicatorColor = MaterialTheme.colorScheme.surface // Color del indicador de fondo
                )
            )
        }
    }
}