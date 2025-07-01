package com.example.up_rivals.navigation

import com.example.up_rivals.network.dto.User
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
import com.example.up_rivals.network.dto.LoginResponse
import com.example.up_rivals.ui.components.AppBottomNavigationBar
import com.example.up_rivals.ui.components.AppDrawerContent
import com.example.up_rivals.ui.screens.*
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun AppNavigation() {
    // ... (casi todo se queda igual)
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    var currentUserRole by remember { mutableStateOf(UserRole.VISITOR) }
    var showCreateConfirmationDialog by remember { mutableStateOf(false) }

    // --- CORREGIDO: onLoginSuccess ya no lee el rol de la respuesta ---
    val onLoginSuccess: (User) -> Unit = { user ->
        // Ahora leemos el rol del objeto 'user' real
        val role = when (user.role.lowercase()) {
            "player" -> UserRole.PLAYER
            "organizer" -> UserRole.ORGANIZER
            else -> UserRole.VISITOR
        }
        currentUserRole = role
    }

    // --- CAMBIO 2: AÑADIMOS UN LaunchedEffect QUE REACCIONA AL CAMBIO DE ROL ---
    // Este bloque se ejecutará automáticamente CADA VEZ que 'currentUserRole' cambie.
    LaunchedEffect(currentUserRole) {
        // Si el rol es de alguien que ha iniciado sesión Y estamos en una pantalla de autenticación...
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        if ((currentUserRole == UserRole.PLAYER || currentUserRole == UserRole.ORGANIZER) && currentRoute == "login_screen") {
            // ...entonces navegamos a la pantalla principal.
            navController.navigate("tournaments_screen") {
                popUpTo("login_screen") { inclusive = true }
            }
        }
    }


    if (showCreateConfirmationDialog) {
        AlertDialog(
            onDismissRequest = { showCreateConfirmationDialog = false },
            title = { Text("Confirmación") },
            text = { Text("¿Estás seguro de que deseas crear un nuevo torneo?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showCreateConfirmationDialog = false
                        navController.navigate("create_tournament_screen")
                    }
                ) { Text("Sí, crear") }
            },
            dismissButton = {
                TextButton(onClick = { showCreateConfirmationDialog = false }) { Text("No") }
            }
        )
    }

    // --- Envolvemos TODO en el ModalNavigationDrawer ---
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            // Le pasamos el rol y la nueva acción de login
            AppDrawerContent(
                userRole = currentUserRole,
                onProfileClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("profile_screen")
                },
                onSettingsClick = { /* TODO */ },
                onLoginClick = {
                    scope.launch { drawerState.close() }
                    navController.navigate("login_screen")
                },
                onLogoutClick = {
                    scope.launch { drawerState.close() }
                    // Al hacer logout, reseteamos el rol a visitante
                    currentUserRole = UserRole.VISITOR
                    navController.navigate("login_screen") { popUpTo(0) }
                }
            )
        }
    ) {
        // El Scaffold ahora vive dentro del NavigationDrawer
        Scaffold(
            floatingActionButton = {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                if (currentRoute == "tournaments_screen" && currentUserRole == UserRole.ORGANIZER) {
                    FloatingActionButton(
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
                val screensWithBottomBar = listOf(
                    "tournaments_screen",
                    "activities_screen",
                    "teams_screen",
                    "requests_screen",
                    "profile_screen"
                )
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
                // ... (rutas de login, register, etc. se quedan igual)
                composable("login_screen") {
                    // --- CAMBIO 2: Le pasamos la nueva función a LoginScreen ---
                    LoginScreen(
                        navController = navController,
                        onLoginSuccess = onLoginSuccess // Le pasamos la función que creamos
                    )
                }

                composable("tournaments_screen") {
                    val onMenuClick: () -> Unit = { scope.launch { drawerState.open() } }
                    when (currentUserRole) {
                        UserRole.ORGANIZER -> MyTournamentsScreen(navController = navController, onMenuClick = onMenuClick)
                        UserRole.PLAYER -> PlayerTournamentsScreen(navController = navController, onMenuClick = onMenuClick)
                        UserRole.VISITOR -> TournamentsScreen(navController = navController, onMenuClick = onMenuClick)
                    }
                }

                // ... (el resto de tus rutas se quedan igual)
                composable("activities_screen") { ActivitiesScreen(navController = navController) }
                composable("teams_screen") { TeamsScreen(navController = navController) }
                composable("requests_screen") { RequestsScreen(navController = navController) }
                composable("profile_screen") { ProfileScreen(navController = navController) }
                composable("create_tournament_screen") { CreateTournamentScreen(navController = navController) }
                composable("tournament_detail_screen/{tournamentId}") { backStackEntry ->
                    TournamentDetailScreen(navController = navController, userRole = currentUserRole)
                }
                composable("register_screen") { RegisterScreen(navController = navController) }
                composable("forgot_password_screen") { ForgotPasswordScreen(navController = navController) }
                composable("create_team_screen") {
                    CreateTeamScreen(navController = navController)
                }
                composable("team_detail_screen/{teamId}") { backStackEntry ->
                    TeamDetailScreen(navController = navController)
                }
                composable("match_detail_screen/{matchId}") { backStackEntry ->
                    MatchDetailScreen(navController = navController)
                }
            }
        }
    }
}