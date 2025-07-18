package com.example.up_rivals.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.up_rivals.R
import com.example.up_rivals.ui.components.StatCard
import com.example.up_rivals.ui.theme.LightBlueBackground
import com.example.up_rivals.ui.theme.SubtleGrey
import com.example.up_rivals.ui.theme.UPRivalsTheme
import com.example.up_rivals.viewmodels.ProfileViewModel
import com.example.up_rivals.viewmodels.ProfileUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    // --- 1. Obtenemos el ViewModel y su estado ---
    val viewModel: ProfileViewModel = viewModel()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var notificationsEnabled by remember { mutableStateOf(true) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showProfilePictureDialog by remember { mutableStateOf(false) }
    var showViewProfilePictureDialog by remember { mutableStateOf(false) }

    // Launcher para seleccionar imagen de la galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            viewModel.updateProfilePicture(it, context)
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Cerrar Sesión") },
            text = { Text("¿Estás seguro de que quieres cerrar la sesión?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        // TODO: Aquí deberíamos llamar a una función de logout en AppNavigation
                        // que borre el token y luego navegue. Por ahora, solo navega.
                        navController.navigate("login_screen") {
                            popUpTo(0)
                        }
                    }
                ) { Text("Sí, cerrar sesión") }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("No") } }
        )
    }

    if (showProfilePictureDialog) {
        AlertDialog(
            onDismissRequest = { showProfilePictureDialog = false },
            title = { Text("Foto de Perfil") },
            text = { Text("¿Qué deseas hacer?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showProfilePictureDialog = false
                        imagePickerLauncher.launch("image/*")
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Actualizar foto")
                    }
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showProfilePictureDialog = false
                        showViewProfilePictureDialog = true
                    }
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Ver foto")
                    }
                }
            }
        )
    }

    if (showViewProfilePictureDialog) {
        Dialog(
            onDismissRequest = { showViewProfilePictureDialog = false },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable { showViewProfilePictureDialog = false }
            ) {
                when (val state = uiState) {
                    is ProfileUiState.Success -> {
                        AsyncImage(
                            model = state.user.profilePicture,
                            placeholder = painterResource(id = R.drawable.img_logo),
                            error = painterResource(id = R.drawable.img_logo),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                    else -> {
                        Image(
                            painter = painterResource(id = R.drawable.img_logo),
                            contentDescription = "Foto de perfil",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                IconButton(
                    onClick = { showViewProfilePictureDialog = false },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp)
                ) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Perfil", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Volver atrás")
                    }
                }
            )
        }
    ) { innerPadding ->
        // --- 2. Manejamos los estados de Carga, Error y Éxito ---
        when (val state = uiState) {
            is ProfileUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            is ProfileUiState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.message)
                }
            }

            is ProfileUiState.Success -> {
                val user = state.user

                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(horizontal = 24.dp)
                        .padding(top = 8.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp)) // Reducido de 16dp a 8dp
                    // --- 3. Mostramos la información real del usuario ---
                    AsyncImage(
                        model = user.profilePicture,
                        placeholder = painterResource(id = R.drawable.img_logo),
                        error = painterResource(id = R.drawable.img_logo),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .clickable {
                                showProfilePictureDialog = true
                            },
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(12.dp)) // Reducido de 16dp a 12dp
                    Text(
                        user.name,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        user.email,
                        style = MaterialTheme.typography.bodyMedium,
                        color = SubtleGrey
                    )
                    Text(
                        "ID: ${user.id}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = SubtleGrey
                    )
                    Spacer(modifier = Modifier.height(16.dp)) // Reducido de 24dp a 16dp

                    // --- El resto de la UI se queda igual ---
                    Button(
                        onClick = { navController.navigate("edit_profile_screen") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Editar Perfil", color = MaterialTheme.colorScheme.onPrimary)
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            "Configuración",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Notifications", modifier = Modifier.weight(1f))
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it })
                    }
                    Divider(modifier = Modifier.padding(vertical = 8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLogoutDialog = true },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Cerrar sesion",
                            modifier = Modifier.weight(1f),
                            color = MaterialTheme.colorScheme.error
                        )
                        Icon(
                            Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "Cerrar sesión",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}



@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ProfileScreenPreview() {
    UPRivalsTheme {
        ProfileScreen(rememberNavController())
    }
}