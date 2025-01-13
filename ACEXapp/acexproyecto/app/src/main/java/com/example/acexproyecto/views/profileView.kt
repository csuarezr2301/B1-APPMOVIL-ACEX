package com.example.acexproyecto.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.acexproyecto.R
import com.example.acexproyecto.ui.theme.*  // Importar los colores personalizados


@Composable
fun profileView(navController: NavController) {
    Scaffold(
        topBar = {TopBar()},
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    UserInfo()
                    Spacer(modifier = Modifier.height(24.dp))
                    UserDetails()
                }
            }
        },
        bottomBar = { BottomDetailBar(navController) }
    )
}

@Composable
fun UserInfo() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Información del Usuario",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary, // Usar color de texto primario
            fontSize = 28.sp, // Aumentar el tamaño de la fuente
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
            Image(
                painter = painterResource(id = R.drawable.images),
                contentDescription = "Perfil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Accent) // Fondo con color Accent
            )
        }

        // Información básica (Nombre, Correo)
        Column(
            modifier = Modifier
                .padding(top = 16.dp)
                .align(Alignment.CenterHorizontally)
        ) {
            Text(
                text = "Juan Pérez", // Nombre del usuario
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary // Usar color de texto primario
            )
            Text(
                text = "juan.perez@email.com", // Correo del usuario
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
        UserDetailField(label = "Contraseña", value = "********")
        Spacer(modifier = Modifier.height(10.dp))
        UserDetailField(label = "Rol", value = "Administrador")
        Spacer(modifier = Modifier.height(10.dp))
        UserDetailField(label = "Activo", value = "Sí")
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
fun ProfileViewPreview() {
    val navController = rememberNavController()
    profileView(navController)
}
