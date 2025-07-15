package com.example.up_rivals.ui.screens

import android.widget.Toast
import androidx.annotation.DrawableRes
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.up_rivals.R
import com.example.up_rivals.UserRole
import com.example.up_rivals.network.dto.MatchDto
import com.example.up_rivals.network.dto.StandingDto
import com.example.up_rivals.network.dto.Tournament
import com.example.up_rivals.ui.components.PrimaryButton
import com.example.up_rivals.ui.theme.LightBlueBackground
import com.example.up_rivals.ui.theme.SubtleGrey
import com.example.up_rivals.ui.theme.UPRivalsTheme
import com.example.up_rivals.viewmodels.*
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

// --- COMPONENTE PRINCIPAL DE LA PANTALLA ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDetailScreen(
    navController: NavController,
    userRole: UserRole,
    tournamentId: String,
    isRegistered: Boolean
) {
    val viewModel: TournamentDetailViewModel = viewModel()
    val detailState by viewModel.uiState.collectAsState()
    val standingsState by viewModel.standingsUiState.collectAsState()
    val matchesState by viewModel.matchesUiState.collectAsState()
    val context = LocalContext.current

    var selectedTabIndex by remember { mutableStateOf(0) }
    var showMenu by remember { mutableStateOf(false) }
    var showRulesDialog by remember { mutableStateOf(false) }
    var showLeaveDialog by remember { mutableStateOf(false) }
    var showJoinDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = tournamentId) {
        viewModel.loadTournamentDetails(tournamentId, userRole)
        viewModel.loadMatches(tournamentId)
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.eventFlow.collectLatest { event: DetailScreenEvent ->
            when (event) {
                is DetailScreenEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
                is DetailScreenEvent.DeletionSuccess -> {
                    Toast.makeText(context, "Torneo eliminado", Toast.LENGTH_SHORT).show()
                    navController.popBackStack()
                }

                else -> {
                }
            }
        }
    }

    when (val state = detailState) {
        is TournamentDetailUiState.Loading -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        }
        is TournamentDetailUiState.Error -> {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { Text(state.message) }
        }
        is TournamentDetailUiState.Success -> {
            val tournament = state.tournament

            if (showRulesDialog) {
                AlertDialog(onDismissRequest = { showRulesDialog = false }, title = { Text("Reglamento") }, text = { Text(tournament.rules) }, confirmButton = { TextButton(onClick = { showRulesDialog = false }) { Text("Entendido") } })
            }
            if (showJoinDialog) {
                AlertDialog(onDismissRequest = { showJoinDialog = false }, title = { Text("Confirmar Inscripción") }, text = { Text("Para unirte al torneo, primero necesitas crear un equipo. ¿Deseas continuar?") }, confirmButton = { TextButton(onClick = { showJoinDialog = false; navController.navigate("create_team_screen/${tournament.id}") }) { Text("Sí, crear equipo") } }, dismissButton = { TextButton(onClick = { showJoinDialog = false }) { Text("No, más tarde") } })
            }
            if (showDeleteDialog) {
                AlertDialog(onDismissRequest = { showDeleteDialog = false }, title = { Text("Confirmar Eliminación") }, text = { Text("¿Estás seguro de que deseas eliminar el torneo '${tournament.name}'? Esta acción no se puede deshacer.") }, confirmButton = { TextButton(onClick = { viewModel.deleteTournament(tournamentId); showDeleteDialog = false }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) { Text("Sí, eliminar") } }, dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancelar") } })
            }
            if (showLeaveDialog) {
                AlertDialog(onDismissRequest = { showLeaveDialog = false }, title = { Text("Confirmar Retiro") }, text = { Text("¿Estás seguro de que deseas retirarte de este torneo?") }, confirmButton = { TextButton(onClick = { /* TODO */ showLeaveDialog = false }) { Text("Sí, retirarme") } }, dismissButton = { TextButton(onClick = { showLeaveDialog = false }) { Text("No") } })
            }

            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = { Text(tournament.name) },
                        navigationIcon = { IconButton(onClick = { navController.popBackStack() }) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver atrás") } },
                        actions = {
                            Box {
                                IconButton(onClick = { showMenu = true }) { Icon(Icons.Default.MoreVert, "Más opciones") }
                                DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                    DropdownMenuItem(text = { Text("Ver reglamento") }, onClick = { showRulesDialog = true; showMenu = false })
                                    if (userRole == UserRole.PLAYER) {
                                        if (isRegistered) {
                                            DropdownMenuItem(text = { Text("Retirarse del torneo") }, onClick = { showLeaveDialog = true; showMenu = false })
                                        } else {
                                            DropdownMenuItem(text = { Text("Unirme al torneo") }, onClick = { showJoinDialog = true; showMenu = false })
                                        }
                                    }
                                    if (userRole == UserRole.ORGANIZER) {
                                        DropdownMenuItem(text = { Text("Eliminar Torneo") }, onClick = { showDeleteDialog = true; showMenu = false })
                                    }
                                }
                            }
                        }
                    )
                }
            ) { innerPadding ->
                LazyColumn(modifier = Modifier.padding(innerPadding)) {
                    item {
                        BannerAndTabs(
                            tournament = tournament,
                            selectedTabIndex = selectedTabIndex,
                            onTabClick = { index ->
                                selectedTabIndex = index
                                when (index) {
                                    0, 2 -> viewModel.loadMatches(tournamentId)
                                    1 -> viewModel.loadStandings(tournamentId)
                                }
                            }
                        )
                    }
                    if (userRole == UserRole.VISITOR) {
                        item {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                PrimaryButton(
                                    text = "Inicia Sesión o Regístrate para Unirte",
                                    onClick = { navController.navigate("login_screen") }
                                )
                            }
                        }
                    }
                    item {
                        when (selectedTabIndex) {
                            0 -> ResultsTabContent(
                                state = matchesState,
                                userRole = userRole,
                                onUpdateResult = { matchId, teamAScore, teamBScore ->
                                    viewModel.updateMatchResult(matchId, teamAScore, teamBScore)
                                }
                            )
                            1 -> StandingsTabContent(navController = navController, state = standingsState)
                            2 -> UpcomingMatchesTabContent(
                                state = matchesState,
                                userRole = userRole,
                                onGenerateClick = { viewModel.generateSchedule(tournamentId) },
                                onUpdateResult = { matchId, teamAScore, teamBScore ->
                                    viewModel.updateMatchResult(matchId, teamAScore, teamBScore)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- SECCIÓN DE PESTAÑAS (TABS) ---

@Composable
fun BannerAndTabs(tournament: Tournament, selectedTabIndex: Int, onTabClick: (Int) -> Unit) {
    val tabs = listOf("Resultados", "Tabla General", "Partidos")
    Box(contentAlignment = Alignment.BottomCenter) {
        val imageRes = when (tournament.category.lowercase()) {
            "fútbol" -> R.drawable.img_futbol
            "básquetbol" -> R.drawable.img_basquetbol
            "voleybol" -> R.drawable.img_voleybol
            else -> R.drawable.img_logo
        }
        Image(painter = painterResource(id = imageRes), contentDescription = "Banner del torneo", modifier = Modifier.fillMaxWidth().height(200.dp), contentScale = ContentScale.Crop)
        Box(modifier = Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f)))))
        Column {
            Text(text = tournament.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp))
            TabRow(selectedTabIndex = selectedTabIndex, containerColor = Color.Transparent, contentColor = Color.White) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTabIndex == index, onClick = { onTabClick(index) }, text = { Text(title) })
                }
            }
        }
    }
}

