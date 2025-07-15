// En: ui/screens/ActivitiesScreen.kt
package com.example.up_rivals.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.up_rivals.ui.components.ActivityCard
import com.example.up_rivals.ui.theme.UPRivalsTheme
import com.example.up_rivals.viewmodels.ActivitiesViewModel
import com.example.up_rivals.utils.TokenManager
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(navController: NavController) {
    val viewModel: ActivitiesViewModel = viewModel()
    val pendingMatches by viewModel.pendingMatches.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val context = LocalContext.current

    var userRole by remember { mutableStateOf<UserRole?>(null) }

    // Obtener el rol del usuario y cargar partidos pendientes
    LaunchedEffect(Unit) {
        val token = TokenManager.getToken(context)
        if (token != null) {
            try {
                // Obtener el perfil del usuario para determinar su rol
                val profileResponse = ApiClient.apiService.getProfile("Bearer $token")
                if (profileResponse.isSuccessful) {
                    val user = profileResponse.body()
                    val role = when (user?.role?.lowercase()) {
                        "organizer" -> UserRole.ORGANIZER
                        "player" -> UserRole.PLAYER
                        else -> UserRole.VISITOR
                    }
                    userRole = role

                    // Cargar partidos pendientes con el rol correcto
                    if (role != UserRole.VISITOR) {
                        viewModel.loadPendingMatches(token, role)
                    }
                }
            } catch (e: Exception) {
                // En caso de error, usar rol por defecto
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
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver atrás")
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(), // Eliminar padding superior completamente
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp), // Reducir padding vertical a 4dp
                verticalArrangement = Arrangement.spacedBy(8.dp) // Reducir espaciado entre elementos a 8dp
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
                        modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                    )
                }

                if (pendingMatches.isEmpty()) {
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
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val emptyMessage = when (userRole) {
                                    UserRole.ORGANIZER -> "No hay partidos pendientes de calificar"
                                    UserRole.PLAYER -> "No tienes partidos pendientes"
                                    else -> "No hay actividades disponibles"
                                }
                                Text(
                                    emptyMessage,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    items(pendingMatches) { match ->
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                        val displayFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                        val formattedTime = try {
                            val date = dateFormat.parse(match.date)
                            displayFormat.format(date ?: Date())
                        } catch (e: Exception) {
                            match.date
                        }

                        ActivityCard(
                            sportName = match.tournament.name,
                            teams = "${match.teamA.name} vs ${match.teamB.name}",
                            time = formattedTime,
                            imageResId = R.drawable.img_futbol, // Usar imagen por defecto
                            onClick = {
                                // Navegar a los detalles del torneo para asignar resultado
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
        ActivitiesScreen(rememberNavController())
    }
}