package com.example.up_rivals.ui.screens
import androidx.annotation.DrawableRes
import com.example.up_rivals.UserRole
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.up_rivals.Match
import com.example.up_rivals.R
import com.example.up_rivals.TeamStanding
import com.example.up_rivals.ui.components.MatchResultItem
import com.example.up_rivals.ui.components.PrimaryButton
import com.example.up_rivals.ui.theme.LightBlueBackground
import com.example.up_rivals.ui.theme.SubtleGrey
import com.example.up_rivals.ui.theme.UPRivalsTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDetailScreen(navController: NavController, userRole: UserRole) {
    // --- ESTADOS PARA CONTROLAR LA UI ---
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Resultados", "Tabla General", "Proximos partidos")
    val isUserRegistered = (userRole == UserRole.PLAYER) // Lógica para saber si mostrar "Unirse" o "Retirarse"
    var showMenu by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(false) }
    var showLeaveDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()


    // --- DIÁLOGOS Y MENSAJES ---
    if (showRulesDialog) {
        AlertDialog(onDismissRequest = { showRulesDialog = false }, title = { Text("Reglamento") }, text = { Text("Aquí se mostrarían las reglas completas del torneo.") }, confirmButton = { TextButton(onClick = { showRulesDialog = false }) { Text("Entendido") } })
    }
    if (showLeaveDialog) {
        AlertDialog(onDismissRequest = { showLeaveDialog = false }, title = { Text("Confirmar Retiro") }, text = { Text("¿Estás seguro de que deseas retirarte de este torneo?") }, confirmButton = { TextButton(onClick = { /* TODO: Lógica de retiro */ showLeaveDialog = false }) { Text("Sí, retirarme") } }, dismissButton = { TextButton(onClick = { showLeaveDialog = false }) { Text("No") } })
    }
    if (showJoinDialog) {
        AlertDialog(onDismissRequest = { showJoinDialog = false }, title = { Text("Confirmar Inscripción") }, text = { Text("Para unirte al torneo, primero necesitas crear un equipo. ¿Deseas continuar?") }, confirmButton = { TextButton(onClick = { showJoinDialog = false; navController.navigate("create_team_screen") }) { Text("Sí, crear equipo") } }, dismissButton = { TextButton(onClick = { showJoinDialog = false }) { Text("No, más tarde") } })
    }


    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Informacion del torneo") },
                navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver atrás") } },
                actions = {
                    Box {
                        IconButton(onClick = { showMenu = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "Más opciones")
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            if (userRole == UserRole.PLAYER) { // Mostramos opciones solo si es un jugador
                                if (isUserRegistered) {
                                    DropdownMenuItem(text = { Text("Ver reglamento") }, onClick = { showRulesDialog = true; showMenu = false })
                                    DropdownMenuItem(text = { Text("Retirarse del torneo") }, onClick = { showLeaveDialog = true; showMenu = false })
                                } else {
                                    DropdownMenuItem(text = { Text("Ver reglamento") }, onClick = { showRulesDialog = true; showMenu = false })
                                    DropdownMenuItem(text = { Text("Unirme al torneo") }, onClick = { showJoinDialog = true; showMenu = false })
                                }
                            } else { // Para visitantes y organizadores
                                DropdownMenuItem(text = { Text("Ver reglamento") }, onClick = { showRulesDialog = true; showMenu = false })
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.padding(innerPadding)
        ) {
            // --- Banner con Pestañas Superpuestas ---
            item {
                Box(contentAlignment = Alignment.BottomCenter) {
                    Image(
                        painter = painterResource(id = R.drawable.img_futbol), // Placeholder
                        contentDescription = "Banner del torneo",
                        modifier = Modifier.fillMaxWidth().height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                    Box(modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))))
                    TabRow(
                        selectedTabIndex = selectedTabIndex,
                        containerColor = Color.Transparent,
                        contentColor = Color.White
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTabIndex == index,
                                onClick = { selectedTabIndex = index },
                                text = { Text(title) }
                            )
                        }
                    }
                }
            }

            // --- Contenido de la pestaña seleccionada ---
            item {
                when (selectedTabIndex) {
                    0 -> ResultsTabContent()
                    1 -> StandingsTabContent(navController = navController)
                    2 -> UpcomingMatchesTabContent(userRole = userRole)
                }
            }
        }
    }
}

