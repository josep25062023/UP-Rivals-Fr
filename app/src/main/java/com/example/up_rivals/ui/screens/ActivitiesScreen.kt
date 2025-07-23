// En: ui/screens/ActivitiesScreen.kt
package com.example.up_rivals.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.up_rivals.R
import com.example.up_rivals.UserRole
import com.example.up_rivals.network.ApiClient
import com.example.up_rivals.ui.components.MatchCard
import com.example.up_rivals.ui.components.MatchStatus
import com.example.up_rivals.ui.theme.UPRivalsTheme
import com.example.up_rivals.viewmodels.ActivitiesViewModel
import com.example.up_rivals.utils.TokenManager
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(
    navController: NavController,
    onMenuClick: () -> Unit
) {
    val viewModel: ActivitiesViewModel = viewModel()
    val pendingMatches by viewModel.pendingMatches.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    var userRole by remember { mutableStateOf<UserRole?>(null) }

    // Estado para SwipeRefresh
    val swipeRefreshState = rememberSwipeRefreshState(isRefreshing = isLoading)

    // Obtener el rol del usuario y cargar partidos pendientes
    LaunchedEffect(Unit) {
        val token = TokenManager.getToken(context)
        if (token != null) {
            try {
                val profileResponse = ApiClient.apiService.getProfile("Bearer $token")
                if (profileResponse.isSuccessful) {
                    val user = profileResponse.body()
                    val role = when (user?.role?.lowercase()) {
                        "organizer" -> UserRole.ORGANIZER
                        "player" -> UserRole.PLAYER
                        else -> UserRole.VISITOR
                    }
                    userRole = role

                    if (role != UserRole.VISITOR) {
                        viewModel.loadPendingMatches(token, role)
                    }
                }
            } catch (e: Exception) {
                userRole = UserRole.VISITOR
            }
        }
    }

    // Mostrar errores como Toast
    LaunchedEffect(error) {
        error?.let {
            Toast.makeText(context, it, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Actividades", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, "Abrir menú")
                    }
                }
            )
        }
    ) { innerPadding ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = {
                val token = TokenManager.getToken(context)
                if (token != null && userRole != null && userRole != UserRole.VISITOR) {
                    viewModel.loadPendingMatches(token, userRole!!)
                }
            }
        ) {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    val titleText = when (userRole) {
                        UserRole.ORGANIZER -> "Partidos Pendientes de Calificar"
                        UserRole.PLAYER -> "Mis Partidos Pendientes"
                        else -> "Actividades"
                    }
                    Text(
                        titleText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                if (pendingMatches.isEmpty() && !isLoading) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant
                            )
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val emptyMessage = when (userRole) {
                                        UserRole.ORGANIZER -> "No hay partidos pendientes de calificar"
                                        UserRole.PLAYER -> "No tienes partidos pendientes"
                                        else -> "No hay actividades disponibles"
                                    }
                                    Text(
                                        emptyMessage,
                                        style = MaterialTheme.typography.titleMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        "Desliza hacia abajo para actualizar",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                } else {
                    items(pendingMatches) { match ->
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                        val displayFormat = SimpleDateFormat("HH:mm - dd/MM/yyyy", Locale.getDefault())
                        val formattedTime = try {
                            val date = dateFormat.parse(match.date)
                            displayFormat.format(date ?: Date())
                        } catch (e: Exception) {
                            match.date
                        }

                        // Usar una imagen por defecto ya que no tenemos acceso a la categoría
                        val imageRes = R.drawable.img_logo2

                        MatchCard(
                            tournamentName = match.tournament.name,
                            teamA = match.teamA.name,
                            teamB = match.teamB.name,
                            matchTime = formattedTime,
                            sport = "Deporte", // Texto genérico ya que no tenemos acceso a la categoría
                            imageResId = imageRes,
                            status = MatchStatus.PENDING,
                            onClick = {
                                navController.navigate("tournament_detail/${match.tournament.id}")
                            }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ActivitiesScreenPreview() {
    UPRivalsTheme {
        ActivitiesScreen(rememberNavController(), onMenuClick = {})
    }
}