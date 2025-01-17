package com.example.acexproyecto.views

import Calendario
import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.acexproyecto.R
import com.example.acexproyecto.objetos.Loading
import com.example.acexproyecto.objetos.Usuario
import com.example.acexproyecto.ui.theme.*  // Importa los colores personalizados
import com.example.appacex.model.ActividadResponse
import com.example.appacex.model.RetrofitClient
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.exception.MsalException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeView(navController: NavController, onLoadingComplete: () -> Unit) {
    LaunchedEffect(Unit) {
        onLoadingComplete()
    }
    // Estructura principal con la barra inferior
        Scaffold(
            topBar = { TopBar(navController) }, // Barra superior con el logo

            content = { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    ContentDetailView(navController) // Contenido principal
                }
            },
            bottomBar = { BottomDetailBar(navController) }, // Barra inferior
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(), // Asegura que el Box ocupe to do el ancho disponible
                contentAlignment = Alignment.Center // Centra el contenido dentro del Box
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logosinnombre),
                    contentDescription = "Logo de la App",
                    modifier = Modifier
                        .size(100.dp) // Ajustar aquí el tamaño del logo
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.faq),
                    contentDescription = "FAQ",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = {
                // Implement your logout logic here
                MsalAppHolder.msalApp?.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
                    override fun onSignOut() {
                        // Navigate back to the login screen
                        navController.navigate("principal")
                    }

                    override fun onError(exception: MsalException) {
                        Log.e("TopBar", "Error during sign out", exception)
                    }
                })
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.logout),
                    contentDescription = "Logout",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
    )

    // Mostrar el diálogo con las preguntas frecuentes
    if (showDialog) {
        FAQDialog(onDismiss = { showDialog = false })
    }
}

@Composable
fun ContentDetailView(navController: NavController) {
    val activities = remember { mutableStateListOf<ActividadResponse>() }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getActividades().execute()
                if (response.code() == 500) {
                    Log.e("HomeView", "Internal Server Error: ${response.code()}")
                    errorMessage.value = "Internal Server Error. Please try again later."
                } else {
                    Log.e("HomeView", "Response: ${response.code()}")
                    if (response.isSuccessful) {
                        val approvedActivities = response.body() ?: emptyList()
                        activities.addAll(approvedActivities)
                    } else {
                        errorMessage.value = "Error: ${response.code()}"
                    }
                }
                if (response.isSuccessful) {
                    val approvedActivities = response.body() ?: emptyList()
                    activities.addAll(approvedActivities)
                } else {
                    errorMessage.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage.value = "Exception: ${e.message}"
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 12.dp) // Adjust padding to avoid overlap with BottomAppBar
    ) {
        if (isLoading.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (errorMessage.value != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = errorMessage.value ?: "Unknown error", color = Color.Red)
            }
        } else {
            LazyColumn {
                item {
                    // Información del usuario
                    UserInformation()
                }
                item {
                    // Espacio para el calendario
                    CalendarView()
                }
            }
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
            ) {
                items(activities.size) { index ->
                    val actividad = activities[index]
                    ActivityCardItem(
                        activityName = actividad.titulo,
                        activityDate = actividad.fini,
                        activityStatus = actividad.estado,
                        index = actividad.id,
                        navController = navController
                    )
                }
            }
        }
    }
}

// Composable que muestra las preguntas frecuentes en un diálogo emergente
@Composable
fun FAQDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Preguntas Frecuentes", color = TextPrimary) },
        text = {
            Column {
                Text("1. ¿Cómo agregar una nueva actividad?", color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Respuesta: Puedes agregar una nueva actividad desde la sección de actividades.", color = TextPrimary)

                Spacer(modifier = Modifier.height(8.dp))

                Text("2. ¿Cómo ver mi perfil?", color = TextPrimary)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Respuesta: Puedes ver tu perfil en la sección 'Perfil' en la barra de navegación.", color = TextPrimary)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar", color = Color.White)
            }
        }
    )
}

@Composable
fun UserInformation() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 48.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Row para la foto, nombre y email
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(30.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                // Ícono de perfil más grande
                if (Usuario.photoPath.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
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
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(Usuario.displayName, fontSize = 20.sp, color = TextPrimary) // Color de texto
                    Text(Usuario.account, fontSize = 16.sp, color = TextPrimary) // Color de texto
                }
            }
        }
    }
}

fun getInitials(name: String?): String {
    return name?.split(" ")?.mapNotNull { it.firstOrNull()?.toString() }?.take(2)?.joinToString("")?.uppercase() ?: "?"
}

@Composable
fun CalendarView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(), // Hace que el Box ocupe todo el ancho
            contentAlignment = Alignment.Center // Centra el contenido dentro del Box
        ) {
            Text(
                "Calendario de Actividades",
                color = TextPrimary,
                fontSize = 20.sp, // Tamaño más grande
                fontWeight = FontWeight.Bold, // Hacer el texto en negrita
            )
        }

            Spacer(modifier = Modifier.height(16.dp))

            Calendario(Usuario.msalToken, Usuario.calendarId)

        }
}