// --- Contenido de la Pestaña "Resultados" ---
@Composable
fun ResultsTabContent() {
    val results = listOf(
        Pair("Team A vs. Team B", "10:00 AM"),
        Pair("Team C vs. Team D", "12:00 PM"),
        Pair("Team E vs. Team F", "Final Score: 2-1"),
        Pair("Team G vs. Team H", "Final Score: 0-0")
    )
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Ultimos resultados", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            results.forEach { result ->
                MatchResultItem(
                    teamLogoResId = R.drawable.ic_launcher_background,
                    matchup = result.first,
                    detail = result.second
                )
            }
        }
    }
}

// --- Contenido de la Pestaña "Tabla General" ---
@Composable
fun StandingsTabContent(navController: NavController) {
    val standings = listOf(
        TeamStanding(1, "Toque y pase", 55),
        TeamStanding(2, "Los Robots MR", 52),
        TeamStanding(3, "Team 3", 48),
        TeamStanding(4, "Otro Equipo", 45),
        TeamStanding(5, "Quinto Lugar", 40)
    )
    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio entre cada tarjeta
    ) {
        standings.forEachIndexed { index, team ->
            StandingRow(
                position = index + 1,
                teamLogoResId = R.drawable.ic_launcher_background,
                teamName = team.teamName,
                points = team.points,
                onClick = { navController.navigate("team_detail_screen/${team.teamId}") }
            )
        }
    }
}

// --- Componente de Fila Rediseñado como una Tarjeta Clicable ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StandingRow(
    position: Int,
    @DrawableRes teamLogoResId: Int,
    teamName: String,
    points: Int,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = LightBlueBackground // Usamos nuestro color azul claro
        )
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$position.",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.width(32.dp)
            )
            Image(
                painter = painterResource(id = teamLogoResId),
                contentDescription = "Logo de $teamName",
                modifier = Modifier.size(40.dp).clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = teamName, fontWeight = FontWeight.Bold)
                Text(text = "$points pts.", color = SubtleGrey)
            }
        }
    }
}

// --- Contenido de la Pestaña "Próximos Partidos" (Placeholder) ---
@Composable
fun UpcomingMatchesTabContent(userRole: UserRole) {
    // 1. Estado para guardar la lista de partidos generados
    var upcomingMatches by remember { mutableStateOf<List<Match>>(emptyList()) }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 2. Si la lista está vacía, mostramos una cosa...
        if (upcomingMatches.isEmpty()) {
            // Si es organizador, mostramos el botón
            if (userRole == UserRole.ORGANIZER) {
                Text("Aún no se han generado los partidos para la siguiente jornada.", textAlign = TextAlign.Center)
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(
                    text = "Generar Partidos",
                    onClick = {
                        // 3. Al hacer clic, "generamos" los partidos (usando datos de ejemplo)
                        upcomingMatches = listOf(
                            Match(5, "Fútbol", "Toque y Pase vs. Los Robots MR", "Sábado 10:00 AM"),
                            Match(6, "Fútbol", "Team 3 vs. Otro Equipo", "Sábado 12:00 PM"),
                            Match(7, "Fútbol", "Quinto Lugar vs. Silver Hawks", "Domingo 10:00 AM")
                        )
                    }
                )
            } else {
                // Si es visitante o jugador, mostramos un mensaje
                Text("Los partidos para la siguiente jornada aún no han sido publicados.")
            }
        } else {
            // 4. Si la lista NO está vacía, la mostramos
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Próximos Partidos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    upcomingMatches.forEach { match ->
                        // Reutilizamos el componente MatchResultItem que ya teníamos
                        MatchResultItem(
                            teamLogoResId = R.drawable.ic_launcher_background,
                            matchup = match.teams,
                            detail = match.time
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TournamentDetailScreenPreview() {
    UPRivalsTheme {
        TournamentDetailScreen(rememberNavController(), UserRole.ORGANIZER)
    }
}