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
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.LocalDateTime

// Enum para el estado del torneo
enum class TournamentStatus {
    IN_PROGRESS,
    UPCOMING,
    FINISHED
}

// --- Función de ayuda para formatear las fechas ---
private fun formatDate(dateString: String): String {
    return try {
        val inputFormatter = DateTimeFormatter.ISO_OFFSET_DATE_TIME
        val outputFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
        val zonedDateTime = ZonedDateTime.parse(dateString, inputFormatter)
        zonedDateTime.format(outputFormatter)
    } catch (e: Exception) {
        "Fecha inválida"
    }
}

// --- Función para determinar el estado del torneo ---
private fun getStatusFromDates(startDate: String, endDate: String): TournamentStatus {
    return try {
        val now = LocalDateTime.now()
        val start = ZonedDateTime.parse(startDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime()
        val end = ZonedDateTime.parse(endDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDateTime()

        when {
            now.isBefore(start) -> TournamentStatus.UPCOMING
            now.isAfter(end) -> TournamentStatus.FINISHED
            else -> TournamentStatus.IN_PROGRESS
        }
    } catch (e: Exception) {
        TournamentStatus.UPCOMING
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentCard(
    startDate: String,
    endDate: String,
    tournamentName: String,
    sport: String,
    @DrawableRes imageResId: Int,
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

                // Indicador de estado del torneo
                StatusIndicator(
                    status = getStatusFromDates(startDate, endDate),
                    modifier = Modifier.align(Alignment.TopEnd)
                )
            }

            // Columna con información del torneo
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
                        text = tournamentName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Chip del deporte
                SportChip(sport = sport)

                // Fechas con iconos
                DateRow(startDate = startDate, endDate = endDate)
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
fun StatusIndicator(
    status: TournamentStatus,
    modifier: Modifier = Modifier
) {
    val color = when (status) {
        TournamentStatus.IN_PROGRESS -> Color(0xFF4CAF50) // Verde
        TournamentStatus.UPCOMING -> Color(0xFFFF9800)    // Naranja
        TournamentStatus.FINISHED -> Color(0xFF9E9E9E)    // Gris
    }

    Box(
        modifier = modifier
            .size(12.dp)
            .background(color, CircleShape)
            .border(2.dp, Color.White, CircleShape)
    )
}

@Composable
fun SportChip(
    sport: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.primaryContainer
    ) {
        Text(
            text = sport,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun DateRow(
    startDate: String,
    endDate: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "${formatDate(startDate)} - ${formatDate(endDate)}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}