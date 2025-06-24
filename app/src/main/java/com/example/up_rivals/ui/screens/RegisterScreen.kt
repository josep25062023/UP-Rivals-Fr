// En: ui/screens/RegisterScreen.kt
package com.example.up_rivals.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.up_rivals.ui.components.FormTextField
import com.example.up_rivals.ui.components.PrimaryButton
import com.example.up_rivals.ui.theme.UPRivalsTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    // Variables de estado para cada campo del formulario
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }

    // Estado para el menú desplegable de roles
    val roles = listOf("Jugador", "Organizador")
    var selectedRole by remember { mutableStateOf("") }
    var isRoleMenuExpanded by remember { mutableStateOf(false) }

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Botón para volver atrás"
                            )
                        }
                    }
                )
            }
        ) { innerPadding ->
            // --- COLUMNA EXTERIOR: Organiza todo verticalmente ---
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 24.dp)
            ) {
                Text(
                    text = "Crear cuenta nueva",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(24.dp))

                // --- COLUMNA INTERIOR: Contiene solo los campos que se deslizan ---
                Column(
                    modifier = Modifier
                        .weight(1f) // <-- Ocupa todo el espacio disponible, empujando los botones hacia abajo
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(16.dp) // Espacio entre cada campo
                ) {
                    FormTextField(value = fullName, onValueChange = { fullName = it }, labelText = "Nombre completo")
                    FormTextField(value = email, onValueChange = { email = it }, labelText = "Email", keyboardType = KeyboardType.Email)
                    FormTextField(value = password, onValueChange = { password = it }, labelText = "Contraseña", keyboardType = KeyboardType.Password, visualTransformation = PasswordVisualTransformation())
                    FormTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, labelText = "Confirmar contraseña", keyboardType = KeyboardType.Password, visualTransformation = PasswordVisualTransformation())
                    FormTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, labelText = "Numero telefonico", keyboardType = KeyboardType.Phone)

                    // Menú desplegable para seleccionar el rol
                    ExposedDropdownMenuBox(
                        expanded = isRoleMenuExpanded,
                        onExpandedChange = { isRoleMenuExpanded = !isRoleMenuExpanded }
                    ) {
                        FormTextField(
                            value = selectedRole,
                            onValueChange = {},
                            readOnly = true,
                            labelText = "Selecciona el rol",
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRoleMenuExpanded) },
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = isRoleMenuExpanded,
                            onDismissRequest = { isRoleMenuExpanded = false }
                        ) {
                            roles.forEach { role ->
                                DropdownMenuItem(
                                    text = { Text(text = role) },
                                    onClick = {
                                        selectedRole = role
                                        isRoleMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                } // --- Fin de la Columna Interior

                // --- Los botones ahora son hijos de la Columna Exterior ---
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(text = "Registrarse", onClick = { /* TODO: Lógica de registro */ })
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Ya cuentas con una cuenta?")
                    TextButton(onClick = { navController.popBackStack() }) {
                        Text("Inicia sesion")
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            } // --- Fin de la Columna Exterior
        }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    UPRivalsTheme {
        RegisterScreen(rememberNavController())
    }
}
