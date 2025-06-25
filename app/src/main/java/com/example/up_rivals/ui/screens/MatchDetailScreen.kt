// En: ui/screens/MatchDetailScreen.kt
package com.example.up_rivals.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.up_rivals.ui.components.DetailRow
import com.example.up_rivals.ui.components.FormTextField
import com.example.up_rivals.ui.components.PrimaryButton
import com.example.up_rivals.ui.theme.UPRivalsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MatchDetailScreen(navController: NavController) {
    // --- (Las variables de estado se quedan igual) ---
    var status by remember { mutableStateOf("Completed") }
    var result by remember { mutableStateOf("2-1") }
    var isEditingStatus by remember { mutableStateOf(false) }
    var isEditingResult by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Partido", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver atrás")
                    }
                }
            )
        }
    ) { innerPadding ->
        // --- COLUMNA EXTERIOR: Organiza la pantalla completa ---
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Team 1 vs Team 2",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(24.dp))

            // --- COLUMNA INTERIOR: Contiene los detalles que se pueden deslizar ---
            Column(
                modifier = Modifier
                    .weight(1f) // Ocupa todo el espacio, empujando el botón hacia abajo
                    .verticalScroll(rememberScrollState())
            ) {
                DetailRow(icon = Icons.Outlined.CalendarToday, label = "Fecha", value = "July 20, 2024")
                Divider() // Separador visual
                DetailRow(icon = Icons.Outlined.Schedule, label = "Hora", value = "10:00 AM")
                Divider()

                if (isEditingStatus) {
                    FormTextField(value = status, onValueChange = { status = it }, labelText = "Nuevo Estado")
                } else {
                    DetailRow(icon = Icons.Outlined.HourglassTop, label = "Estado", value = status, isEditable = true, onEditClick = { isEditingStatus = true })
                }
                Divider()

                if (isEditingResult) {
                    FormTextField(value = result, onValueChange = { result = it }, labelText = "Nuevo Resultado")
                } else {
                    DetailRow(icon = Icons.Outlined.EmojiEvents, label = "Resultado", value = result, isEditable = true, onEditClick = { isEditingResult = true })
                }
                Divider()

                DetailRow(icon = Icons.Outlined.People, label = "Equipos", value = "Team A vs. Team B")
            } // --- Fin de la Columna Interior

            // El botón ahora es hijo de la Columna Exterior y se queda abajo
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(text = "Save Result", onClick = {
                isEditingResult = false
                isEditingStatus = false
                // TODO: Lógica para guardar en el backend
            })
            Spacer(modifier = Modifier.height(16.dp))
        } // --- Fin de la Columna Exterior
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun MatchDetailScreenPreview() {
    UPRivalsTheme {
        MatchDetailScreen(rememberNavController())
    }
}