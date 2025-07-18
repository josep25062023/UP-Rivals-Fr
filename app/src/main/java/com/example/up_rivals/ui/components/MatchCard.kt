package com.example.up_rivals.ui.components


import androidx.annotation.DrawableRes
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.SportsSoccer
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.up_rivals.R
import com.example.up_rivals.ui.theme.UPRivalsTheme

enum class MatchStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchCard(
    tournamentName: String,
    teamA: String,
    teamB: String,
    matchTime: String,
    sport: String,
    @DrawableRes imageResId: Int,
    status: MatchStatus = MatchStatus.PENDING,
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
            // Imagen con efecto de pulso y indicador de estado
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

                Image(
                    painter = painterResource(id = imageResId),
                    contentDescription = "Logo de $sport",
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .alpha(pulseAlpha),
                    contentScale = ContentScale.Crop
                )

                // Indicador de estado del partido
                MatchStatusIndicator(
                    status = status,
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }

            // Columna con información del partido
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Título del torneo con animación
                AnimatedVisibility(
                    visible = true,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn()
                ) {
                    Text(
                        text = tournamentName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Equipos enfrentados
                Text(
                    text = "$teamA vs $teamB",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Chip del deporte
                SportChip(sport = sport)

                // Hora del partido con icono
                TimeRow(time = matchTime)
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

@Composable
fun MatchStatusIndicator(
    status: MatchStatus,
    modifier: Modifier = Modifier
) {
    val color = when (status) {
        MatchStatus.IN_PROGRESS -> Color(0xFF4CAF50) // Verde
        MatchStatus.PENDING -> Color(0xFFFF9800)     // Naranja
        MatchStatus.COMPLETED -> Color(0xFF9E9E9E)   // Gris
    }

    Box(
        modifier = modifier
            .size(12.dp)
            .background(color, CircleShape)
            .border(2.dp, Color.White, CircleShape)
    )
}

@Composable
fun TimeRow(
    time: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Schedule,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = time,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MatchCardPreview() {
    UPRivalsTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MatchCard(
                tournamentName = "Torneo de Fútbol Universitario",
                teamA = "Equipo Alpha",
                teamB = "Equipo Beta",
                matchTime = "15:30 - 25/12/2024",
                sport = "Fútbol",
                imageResId = R.drawable.img_futbol,
                status = MatchStatus.PENDING,
                onClick = {}
            )

            MatchCard(
                tournamentName = "Liga de Básquetbol",
                teamA = "Tigres",
                teamB = "Leones",
                matchTime = "18:00 - 26/12/2024",
                sport = "Básquetbol",
                imageResId = R.drawable.img_basquetbol,
                status = MatchStatus.IN_PROGRESS,
                onClick = {}
            )
        }
    }
}