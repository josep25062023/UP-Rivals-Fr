// En: ui/screens/CreateTournamentScreen.kt
package com.example.up_rivals.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.up_rivals.network.dto.CreateTournamentRequest
import com.example.up_rivals.ui.components.FormTextField
import com.example.up_rivals.ui.components.PrimaryButton
import com.example.up_rivals.ui.theme.UPRivalsTheme
import com.example.up_rivals.viewmodels.CreateTournamentUiState
import com.example.up_rivals.viewmodels.CreateTournamentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTournamentScreen(navController: NavController) {
    val viewModel: CreateTournamentViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var tournamentName by remember { mutableStateOf("") }
    var maxTeams by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var rules by remember { mutableStateOf("") }

    val categories = listOf("Fútbol", "Voleybol", "Básquetbol")
    var selectedCategory by remember { mutableStateOf("") }
    var isCategoryMenuExpanded by remember { mutableStateOf(false) }

    val modalities = listOf("Varonil", "Femenil", "Mixto")
    var selectedModality by remember { mutableStateOf("") }
    var isModalityMenuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = uiState) {
        when (val state = uiState) {
            is CreateTournamentUiState.Success -> {
                Toast.makeText(context, "¡Torneo creado exitosamente!", Toast.LENGTH_LONG).show()
                navController.popBackStack()
            }
            is CreateTournamentUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Nuevo Torneo", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Close, "Cerrar pantalla")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp))
                FormTextField(value = tournamentName, onValueChange = { tournamentName = it }, labelText = "Nombre del torneo")

                ExposedDropdownMenuBox(expanded = isCategoryMenuExpanded, onExpandedChange = { isCategoryMenuExpanded = it }) {
                    FormTextField(
                        value = selectedCategory, onValueChange = {}, readOnly = true, labelText = "Categoría",
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryMenuExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = isCategoryMenuExpanded, onDismissRequest = { isCategoryMenuExpanded = false }) {
                        categories.forEach { category ->
                            DropdownMenuItem(text = { Text(text = category) }, onClick = { selectedCategory = category; isCategoryMenuExpanded = false })
                        }
                    }
                }

                ExposedDropdownMenuBox(expanded = isModalityMenuExpanded, onExpandedChange = { isModalityMenuExpanded = it }) {
                    FormTextField(
                        value = selectedModality, onValueChange = {}, readOnly = true, labelText = "Modalidad",
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isModalityMenuExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = isModalityMenuExpanded, onDismissRequest = { isModalityMenuExpanded = false }) {
                        modalities.forEach { modality ->
                            DropdownMenuItem(text = { Text(text = modality) }, onClick = { selectedModality = modality; isModalityMenuExpanded = false })
                        }
                    }
                }

                FormTextField(value = maxTeams, onValueChange = { maxTeams = it }, labelText = "Número de equipos", keyboardType = KeyboardType.Number)

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        FormTextField(value = startDate, onValueChange = { startDate = it }, labelText = "Fecha de Inicio (YYYY-MM-DD)")
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FormTextField(value = endDate, onValueChange = { endDate = it }, labelText = "Fecha de Termino (YYYY-MM-DD)")
                    }
                }

                TextField(
                    value = rules,
                    onValueChange = { rules = it },
                    label = { Text("Reglas")},
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Box(contentAlignment = Alignment.Center) {
                PrimaryButton(
                    text = "Crear torneo",
                    enabled = uiState !is CreateTournamentUiState.Loading,
                    onClick = {
                        val maxTeamsInt = maxTeams.toIntOrNull()
                        if (tournamentName.isBlank() || selectedCategory.isBlank() || selectedModality.isBlank() || startDate.isBlank() || endDate.isBlank() || rules.isBlank() || maxTeamsInt == null) {
                            Toast.makeText(context, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
                            return@PrimaryButton
                        }

                        // NOTA: Añadimos una hora por defecto para cumplir con el formato del backend.
                        // Lo ideal a futuro sería usar un selector de fecha Y hora.
                        val formattedStartDate = "${startDate}T00:00:00Z"
                        val formattedEndDate = "${endDate}T23:59:59Z"

                        val request = CreateTournamentRequest(
                            name = tournamentName,
                            category = selectedCategory,
                            modality = selectedModality.lowercase(),
                            maxTeams = maxTeamsInt,
                            startDate = formattedStartDate,
                            endDate = formattedEndDate,
                            rules = rules
                        )
                        viewModel.createTournament(request)
                    }
                )
                if (uiState is CreateTournamentUiState.Loading) {
                    CircularProgressIndicator()
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateTournamentScreenPreview() {
    UPRivalsTheme {
        CreateTournamentScreen(rememberNavController())
    }
}