@Composable
fun ResultsTabContent(
    state: MatchesUiState,
    userRole: UserRole,
    onUpdateResult: (String, Int, Int) -> Unit
) {
    when (state) {
        is MatchesUiState.Loading -> {
            Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) { CircularProgressIndicator() }
        }
        is MatchesUiState.Error -> {
            Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) { Text(state.message) }
        }
        is MatchesUiState.Success -> {
            val finishedMatches = state.matches.filter { it.status.lowercase() == "finished" }

            if (finishedMatches.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) {
                    Text("Aún no hay resultados disponibles.")
                }
            } else {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Text("Resultados", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        finishedMatches.forEach { match ->
                            FinishedMatchRow(
                                match = match,
                                userRole = userRole,
                                onUpdateResult = onUpdateResult
                            )
                            if (match != finishedMatches.last()) {
                                Divider(color = SubtleGrey, thickness = 0.5.dp)
                            }
                        }
                    }
                }
            }
        }
        is MatchesUiState.Idle -> {
            Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) {
                Text("Cargando resultados...")
            }
        }
    }
}

@Composable
fun StandingsTabContent(navController: NavController, state: StandingsUiState) {
    when (state) {
        is StandingsUiState.Loading -> Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) { CircularProgressIndicator() }
        is StandingsUiState.Error -> Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) { Text(state.message) }
        is StandingsUiState.Success -> {
            if (state.standings.isEmpty()) {
                Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) { Text("Aún no hay equipos en la tabla de posiciones.") }
            } else {
                Column(Modifier.padding(16.dp), Arrangement.spacedBy(12.dp)) {
                    Row(Modifier.padding(horizontal = 16.dp)) {
                        Text("Pos", Modifier.width(48.dp), fontWeight = FontWeight.Bold)
                        Text("Equipo", Modifier.weight(1f), fontWeight = FontWeight.Bold)
                        Text("Pts", fontWeight = FontWeight.Bold)
                    }
                    Divider()
                    state.standings.forEach { standing ->
                        StandingRow(position = standing.position, teamLogoUrl = standing.team.logo, teamName = standing.team.name, points = standing.points, onClick = { navController.navigate("team_detail_screen/${standing.team.id}") })
                    }
                }
            }
        }
        is StandingsUiState.Idle -> {}
    }
}

