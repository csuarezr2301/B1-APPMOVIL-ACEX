package com.example.acexproyecto.views

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.acexproyecto.R
import com.example.acexproyecto.ui.theme.*  // Importa los colores personalizados

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeView(navController: NavController, displayName: String, photoPath: String, account: String) {
    // Estructura principal con la barra inferior
    Scaffold(
        topBar = { TopBar() }, // Barra superior con el logo

        content = {
            Log.e("HomeView", "displayName: $displayName, photoPath: $photoPath, account: $account")
            ContentDetailView(navController, displayName, photoPath, account) // Contenido principal
        },
        bottomBar = { BottomDetailBar(navController) }, // Barra inferior
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar() {
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
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = TopAppBarBackground
        ),
    )
}

@Composable
fun ContentDetailView(navController: NavController, displayName: String, photoPath: String, account: String) {
    var showDialog by remember { mutableStateOf(false) } // Variable para mostrar el pop-up de preguntas frecuentes

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
    ) {
        item {
            // Información del usuario
            Log.e("HomeView", "displayName: $displayName, photoPath: $photoPath")
            UserInformation(displayName, photoPath, account)
        }
        item {
            // Espacio para el calendario
            CalendarView()
        }
        item {
            //  espacio entre el calendario y las actividades
            Spacer(modifier = Modifier.height(15.dp))

            // LazyRow con cartas de actividades
            LazyRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(5) { index ->
                    ActivityCardItem(activityName = "Actividad $index", activityDate = "01-02-2025", index = index, navController)
                }
            }
        }
        item {
            // Sección de Preguntas Frecuentes
            FAQSection(onClick = { showDialog = true })
        }
    }

    // Mostrar el diálogo con las preguntas frecuentes
    if (showDialog) {
        FAQDialog(onDismiss = { showDialog = false })
    }
}

@Composable
fun FAQSection(onClick: () -> Unit) {
    // Este es el ítem de Preguntas Frecuentes en el contenido principal
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Preguntas Frecuentes", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text(
            "Accede a las preguntas",
            fontSize = 16.sp,
            color = TextPrimary.copy(alpha = 0.6f), // Color más suave
            modifier = Modifier
                .padding(top = 8.dp)
                .clickable { onClick() } // Abre el popup al hacer clic
        )
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
fun UserInformation(displayName: String, photoPath: String, account: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Log.e("HomeView", "displayName: $displayName, photoPath: $photoPath")
            // Row para la foto, nombre y email
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                // Ícono de perfil más grande
                if (photoPath.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getInitials(displayName),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }
                } else {
                    Image(
                        painter = rememberImagePainter(data = photoPath),
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
                    Text(displayName, fontSize = 20.sp, color = TextPrimary) // Color de texto
                    Text(account, fontSize = 16.sp, color = TextPrimary) // Color de texto
                }

                Spacer(modifier = Modifier.weight(1f))
                // Botón de preguntas frecuentes
                //FAQPopupButton()
            }
        }
    }
}

fun getInitials(name: String?): String {
    return name?.split(" ")?.mapNotNull { it.firstOrNull()?.toString() }?.take(2)?.joinToString("")?.uppercase() ?: "?"
}
/*
@Composable
fun FAQPopupButton() {
    var showDialog by remember { mutableStateOf(false) }


    //Botón para abrir el popup
    IconButton(onClick = { showDialog = true }) {
        Icon(imageVector = Icons.Default.Search, contentDescription = "Preguntas Frecuentes", tint = ButtonPrimary) // Color del ícono
    }


    // Popup con las preguntas frecuentes
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Preguntas Frecuentes", color = TextPrimary) }, // Título en color de texto primario
            text = { Text("Aquí irían las preguntas frecuentes.", color = TextPrimary) }, // Texto en color de texto primario
            confirmButton = {
                Button(onClick = { showDialog = false }) {
                    Text("Cerrar", color = Color.White)
                }
            }
        )
    }
}
*/

@Composable
fun CalendarView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
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

            Image(
                painter = painterResource(id = R.drawable.calendario),
                contentDescription = "Calendario",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

        }

}

@Composable
fun ActivityCardItem(activityName: String, activityDate: String, index: Int, navController: NavController) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(150.dp)
            .clickable {
                // Navegar a otra pantalla con la información de la actividad
                navController.navigate("detalle_actividad_screen")
            },
        colors = CardDefaults.cardColors(
            containerColor = ButtonPrimary // Color de fondo para las tarjetas
        )
    ) {
        Column(
            modifier = Modifier
                .height(180.dp)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Título de la actividad
            Text(activityName, color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)

            // Fecha y hora
            Text(activityDate, color = TextPrimary, fontSize = 14.sp)

            // Íconos o imágenes para representar la actividad
            Icon(
                imageVector = Icons.Filled.Star,
                contentDescription = "Evento",
                tint = TextPrimary,
                modifier = Modifier.size(30.dp)
            )
        }
    }
}

@Composable
fun BottomDetailBar(navController: NavController) {
    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Íconos de la barra inferior
            IconButton(
                onClick = { navController.navigate("maps") },
                modifier = Modifier.size(65.dp)
            ) {
                Icon(imageVector = Icons.Filled.LocationOn, contentDescription = "Maps", tint = TextPrimary)
            }

            IconButton(
                onClick = { navController.navigate("activities") },
                modifier = Modifier.size(65.dp)
            ) {
                Icon(imageVector = Icons.Filled.Star, contentDescription = "Activities", tint = TextPrimary)
            }

            IconButton(
                onClick = { navController.navigate("home") },
                modifier = Modifier.size(65.dp)
            ) {
                Icon(imageVector = Icons.Filled.Home, contentDescription = "Home", tint = TextPrimary)
            }

            IconButton(
                onClick = { navController.navigate("chat") },
                modifier = Modifier.size(65.dp)
            ) {
                Icon(imageVector = Icons.Filled.Email , contentDescription = "Chat", tint = TextPrimary)
            }

            IconButton(
                onClick = { navController.navigate("settingsandprofile") },
                modifier = Modifier.size(65.dp)
            ) {
                Icon(imageVector = Icons.Filled.Person , contentDescription = "Perfil", tint = TextPrimary)
            }
        }
    }
}

@Preview
@Composable
fun PreviewHomeView() {
    // Crear un NavController simulado para el preview
    val navController = rememberNavController()
    HomeView(navController = navController, displayName = "Usuario de Prueba", photoPath = "", account = "")
}
