// En: ui/screens/ActivitiesScreen.kt
package com.example.up_rivals.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.up_rivals.Match
import com.example.up_rivals.R
import com.example.up_rivals.ui.components.ActivityCard
import com.example.up_rivals.ui.theme.UPRivalsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivitiesScreen(navController: NavController) {
    // --- Mock Data (Datos de ejemplo) ---
    val inProgressMatches = listOf(
        Match(1, "Soccer Match", "Team A vs. Team B", "10:00 AM"),
        Match(2, "Basketball Game", "Team C vs. Team D", "12:00 PM")
    )
    val completedMatches = listOf(
        Match(3, "Soccer Match", "Team A vs. Team B", "10:00 AM"),
        Match(4, "Basketball Game", "Team C vs. Team D", "12:00 PM")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Actividades", fontWeight = FontWeight.Bold) },
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // --- Sección "En proceso" ---
            item {
                Text("En proceso", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            items(inProgressMatches) { match ->
                ActivityCard(
                    sportName = match.sportName,
                    teams = match.teams,
                    time = match.time,
                    imageResId = if (match.sportName.contains("Soccer")) R.drawable.img_futbol else R.drawable.img_basquetbol,
                    // Le pasamos el ID del partido en la ruta
                    onClick = { navController.navigate("match_detail_screen/${match.id}") }
                )
            }

            // --- Sección "Completados" ---
            item {
                Spacer(modifier = Modifier.height(16.dp))
                Text("Completados", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            items(completedMatches) { match ->
                ActivityCard(
                    sportName = match.sportName,
                    teams = match.teams,
                    time = match.time,
                    imageResId = if (match.sportName.contains("Soccer")) R.drawable.img_futbol else R.drawable.img_basquetbol,
                    onClick = { navController.navigate("match_detail_screen/${match.id}") }
                )
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