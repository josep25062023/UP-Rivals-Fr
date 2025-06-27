// En: ui/screens/TeamsScreen.kt
package com.example.up_rivals.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.up_rivals.R
import com.example.up_rivals.Team
import com.example.up_rivals.Tournament
import com.example.up_rivals.ui.components.InfoRow
import com.example.up_rivals.ui.theme.UPRivalsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(navController: NavController) {
    // --- Mock Data (Datos de ejemplo) ---
    val myTeams = listOf(
        Team(1, "The Titans", 12, R.drawable.ic_launcher_background),
        Team(2, "The Warriors", 8, R.drawable.ic_launcher_background)
    )
    val myTournaments = listOf(
        Tournament(101, "Summer League", "The Titans", "Fútbol"),
        Tournament(102, "Fall Classic", "The Warriors", "Básquetbol")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Teams", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // --- Sección "Mis Equipos" ---
            item {
                Text("Mis Equipos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            items(myTeams) { team ->
                InfoRow(
                    title = team.name,
                    subtitle = "${team.memberCount} members",
                    imageResId = team.logoResId,
                    onClick = { navController.navigate("team_detail_screen/${team.id}") }
                )
            }

            // --- Sección "Tournaments" ---
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Tournaments", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            items(myTournaments) { tournament ->
                InfoRow(
                    title = tournament.name,
                    subtitle = tournament.sport,
                    imageResId = R.drawable.ic_launcher_background,
                    onClick = { navController.navigate("tournament_detail_screen/${tournament.id}") }
                )
            }

            // --- ¡NUEVA SECCIÓN! "Próximo Partido" ---
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Próximo Partido", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                NextMatchCard(
                    date = "Sabado, 22 de Junio",
                    time = "10:00 AM",
                    teams = "The Titans vs. Silver Hawks",
                    location = "Cancha principal"
                )
            }
        }
    }
}

// Componente para la tarjeta del próximo partido
@Composable
fun NextMatchCard(date: String, time: String, teams: String, location: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = date, style = MaterialTheme.typography.titleMedium)
            Text(text = time, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(text = teams, style = MaterialTheme.typography.bodyLarge)
            Text(text = location, style = MaterialTheme.typography.bodyMedium)
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TeamsScreenPreview() {
    UPRivalsTheme {
        TeamsScreen(rememberNavController())
    }
}