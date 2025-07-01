package com.example.up_rivals.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.up_rivals.UserRole
import com.example.up_rivals.data.UserPreferencesRepository
import com.example.up_rivals.network.ApiClient
import com.example.up_rivals.network.dto.User
import com.example.up_rivals.ui.components.AppBottomNavigationBar
import com.example.up_rivals.ui.components.AppDrawerContent
import com.example.up_rivals.ui.screens.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavigation() {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navController = rememberNavController()
    var currentUserRole by remember { mutableStateOf(UserRole.VISITOR) }
    var showCreateConfirmationDialog by remember { mutableStateOf(false) }

    // --- LÓGICA DE SESIÓN ---
    val context = LocalContext.current
    val userPreferencesRepository = remember { UserPreferencesRepository(context) }
    var isLoadingSession by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        val token = userPreferencesRepository.authToken.first()
        if (token.isNullOrBlank()) {
            isLoadingSession = false
        } else {
            val bearerToken = "Bearer $token"
            try {
                val profileResponse = ApiClient.apiService.getProfile(bearerToken)
                if (profileResponse.isSuccessful && profileResponse.body() != null) {
                    val user = profileResponse.body()!!
                    currentUserRole = when (user.role.lowercase()) {
                        "player" -> UserRole.PLAYER
                        "organizer" -> UserRole.ORGANIZER
                        else -> UserRole.VISITOR
                    }
                }
            } catch (e: Exception) {
                // Si hay error, el rol se queda como VISITOR y se podría limpiar el token
                userPreferencesRepository.clearAuthToken()
            } finally {
                isLoadingSession = false
            }
        }
    }

    // --- DIÁLOGO DE CONFIRMACIÓN (para crear torneo) ---
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

    // --- UI PRINCIPAL ---
    if (isLoadingSession) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
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
                        scope.launch {
                            userPreferencesRepository.clearAuthToken()
                            currentUserRole = UserRole.VISITOR
                            drawerState.close()
                            navController.navigate("login_screen") {
                                popUpTo(navController.graph.startDestinationId) { inclusive = true }
                            }
                        }
                    }
                )
            }
        ) {
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
                    startDestination = if (currentUserRole == UserRole.VISITOR) "login_screen" else "tournaments_screen",
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable("login_screen") {
                        LoginScreen(
                            navController = navController,
                            onLoginSuccess = { user ->
                                currentUserRole = when (user.role.lowercase()) {
                                    "player" -> UserRole.PLAYER
                                    "organizer" -> UserRole.ORGANIZER
                                    else -> UserRole.VISITOR
                                }
                                navController.navigate("tournaments_screen") {
                                    popUpTo("login_screen") { inclusive = true }
                                }
                            }
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

                    composable("activities_screen") { ActivitiesScreen(navController = navController) }
                    composable("teams_screen") { TeamsScreen(navController = navController) }
                    composable("requests_screen") { RequestsScreen(navController = navController) }
                    composable("profile_screen") { ProfileScreen(navController = navController) }
                    composable("create_tournament_screen") { CreateTournamentScreen(navController = navController) }
                    composable("tournament_detail_screen/{tournamentId}") {
                        TournamentDetailScreen(navController = navController, userRole = currentUserRole)
                    }
                    composable("register_screen") { RegisterScreen(navController = navController) }
                    composable("forgot_password_screen") { ForgotPasswordScreen(navController = navController) }
                    composable("create_team_screen") {
                        CreateTeamScreen(navController = navController)
                    }
                    composable("team_detail_screen/{teamId}") {
                        TeamDetailScreen(navController = navController)
                    }
                    composable("match_detail_screen/{matchId}") {
                        MatchDetailScreen(navController = navController)
                    }
                }
            }
        }
    }
}