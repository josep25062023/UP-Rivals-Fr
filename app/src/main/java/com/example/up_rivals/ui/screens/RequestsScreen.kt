// En: ui/screens/RequestsScreen.kt
package com.example.up_rivals.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.up_rivals.R
import com.example.up_rivals.ui.components.RequestCard
import com.example.up_rivals.ui.theme.UPRivalsTheme

// Modelo de datos de ejemplo para una solicitud
data class TeamRequest(val id: Int, val tournamentName: String, val teamName: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestsScreen(navController: NavController) {
    // --- 1. ESTADOS PARA CONTROLAR LOS DIÁLOGOS ---
    var showAcceptDialog by remember { mutableStateOf(false) }
    var showDeclineDialog by remember { mutableStateOf(false) }
    // Guardamos la solicitud sobre la que se está actuando
    var requestToAction by remember { mutableStateOf<TeamRequest?>(null) }

    val requests = listOf(
        TeamRequest(1, "Fútbol 7", "Toque y pase"),
        TeamRequest(2, "Basketball Mecatrónica", "Los Robots MR"),
        TeamRequest(3, "Volleyball Software", "Team 3")
    )

    Scaffold(
        // --- ¡AQUÍ ESTÁ LA CORRECCIÓN! ---
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Solicitudes", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver atrás")
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(requests) { request ->
                RequestCard(
                    tournamentName = request.tournamentName,
                    teamName = request.teamName,
                    teamImageResId = R.drawable.ic_launcher_background,
                    onAcceptClick = {
                        requestToAction = request
                        showAcceptDialog = true
                    },
                    onDeclineClick = {
                        requestToAction = request
                        showDeclineDialog = true
                    },
                    onCardClick = { navController.navigate("team_detail_screen/${request.id}") }
                )
            }
        }
    }

    // --- 3. DIÁLOGO PARA ACEPTAR ---
    if (showAcceptDialog) {
        AlertDialog(
            onDismissRequest = { showAcceptDialog = false },
            title = { Text("Aceptar Solicitud") },
            text = { Text("¿Estás seguro de que quieres aceptar al equipo '${requestToAction?.teamName}' en el torneo '${requestToAction?.tournamentName}'?") },
            confirmButton = {
                TextButton(onClick = {
                    // TODO: Aquí iría la lógica para aceptar en el backend
                    showAcceptDialog = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { showAcceptDialog = false }) { Text("Cancelar") }
            }
        )
    }

    // --- 4. DIÁLOGO PARA DECLINAR ---
    if (showDeclineDialog) {
        AlertDialog(
            onDismissRequest = { showDeclineDialog = false },
            title = { Text("Declinar Solicitud") },
            text = { Text("¿Estás seguro de que quieres declinar al equipo '${requestToAction?.teamName}'?") },
            confirmButton = {
                TextButton(onClick = {
                    // TODO: Aquí iría la lógica para declinar en el backend
                    showDeclineDialog = false
                }) { Text("Sí, declinar") }
            },
            dismissButton = {
                TextButton(onClick = { showDeclineDialog = false }) { Text("Cancelar") }
            }
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