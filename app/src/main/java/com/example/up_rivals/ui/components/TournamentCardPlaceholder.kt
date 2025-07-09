// En: ui/components/TournamentCardPlaceholder.kt
package com.example.up_rivals.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun TournamentCardPlaceholder() {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Placeholder para la imagen circular
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(Color.LightGray)
            )
            // Placeholder para los textos
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .height(20.dp)
                        .width(150.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                )
                Box(
                    modifier = Modifier
                        .height(16.dp)
                        .width(100.dp)
                        .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
                )
            }
        }
    }
}