package com.example.up_rivals.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.up_rivals.viewmodels.MyTournamentsViewModel
import com.example.up_rivals.viewmodels.TournamentsUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyTournamentsScreen(
    navController: NavController,
    onMenuClick: () -> Unit
) {
    // 1. Usamos el nuevo MyTournamentsViewModel
    val viewModel: MyTournamentsViewModel = viewModel()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val inProgressTournaments by viewModel.inProgressTournaments.collectAsState()
    val upcomingTournaments by viewModel.upcomingTournaments.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Mis Torneos", fontWeight = FontWeight.Bold) },
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
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
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
                labelText = "Buscar mis torneos...",
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )

            when (uiState) {
                is TournamentsUiState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                is TournamentsUiState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text((uiState as TournamentsUiState.Error).message)
                    }
                }
                is TournamentsUiState.Success -> {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (inProgressTournaments.isEmpty() && upcomingTournaments.isEmpty()) {
                            item {
                                Box(modifier = Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                                    Text("Aún no has creado ningún torneo.")
                                }
                            }
                        }

                        if (inProgressTournaments.isNotEmpty()) {
                            item {
                                Text(
                                    text = "En curso",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                            }
                            items(inProgressTournaments) { tournament ->
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
                                    onClick = { navController.navigate("tournament_detail_screen/${tournament.id}") }
                                )
                            }
                        }

                        if (upcomingTournaments.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Próximos",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(top = 16.dp)
                                )
                            }
                            items(upcomingTournaments) { tournament ->
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
fun MyTournamentsScreenPreview() {
    UPRivalsTheme {
        MyTournamentsScreen(
            navController = rememberNavController(),
            onMenuClick = {}
        )
    }
}