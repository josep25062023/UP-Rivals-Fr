// En: ui/screens/TournamentsScreen.kt
package com.example.up_rivals.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.up_rivals.R // Importante para acceder a R.drawable
import com.example.up_rivals.ui.components.FormTextField
import com.example.up_rivals.ui.components.TournamentCard

// El modelo de datos ya no necesita la URL
data class Tournament(val id: Int, val startDate: String, val name: String, val sport: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentsScreen() {
    val tournaments = listOf(
        Tournament(1, "Inicia hace 2 días", "Copa Inter-Facultades", "Futbol"),
        Tournament(2, "Hoy", "Torneo Relámpago", "Basquetbol"),
        Tournament(3, "Próximo Sábado", "Duelo de Remates", "Voleybol")
    )
    var searchText by remember { mutableStateOf("") }

    Scaffold { innerPadding ->
        LazyColumn(
            // El padding del scaffold ahora se aplica a todo el contenido
            modifier = Modifier.padding(innerPadding),
            // Y añadimos nuestro propio padding horizontal
            contentPadding = PaddingValues(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Ponemos la barra superior como el PRIMER item de la lista
            item {
                // Reemplazamos el TopAppBar por un Text con padding y alineación manual
                Text(
                    text = "Torneos",
                    style = MaterialTheme.typography.titleLarge, // Usamos un estilo grande de nuestro tema
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth() // Ocupa todo el ancho para poder centrarse
                       , // <-- ¡CONTROL TOTAL! Cambia este valor a tu gusto
                    textAlign = TextAlign.Center // Centramos el texto
                )
            }

            // El buscador ahora es el segundo item
            item {
                FormTextField(
                    value = searchText,
                    onValueChange = { searchText = it },
                    labelText = "Buscar torneo",
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Icono de búsqueda") },
                    modifier = Modifier.padding(vertical = 8.dp)
                )
                Text(text = "En curso", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
            }

            items(tournaments) { tournament ->
                // Lógica para elegir la imagen correcta según el deporte
                val imageRes = when (tournament.sport.lowercase()) {
                    "futbol" -> R.drawable.img_futbol
                    "basquetbol" -> R.drawable.img_basquetbol
                    "voleybol" -> R.drawable.img_voleybol
                    else -> R.drawable.ic_launcher_background // Imagen por defecto
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