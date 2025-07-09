// En: ui/screens/TeamsScreen.kt
package com.example.up_rivals.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.up_rivals.R
import com.example.up_rivals.ui.theme.UPRivalsTheme
import com.example.up_rivals.viewmodels.MyTeamsUiState
import com.example.up_rivals.viewmodels.MyTeamsViewModel

// Eliminamos el data class de prueba de este archivo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(navController: NavController) {
    // 1. Obtenemos el ViewModel y su estado
    val viewModel: MyTeamsViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Equipos", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        // 2. Manejamos los estados de Carga, Error y Éxito
        when (val state = uiState) {
            is MyTeamsUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is MyTeamsUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                    Text(state.message)
                }
            }
            is MyTeamsUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.padding(innerPadding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Text(
                            text = "Mis Equipos",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    if (state.teams.isEmpty()) {
                        item {
                            Text("Aún no has creado ni te has unido a ningún equipo.")
                        }
                    } else {
                        // 3. Mostramos la lista real de equipos desde el ViewModel
                        items(state.teams) { team ->
                            TeamInfoCard(
                                teamName = team.teamName,
                                tournamentName = team.tournament.tournamentName,
                                teamLogoUrl = team.teamLogo, // Le pasamos la URL del logo
                                onClick = { navController.navigate("team_detail_screen/${team.teamId}") }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Componente de tarjeta actualizado para usar la URL de la imagen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamInfoCard(
    teamName: String,
    tournamentName: String,
    teamLogoUrl: String?, // Acepta una URL de tipo String (puede ser nula)
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Usamos AsyncImage de Coil para cargar la imagen
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(teamLogoUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(id = R.drawable.img_logo),
                error = painterResource(id = R.drawable.img_logo),
                contentDescription = "Logo de $teamName",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Column(modifier = Modifier.weight(1f)) {
                Text(text = teamName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = tournamentName, style = MaterialTheme.typography.bodyMedium)
            }
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