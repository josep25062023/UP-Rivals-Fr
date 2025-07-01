// En: ui/components/PrimaryButton.kt
package com.example.up_rivals.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.up_rivals.ui.theme.UPRivalsTheme

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        // Usamos CircleShape para obtener la forma de píldora perfecta.
        shape = CircleShape,
        // Aquí definimos los colores personalizados.
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFF0A80ED), // <-- Tu color de Figma.
            contentColor = Color.White // El color del texto
        ),
        modifier = modifier
            .fillMaxWidth()
            .height(50.dp) // Una altura estándar y agradable
    ) {
        Text(
            text = text,
            fontWeight = FontWeight.Bold // Hacemos el texto un poco más grueso como en tu diseño
        )
    }
}


// --- Preview para ver nuestro componente ---
@Preview(showBackground = true, widthDp = 320)
@Composable
fun PrimaryButtonPreview() {
    UPRivalsTheme {
        PrimaryButton(text = "Iniciar Sesión", onClick = {})
    }
}

