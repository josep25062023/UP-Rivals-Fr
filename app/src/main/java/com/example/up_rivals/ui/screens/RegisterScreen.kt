// En: ui/screens/RegisterScreen.kt
package com.example.up_rivals.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.up_rivals.network.dto.RegisterRequest
import com.example.up_rivals.ui.components.FormTextField
import com.example.up_rivals.ui.components.PrimaryButton
import com.example.up_rivals.ui.theme.UPRivalsTheme
import com.example.up_rivals.viewmodels.RegisterUiState
import com.example.up_rivals.viewmodels.RegisterViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController) {
    // --- AÑADIDO: Obtenemos el ViewModel y observamos su estado ---
    val viewModel: RegisterViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Tus variables de estado para el formulario se quedan igual
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    val roles = listOf("Jugador", "Organizador")
    var selectedRole by remember { mutableStateOf("") }
    var isRoleMenuExpanded by remember { mutableStateOf(false) }

    // --- AÑADIDO: Efecto para manejar el resultado de la API ---
    LaunchedEffect(key1 = uiState) {
        when (uiState) {
            is RegisterUiState.Success -> {
                // Registro exitoso, mostramos mensaje y volvemos al login
                Toast.makeText(context, "¡Registro exitoso! Por favor, inicia sesión.", Toast.LENGTH_LONG).show()
                navController.popBackStack()
            }
            is RegisterUiState.Error -> {
                // Mostramos el mensaje de error del ViewModel
                Toast.makeText(context, (uiState as RegisterUiState.Error).message, Toast.LENGTH_LONG).show()
            }
            else -> { /* No hacemos nada en Idle o Loading */ }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Botón para volver atrás")
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
            Text(
                text = "Crear cuenta nueva",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Tus campos de texto y menú desplegable se quedan igual
                FormTextField(value = fullName, onValueChange = { fullName = it }, labelText = "Nombre completo")
                FormTextField(value = email, onValueChange = { email = it }, labelText = "Email", keyboardType = KeyboardType.Email)
                FormTextField(value = password, onValueChange = { password = it }, labelText = "Contraseña", keyboardType = KeyboardType.Password, visualTransformation = PasswordVisualTransformation())
                FormTextField(value = confirmPassword, onValueChange = { confirmPassword = it }, labelText = "Confirmar contraseña", keyboardType = KeyboardType.Password, visualTransformation = PasswordVisualTransformation())
                FormTextField(value = phoneNumber, onValueChange = { phoneNumber = it }, labelText = "Numero telefonico", keyboardType = KeyboardType.Phone)
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
                    ExposedDropdownMenu(expanded = isRoleMenuExpanded, onDismissRequest = { isRoleMenuExpanded = false }) {
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
            }
            Spacer(modifier = Modifier.height(16.dp))

            // --- MODIFICADO: La lógica del botón ---
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                PrimaryButton(
                    text = "Registrarse",
                    enabled = uiState !is RegisterUiState.Loading,
                    onClick = {
                        // --- AÑADIDO: Validación de campos ---
                        if (fullName.isBlank() || email.isBlank() || password.isBlank() || phoneNumber.isBlank() || selectedRole.isBlank()) {
                            Toast.makeText(context, "Por favor, llena todos los campos.", Toast.LENGTH_SHORT).show()
                            return@PrimaryButton
                        }
                        if (password != confirmPassword) {
                            Toast.makeText(context, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show()
                            return@PrimaryButton
                        }

                        // Mapeamos el rol al formato que espera la API
                        val apiRole = when (selectedRole) {
                            "Jugador" -> "player"
                            "Organizador" -> "organizer"
                            else -> ""
                        }

                        // Creamos el objeto de la petición
                        val request = RegisterRequest(
                            name = fullName,
                            email = email,
                            password = password,
                            phone = phoneNumber,
                            role = apiRole
                        )

                        // Llamamos al ViewModel
                        viewModel.register(request)
                    }
                )
                if (uiState is RegisterUiState.Loading) {
                    CircularProgressIndicator()
                }
            }

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
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    UPRivalsTheme {
        RegisterScreen(rememberNavController())
    }
}