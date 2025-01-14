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
import com.example.acexproyecto.fragment.LoginDialogFragment
import com.example.acexproyecto.ui.theme.Background
import com.example.acexproyecto.ui.theme.TextPrimary
import com.example.acexproyecto.ui.theme.ButtonPrimary
import com.example.acexproyecto.ui.theme.Accent
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import android.app.Application
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.fragment.app.FragmentActivity
import com.microsoft.identity.client.AcquireTokenParameters
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.Prompt
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.exception.MsalException


object MsalAppHolder {
    var msalApp: ISingleAccountPublicClientApplication? = null

    fun initialize(application: Application, onInitialized: () -> Unit) {
        PublicClientApplication.createSingleAccountPublicClientApplication(
            application,
            R.raw.auth_config_single_account,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(app: ISingleAccountPublicClientApplication) {
                    msalApp = app
                    Log.d("MainActivity", "Created MSAL app")
                    onInitialized()
                }

                override fun onError(exception: MsalException) {
                    Log.e("MainActivity", "Error initializing MSAL app", exception)
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginView(navController: NavController) {
    // Variables de estado para el nombre de usuario y la contraseña
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current as ComponentActivity
    var isLoading by remember { mutableStateOf(false) }
    var isLoggedIn by remember { mutableStateOf(false) }


    fun showLoginDialog() {
        val msalApp = MsalAppHolder.msalApp
        if (msalApp != null) {
            val parameters = AcquireTokenParameters.Builder()
                .startAuthorizationFromActivity(context)
                .withScopes(listOf("User.Read", "Calendars.Read"))
                .withPrompt(Prompt.SELECT_ACCOUNT)
                .withCallback(object : AuthenticationCallback {
                    override fun onSuccess(authenticationResult: IAuthenticationResult) {
                        // Manejar el éxito de la autenticación
                        val accessToken = authenticationResult.accessToken
                        Log.d("LoginView", "Access Token: $accessToken")
                        isLoading = false
                        isLoggedIn = true
                    }

                    override fun onError(exception: MsalException) {
                        // Manejar el error de la autenticación
                        isLoading = false
                    }

                    override fun onCancel() {
                        // Manejar la cancelación de la autenticación
                        isLoading = false
                    }
                })
                .build()

            isLoading = true
            msalApp.acquireToken(parameters)
        } else {

        }
    }

    LaunchedEffect(isLoading) {
        if (!isLoading && isLoggedIn) {
            navController.navigate("home")
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Background) // Fondo general
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = MaterialTheme.colorScheme.primary
            )
        } else {

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
                    showLoginDialog()
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
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    LoginView(navController = navController)
}