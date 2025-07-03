// En: ui/screens/RequestsScreen.kt
package com.example.up_rivals.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.up_rivals.network.dto.InscriptionRequestDto
import com.example.up_rivals.ui.components.RequestCard
import com.example.up_rivals.ui.theme.UPRivalsTheme
import com.example.up_rivals.viewmodels.InscriptionsViewModel
import com.example.up_rivals.viewmodels.InscriptionsUiState
import com.example.up_rivals.viewmodels.UiEvent
import kotlinx.coroutines.flow.collectLatest

// Eliminamos el data class de prueba de este archivo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(navController: NavController) {
    // --- 1. Obtenemos el ViewModel y los estados ---
    val viewModel: InscriptionsViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Los estados para los diálogos ahora usarán nuestro DTO real
    var showAcceptDialog by remember { mutableStateOf(false) }
    var showDeclineDialog by remember { mutableStateOf(false) }
    var requestToAction by remember { mutableStateOf<InscriptionRequestDto?>(null) }

    // --- 2. Efecto para escuchar eventos (Toasts) ---
    LaunchedEffect(key1 = Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Solicitudes", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        // --- 3. Manejamos los estados de Carga, Error y Éxito ---
        when (val state = uiState) {
            is InscriptionsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is InscriptionsUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(state.message)
                }
            }
            is InscriptionsUiState.Success -> {
                if (state.requests.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No hay solicitudes pendientes.")
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.padding(innerPadding),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(state.requests) { request ->
                            RequestCard(
                                tournamentName = request.tournament.name,
                                teamName = request.team.name,
                                teamLogoUrl = request.team.logo,
                                onAcceptClick = {
                                    requestToAction = request
                                    showAcceptDialog = true
                                },
                                onDeclineClick = {
                                    requestToAction = request
                                    showDeclineDialog = true
                                },
                                onCardClick = { navController.navigate("team_detail_screen/${request.team.id}") }
                            )
                        }
                    }
                }
            }
            is InscriptionsUiState.Idle -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Cargando solicitudes...")
                }
            }
        }
    }

    // --- 4. Diálogos conectados al ViewModel ---
    if (showAcceptDialog) {
        AlertDialog(
            onDismissRequest = { showAcceptDialog = false },
            title = { Text("Aceptar Solicitud") },
            text = { Text("¿Estás seguro de que quieres aceptar al equipo '${requestToAction?.team?.name}' en el torneo '${requestToAction?.tournament?.name}'?") },
            confirmButton = {
                TextButton(onClick = {
                    requestToAction?.let {
                        viewModel.approveInscription(tournamentId = it.tournament.id, teamId = it.team.id)
                    }
                    showAcceptDialog = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { showAcceptDialog = false }) { Text("Cancelar") } }
        )
    }

    if (showDeclineDialog) {
        AlertDialog(
            onDismissRequest = { showDeclineDialog = false },
            title = { Text("Declinar Solicitud") },
            text = { Text("¿Estás seguro de que quieres declinar al equipo '${requestToAction?.team?.name}'?") },
            confirmButton = {
                TextButton(onClick = {
                    requestToAction?.let {
                        viewModel.rejectInscription(tournamentId = it.tournament.id, teamId = it.team.id)
                    }
                    showDeclineDialog = false
                }) { Text("Sí, declinar") }
            },
            dismissButton = { TextButton(onClick = { showDeclineDialog = false }) { Text("Cancelar") } }
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RequestsScreenPreview() {
    UPRivalsTheme {
        RequestsScreen(rememberNavController())
    }
}