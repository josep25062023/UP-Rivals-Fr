// En: ui/screens/TeamsScreen.kt
package com.example.up_rivals.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.People
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.up_rivals.R
import com.example.up_rivals.network.dto.PlayerTeamDto
import com.example.up_rivals.ui.theme.UPRivalsTheme
import com.example.up_rivals.viewmodels.MyTeamsUiState
import com.example.up_rivals.viewmodels.MyTeamsViewModel
import com.google.accompanist.swiperefresh.SwipeRefresh
import com.google.accompanist.swiperefresh.rememberSwipeRefreshState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamsScreen(navController: NavController) {
    val viewModel: MyTeamsViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()

    // ✅ NUEVO: Estado para SwipeRefresh
    val swipeRefreshState = rememberSwipeRefreshState(
        isRefreshing = uiState is MyTeamsUiState.Loading
    )

    // ✅ NUEVO: Recargar cuando se regresa a esta pantalla
    LaunchedEffect(Unit) {
        viewModel.refreshTeams()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Equipos", fontWeight = FontWeight.Bold) }
            )
        }
    ) { innerPadding ->
        // ✅ NUEVO: Envolver contenido con SwipeRefresh
        SwipeRefresh(
            state = swipeRefreshState,
            onRefresh = { viewModel.refreshTeams() },
            modifier = Modifier.padding(innerPadding)
        ) {
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
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(state.teams) { team ->
                            TeamInfoCard(
                                team = team,
                                onClick = {
                                    navController.navigate("team_detail_screen/${team.teamId}")
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

// Componente de tarjeta mejorado siguiendo el estilo de TournamentCard
// ✅ Corregir parámetros para usar PlayerTeamDto
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamInfoCard(
    team: PlayerTeamDto, // ✅ Usar el DTO correcto
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isPressed by remember { mutableStateOf(false) }

    // Animaciones para el efecto de presión
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "scale"
    )

    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 8.dp,
        animationSpec = tween(150),
        label = "elevation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(scale)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        isPressed = true
                        tryAwaitRelease()
                        isPressed = false
                    },
                    onTap = { onClick() }
                )
            },
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE0EFFF)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen con efecto de pulso
            Box {
                val infiniteTransition = rememberInfiniteTransition(label = "pulse")
                val pulseAlpha by infiniteTransition.animateFloat(
                    initialValue = 0.7f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1500),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "pulseAlpha"
                )

                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(team.teamLogo) // ✅ Usar team.logo
                        .crossfade(true)
                        .build(),
                    placeholder = painterResource(id = R.drawable.img_logo),
                    error = painterResource(id = R.drawable.img_logo),
                    contentDescription = "Logo de ${team.teamName}", // ✅ Usar team.name
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .alpha(pulseAlpha),
                    contentScale = ContentScale.Crop
                )

                // Indicador de equipo activo
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(12.dp)
                        .background(Color(0xFF4CAF50), CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
            }

            // Columna con información del equipo
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Título con animación de aparición
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn()
                ) {
                    Text(
                        text = team.teamName, // ✅ Usar team.name
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Chip del torneo
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = team.tournament.tournamentName, // ✅ Usar team.tournamentName
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Información adicional con icono
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "Mi equipo",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Flecha indicadora animada
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = "Ver detalles",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(24.dp)
                    .rotate(if (isPressed) 15f else 0f)
            )
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