@Composable
fun UpcomingMatchesTabContent(
    state: MatchesUiState,
    userRole: UserRole,
    onGenerateClick: () -> Unit,
    onUpdateResult: (String, Int, Int) -> Unit
) {
    when (state) {
        is MatchesUiState.Loading -> {
            Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) { CircularProgressIndicator() }
        }
        is MatchesUiState.Error -> {
            Box(Modifier.fillMaxWidth().padding(32.dp), Alignment.Center) { Text(state.message) }
        }
        is MatchesUiState.Success -> {
            val upcomingMatches = state.matches.filter { it.status.lowercase() == "scheduled" }
            Column(
                modifier = Modifier.fillMaxSize().padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Si la lista está vacía...
                if (upcomingMatches.isEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))
                    // ...y el usuario es un organizador, mostramos el botón
                    if (userRole == UserRole.ORGANIZER) {
                        Text("Aún no se han generado los partidos para la siguiente jornada.", textAlign = TextAlign.Center)
                        Spacer(modifier = Modifier.height(16.dp))
                        PrimaryButton(
                            text = "Generar Partidos",
                            onClick = {
                                Log.d("DEBUG_PARTIDOS", "Botón 'Generar Partidos' CLICADO en la UI.")
                                onGenerateClick()
                            }
                        )
                    } else {
                        Text("Los partidos para la siguiente jornada aún no han sido publicados.")
                    }
                } else {
                    // Si la lista NO está vacía, la mostramos
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text("Próximos Partidos", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(16.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            upcomingMatches.forEach { match ->
                                PendingMatchRow(
                                    match = match,
                                    userRole = userRole,
                                    onUpdateResult = onUpdateResult
                                )
                                if (match != upcomingMatches.last()) {
                                    Divider(color = SubtleGrey, thickness = 0.5.dp)
                                }
                            }
                        }
                    }
                }
            }
        }
        is MatchesUiState.Idle -> {
            if (userRole == UserRole.ORGANIZER) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("Aún no se han generado los partidos para la siguiente jornada.", textAlign = TextAlign.Center)
                    Spacer(modifier = Modifier.height(16.dp))
                    PrimaryButton(
                        text = "Generar Partidos",
                        onClick = onGenerateClick
                    )
                }
            }
        }
    }
}


// --- COMPONENTES DE FILA (HELPERS) ---

