// En: ui/screens/TeamDetailScreen.kt
package com.example.up_rivals.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.example.up_rivals.network.dto.User
import com.example.up_rivals.ui.components.FormTextField
import com.example.up_rivals.ui.components.StatCard
import com.example.up_rivals.ui.theme.SubtleGrey
import com.example.up_rivals.ui.theme.UPRivalsTheme
import com.example.up_rivals.viewmodels.TeamDetailUiState
import com.example.up_rivals.viewmodels.TeamDetailViewModel
import com.example.up_rivals.viewmodels.UiEvent
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamDetailScreen(navController: NavController, teamId: String) {
    val viewModel: TeamDetailViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Efecto para cargar los detalles del equipo una sola vez
    LaunchedEffect(key1 = teamId) {
        viewModel.loadTeamDetails()
    }

    // Efecto para escuchar eventos de una sola vez (como Toasts)
    LaunchedEffect(key1 = Unit) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is UiEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    // Muestra el nombre del equipo cuando ya se haya cargado
                    val title = if (uiState is TeamDetailUiState.Success) {
                        (uiState as TeamDetailUiState.Success).teamDetail.name
                    } else {
                        "Detalle del Equipo"
                    }
                    Text(title)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver atrás")
                    }
                }
            )
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is TeamDetailUiState.Loading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is TeamDetailUiState.Error -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(state.message)
                }
            }
            is TeamDetailUiState.Success -> {
                val teamDetail = state.teamDetail
                var selectedTabIndex by remember { mutableStateOf(0) }
                val tabs = listOf("Información", "Integrantes")

                LazyColumn(
                    modifier = Modifier.padding(innerPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    contentPadding = PaddingValues(vertical = 16.dp)
                ) {
                    // --- Header del Equipo con datos reales ---
                    item {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            AsyncImage(
                                model = ImageRequest.Builder(LocalContext.current)
                                    .data(teamDetail.logo)
                                    .crossfade(true)
                                    .build(),
                                placeholder = painterResource(id = R.drawable.img_logo),
                                error = painterResource(id = R.drawable.img_logo),
                                contentDescription = "Logo del Equipo",
                                modifier = Modifier.size(90.dp).clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(Modifier.height(8.dp))
                            Text(teamDetail.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Spacer(Modifier.height(16.dp))
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
                            0 -> InformationTabContent() // Sigue usando datos de prueba
                            1 -> MembersTabContent(
                                members = teamDetail.members,
                                onAddMember = { memberId -> viewModel.addMember(memberId) }
                            )
                        }
                    }
                }
            }
        }
    }
}

// --- Contenido de la Pestaña "Integrantes" CONECTADA ---
@Composable
fun MembersTabContent(members: List<User>, onAddMember: (String) -> Unit) {
    var showAddMemberDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Integrantes (${members.size})", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = { showAddMemberDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Añadir integrante")
            }
        }
        Spacer(Modifier.height(8.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            members.forEach { member ->
                MemberRow(member = member)
                Divider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.1f))
            }
        }
    }

    if (showAddMemberDialog) {
        AddMemberDialog(
            onDismiss = { showAddMemberDialog = false },
            onConfirm = { memberId ->
                onAddMember(memberId)
                showAddMemberDialog = false
            }
        )
    }
}

// Diálogo para añadir un nuevo integrante
@Composable
fun AddMemberDialog(onDismiss: () -> Unit, onConfirm: (String) -> Unit) {
    var memberId by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Añadir Integrante") },
        text = {
            FormTextField(
                value = memberId,
                onValueChange = { memberId = it },
                labelText = "ID del Jugador"
            )
        },
        confirmButton = {
            Button(onClick = { onConfirm(memberId) }) { Text("Añadir") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}

// Componente para cada fila de miembro
@Composable
private fun MemberRow(member: User) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Text(text = member.name, fontWeight = FontWeight.Bold)
        Text(text = member.email, style = MaterialTheme.typography.bodyMedium, color = SubtleGrey)
    }
}

// --- Contenido de la Pestaña "Información" (con datos de prueba) ---
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
// El resto de tus composables (InformationTabContent, etc.) y la Preview se quedan igual...

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun TeamDetailScreenPreview() {
    UPRivalsTheme {
        TeamDetailScreen(rememberNavController(), teamId = "123")
    }
}