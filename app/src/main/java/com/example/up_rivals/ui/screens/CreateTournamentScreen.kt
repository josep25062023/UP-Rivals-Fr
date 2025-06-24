// En: ui/screens/CreateTournamentScreen.kt
package com.example.up_rivals.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.up_rivals.ui.components.FormTextField
import com.example.up_rivals.ui.components.PrimaryButton
import com.example.up_rivals.ui.theme.UPRivalsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTournamentScreen(navController: NavController) {
    // --- Variables de estado para cada campo ---
    var tournamentName by remember { mutableStateOf("") }
    var maxTeams by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var rules by remember { mutableStateOf("") }

    // --- Estados para los menús desplegables ---
    val categories = listOf("Fútbol", "Voleybol", "Básquetbol")
    var selectedCategory by remember { mutableStateOf("") }
    var isCategoryMenuExpanded by remember { mutableStateOf(false) }

    val modalities = listOf("Varonil", "Femenil", "Mixto")
    var selectedModality by remember { mutableStateOf("") }
    var isModalityMenuExpanded by remember { mutableStateOf(false) }

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
            // --- Columna con scroll para los campos del formulario ---
            Column(
                modifier = Modifier
                    .weight(1f) // Ocupa el espacio disponible, empujando el botón hacia abajo
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Spacer(modifier = Modifier.height(8.dp)) // Pequeño espacio inicial

                FormTextField(value = tournamentName, onValueChange = { tournamentName = it }, labelText = "Nombre del torneo")

                // Menú para Categorías usando nuestro FormTextField
                ExposedDropdownMenuBox(expanded = isCategoryMenuExpanded, onExpandedChange = { isCategoryMenuExpanded = it }) {
                    FormTextField(
                        value = selectedCategory,
                        onValueChange = {},
                        readOnly = true,
                        labelText = "Categorías",
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isCategoryMenuExpanded) },
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = isCategoryMenuExpanded, onDismissRequest = { isCategoryMenuExpanded = false }) {
                        categories.forEach { category ->
                            DropdownMenuItem(text = { Text(text = category) }, onClick = { selectedCategory = category; isCategoryMenuExpanded = false })
                        }
                    }
                }

                // Menú para Modalidad usando nuestro FormTextField
                ExposedDropdownMenuBox(expanded = isModalityMenuExpanded, onExpandedChange = { isModalityMenuExpanded = it }) {
                    FormTextField(
                        value = selectedModality,
                        onValueChange = {},
                        readOnly = true,
                        labelText = "Modalidad",
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

                // Fechas en una fila
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        FormTextField(value = startDate, onValueChange = { startDate = it }, labelText = "Fecha de Inicio")
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        FormTextField(value = endDate, onValueChange = { endDate = it }, labelText = "Fecha de Termino")
                    }
                }

                // Reglas (usando TextField básico para permitir múltiples líneas)
                TextField(
                    value = rules,
                    onValueChange = { rules = it },
                    label = { Text("Reglas")},
                    modifier = Modifier.fillMaxWidth().height(120.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(unfocusedIndicatorColor = Color.Transparent, focusedIndicatorColor = Color.Transparent)
                )
            }

            // Botón de Crear Torneo
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(text = "Crear torneo", onClick = { /*TODO*/ })
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