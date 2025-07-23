package com.example.up_rivals.ui.screens

import com.valentinilk.shimmer.shimmer
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
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.LaunchedEffect
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
import com.example.up_rivals.viewmodels.TournamentsUiState
import com.example.up_rivals.viewmodels.TournamentsViewModel
// Importaciones para SwipeRefresh
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState
import coil.compose.AsyncImage
import com.example.up_rivals.UserRole
import com.example.up_rivals.viewmodels.ProfileViewModel
import com.example.up_rivals.viewmodels.ProfileUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentsScreen(
    navController: NavController,
    onMenuClick: () -> Unit,
    userRole: UserRole = UserRole.VISITOR // Agregar parámetro userRole
) {
    // 1. Obtenemos el ViewModel y todos los estados que necesitamos
    val viewModel: TournamentsViewModel = viewModel()
    val profileViewModel: ProfileViewModel = viewModel()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val inProgressTournaments by viewModel.inProgressTournaments.collectAsState()
    val upcomingTournaments by viewModel.upcomingTournaments.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val profileUiState by profileViewModel.uiState.collectAsState()

    // 2. Estado para SwipeRefresh
    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = uiState is TournamentsUiState.Loading
    )

    // 3. Refrescar el perfil cuando la pantalla se vuelve visible
    LaunchedEffect(Unit) {
        if (userRole != UserRole.VISITOR) {
            profileViewModel.loadProfile()
        }
    }

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
                    // Solo mostrar botones para usuarios registrados (no visitantes)
                    if (userRole != UserRole.VISITOR) {
                        // Botón de refresh manual
                        IconButton(
                            onClick = {
                                viewModel.loadTournaments()
                                profileViewModel.loadProfile()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Actualizar torneos",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }

                        IconButton(onClick = { navController.navigate("profile_screen") }) {
                            when (val state = profileUiState) {
                                is ProfileUiState.Success -> {
                                    // Debug: Verificar el valor de profilePicture
                                    android.util.Log.d("TournamentsScreen", "Profile picture URL: '${state.user.profilePicture}'")

                                    AsyncImage(
                                        model = state.user.profilePicture?.takeIf { it.isNotBlank() },
                                        contentDescription = "Foto de perfil",
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape),
                                        placeholder = painterResource(id = R.drawable.img_logo),
                                        error = painterResource(id = R.drawable.img_logo),
                                        fallback = painterResource(id = R.drawable.img_logo),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                else -> {
                                    Image(
                                        painter = painterResource(id = R.drawable.img_logo),
                                        contentDescription = "Perfil",
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                    )
                                }
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
                // 3. Barra de búsqueda
                FormTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    labelText = "Buscar torneo",
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )

                // 4. Contenido principal con estados
                when (uiState) {
                    is TournamentsUiState.Loading -> {
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
                    is TournamentsUiState.Error -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = (uiState as TournamentsUiState.Error).message,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.error
                                )
                                IconButton(
                                    onClick = { viewModel.loadTournaments() }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Refresh,
                                        contentDescription = "Reintentar",
                                        tint = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                    is TournamentsUiState.Success -> {
                        LazyColumn(
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            // Sección "En curso"
                            if (inProgressTournaments.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "En curso (${inProgressTournaments.size})",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                                    )
                                }
                                items(inProgressTournaments) { tournament ->
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
                                        onClick = {
                                            navController.navigate("tournament_detail_screen/${tournament.id}/false")
                                        }
                                    )
                                }
                            }

                            // Sección "Próximos"
                            if (upcomingTournaments.isNotEmpty()) {
                                item {
                                    Text(
                                        text = "Próximos (${upcomingTournaments.size})",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.secondary,
                                        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
                                    )
                                }
                                items(upcomingTournaments) { tournament ->
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
                                        onClick = {
                                            navController.navigate("tournament_detail_screen/${tournament.id}/false")
                                        }
                                    )
                                }
                            }

                            // Mensaje cuando no hay torneos
                            if (inProgressTournaments.isEmpty() && upcomingTournaments.isEmpty()) {
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
fun TournamentsScreenPreview(){
    UPRivalsTheme {
        TournamentsScreen(
            navController = rememberNavController(),
            onMenuClick = {}
        )
    }
}