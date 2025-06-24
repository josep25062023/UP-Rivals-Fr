// En: ui/components/TournamentCard.kt
package com.example.up_rivals.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.up_rivals.R
import com.example.up_rivals.ui.theme.LightBlueBackground
import com.example.up_rivals.ui.theme.SubtleGrey
import com.example.up_rivals.ui.theme.UPRivalsTheme

@Composable
fun TournamentCard(
    startDate: String,
    tournamentName: String,
    sport: String,
    @DrawableRes imageResId: Int,
    modifier: Modifier = Modifier
) {
    // 1. Creamos la fuente de interacción para detectar la presión
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    // 2. Creamos la animación para la escala (se encoge un 2% al presionar)
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.98f else 1.0f, label = "scale")

    Card(
        modifier = modifier
            .fillMaxWidth()
            // 3. Aplicamos la animación de escala aquí
            .scale(scale)
            // 4. LA CORRECCIÓN: Usamos el modificador .clickable
            .clickable(
                interactionSource = interactionSource,
                indication = null, // Ponemos null para quitar la onda y que solo se vea la animación de escala
                onClick = { /* TODO: Navegar a los detalles de este torneo */ }
            ),
        colors = CardDefaults.cardColors(
            containerColor = LightBlueBackground
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = startDate, color = SubtleGrey)
                Text(text = tournamentName, fontWeight = FontWeight.Bold)
                Text(text = sport)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Imagen del torneo $tournamentName",
                // contentScale.Crop asegura que la imagen llene el espacio antes de ser recortada
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    // LA MAGIA ESTÁ AQUÍ: Recortamos la imagen con una forma circular
                    .clip(CircleShape)
            )
        }
    }
}


@Preview
@Composable
fun TournamentCardPreview() {
    UPRivalsTheme {
        TournamentCard(
            startDate = "Inicia en 2 días",
            tournamentName = "Summer Slam",
            sport = "Torneo de Tenis",
            imageResId = R.drawable.ic_launcher_background
        )
    }
}