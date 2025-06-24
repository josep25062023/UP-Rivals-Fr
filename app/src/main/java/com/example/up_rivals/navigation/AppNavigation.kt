// En: navigation/AppNavigation.kt
package com.example.up_rivals.navigation

// ... (asegúrate de que todos los imports necesarios estén aquí)
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.*
import com.example.up_rivals.UserRole
import com.example.up_rivals.ui.components.AppBottomNavigationBar
import com.example.up_rivals.ui.screens.*
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    var currentUserRole by remember { mutableStateOf(UserRole.VISITOR) }

    // Obtenemos la ruta actual para saber en qué pantalla estamos
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Definimos en qué pantallas SÍ queremos que se vea el menú
    val screensWithBottomBar = listOf(
        "tournaments_screen",
        "activities_screen",
        "teams_screen",
        "requests_screen"
    )

    Scaffold(
        floatingActionButton = {
            // El FAB también será condicional
            if (currentRoute in screensWithBottomBar && currentUserRole != UserRole.VISITOR) {
                FloatingActionButton(
                    onClick = { /* TODO: Acción para crear torneo */ }
                ) {
                    Icon(Icons.Filled.Add, "Crear Torneo")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            // ¡LA CLAVE! El menú solo se mostrará si la ruta actual está en nuestra lista
            if (currentRoute in screensWithBottomBar) {
                BottomAppBar {
                    AppBottomNavigationBar(navController = navController, userRole = currentUserRole)
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "tournaments_screen",
            modifier = Modifier.padding(innerPadding)
        ) {
            // Nuestra pantalla de Login ahora está fuera de la lógica del menú
            composable("login_screen") {
                LoginScreen(navController = navController)
            }
            composable("login_screen") {
                LoginScreen(navController = navController)
            }

// ¡NUEVA RUTA!
            composable("register_screen") {
                RegisterScreen(navController = navController)
            }
            // ... (el resto de las rutas se quedan igual)
            composable("tournaments_screen") { TournamentsScreen() }
            composable("activities_screen") { ActivitiesScreen() }
            composable("teams_screen") { TeamsScreen() }
            composable("requests_screen") { RequestsScreen() }
            composable("forgot_password_screen") {
                ForgotPasswordScreen(navController = navController)
            }
        }
    }
}