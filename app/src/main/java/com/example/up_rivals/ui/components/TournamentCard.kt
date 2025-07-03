package com.example.up_rivals.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentCard(
    startDate: String,
    endDate: String, // Añadimos la fecha de fin
    tournamentName: String,
    sport: String,
    @DrawableRes imageResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick,
        // Le damos el color de fondo azul
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFE0EFFF) // Un azul claro, puedes ajustarlo
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Imagen circular
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Logo de $sport",
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape), // La hacemos circular
                contentScale = ContentScale.Crop
            )
            // Columna con los textos
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = tournamentName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = sport,
                    style = MaterialTheme.typography.bodyMedium
                )
                // Usamos la función para mostrar las fechas formateadas
                Text(
                    text = "Fecha: ${formatDate(startDate)} - ${formatDate(endDate)}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}