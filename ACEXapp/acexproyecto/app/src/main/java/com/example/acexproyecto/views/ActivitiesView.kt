package com.example.acexproyecto.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.acexproyecto.ui.theme.ButtonPrimary
import com.example.acexproyecto.ui.theme.TextPrimary

// Define color palette for the app
val PrimaryColor = Color(0xFF79B3BB)   // Primary color (light blue)
val SecondaryColor = Color(0xFF9AE7DF) // Secondary color (lighter blue)
val TertiaryColor = Color(0xFFD0E8F2)  // Tertiary color (light cyan)
val BackgroundColor = Color(0xFFF1F1F1) // Background color (off white)
val TextColor = Color(0xFF000000) // Text color (black)


@Composable
fun ActivitiesView(navController: NavController) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {TopBar(navController)},
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

                    // Barra de búsqueda (debajo de la TopAppBar)
                    SearchBar()

                    Spacer(modifier = Modifier.height(16.dp))
                    // Box para Mis Actividades (arriba)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.5f) // Esto asegura que ocupe la mitad superior de la pantalla
                    ) {
                        MisActividades(navController)
                    }

                    // Espacio entre las secciones
                    Spacer(modifier = Modifier.height(16.dp))

                    // Box para Otras Actividades (abajo)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f) // Esto asegura que ocupe la mitad inferior de la pantalla
                    ) {
                        OtrasActividades(navController)
                    }
                }
            }
        },
        bottomBar = { BottomDetailBar(navController) }
    )
}

// Search bar with updated colors
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar() {
    var searchText by remember { mutableStateOf("") } // Almacenar el texto de búsqueda

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp), // Espaciado superior
            label = { Text("Buscar actividad...", color = TextPrimary) },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Buscar", tint = TextColor)
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = BackgroundColor,
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = SecondaryColor
            )
        )
    }
}

// MisActividades with updated colors
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun MisActividades(navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Actividades",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally),
            color = TextPrimary // Text color for section title
        )

        // LazyColumn for activities
        LazyColumn() {
            items(2) { _ ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    repeat(2) {
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp)
                                .height(180.dp)
                            .clickable {
                            // Navegar a otra pantalla con la información de la actividad
                            navController.navigate("detalle_actividad_screen")
                        },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = ButtonPrimary) // Card color
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                GlideImage(
                                    model = "https://via.placeholder.com/150", // Image example
                                    contentDescription = "Actividad",
                                    modifier = Modifier
                                        .size(100.dp)
                                        .clip(CircleShape)
                                        .padding(bottom = 8.dp),
                                    contentScale = ContentScale.Crop
                                )
                                Text(
                                    text = "Actividad",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary,
                                    modifier = Modifier.padding(bottom = 8.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// OtrasActividades with updated colors
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun OtrasActividades(navController: NavController) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Mis Actividades",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally),
            color = TextPrimary // Text color for section title
        )

        // LazyRow for other activities
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            items(5) { _ ->
                Card(
                    modifier = Modifier
                        .padding(8.dp)
                        .width(150.dp)
                        .height(180.dp)
                        .clickable {
                            // Navegar a otra pantalla con la información de la actividad
                            navController.navigate("detalle_actividad_screen")
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = ButtonPrimary) // Card color
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GlideImage(
                            model = "https://via.placeholder.com/150", // Image example
                            contentDescription = "Actividad",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .padding(bottom = 8.dp),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = "Actividad",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val navController = rememberNavController()
    ActivitiesView(navController)
}
