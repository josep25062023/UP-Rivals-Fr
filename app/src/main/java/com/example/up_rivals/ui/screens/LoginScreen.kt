// En: ui/screens/LoginScreen.kt
package com.example.up_rivals.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.up_rivals.R // MUY IMPORTANTE: Para acceder a R.drawable.img_logo
import com.example.up_rivals.ui.components.FormTextField
import com.example.up_rivals.ui.components.PrimaryButton
import com.example.up_rivals.ui.theme.PrimaryBlue
import com.example.up_rivals.ui.theme.SecondaryBlue
import com.example.up_rivals.ui.theme.UPRivalsTheme
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun LoginScreen(navController: NavController) {
    // ... (las variables de estado se quedan igual)
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // --- REEMPLAZA EL IMAGE ANTERIOR POR ESTE BOX ---
        Box(
            modifier = Modifier
                .size(180.dp) // El tamaño total del círculo
                .shadow(elevation = 8.dp, shape = CircleShape) // Mantenemos la sombra
                .clip(CircleShape) // Recortamos todo el Box para que sea circular
                .background(
                    // Creamos un degradado radial (del centro hacia afuera)
                    brush = Brush.radialGradient(
                        colors = listOf(
                            SecondaryBlue, // Un azul más claro en el centro
                            PrimaryBlue    // Nuestro azul principal en los bordes
                        )
                    )
                ),
            contentAlignment = Alignment.Center // Centramos el logo dentro del Box
        ) {
            // Ponemos tu logo dentro del Box, sobre el fondo de degradado
            Image(
                painter = painterResource(id = R.drawable.img_logo),
                contentDescription = "Logo de UP-Rivals",
                modifier = Modifier.size(160.dp) // Hacemos el logo un poco más pequeño que el fondo
            )
        }

        // 3. AUMENTAMOS EL ESPACIO DE ABAJO CON UN SPACER
        Spacer(modifier = Modifier.height(64.dp)) // <-- Juega con este valor

        Text(
            text = "Iniciar sesión",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(24.dp))

        FormTextField(
            value = username,
            onValueChange = { username = it },
            labelText = "Nombre de usuario"
        )

        Spacer(modifier = Modifier.height(16.dp))

        FormTextField(
            value = password,
            onValueChange = { password = it },
            labelText = "Contraseña",
            keyboardType = KeyboardType.Password,
            visualTransformation = PasswordVisualTransformation()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = {
                // CAMBIO 2: Añadimos la acción de navegar
                navController.navigate("forgot_password_screen")
            }) {
                Text("¿Olvidaste tu contraseña?")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        PrimaryButton(
            text = "Iniciar sesión",
            onClick = { /* TODO: Lógica de inicio de sesión */ }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.weight(1f))

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("¿No tienes cuenta?")
            TextButton(onClick = { navController.navigate("register_screen") }) {
                Text("Regístrate aquí")
            }
        }
    }
}


// --- Preview para ver la pantalla completa ---
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    UPRivalsTheme {
        // Creamos un navController de mentira solo para que la preview funcione
        LoginScreen(navController = rememberNavController())
    }
}