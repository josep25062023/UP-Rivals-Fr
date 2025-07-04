// En: ui/screens/PlayerTournamentsScreen.kt
package com.example.up_rivals.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.up_rivals.R
import com.example.up_rivals.ui.components.FormTextField
import com.example.up_rivals.ui.components.TournamentCard
import com.example.up_rivals.ui.theme.UPRivalsTheme
import com.example.up_rivals.viewmodels.PlayerTournamentsViewModel
import com.example.up_rivals.viewmodels.PlayerTournamentsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerTournamentsScreen(
    navController: NavController,
    onMenuClick: () -> Unit
) {
    // 1. Usamos el nuevo PlayerTournamentsViewModel
    val viewModel: PlayerTournamentsViewModel = viewModel()
    val searchQuery by viewModel.searchQuery.collectAsState()
    // 2. Obtenemos las nuevas listas: inscritos y disponibles
    val registeredTournaments by viewModel.registeredTournaments.collectAsState()
    val availableTournaments by viewModel.availableTournaments.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Torneos", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) {
                        Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("profile_screen") }) {
                        Image(
                            painter = painterResource(id = R.drawable.img_logo),
                            contentDescription = "Perfil",
                            modifier = Modifier.size(32.dp).clip(CircleShape)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            FormTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                labelText = "Buscar torneo",
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            when (uiState) {
                is PlayerTournamentsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is PlayerTournamentsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text((uiState as PlayerTournamentsUiState.Error).message)
                    }
                }
                is PlayerTournamentsUiState.Success -> {
                    // 3. Construimos la UI con las secciones correctas
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Sección "Inscritos"
                        if (registeredTournaments.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Inscritos",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            items(registeredTournaments) { tournament ->
                                val imageRes = when (tournament.category.lowercase()) {
                                    "fútbol" -> R.drawable.img_futbol
                                    "básquetbol" -> R.drawable.img_basquetbol
                                    "voleybol" -> R.drawable.img_voleybol
                                    else -> R.drawable.img_logo
                                }
                                TournamentCard(
                                    startDate = tournament.startDate,
                                    endDate = tournament.endDate,
                                    tournamentName = tournament.name,
                                    sport = tournament.category,
                                    imageResId = imageRes,
                                    onClick = { navController.navigate("tournament_detail_screen/${tournament.id}/true") }
                                )
                            }
                        }

                        // Sección "Disponibles"
                        if (availableTournaments.isNotEmpty()) {
                            item {
                                if (registeredTournaments.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                                Text(
                                    text = "Disponibles",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            items(availableTournaments) { tournament ->
                                val imageRes = when (tournament.category.lowercase()) {
                                    "fútbol" -> R.drawable.img_futbol
                                    "básquetbol" -> R.drawable.img_basquetbol
                                    "voleybol" -> R.drawable.img_voleybol
                                    else -> R.drawable.img_logo
                                }
                                TournamentCard(
                                    startDate = tournament.startDate,
                                    endDate = tournament.endDate,
                                    tournamentName = tournament.name,
                                    sport = tournament.category,
                                    imageResId = imageRes,
                                    onClick = { navController.navigate("tournament_detail_screen/${tournament.id}/false") }
                                )
                            }
                        }
                    }
                }
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