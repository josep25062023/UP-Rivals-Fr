// En: ui/screens/CreateTeamScreen.kt
package com.example.up_rivals.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.up_rivals.ui.components.FormTextField
import com.example.up_rivals.ui.components.PrimaryButton
import com.example.up_rivals.ui.theme.UPRivalsTheme
import com.example.up_rivals.viewmodels.CreateTeamUiState
import com.example.up_rivals.viewmodels.CreateTeamViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTeamScreen(navController: NavController, tournamentId: String) {
    val viewModel: CreateTeamViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var teamName by remember { mutableStateOf("") }

    // Efecto para manejar el resultado de la creación e inscripción
    LaunchedEffect(key1 = uiState) {
        when(uiState) {
            is CreateTeamUiState.Success -> {
                Toast.makeText(context, "¡Solicitud enviada exitosamente!", Toast.LENGTH_LONG).show()
                // Volvemos a la pantalla de detalle del torneo
                navController.popBackStack()
            }
            is CreateTeamUiState.Error -> {
                Toast.makeText(context, (uiState as CreateTeamUiState.Error).message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Equipo para Torneo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver atrás")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                // Sección para el Logo
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { /* TODO: Lógica para abrir la galería */ },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Añadir logo", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(40.dp))
                }
                Spacer(modifier = Modifier.height(24.dp))

                // Nombre del equipo
                FormTextField(value = teamName, onValueChange = { teamName = it }, labelText = "Nombre del equipo")
                Spacer(modifier = Modifier.height(16.dp))

                // La sección para añadir integrantes ha sido eliminada temporalmente
            }

            // Botón para crear el equipo
            Spacer(modifier = Modifier.height(16.dp))
            Box(contentAlignment = Alignment.Center) {
                PrimaryButton(
                    text = "Crear Equipo y Enviar Solicitud",
                    enabled = uiState !is CreateTeamUiState.Loading,
                    onClick = {
                        if (teamName.isNotBlank()) {
                            viewModel.createAndInscribeTeam(
                                teamName = teamName,
                                tournamentId = tournamentId
                            )
                        } else {
                            Toast.makeText(context, "El nombre del equipo no puede estar vacío.", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
                if (uiState is CreateTeamUiState.Loading) {
                    CircularProgressIndicator()
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateTeamScreenPreview() {
    UPRivalsTheme {
        CreateTeamScreen(rememberNavController(), tournamentId = "123")
    }
}