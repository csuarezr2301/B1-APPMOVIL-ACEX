package com.example.acexproyecto.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
<<<<<<< Updated upstream
=======
import androidx.compose.foundation.lazy.items
>>>>>>> Stashed changes
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.acexproyecto.ui.theme.ButtonPrimary
import com.example.acexproyecto.ui.theme.TextPrimary
import com.example.appacex.model.ActividadResponse
import com.example.appacex.model.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.TextFieldDefaults
<<<<<<< Updated upstream
=======
import androidx.compose.ui.window.Popup
>>>>>>> Stashed changes

// Define color palette for the app
val PrimaryColor = Color(0xFF79B3BB)   // Primary color (light blue)
val SecondaryColor = Color(0xFF9AE7DF) // Secondary color (lighter blue)
val TertiaryColor = Color(0xFFD0E8F2)  // Tertiary color (light cyan)
val BackgroundColor = Color(0xFFF1F1F1) // Background color (off white)
val TextColor = Color(0xFF000000) // Text color (black)


@Composable
fun ActivitiesView(navController: NavController) {
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
<<<<<<< Updated upstream
        topBar = {TopBar(navController)},
=======
        topBar = { TopBar(navController) },
>>>>>>> Stashed changes
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

                    // Barra de búsqueda con filtro
                    SearchBar(
                        onSearchQueryChanged = { query ->
                            searchQuery = query  // Actualiza el texto de búsqueda
                        },
                        onFilterSelected = { filter ->
                            selectedFilter = filter  // Actualiza el filtro
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Box para Mis Actividades (arriba)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.5f) // Esto asegura que ocupe la mitad superior de la pantalla
                    ) {
<<<<<<< Updated upstream
                        AllActividades(navController)
=======
                        AllActividades(navController, selectedFilter, searchQuery)
>>>>>>> Stashed changes
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

<<<<<<< Updated upstream
// Search bar with updated colors
@OptIn(ExperimentalMaterial3Api::class)
=======


// Barra de búsqueda con filtro
>>>>>>> Stashed changes
@Composable
fun SearchBar(
    onSearchQueryChanged: (String) -> Unit, // Callback para recibir el texto de búsqueda
    onFilterSelected: (String?) -> Unit // Callback para recibir el filtro seleccionado
) {
    var searchText by remember { mutableStateOf("") }
    val filterOptions = listOf("Aprobada", "Realizada", "Cancelada", "Todas")
    var selectedFilter by remember { mutableStateOf<String?>(null) }  // Valor por defecto
    var showPopup by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Barra de búsqueda
        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                onSearchQueryChanged(it) // Actualizamos la búsqueda cada vez que cambia el texto
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp), // Espaciado superior
            label = { Text("Buscar actividad...", color = TextPrimary) },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Buscar", tint = TextColor)
            },
            singleLine = true,
<<<<<<< Updated upstream
            shape = RoundedCornerShape(8.dp)
            /*colors = TextFieldDefaults.outlinedTextFieldColors(
                containerColor = BackgroundColor,
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = SecondaryColor
            )*/
        )
    }
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun AllActividades(navController: NavController) {
    val actividades = remember { mutableStateListOf<ActividadResponse>() }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getActividades().execute()
                if (response.isSuccessful) {
                    val approvedActividades = response.body()?.filter { it.estado == "APROBADA" } ?: emptyList()
                    actividades.addAll(approvedActividades)
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

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Actividades",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally),
            color = TextPrimary // Text color for section title
        )

        // LazyVerticalGrid for activities
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(actividades.size) { index ->
                val actividad = actividades[index]
                Card(
                    modifier = Modifier
                        .height(200.dp)
                        .clickable {
                            // Navegar a otra pantalla con la información de la actividad
                            navController.navigate("detalle_actividad_screen/${actividad.id}")
                        },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Card color
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        GlideImage(
                            model = "https://via.placeholder.com/150", // Image example
                            contentDescription = actividad.titulo,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .padding(bottom = 8.dp),
                            contentScale = ContentScale.Crop
                        )
                        Text(
                            text = actividad.titulo,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary,
                            modifier = Modifier.padding(bottom = 8.dp),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.Center
                        )
=======
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                // Ícono de filtro dentro de la barra de búsqueda
                IconButton(onClick = { showPopup = !showPopup }) {
                    Icon(imageVector = Icons.Filled.Menu, contentDescription = "Filtrar")
                }
            }
        )

        // Si showPopup es verdadero, se muestra el popup
        if (showPopup) {
            Popup(
                alignment = Alignment.TopStart,
                onDismissRequest = { showPopup = false }
            ) {
                // Aquí creamos el contenido del Popup
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = TertiaryColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        filterOptions.forEach { filter ->
                            Text(
                                text = filter,
                                modifier = Modifier
                                    .clickable {
                                        // Cuando se selecciona "Todas", asignamos null para no aplicar filtro
                                        selectedFilter = if (filter == "Todas") null else filter
                                        onFilterSelected(selectedFilter) // Llamamos al callback
                                        showPopup = false // Cerramos el popup
                                    }
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary
                            )
                        }
>>>>>>> Stashed changes
                    }
                }
            }
        }
    }
}

