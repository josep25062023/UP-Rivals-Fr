// En: ui/screens/PlayerTournamentsScreen.kt
package com.example.up_rivals.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.up_rivals.R
import com.example.up_rivals.Tournament
import com.example.up_rivals.ui.components.FormTextField
import com.example.up_rivals.ui.components.TournamentCard
import com.example.up_rivals.ui.theme.UPRivalsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerTournamentsScreen(
    navController: NavController,
    onMenuClick: () -> Unit
) {
    // --- Mock Data (Datos de ejemplo) ---
    val registeredTournaments = listOf(
        Tournament(1, "Inicia hace 2 días", "Summer Slam", "Tenis"),
        Tournament(2, "Hoy", "City Marathon", "Atletismo")
    )
    val availableTournaments = listOf(
        Tournament(3, "Finaliza en 3 días", "Basketball League", "Basquetbol"),
        Tournament(4, "Finaliza en 1 semana", "Swimming Championship", "Voleybol")
    )
    var searchText by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("UP-Rivals", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("profile_screen") }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_background),
                            contentDescription = "Perfil",
                            modifier = Modifier.size(32.dp).clip(CircleShape)
                        )
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
            // Barra de búsqueda
            item {
                FormTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    labelText = "Buscar torneo",
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
            }

            // --- Sección "Inscritos" ---
            item {
                Text("Inscritos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            items(registeredTournaments) { tournament ->
                TournamentCard(
                    startDate = tournament.startDate,
                    tournamentName = tournament.name,
                    sport = tournament.sport,
                    imageResId = R.drawable.img_futbol, // Placeholder
                    onClick = { navController.navigate("tournament_detail_screen/${tournament.id}") }
                )
            }

            // --- Sección "Disponibles" ---
            item {
                Text("Disponibles", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            items(availableTournaments) { tournament ->
                val imageRes = when (tournament.sport.lowercase()) {
                    "basquetbol" -> R.drawable.img_basquetbol
                    "voleybol" -> R.drawable.img_voleybol
                    else -> R.drawable.ic_launcher_background
                }
                TournamentCard(
                    startDate = tournament.startDate,
                    tournamentName = tournament.name,
                    sport = tournament.sport,
                    imageResId = imageRes,
                    onClick = { navController.navigate("tournament_detail_screen/${tournament.id}") }
                )
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun PlayerTournamentsScreenPreview() {
    UPRivalsTheme {
        PlayerTournamentsScreen(navController = rememberNavController(), onMenuClick = {})
    }
}