package com.example.acexproyecto.views

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.acexproyecto.objetos.Usuario
import com.example.acexproyecto.ui.theme.* // Asegúrate de importar tus colores

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsView(navController: NavController, isDarkMode: Boolean, onThemeChanged: (Boolean) -> Unit) {
    // Estructura principal con la barra inferior
    Scaffold(
        topBar = {TopBar(navController)},
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {
                    UserInfo()
                    Spacer(modifier = Modifier.height(24.dp))
                    UserDetails()
                    Spacer(modifier = Modifier.height(5.dp))
                    SettingsViewapp(navController, isDarkMode, onThemeChanged)
                }
            }
        },
        bottomBar = { BottomDetailBar(navController) }
    )
}

@Composable
fun SettingsViewapp(
    navController: NavController,
    isDarkMode: Boolean,
    onThemeChanged: (Boolean) -> Unit
) {
    // Variables para las opciones de ajustes
    val notificationsState = remember { mutableStateOf(true) }  // Notificaciones
    var showDialog by remember { mutableStateOf(false) } // Estado para mostrar el diálogo de confirmación de cerrar sesión

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
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

@Composable
fun UserInfo() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Información del Usuario",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary, // Usar color de texto primario
            fontSize = 22.sp, // Aumentar el tamaño de la fuente
            modifier = Modifier
                .fillMaxWidth() // Asegura que el modificador ocupe todo el ancho disponible
                .padding(bottom = 20.dp)
                .wrapContentWidth(Alignment.CenterHorizontally) // Centra el texto horizontalmente
        )

        // Imagen de perfil
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .align(Alignment.CenterHorizontally)
        ) {
            if (Usuario.photoPath.isNullOrEmpty()) {
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = getInitials(Usuario.displayName),
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                }
            } else {
                Image(
                    painter = rememberImagePainter(data = Usuario.photoPath),
                    contentDescription = "Profile Image",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        // Información básica (Nombre, Correo)
        Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = Usuario.displayName, // Nombre del usuario
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary // Usar color de texto primario
            )
            Text(
                text = Usuario.account, // Correo del usuario
                fontSize = 16.sp,
                color = TextPrimary // Usar color de texto primario
            )
        }
    }
}

@Composable
fun UserDetails() {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Información detallada (Contraseña, Rol, Activo, Departamento)

        UserDetailField(label = "Rol", value = "Administrador")
        Spacer(modifier = Modifier.height(10.dp))
        UserDetailField(label = "Departamento", value = "Informática")
    }
}

@Composable
fun UserDetailField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary) // Usar color de texto primario

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth(),
            color = TextPrimary // Usar color de texto primario
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    val navController = rememberNavController()
    SettingsView(navController = navController, isDarkMode = false) { }
}