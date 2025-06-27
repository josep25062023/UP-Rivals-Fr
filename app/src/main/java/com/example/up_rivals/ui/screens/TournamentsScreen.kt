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
fun TournamentsScreen(
    navController: NavController,
    onMenuClick: () -> Unit // <-- 1. CAMBIO: Recibe una función para abrir el menú
) {
    val tournaments = listOf(
        Tournament(1, "Inicia hace 2 días", "Copa Inter-Facultades", "Futbol"),
        Tournament(2, "Hoy", "Torneo Relámpago", "Basquetbol"),
        Tournament(3, "Próximo Sábado", "Duelo de Remates", "Voleybol")
    )
    var searchText by remember { mutableStateOf("") }

    Scaffold(
        // --- 2. CAMBIO: Añadimos la nueva barra superior ---
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("UP-Rivals", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onMenuClick) { // Llama a la función para abrir el menú
                        Icon(Icons.Default.Menu, contentDescription = "Abrir menú")
                    }
                },
                actions = {
                    IconButton(onClick = { navController.navigate("profile_screen") }) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_background), // Placeholder
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
            contentPadding = PaddingValues(all = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // --- 3. CAMBIO: Ya no necesitamos el título aquí, lo quitamos ---

            // Barra de búsqueda
            item {
                FormTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    labelText = "Buscar torneo",
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                )
            }

            // Sección "Inscritos" y "Disponibles" (o "En curso")
            item {
                Text(
                    text = "Inscritos", // O el texto que prefieras
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            items(tournaments) { tournament ->
                val imageRes = when (tournament.sport.lowercase()) {
                    "futbol" -> R.drawable.img_futbol
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
fun TournamentsScreenPreview(){
    UPRivalsTheme {
        // 4. CAMBIO: La preview necesita la nueva función, le pasamos una acción vacía
        TournamentsScreen(
            navController = rememberNavController(),
            onMenuClick = {}
        )
    }
}