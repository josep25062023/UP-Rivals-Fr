package com.example.up_rivals.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.up_rivals.UserRole
import com.example.up_rivals.ui.components.AppBottomNavigationBar
import com.example.up_rivals.ui.screens.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Para simular, creamos una variable de estado para el rol.
    // Cámbiala a VISITOR, PLAYER u ORGANIZER para probar las diferentes interfaces.
    var currentUserRole by remember { mutableStateOf(UserRole.ORGANIZER) }

    // 1. Estado para controlar la visibilidad del diálogo de confirmación
    var showCreateConfirmationDialog by remember { mutableStateOf(false) }

    // 2. El diálogo de confirmación en sí
    if (showCreateConfirmationDialog) {
        AlertDialog(
            onDismissRequest = {
                // Se ejecuta si el usuario presiona afuera del diálogo
                showCreateConfirmationDialog = false
            },
            title = { Text(text = "Confirmación") },
            text = { Text("¿Estás seguro de que deseas crear un nuevo torneo?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCreateConfirmationDialog = false // Cerramos el diálogo
                        navController.navigate("create_tournament_screen") // Navegamos a la pantalla de creación
                    }
                ) {
                    Text("Sí, crear")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showCreateConfirmationDialog = false // Simplemente cerramos el diálogo
                    }
                ) {
                    Text("No")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val screensWithBottomBar = listOf("tournaments_screen", "activities_screen", "teams_screen", "requests_screen")

            // El FAB solo aparece en las pantallas principales y si el usuario no es visitante
            if (currentRoute in screensWithBottomBar && currentUserRole != UserRole.VISITOR) {
                FloatingActionButton(
                    // 3. La acción del botón ahora es solo mostrar el diálogo
                    onClick = { showCreateConfirmationDialog = true }
                ) {
                    Icon(Icons.Filled.Add, "Crear Torneo")
                }
            }
        },
        floatingActionButtonPosition = FabPosition.End,
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            val screensWithBottomBar = listOf("tournaments_screen", "activities_screen", "teams_screen", "requests_screen")

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
            composable("login_screen") { LoginScreen(navController = navController) }
            composable("register_screen") { RegisterScreen(navController = navController) }
            composable("forgot_password_screen") { ForgotPasswordScreen(navController = navController) }
            composable("create_tournament_screen") { CreateTournamentScreen(navController = navController) }

            composable("tournaments_screen") {
                if (currentUserRole == UserRole.ORGANIZER) {
                    MyTournamentsScreen(navController = navController)
                } else {
                    TournamentsScreen()
                }
            }

            composable("activities_screen") { ActivitiesScreen() }
            composable("teams_screen") { TeamsScreen() }
            composable("requests_screen") { RequestsScreen() }
        }
    }
}