<<<<<<< Updated upstream
=======
@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun AllActividades(navController: NavController, selectedFilter: String?, searchQuery: String) {
    val actividades = remember { mutableStateListOf<ActividadResponse>() }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // Actualizamos la carga de actividades y filtrado aquí
    LaunchedEffect(selectedFilter, searchQuery) {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getActividades().execute()
                if (response.isSuccessful) {
                    // Filtramos actividades aquí
                    val filteredActividades = response.body()?.filter { actividad ->
                        // Si no hay filtro, mostramos todas las actividades
                        val matchesFilter = if (selectedFilter != null) {
                            actividad.estado.equals(selectedFilter, ignoreCase = true)
                        } else {
                            true // Si no hay filtro, no se aplica ninguno (mostrar todas)
                        }

                        // Filtramos por búsqueda
                        val matchesSearchQuery = actividad.titulo.contains(searchQuery, ignoreCase = true)

                        // Solo mantenemos actividades que coinciden con ambos filtros
                        matchesFilter && matchesSearchQuery
                    } ?: emptyList()

                    actividades.clear()
                    actividades.addAll(filteredActividades)
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

    // Composición de la interfaz de usuario
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Actividades",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = 15.dp)
                .align(Alignment.CenterHorizontally),
            color = TextPrimary
        )

        if (isLoading.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (errorMessage.value != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = errorMessage.value ?: "Unknown error", color = Color.Red)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(actividades.size) { index ->
                    val actividad = actividades[index]
                    // Usamos el componente ActivityCardItem aquí
                    ActivityCardItem(
                        activityName = actividad.titulo,
                        activityDate = actividad.fini, // Asegúrate de que 'fecha' sea un campo válido
                        activityStatus = actividad.estado,
                        index = actividad.id, // Usamos el ID para navegar a la pantalla de detalles
                        navController = navController
                    )
                }
            }
        }
    }
}


>>>>>>> Stashed changes
@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun OtrasActividades(navController: NavController) {
    val actividades = remember { mutableStateListOf<ActividadResponse>() }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getActividades().execute()
                if (response.isSuccessful) {
                    val approvedActividades = response.body()?.filter { it.estado == "APROBADA" } ?: emptyList()
                    actividades.addAll(approvedActividades)
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

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Mis Actividades",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally),
            color = TextPrimary // Color del texto para el título de la sección
        )

        if (isLoading.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (errorMessage.value != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
<<<<<<< Updated upstream
                Text(text = errorMessage.value ?: "Unknown error", color = Color.Red)
=======
                Text(text = errorMessage.value ?: "Error desconocido", color = Color.Red)
>>>>>>> Stashed changes
            }
        } else {
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(actividades.size) { index ->
                    val actividad = actividades[index]
<<<<<<< Updated upstream
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .width(150.dp)
                            .height(100.dp)
                            .clickable {
                                // Navegar a otra pantalla con la información de la actividad
                                navController.navigate("detalle_actividad_screen/${actividad.id}")
                            },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant) // Card color
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = actividad.titulo,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                modifier = Modifier.padding(bottom = 8.dp),
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
=======
                    // Usamos el componente ActivityCardItem
                    ActivityCardItem(
                        activityName = actividad.titulo,
                        activityDate = actividad.fini , // Asegúrate de que 'fecha' sea un campo válido
                        activityStatus = actividad.estado,
                        index = actividad.id, // Usamos el ID para navegar a la pantalla de detalles
                        navController = navController
                    )
>>>>>>> Stashed changes
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
