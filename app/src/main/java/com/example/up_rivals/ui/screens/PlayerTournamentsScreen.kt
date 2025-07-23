// En: ui/screens/PlayerTournamentsScreen.kt
package com.example.up_rivals.ui.screens

import com.valentinilk.shimmer.shimmer
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
import androidx.compose.ui.layout.ContentScale
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
import com.example.up_rivals.ui.components.TournamentCardPlaceholder
import com.example.up_rivals.ui.theme.UPRivalsTheme
import com.example.up_rivals.viewmodels.PlayerTournamentsViewModel
import com.example.up_rivals.viewmodels.PlayerTournamentsUiState
// Importaciones para SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import coil.compose.AsyncImage
import com.example.up_rivals.viewmodels.ProfileViewModel
import com.example.up_rivals.viewmodels.ProfileUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerTournamentsScreen(
    navController: NavController,
    onMenuClick: () -> Unit
) {
    // 1. Usamos el nuevo PlayerTournamentsViewModel
    val viewModel: PlayerTournamentsViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val searchQuery by viewModel.searchQuery.collectAsState()
    // 2. Obtenemos las nuevas listas: inscritos y disponibles
    val registeredTournaments by viewModel.registeredTournaments.collectAsState()
    val availableTournaments by viewModel.availableTournaments.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val profileUiState by profileViewModel.uiState.collectAsState()

    // 3. Estado para SwipeRefresh
    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = uiState is PlayerTournamentsUiState.Loading
    )

    // 4. Refrescar el perfil cuando la pantalla se vuelve visible
    LaunchedEffect(Unit) {
        profileViewModel.loadProfile()
    }

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
                        when (val state = profileUiState) {
                            is ProfileUiState.Success -> {
                                AsyncImage(
                                    model = state.user.profilePicture?.takeIf { it.isNotBlank() },
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape),
                                    placeholder = painterResource(id = R.drawable.img_logo2),
                                    error = painterResource(id = R.drawable.img_logo2),
                                    fallback = painterResource(id = R.drawable.img_logo2),
                                    contentScale = ContentScale.Crop
                                )
                            }
                            else -> {
                                Image(
                                    painter = painterResource(id = R.drawable.img_logo2),
                                    contentDescription = "Perfil",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                )
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.loadTournaments() },
            modifier = Modifier.padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 8.dp)
            ) {
                // Barra de búsqueda
                FormTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    labelText = "Buscar torneo",
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // Contenido principal con estados
                when (uiState) {
                    is PlayerTournamentsUiState.Loading -> {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(5) {
                                Box(modifier = Modifier.shimmer()) {
                                    TournamentCardPlaceholder()
                                }
                            }
                        }
                    }
                    is PlayerTournamentsUiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = (uiState as PlayerTournamentsUiState.Error).message,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.error
                                )
                                Button(
                                    onClick = { viewModel.loadTournaments() }
                                ) {
                                    Text("Reintentar")
                                }
                            }
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
                                        text = "Inscritos (${registeredTournaments.size})",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                                    )
                                }
                                items(registeredTournaments) { tournament ->
                                    val imageRes = when (tournament.category.lowercase()) {
                                        "fútbol" -> R.drawable.img_futbol
                                        "básquetbol" -> R.drawable.img_basquetbol
                                        "voleybol" -> R.drawable.img_voleybol
                                        else -> R.drawable.img_logo2
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
                                    Text(
                                        text = "Disponibles (${availableTournaments.size})",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                    )
                                }
                                items(availableTournaments) { tournament ->
                                    val imageRes = when (tournament.category.lowercase()) {
                                        "fútbol" -> R.drawable.img_futbol
                                        "básquetbol" -> R.drawable.img_basquetbol
                                        "voleybol" -> R.drawable.img_voleybol
                                        else -> R.drawable.img_logo2
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

                            // Mensaje cuando no hay torneos
                            if (registeredTournaments.isEmpty() && availableTournaments.isEmpty()) {
                                item {
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
                                            Text(
                                                text = "No hay torneos disponibles",
                                                style = MaterialTheme.typography.titleMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                            Text(
                                                text = "Desliza hacia abajo para actualizar",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
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