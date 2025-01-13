package com.example.acexproyecto.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.acexproyecto.R
import com.example.acexproyecto.ui.theme.Background
import com.example.acexproyecto.ui.theme.TextPrimary
import com.example.acexproyecto.ui.theme.ButtonPrimary
import com.example.acexproyecto.ui.theme.Accent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(navController: NavController) {
    // Variables de estado para el nombre de usuario y la contraseña
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background) // Fondo general
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logorecortado), // Asegúrate de tener el logo en la carpeta drawable
                contentDescription = "Logo",
                modifier = Modifier
                    .size(300.dp)
                    .padding(bottom = 0.dp) // Espaciado debajo del logo
            )

            // Título de Login
            Text(
                text = "Login",
                fontSize = 30.sp,
                color = TextPrimary, // Color del texto principal
                modifier = Modifier.padding(bottom = 32.dp)
            )

            // Campo de texto para el nombre de usuario con ícono
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario", color = TextPrimary) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Accent, // Color del borde cuando está enfocado
                    unfocusedBorderColor = ButtonPrimary, // Color del borde cuando no está enfocado
                    focusedLabelColor = Accent, // Color de la etiqueta enfocada
                    unfocusedLabelColor = TextPrimary, // Color de la etiqueta no enfocada
                    cursorColor = TextPrimary // Color del cursor
                ),
                leadingIcon = {
                    Image(
                        imageVector = Icons.Filled.Person, // Ícono de usuario
                        contentDescription = "User Icon",
                        modifier = Modifier.size(24.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(TextPrimary)
                    )
                }
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Campo de texto para la contraseña con ícono
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña", color = TextPrimary) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Accent,
                    unfocusedBorderColor = ButtonPrimary,
                    focusedLabelColor = Accent,
                    unfocusedLabelColor = TextPrimary,
                    cursorColor = TextPrimary // Color del cursor
                ),
                leadingIcon = {
                    Image(
                        imageVector = Icons.Filled.Lock, // Ícono de candado
                        contentDescription = "Password Icon",
                        modifier = Modifier.size(24.dp),
                        colorFilter = androidx.compose.ui.graphics.ColorFilter.tint(TextPrimary)
                    )
                }
            )
            Spacer(modifier = Modifier.height(32.dp))

            // Botón de inicio de sesión
            Button(
                onClick = { navController.navigate("home") },
                modifier = Modifier
                    .fillMaxWidth()  // Asegura que el botón ocupe to do el ancho disponible
                    .height(60.dp),  // Ajusta la altura del botón
                colors = ButtonDefaults.buttonColors(
                    containerColor = ButtonPrimary // Color del botón
                )
            ) {
                Text(
                    text = "Iniciar Sesión",
                    fontSize = 24.sp,  // Aumenta el tamaño de la fuente
                    color = Color.White
                )
            }


            Spacer(modifier = Modifier.height(16.dp))

            // Botón de Microsoft con su logo
            Button(
                onClick = {
                    // Aquí rediriges al usuario al flujo de inicio de sesión de Microsoft
                },
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .align(Alignment.CenterHorizontally)
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Accent // Color para Microsoft
                )
            ) {
                Image(
                    painter = painterResource(id = R.drawable.images), // Logo de Microsoft
                    contentDescription = "Microsoft Logo",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Iniciar sesión con Microsoft", fontSize = 16.sp, color = Color.White)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de "¿Olvidaste la contraseña?"
            TextButton(
                onClick = { navController.navigate("reset_password") }, // Redirige a recuperación de contraseña
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(text = "He olvidado la contraseña", color = Accent)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    LoginView(navController = navController)
}
