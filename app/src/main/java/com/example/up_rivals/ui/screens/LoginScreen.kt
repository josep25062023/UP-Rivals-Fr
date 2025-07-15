// En: ui/screens/LoginScreen.kt
package com.example.up_rivals.ui.screens

// --- AÑADIDO: Imports necesarios para el ViewModel, estado y efectos secundarios ---
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.up_rivals.R
import com.example.up_rivals.network.dto.LoginResponse
import com.example.up_rivals.network.dto.User
import com.example.up_rivals.ui.components.FormTextField
import com.example.up_rivals.ui.components.PrimaryButton
import com.example.up_rivals.ui.theme.PrimaryBlue
import com.example.up_rivals.ui.theme.SecondaryBlue
import com.example.up_rivals.ui.theme.UPRivalsTheme
// --- AÑADIDO: Import del ViewModel y el Estado de la UI ---
import com.example.up_rivals.viewmodels.LoginUiState
import com.example.up_rivals.viewmodels.LoginViewModel


@Composable
fun LoginScreen(
    navController: NavController, onLoginSuccess: (User) -> Unit) {
    // --- AÑADIDO: Obtenemos la instancia del ViewModel y observamos su estado ---
    val viewModel: LoginViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    // Tus variables de estado para los campos de texto se quedan igual
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    // --- AÑADIDO: Estado para controlar la visibilidad de la contraseña ---
    var passwordVisible by remember { mutableStateOf(false) }

    // --- AÑADIDO: Un LaunchedEffect para manejar los Toasts y la Navegación ---
    // Esto asegura que la navegación o el Toast solo se ejecuten una vez cuando el estado cambia
    LaunchedEffect(key1 = uiState) {
        when (val state = uiState) {
            is LoginUiState.Success -> {
                Toast.makeText(context, "¡Bienvenido!", Toast.LENGTH_SHORT).show()
                // --- CAMBIO 2: En lugar de navegar, llamamos a la función ---
                onLoginSuccess(state.user)
            }
            is LoginUiState.Error -> {
                Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
            }
            else -> { /* No hacer nada en Idle o Loading */ }
        }
    }

    // --- OPTIMIZADO: Mejor distribución del espacio vertical ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- REDUCIDO: Espaciado superior más pequeño ---
        Spacer(modifier = Modifier.height(48.dp))

        // --- REDUCIDO: Logo más pequeño ---
        Box(
            modifier = Modifier
                .size(140.dp)
                .shadow(elevation = 8.dp, shape = CircleShape)
                .clip(CircleShape)
                .background(brush = Brush.radialGradient(colors = listOf(SecondaryBlue, PrimaryBlue))),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_logo),
                contentDescription = "Logo de UP-Rivals",
                modifier = Modifier.size(120.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        FormTextField(
            value = username,
            onValueChange = { username = it },
            labelText = "Email",
            keyboardType = KeyboardType.Email
        )

        FormTextField(
            value = password,
            onValueChange = { password = it },
            labelText = "Contraseña",
            keyboardType = KeyboardType.Password,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(
                    onClick = { passwordVisible = !passwordVisible }
                ) {
                    Icon(
                        imageVector = if (passwordVisible) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                        contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña"
                    )
                }
            }
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = { navController.navigate("forgot_password_screen") }) {
                Text("¿Olvidaste tu contraseña?")
            }
        }

        // --- MODIFICADO: El botón ahora muestra una ruedita de carga ---
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            PrimaryButton(
                text = "Iniciar sesión",
                // Deshabilitamos el botón mientras está cargando
                enabled = uiState !is LoginUiState.Loading,
                onClick = {
                    // Llamamos al ViewModel para que inicie la lógica de login
                    viewModel.login(email = username, password = password)
                }
            )
            // Si el estado es "cargando", mostramos la ruedita
            if (uiState is LoginUiState.Loading) {
                CircularProgressIndicator(modifier = Modifier.size(32.dp))
            }
        }

        // --- OPTIMIZADO: Espaciado flexible pero controlado ---
        Spacer(modifier = Modifier.weight(0.3f))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("¿No tienes cuenta?")
            TextButton(onClick = { navController.navigate("register_screen") }) {
                Text("Regístrate aquí")
            }
        }

        // --- AÑADIDO: Espaciado inferior mínimo ---
        Spacer(modifier = Modifier.height(24.dp))
    }
}


@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    UPRivalsTheme {
        LoginScreen(
            navController = rememberNavController(),
            onLoginSuccess = {} // En la preview, no hace nada
        )
    }
}