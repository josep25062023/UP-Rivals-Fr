// En: ui/screens/TeamDetailScreen.kt
package com.example.up_rivals.ui.screens
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.example.up_rivals.TeamMember
import com.example.up_rivals.ui.components.StatCard
import com.example.up_rivals.ui.theme.SubtleGrey
import com.example.up_rivals.ui.theme.UPRivalsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(navController: NavController) {
    // Estado para saber qué pestaña está seleccionada (0 = Información, 1 = Integrantes)
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Informacion", "Integrantes")

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Torneo futbol") }, // Esto será dinámico más adelante
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
            horizontalAlignment = Alignment.CenterHorizontally,
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            // --- Header del Equipo ---
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_launcher_background), // Placeholder del logo
                        contentDescription = "Logo del Equipo",
                        modifier = Modifier.size(90.dp).clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Golden Eagles", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                    Text("Division / Section 3")
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // --- Pestañas (Tabs) ---
            item {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title) }
                        )
                    }
                }
            }

            // --- Contenido de la pestaña seleccionada ---
            item {
                when (selectedTabIndex) {
                    0 -> InformationTabContent()
                    1 -> MembersTabContent() // <-- Ahora esta función tendrá contenido real
                }
            }
        }
    }
}

// --- Contenido de la Pestaña "Información" ---
@Composable
fun InformationTabContent() {
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Cuadrícula de estadísticas
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(label = "Triunfos", value = "15", modifier = Modifier.weight(1f))
            StatCard(label = "Puntos", value = "45", modifier = Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            StatCard(label = "Derrotas", value = "5", modifier = Modifier.weight(1f))
            StatCard(label = "Partidos Jugados", value = "20", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Ultimos Partidos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

        // Lista de últimos partidos
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            MatchResultRow("Golden Eagles vs. Silver Hawks", "Home", "3-1")
            MatchResultRow("Golden Eagles vs. Bronze Lions", "Away", "2-2")
            MatchResultRow("Golden Eagles vs. Crimson Falcons", "Home", "4-0")
        }
    }
}

@Composable
fun MatchResultRow(matchup: String, venue: String, result: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = matchup, fontWeight = FontWeight.Bold)
            Text(text = venue, style = MaterialTheme.typography.bodySmall)
        }
        Text(text = result, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
    }
}

// --- Contenido de la Pestaña "Integrantes" (por ahora, un placeholder) ---
@Composable
fun MembersTabContent() {
    // Creamos datos de ejemplo para los miembros del equipo
    val members = listOf(
        TeamMember(1, "Christian Josep Toledo Villarreal", "223260@ids.upchiapas.edu.mx"),
        TeamMember(2, "Jhair Alejandro Cruz Palacios", "223261@ids.upchiapas.edu.mx"),
        TeamMember(3, "Tercer Integrante de Prueba", "223262@ids.upchiapas.edu.mx"),
        TeamMember(4, "Cuarto Integrante de Prueba", "223263@ids.upchiapas.edu.mx")
    )

    // Usamos un Column normal porque la pantalla entera ya es un LazyColumn (deslizable)
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        members.forEach { member ->
            MemberRow(member = member)
            Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f))
        }
    }
}

// Componente pequeño para dibujar cada fila de la lista de miembros
@Composable
private fun MemberRow(member: TeamMember) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = member.name, fontWeight = FontWeight.Bold)
        Text(text = member.email, style = MaterialTheme.typography.bodyMedium, color = SubtleGrey)
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TeamDetailScreenPreview() {
    UPRivalsTheme {
        TeamDetailScreen(rememberNavController())
    }
}