/**
 * Aplicación de gestión de actividades extraescolares
 * Realizada por el grupo 1 de DAM2
 * Santiago Tamayo
 * Carmen Suarez
 */

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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import coil.compose.rememberImagePainter
import com.example.acexproyecto.objetos.Usuario
import com.example.acexproyecto.ui.theme.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsView(navController: NavController, isDarkMode: Boolean, onThemeChanged: (Boolean) -> Unit) {
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
    val notificationsState = remember { mutableStateOf(true) }
    var showDialog by remember { mutableStateOf(false) }

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
            textAlign = TextAlign.Center,
            color = TextPrimary
        )

        SettingsOption(
            label = "Notificaciones",
            description = "Recibe notificaciones de la app",
            isChecked = notificationsState.value,
            onCheckedChange = { notificationsState.value = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        SettingsOption(
            label = "Modo oscuro",
            description = "Cambiar entre modo claro y oscuro",
            isChecked = isDarkMode,
            onCheckedChange = {
                onThemeChanged(it)
            }
        )

    }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("¿Estás seguro que quieres cerrar sesión?", color = TextPrimary) },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog = false
                        navController.navigate("principal")
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
                checkedThumbColor = Accent,
                uncheckedThumbColor = Color.Gray,
                checkedTrackColor = Accent.copy(alpha = 0.3f),
                uncheckedTrackColor = Color.LightGray,
                uncheckedBorderColor = Color.Transparent
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
            color = TextPrimary,
            fontSize = 22.sp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .wrapContentWidth(Alignment.CenterHorizontally)
        )

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

        Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = Usuario.displayName,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = Usuario.account,
                fontSize = 16.sp,
                color = TextPrimary
            )
        }
    }
}

@Composable
fun UserDetails() {
    Column(modifier = Modifier.fillMaxWidth()) {

        UserDetailField(label = "Rol", value = getRoleDisplayName(Usuario.profesor?.rol ?: ""))
        Spacer(modifier = Modifier.height(10.dp))
        UserDetailField(label = "Departamento", value = Usuario.profesor?.depart?.nombre ?: "Sin Departamento")
    }
}

@Composable
fun UserDetailField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary)

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = value,
            fontSize = 18.sp,
            modifier = Modifier.fillMaxWidth(),
            color = TextPrimary
        )
    }
}

fun getRoleDisplayName(role: String): String {
    return when (role) {
        "PROF" -> "Profesor"
        "ED" -> "Equipo Directivo"
        "ADM" -> "Admin"
        else -> "Desconocido"
    }
}