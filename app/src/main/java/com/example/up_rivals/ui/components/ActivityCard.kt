// En: ui/components/ActivityCard.kt
package com.example.up_rivals.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
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
fun ActivityCard(
    sportName: String,
    teams: String,
    time: String,
    @DrawableRes imageResId: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        onClick = onClick, // Hacemos la tarjeta clicable para añadir resultados después
        colors = CardDefaults.cardColors(
            containerColor = LightBlueBackground
        )
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Image(
                painter = painterResource(id = imageResId),
                contentDescription = "Icono de deporte",
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(12.dp)) // Usamos esquinas redondeadas como en tu diseño
            )
            Column {
                Text(text = sportName, fontWeight = FontWeight.Bold)
                Text(text = teams)
                Text(text = time)
            }
        }
    }
}

@Preview
@Composable
fun ActivityCardPreview() {
    UPRivalsTheme {
        ActivityCard(
            sportName = "Soccer Match",
            teams = "Team A vs. Team B",
            time = "10:00 AM",
            imageResId = R.drawable.img_futbol,
            onClick = {}
        )
    }
}