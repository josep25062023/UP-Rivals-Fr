// En: ui/screens/CreateTournamentScreen.kt
package com.example.up_rivals.ui.screens

import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DateRange
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
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTournamentScreen(navController: NavController) {
    val viewModel: CreateTournamentViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var tournamentName by remember { mutableStateOf("") }
    var maxTeams by remember { mutableStateOf("") }
    var rules by remember { mutableStateOf("") }

    var startDate by remember { mutableStateOf<LocalDate?>(null) }
    var endDate by remember { mutableStateOf<LocalDate?>(null) }

    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    val categories = listOf("Fútbol", "Voleybol", "Básquetbol")
    var selectedCategory by remember { mutableStateOf("") }
    var isCategoryMenuExpanded by remember { mutableStateOf(false) }

    val modalities = listOf("Varonil", "Femenil", "Mixto")
    var selectedModality by remember { mutableStateOf("") }
    var isModalityMenuExpanded by remember { mutableStateOf(false) }

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault())

    val startDateInteractionSource = remember { MutableInteractionSource() }
    val endDateInteractionSource = remember { MutableInteractionSource() }

    val isStartDatePressed by startDateInteractionSource.collectIsPressedAsState()
    val isEndDatePressed by endDateInteractionSource.collectIsPressedAsState()

    LaunchedEffect(isStartDatePressed) {
        if (isStartDatePressed) {
            showStartDatePicker = true
        }
    }

    LaunchedEffect(isEndDatePressed) {
        if (isEndDatePressed) {
            showEndDatePicker = true
        }
    }

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

                FormTextField(
                    value = maxTeams,
                    onValueChange = { newValue ->
                        maxTeams = newValue.filter { it.isDigit() }
                    },
                    labelText = "Número de equipos",
                    keyboardType = KeyboardType.Number
                )

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedTextField(
                        value = startDate?.format(dateFormatter) ?: "",
                        onValueChange = { },
                        label = { Text("Fecha de Inicio") },
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        interactionSource = startDateInteractionSource
                    )
                    OutlinedTextField(
                        value = endDate?.format(dateFormatter) ?: "",
                        onValueChange = { },
                        label = { Text("Fecha de Término") },
                        readOnly = true,
                        trailingIcon = { Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha") },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent
                        ),
                        interactionSource = endDateInteractionSource
                    )
                }

                if (showStartDatePicker) {
                    TournamentDatePickerDialog(
                        onDateSelected = {
                            startDate = it
                            if (endDate?.isBefore(it) == true) {
                                endDate = null
                            }
                            showStartDatePicker = false
                        },
                        onDismiss = { showStartDatePicker = false }
                    )
                }

                if (showEndDatePicker) {
                    // --- INICIO DE LA MODIFICACIÓN 1 ---
                    // Se calcula la fecha mínima en milisegundos usando el estándar UTC para evitar errores de zona horaria.
                    val minDateMillis = startDate
                        ?.atStartOfDay(ZoneOffset.UTC)
                        ?.toInstant()
                        ?.toEpochMilli()

                    TournamentDatePickerDialog(
                        onDateSelected = {
                            endDate = it
                            showEndDatePicker = false
                        },
                        onDismiss = { showEndDatePicker = false },
                        minDateMillis = minDateMillis
                    )
                }

                TextField(
                    value = rules,
                    onValueChange = { rules = it },
                    label = { Text("Reglas")},
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
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

                        when {
                            tournamentName.isBlank() ||
                                    selectedCategory.isBlank() ||
                                    selectedModality.isBlank() ||
                                    startDate == null ||
                                    endDate == null ||
                                    rules.isBlank() -> {
                                Toast.makeText(context, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
                                return@PrimaryButton
                            }
                            maxTeamsInt == null -> {
                                Toast.makeText(context, "Ingresa un número de equipos válido.", Toast.LENGTH_SHORT).show()
                                return@PrimaryButton
                            }
                            maxTeamsInt < 2 -> {
                                Toast.makeText(context, "El número mínimo de equipos debe ser 2.", Toast.LENGTH_SHORT).show()
                                return@PrimaryButton
                            }
                            endDate!!.isBefore(startDate) -> {
                                Toast.makeText(context, "La fecha de término no puede ser anterior a la de inicio.", Toast.LENGTH_SHORT).show()
                                return@PrimaryButton
                            }
                        }

                        val formattedStartDate = "${startDate!!.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}T00:00:00Z"
                        val formattedEndDate = "${endDate!!.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}T23:59:59Z"

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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentDatePickerDialog(
    onDateSelected: (LocalDate) -> Unit,
    onDismiss: () -> Unit,
    minDateMillis: Long? = null
) {
    val selectableDates = remember(minDateMillis) {
        object : SelectableDates {
            override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                return minDateMillis?.let { utcTimeMillis >= it } ?: true
            }
        }
    }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        selectableDates = selectableDates
    )

    DatePickerDialog(
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        // --- INICIO DE LA MODIFICACIÓN 2 ---
                        // Se convierte la fecha seleccionada desde milisegundos usando UTC para asegurar la fecha correcta.
                        val selectedDate = java.time.Instant.ofEpochMilli(millis)
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
                        onDateSelected(selectedDate)
                    }
                    onDismiss()
                }
            ) {
                Text("Aceptar")
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text("Cancelar")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateTournamentScreenPreview() {
    UPRivalsTheme {
        CreateTournamentScreen(rememberNavController())
    }
}
