package com.example.up_rivals.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.up_rivals.UserRole
import com.example.up_rivals.data.UserPreferencesRepository
import com.example.up_rivals.network.ApiClient
import com.example.up_rivals.network.dto.User
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

    // --- LÓGICA DE SESIÓN ---
    val context = LocalContext.current
    val userPreferencesRepository = remember { UserPreferencesRepository(context) }
    var isLoadingSession by remember { mutableStateOf(true) } // Para mostrar una pantalla de carga

    // Efecto que se ejecuta UNA SOLA VEZ para verificar si hay una sesión guardada
    LaunchedEffect(Unit) {
        val token = userPreferencesRepository.authToken.first()
        if (token.isNullOrBlank()) {
            isLoadingSession = false // No hay token, dejamos de cargar
        } else {
            // Si hay token, intentamos obtener el perfil para validarlo
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
                // Si el token es inválido o hay error de red, el rol se queda como VISITOR
            } finally {
                isLoadingSession = false // Terminamos de cargar
            }
        }
    }

    // --- UI PRINCIPAL ---
    if (isLoadingSession) {
        // Pantalla de carga mientras se verifica la sesión
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        // Una vez verificada la sesión, se muestra la UI principal
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
                // Aquí puedes poner tu FloatingActionButton y tu BottomBar si los necesitas
            ) { innerPadding ->
                NavHost(
                    navController = navController,
                    // Si hay un usuario, empieza en torneos; si no, en login.
                    startDestination = if (currentUserRole == UserRole.VISITOR) "login_screen" else "tournaments_screen",
                    modifier = Modifier.padding(innerPadding)
                ) {
                    composable("login_screen") {
                        LoginScreen(
                            navController = navController,
                            onLoginSuccess = { user ->
                                // Cuando el login es exitoso, actualizamos el rol...
                                currentUserRole = when (user.role.lowercase()) {
                                    "player" -> UserRole.PLAYER
                                    "organizer" -> UserRole.ORGANIZER
                                    else -> UserRole.VISITOR
                                }
                                // ...y navegamos
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

                    // --- TODAS TUS RUTAS ORIGINALES ---
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
}