package com.example.acexproyecto.views

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.acexproyecto.R
import com.example.acexproyecto.ui.theme.Background
import com.example.acexproyecto.ui.theme.TextPrimary
import com.example.acexproyecto.ui.theme.Accent
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import android.app.Application
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.ui.platform.LocalContext
import com.microsoft.identity.client.AcquireTokenParameters
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.Prompt
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.exception.MsalException
import com.example.acexproyecto.objetos.Usuario
import com.example.acexproyecto.utils.fetchUserProfile
import com.microsoft.graph.http.GraphServiceException
import com.microsoft.graph.requests.GraphServiceClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.CompletableFuture


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

@Composable
fun LoginView(navController: NavController) {
    // Variables de estado para el nombre de usuario y la contraseña
    val context = LocalContext.current as ComponentActivity
    var isLoading by remember { mutableStateOf(false) }


    fun showLoginDialog() {
        val graphScopes = listOf(
            "User.Read",
            "Calendars.Read"
        )

        val apiScopes = listOf(
            "api://7c80ff29-dc1d-47a3-9cc3-78997d1de943/access_as_user"
        )

        val msalApp = MsalAppHolder.msalApp
        if (msalApp != null) {
            val parameters = AcquireTokenParameters.Builder()
                .startAuthorizationFromActivity(context)
                .withScopes(graphScopes)
                .withPrompt(Prompt.SELECT_ACCOUNT)
                .withCallback(object : AuthenticationCallback {
                    override fun onSuccess(authenticationResult: IAuthenticationResult) {
                        // Manejar el éxito de la autenticación
                        Usuario.msalToken = authenticationResult.accessToken
                        CoroutineScope(Dispatchers.Main).launch {
                            val calendarId = fetchCalendarId(authenticationResult.accessToken, "ACEX")
                            if (calendarId != null) {
                                Usuario.calendarId = calendarId
                            }
                        }
                        fetchUserProfile(context, authenticationResult) {
                            Usuario.account = authenticationResult.account?.username ?: ""
                            navController.navigate("home") {
                                popUpTo("principal") { inclusive = true }
                            }
                        }
                    }

                    override fun onError(exception: MsalException) {
                        Log.e("LoginView", "Authentication error: ${exception.message}", exception)
                        isLoading = false
                    }

                    override fun onCancel() {
                        Log.d("LoginView", "Authentication canceled")
                        isLoading = false
                    }
                })
                .build()

            isLoading = true
            msalApp.acquireToken(parameters)

            val apiParameters = AcquireTokenParameters.Builder()
                .startAuthorizationFromActivity(context)
                .withScopes(apiScopes)
                .withPrompt(Prompt.SELECT_ACCOUNT)
                .withCallback(object : AuthenticationCallback {
                    override fun onSuccess(authenticationResult: IAuthenticationResult) {
                        Usuario.apiToken = authenticationResult.accessToken
                    }

                    override fun onError(exception: MsalException) {
                        // Handle authentication error
                        Log.e("Authentication", "Custom API Error: ${exception.message}")
                    }

                    override fun onCancel() {
                        // Handle user canceling the authentication
                        Log.d("Authentication", "Custom API Authentication canceled")
                    }
                })
                .build()

            MsalAppHolder.msalApp?.acquireToken(apiParameters)
        } else {
            Log.e("LoginView", "MSAL app is not initialized")
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

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de Microsoft con su logo
                Button(
                    onClick = { showLoginDialog() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Accent // Color para Microsoft
                    )
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.microsoft), // Logo de Microsoft
                        contentDescription = "Microsoft Logo",
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = "Iniciar sesión con Microsoft", fontSize = 16.sp, color = Color.White)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

suspend fun fetchCalendarId(accessToken: String, calendarName: String): String? {
    if (accessToken.isEmpty()) {
        Log.e("fetchCalendarId", "Access token is empty")
        return null
    }

    return try {
        withContext(Dispatchers.IO) {
            val graphClient = GraphServiceClient
                .builder()
                .authenticationProvider { CompletableFuture.completedFuture(accessToken) }
                .buildClient()

            val calendars = graphClient.me().calendars().buildRequest()?.get()?.currentPage

            val calendar = calendars?.find { it.name == calendarName }
            if (calendar != null) {
                calendar.id
            } else {
                Log.e("fetchCalendarId", "Calendar with name $calendarName not found")
                null
            }
        }
    } catch (e: GraphServiceException) {
        Log.e("fetchCalendarId", "Error fetching calendar ID", e)
        null
    } catch (e: Exception) {
        Log.e("fetchCalendarId", "Unexpected error", e)
        null
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    val navController = rememberNavController()
    LoginView(navController = navController)
}