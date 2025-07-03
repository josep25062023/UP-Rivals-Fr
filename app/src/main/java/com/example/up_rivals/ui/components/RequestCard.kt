// En: ui/components/RequestCard.kt
package com.example.up_rivals.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.up_rivals.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestCard(
    tournamentName: String, // <-- Acepta nombre del torneo
    teamName: String,
    teamLogoUrl: String?,   // <-- Acepta la URL del logo
    onAcceptClick: () -> Unit,
    onDeclineClick: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onCardClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(teamLogoUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(id = R.drawable.img_logo),
                error = painterResource(id = R.drawable.img_logo),
                contentDescription = "Logo del equipo $teamName",
                modifier = Modifier
                    .size(60.dp)
                    .clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f)) {
                // Mostramos ambos nombres
                Text(text = tournamentName, style = MaterialTheme.typography.bodySmall)
                Text(text = teamName, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Row {
                Button(onClick = onAcceptClick) { Text("Aceptar") }
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedButton(onClick = onDeclineClick) { Text("Declinar") }
            }
        }
    }
}