@Composable
fun ActivityCardItem(
    activityName: String,
    activityDate: String,
    activityStatus: String,
    index: Int,
    navController: NavController
) {
    // Color de íconos según el estado de la actividad
    val iconColor = when (activityStatus) {
        "APROBADA" -> TextPrimary // Verde para aprobada
        "REALIZADA" -> TextPrimary // Amarillo para realizada
        "CANCELADA" -> TextPrimary // Rojo para cancelada
        else -> TextPrimary // Azul para pendiente
    }

    // Card con sombra
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(150.dp)
            .height(110.dp)
            .clickable {
                // Navegar a otra pantalla con la información de la actividad
                navController.navigate("detalle_actividad_screen/${index}")
            }
            .shadow(8.dp, RoundedCornerShape(8.dp)), // Sombra debajo de la tarjeta
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant // Color de fondo para las tarjetas
        ),
        shape = RoundedCornerShape(8.dp) // Bordes redondeados para la card
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título de la actividad
            Text(
                activityName,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            // Fecha y hora
            Text(activityDate, color = TextPrimary, fontSize = 14.sp)

            // Íconos o imágenes para representar el estado de la actividad
            when (activityStatus) {
                "PENDIENTE" -> {
                    Icon(
                        imageVector = Icons.Filled.Lock , // Reloj para pendiente
                        contentDescription = "Pendiente",
                        tint = iconColor,
                        modifier = Modifier.size(30.dp)
                    )
                }
                "APROBADA" -> {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle, // Check (tick) para aprobada
                        contentDescription = "Aprobada",
                        tint = iconColor,
                        modifier = Modifier.size(30.dp)
                    )
                }
                "CANCELADA" -> {
                    Icon(
                        imageVector = Icons.Filled.Close, // Cruz para cancelada
                        contentDescription = "Cancelada",
                        tint = iconColor,
                        modifier = Modifier.size(30.dp)
                    )
                }
                else -> {
                    // Si el estado no es ninguno de los anteriores, mostrar un reloj por defecto
                    Icon(
                        imageVector = Icons.Filled.Lock,
                        contentDescription = "Pendiente",
                        tint = iconColor,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun BottomDetailBar(navController: NavController) {
    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp),
    ) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Íconos de la barra inferior
            IconButton(
                onClick = { if (currentRoute != "maps") navController.navigate("maps") },
                modifier = Modifier.size(65.dp)
            ) {
                Icon(imageVector = Icons.Filled.LocationOn, contentDescription = "Maps", tint = TextPrimary, modifier = Modifier.shadow(elevation = if (currentRoute == "maps") 68.dp else 0.dp, spotColor = MaterialTheme.colorScheme.primary))
            }

            IconButton(
                onClick = { if (currentRoute != "activities") navController.navigate("activities") },
                modifier = Modifier.size(65.dp)
            ) {
                Icon(imageVector = Icons.Filled.Star, contentDescription = "Activities", tint = TextPrimary, modifier = Modifier.shadow(if (currentRoute == "activities") 68.dp else 0.dp, spotColor = MaterialTheme.colorScheme.primary))
            }

            IconButton(
                onClick = { if (currentRoute != "home") navController.navigate("home") },
                modifier = Modifier.size(65.dp)
            ) {
                Icon(imageVector = Icons.Filled.Home, contentDescription = "Home", tint = TextPrimary, modifier = Modifier.shadow(if (currentRoute == "home") 68.dp else 0.dp, spotColor = MaterialTheme.colorScheme.primary))
            }

            IconButton(
                onClick = { if (currentRoute != "chat") navController.navigate("chat") },
                modifier = Modifier.size(65.dp)
            ) {
                Icon(imageVector = Icons.Filled.Email , contentDescription = "Chat", tint = TextPrimary, modifier = Modifier.shadow(if (currentRoute == "chat") 68.dp else 0.dp, spotColor = MaterialTheme.colorScheme.primary))
            }

            IconButton(
                onClick = { if (currentRoute != "settingsandprofile") navController.navigate("settingsandprofile") },
                modifier = Modifier.size(65.dp)
            ) {
                Icon(imageVector = Icons.Filled.Person , contentDescription = "Perfil", tint = TextPrimary, modifier = Modifier.shadow(if (currentRoute == "settingsandprofile") 68.dp else 0.dp, spotColor = MaterialTheme.colorScheme.primary))
            }
        }
    }
}

@Preview
@Composable
fun PreviewHomeView() {
    // Crear un NavController simulado para el preview
    val navController = rememberNavController()
    HomeView(navController = navController, { Loading.isLoading = false})
}