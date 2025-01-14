package com.example.acexproyecto

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.acexproyecto.navigation.NavManager
import com.example.acexproyecto.ui.theme.acexproyecto_BaseTheme
import com.example.acexproyecto.views.MsalAppHolder

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val onInitialized: () -> Unit = {
            setContent {
                // Definir el estado para el modo oscuro
                var isDarkMode by remember { mutableStateOf(false) }

                // Función para cambiar el estado de isDarkMode
                val onThemeChanged: (Boolean) -> Unit = { darkMode ->
                    isDarkMode = darkMode
                }

                // Crear el navController para la navegación
                val navController = rememberNavController()

                acexproyecto_BaseTheme(darkTheme = isDarkMode) {
                    // Pasamos el navController, isDarkMode y onThemeChanged a NavManager
                    NavManager(
                        navController = navController,
                        isDarkMode = isDarkMode,
                        onThemeChanged = onThemeChanged
                    )
                }
            }
        }

        MsalAppHolder.initialize(application, onInitialized)
    }
}




