package com.example.acexproyecto.views

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import com.example.acexproyecto.ui.theme.* // Asegúrate de importar tus colores

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsView(navController: NavController, isDarkMode: Boolean, onThemeChanged: (Boolean) -> Unit) {
    // Estructura principal con la barra inferior
    Scaffold(
        topBar = {TopBar()},
        content = { paddingValues ->
            // Pasamos los valores de padding a la vista del mapa para que no quede debajo de la barra inferior
            SettingsViewapp(navController, isDarkMode, onThemeChanged, paddingValues)
        },
        bottomBar = { BottomDetailBar(navController) }
    )
}

@Composable
fun SettingsViewapp(
    navController: NavController,
    isDarkMode: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    paddingValues: PaddingValues
) {
    // Variables para las opciones de ajustes
    val notificationsState = remember { mutableStateOf(true) }  // Notificaciones
    var showDialog by remember { mutableStateOf(false) } // Estado para mostrar el diálogo de confirmación de cerrar sesión

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(top = paddingValues.calculateTopPadding())
            .padding(bottom = paddingValues.calculateBottomPadding())
    ) {
        Text(
            text = "Ajustes",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            textAlign = TextAlign.Center
        )

        // Opción de notificaciones
        SettingsOption(
            label = "Notificaciones",
            description = "Recibe notificaciones de la app",
            isChecked = notificationsState.value,
            onCheckedChange = { notificationsState.value = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Opción para cambiar tema
        SettingsOption(
            label = "Modo oscuro",
            description = "Cambiar entre modo claro y oscuro",
            isChecked = isDarkMode,
            onCheckedChange = {
                onThemeChanged(it) // Actualiza el estado del tema
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Opción para cerrar sesión
        Button(
            onClick = { showDialog = true }, // Muestra el diálogo de confirmación
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = ButtonPrimary) // Usar ButtonPrimary
        ) {
            Text("Cerrar sesión", color = Color.White) // Texto blanco en los botones
        }
    }

    // Mostrar el diálogo de confirmación si showDialog es verdadero
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false }, // Si el usuario toca fuera del diálogo, lo cierra
            title = { Text("¿Estás seguro que quieres cerrar sesión?", color = TextPrimary) },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        navController.navigate("principal") // Navegar a la pantalla de login
                    }
                ) {
                    Text("Sí", color = Color.White)
                }
            },
            dismissButton = {
                Button(onClick = { showDialog = false }) {
                    Text("No", color = Color.White)
                }
            }
        )
    }
}

@Composable
fun SettingsOption(
    label: String,
    description: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(text = label, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
            Text(text = description, style = MaterialTheme.typography.bodySmall, color = TextPrimary)
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Accent, // Usar el color Accent para el "thumb" del switch
                uncheckedThumbColor = Color.Gray,
                checkedTrackColor = Accent.copy(alpha = 0.3f), // Track más suave para el estado activado
                uncheckedTrackColor = Color.Gray
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    val navController = rememberNavController()
    SettingsView(navController = navController, isDarkMode = false) { }
}
