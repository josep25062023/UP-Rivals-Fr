// En: ui/components/RequestCard.kt
package com.example.up_rivals.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.up_rivals.R
import com.example.up_rivals.ui.theme.LightBlueBackground
import com.example.up_rivals.ui.theme.UPRivalsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RequestCard(
    tournamentName: String,
    teamName: String,
    @DrawableRes teamImageResId: Int,
    onAcceptClick: () -> Unit,
    onDeclineClick: () -> Unit,
    onCardClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onCardClick, // Hacemos toda la tarjeta clicable
        colors = CardDefaults.cardColors(
            containerColor = LightBlueBackground // Reutilizamos el color de fondo
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Fila con la información del equipo y torneo
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = teamImageResId),
                    contentDescription = "Logo del equipo $teamName",
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(text = tournamentName, fontWeight = FontWeight.Bold)
                    Text(text = teamName)
                }
            }
            // Fila con los botones de acción
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Usamos OutlinedButton para el botón de "Declinar" para que tenga un estilo secundario
                OutlinedButton(
                    onClick = onDeclineClick,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Declinar")
                }
                // Usamos nuestro PrimaryButton para la acción principal "Aceptar"
                PrimaryButton(
                    text = "Aceptar",
                    onClick = onAcceptClick,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Preview
@Composable
fun RequestCardPreview() {
    UPRivalsTheme {
        RequestCard(
            tournamentName = "Fútbol 7",
            teamName = "Toque y Pase",
            teamImageResId = R.drawable.ic_launcher_background,
            onAcceptClick = {},
            onDeclineClick = {},
            onCardClick = {}
        )
    }
}