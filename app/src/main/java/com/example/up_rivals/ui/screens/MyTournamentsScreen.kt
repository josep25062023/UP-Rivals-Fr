package com.example.up_rivals.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.up_rivals.R
import com.example.up_rivals.ui.components.FormTextField
import com.example.up_rivals.ui.components.TournamentCard
import com.example.up_rivals.ui.theme.UPRivalsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTournamentsScreen(navController: NavController) {
    // Datos de ejemplo para los torneos del organizador
    val myTournamentsInProgress = listOf(
        Tournament(1, "Inicia hace 2 días", "Summer Slam", "Tenis")
    )
    val myUpcomingTournaments = listOf(
        Tournament(3, "Finaliza en 3 días", "Basketball League", "Basquetbol"),
        Tournament(4, "Finaliza en 1 semana", "Swimming Championship", "Voleybol")
    )
    var searchText by remember { mutableStateOf("") }

    Scaffold { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ITEM 1: NUESTRO HEADER PERSONALIZADO
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Mis Torneos",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = { navController.navigate("profile_screen") })  {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_background),
                            contentDescription = "Perfil de usuario",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                    }
                }
            }

            // ITEM 2: LA BARRA DE BÚSQUEDA
            item {
                FormTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    labelText = "Buscar torneo",
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
            }

            // ITEM 3: SECCIÓN "EN CURSO"
            // Sección "En curso"
            item {
                Text(text = "En curso", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold) // <-- Añadimos negrita
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(myTournamentsInProgress) { tournament ->
                TournamentCard(
                    startDate = tournament.startDate,
                    tournamentName = tournament.name,
                    sport = tournament.sport,
                    imageResId = R.drawable.img_futbol // Placeholder
                )
            }

            // ITEM 4: SECCIÓN "PRÓXIMOS"
            // Sección "Próximos"
            item {
                Text(text = "Próximos", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(top=16.dp), fontWeight = FontWeight.Bold) // <-- Añadimos negrita
                Spacer(modifier = Modifier.height(8.dp))
            }
            items(myUpcomingTournaments) { tournament ->
                val imageRes = when (tournament.sport.lowercase()) {
                    "basquetbol" -> R.drawable.img_basquetbol
                    "voleybol" -> R.drawable.img_voleybol
                    else -> R.drawable.ic_launcher_background
                }
                TournamentCard(
                    startDate = tournament.startDate,
                    tournamentName = tournament.name,
                    sport = tournament.sport,
                    imageResId = imageRes
                )
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MyTournamentsScreenPreview(){
    UPRivalsTheme {
        MyTournamentsScreen(navController = rememberNavController())
    }
}