@Composable
private fun StandingRow(position: Int, teamLogoUrl: String?, teamName: String, points: Int, onClick: () -> Unit) {
    Surface(onClick = onClick, modifier = Modifier.fillMaxWidth(), color = Color.Transparent) {
        Row(Modifier.padding(vertical = 8.dp, horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            Text(text = "$position.", style = MaterialTheme.typography.bodyLarge, modifier = Modifier.width(48.dp))
            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(teamLogoUrl).crossfade(true).build(), placeholder = painterResource(id = R.drawable.img_logo), error = painterResource(id = R.drawable.img_logo), contentDescription = "Logo de $teamName", modifier = Modifier.size(40.dp).clip(CircleShape), contentScale = ContentScale.Crop)
            Spacer(Modifier.width(16.dp))
            Text(text = teamName, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(text = "$points", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
    }
}



@Composable
fun PendingMatchRow(
    match: MatchDto,
    userRole: UserRole,
    onUpdateResult: (String, Int, Int) -> Unit
) {
    var showDialog by remember { mutableStateOf(false) }
    var teamAScore by remember { mutableStateOf("") }
    var teamBScore by remember { mutableStateOf("") }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                AsyncImage(model = match.teamA.logo, placeholder = painterResource(id = R.drawable.img_logo), error = painterResource(id = R.drawable.img_logo), contentDescription = "Logo de ${match.teamA.name}", modifier = Modifier.size(32.dp).clip(CircleShape))
                Text(match.teamA.name, fontWeight = FontWeight.SemiBold, maxLines = 1)
            }

            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 8.dp)) {
                Text(text = "VS", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                Text(text = formatMatchDate(match.matchDate), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.weight(1f))
                Text(match.teamB.name, fontWeight = FontWeight.SemiBold, maxLines = 1, textAlign = TextAlign.End)
                AsyncImage(model = match.teamB.logo, placeholder = painterResource(id = R.drawable.img_logo), error = painterResource(id = R.drawable.img_logo), contentDescription = "Logo de ${match.teamB.name}", modifier = Modifier.size(32.dp).clip(CircleShape))
            }
        }

        // Botón para asignar resultado si es organizador
        if (userRole == UserRole.ORGANIZER) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = { showDialog = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Asignar Resultado")
            }
        }
    }

    // Diálogo para asignar resultado
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Asignar Resultado") },
            text = {
                Column {
                    Text("${match.teamA.name} vs ${match.teamB.name}", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(match.teamA.name, modifier = Modifier.weight(1f))
                        OutlinedTextField(
                            value = teamAScore,
                            onValueChange = { teamAScore = it },
                            modifier = Modifier.width(80.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(match.teamB.name, modifier = Modifier.weight(1f))
                        OutlinedTextField(
                            value = teamBScore,
                            onValueChange = { teamBScore = it },
                            modifier = Modifier.width(80.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val scoreA = teamAScore.toIntOrNull()
                        val scoreB = teamBScore.toIntOrNull()
                        if (scoreA != null && scoreB != null) {
                            onUpdateResult(match.id, scoreA, scoreB)
                            showDialog = false
                            teamAScore = ""
                            teamBScore = ""
                        }
                    },
                    enabled = teamAScore.toIntOrNull() != null && teamBScore.toIntOrNull() != null
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        teamAScore = ""
                        teamBScore = ""
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun FinishedMatchRow(
    match: MatchDto,
    userRole: UserRole,
    onUpdateResult: (String, Int, Int) -> Unit
) {
    var showEditDialog by remember { mutableStateOf(false) }
    var teamAScore by remember { mutableStateOf(match.scoreA?.toString() ?: "") }
    var teamBScore by remember { mutableStateOf(match.scoreB?.toString() ?: "") }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                AsyncImage(model = match.teamA.logo, placeholder = painterResource(id = R.drawable.img_logo), error = painterResource(id = R.drawable.img_logo), contentDescription = "Logo de ${match.teamA.name}", modifier = Modifier.size(32.dp).clip(CircleShape))
                Text(match.teamA.name, fontWeight = FontWeight.SemiBold, maxLines = 1)
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = "${match.scoreA ?: '?'} - ${match.scoreB ?: '?'}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)

                // Icono de editar solo para organizadores
                if (userRole == UserRole.ORGANIZER) {
                    IconButton(
                        onClick = { showEditDialog = true },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar resultado",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.weight(1f)) {
                Spacer(modifier = Modifier.weight(1f))
                Text(match.teamB.name, fontWeight = FontWeight.SemiBold, maxLines = 1, textAlign = TextAlign.End)
                AsyncImage(model = match.teamB.logo, placeholder = painterResource(id = R.drawable.img_logo), error = painterResource(id = R.drawable.img_logo), contentDescription = "Logo de ${match.teamB.name}", modifier = Modifier.size(32.dp).clip(CircleShape))
            }
        }
    }

    // Diálogo para editar resultado
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Editar Resultado") },
            text = {
                Column {
                    Text("${match.teamA.name} vs ${match.teamB.name}", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(match.teamA.name, modifier = Modifier.weight(1f))
                        OutlinedTextField(
                            value = teamAScore,
                            onValueChange = { teamAScore = it },
                            modifier = Modifier.width(80.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(match.teamB.name, modifier = Modifier.weight(1f))
                        OutlinedTextField(
                            value = teamBScore,
                            onValueChange = { teamBScore = it },
                            modifier = Modifier.width(80.dp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val scoreA = teamAScore.toIntOrNull()
                        val scoreB = teamBScore.toIntOrNull()
                        if (scoreA != null && scoreB != null) {
                            onUpdateResult(match.id, scoreA, scoreB)
                            showEditDialog = false
                        }
                    },
                    enabled = teamAScore.toIntOrNull() != null && teamBScore.toIntOrNull() != null
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showEditDialog = false
                        teamAScore = match.scoreA?.toString() ?: ""
                        teamBScore = match.scoreB?.toString() ?: ""
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private fun formatMatchDate(dateString: String): String {
    return try {
        val inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val outputFormatter = DateTimeFormatter.ofPattern("EEE, d MMM 'a las' HH:mm", Locale("es", "ES"))
        val zonedDateTime = ZonedDateTime.parse(dateString, inputFormatter)
        zonedDateTime.format(outputFormatter)
    } catch (e: Exception) {
        "Próximamente"
    }
}

// --- PREVIEW ---
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TournamentDetailScreenPreview() {
    UPRivalsTheme {
        TournamentDetailScreen(rememberNavController(), UserRole.ORGANIZER, "123", isRegistered = false)
    }
}