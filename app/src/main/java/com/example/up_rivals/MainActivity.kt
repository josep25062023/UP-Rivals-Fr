// En: MainActivity.kt
package com.example.up_rivals

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.up_rivals.navigation.AppNavigation
import com.example.up_rivals.ui.theme.UPRivalsTheme // El nombre puede variar un poco si le cambiaste el nombre a tu app

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            UPRivalsTheme { // Tu tema envuelve toda la app
                AppNavigation() // Llamamos a nuestro sistema de navegación
            }
        }
    }
}