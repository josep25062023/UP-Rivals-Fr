// En: ui/components/MatchResultItem.kt
package com.example.up_rivals.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.example.up_rivals.ui.theme.SubtleGrey
import com.example.up_rivals.ui.theme.UPRivalsTheme

@Composable
fun MatchResultItem(
    @DrawableRes teamLogoResId: Int,
    matchup: String,
    detail: String, // Puede ser la hora o el resultado final
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Image(
            painter = painterResource(id = teamLogoResId),
            contentDescription = "Logo de equipo",
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
        )
        Column {
            Text(text = matchup, fontWeight = FontWeight.Bold)
            Text(text = detail, color = SubtleGrey)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MatchResultItemPreview() {
    UPRivalsTheme {
        MatchResultItem(
            teamLogoResId = R.drawable.ic_launcher_background,
            matchup = "Team A vs. Team B",
            detail = "Final Score: 2-1"
        )
    }
}