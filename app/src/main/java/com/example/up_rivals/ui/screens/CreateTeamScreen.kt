// En: ui/screens/CreateTeamScreen.kt
package com.example.up_rivals.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.up_rivals.ui.components.FormTextField
import com.example.up_rivals.ui.components.PrimaryButton
import com.example.up_rivals.ui.theme.UPRivalsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateTeamScreen(navController: NavController) {
    var teamName by remember { mutableStateOf("") }
    var memberId by remember { mutableStateOf("") }
    var membersList by remember { mutableStateOf(listOf<String>()) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Nuevo Equipo", fontWeight = FontWeight.Bold) },
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

                // Sección para añadir integrantes
                Text("Añadir Integrantes", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FormTextField(
                        value = memberId,
                        onValueChange = { memberId = it },
                        labelText = "ID del Jugador",
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(onClick = {
                        if (memberId.isNotBlank()) {
                            membersList = membersList + memberId // Añade el ID a la lista
                            memberId = "" // Limpia el campo de texto
                        }
                    }) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir integrante")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                // Lista de integrantes añadidos
                membersList.forEach { id ->
                    Text(" - Jugador con ID: $id")
                }
            }

            // Botón para crear el equipo
            Spacer(modifier = Modifier.height(16.dp))
            PrimaryButton(text = "Crear Equipo y Enviar Solicitud", onClick = { /* TODO */ })
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CreateTeamScreenPreview() {
    UPRivalsTheme {
        CreateTeamScreen(rememberNavController())
    }
}