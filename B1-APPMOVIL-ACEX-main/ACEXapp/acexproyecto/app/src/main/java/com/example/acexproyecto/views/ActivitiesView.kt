package com.example.acexproyecto.views

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid

import androidx.compose.foundation.lazy.items

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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Popup
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


// Define color palette for the app
val PrimaryColor = Color(0xFF79B3BB)   // Primary color (light blue)
val SecondaryColor = Color(0xFF9AE7DF) // Secondary color (lighter blue)
val TertiaryColor = Color(0xFFC3E6F5)  // Tertiary color (light cyan)
val BackgroundColor = Color(0xFFF1F1F1) // Background color (off white)
val TextColor = Color(0xFF000000) // Text color (black)


@Composable
fun ActivitiesView(navController: NavController) {
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Long?>(null) }  // Para el filtro de fecha (un solo día)

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar(navController) },
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
                        onFilterSelected = { filter, date ->
                            selectedFilter = filter  // Actualiza el filtro de texto
                            selectedDate = date  // Actualiza la fecha seleccionada (solo un día)
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Box para Mis Actividades (arriba)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.5f) // Esto asegura que ocupe la mitad superior de la pantalla
                    ) {
                        AllActividades(navController, selectedFilter, searchQuery, selectedDate)
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

@Composable
fun SearchBar(
    onSearchQueryChanged: (String) -> Unit,  // Callback para recibir el texto de búsqueda
    onFilterSelected: (String?, Long?) -> Unit  // Callback para recibir el filtro seleccionado
) {
    var searchText by remember { mutableStateOf("") }
    val filterOptions = listOf("Aprobada", "Realizada", "Cancelada", "Todas", "Por Fecha")
    var selectedFilter by remember { mutableStateOf<String?>(null) }  // Valor por defecto
    var showPopup by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }  // Fecha seleccionada para el filtro de fechas

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
                .padding(top = 16.dp),  // Espaciado superior
            label = { Text("Buscar actividad...", color = TextPrimary) },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Buscar", tint = TextColor)
            },
            singleLine = true,
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
                                        if (filter == "Por Fecha") {
                                            // Activar el selector de fechas si se selecciona "Por Fecha"
                                            showDatePicker = true
                                        } else {
                                            // Si no es "Por Fecha", aplicamos el filtro de texto
                                            selectedFilter = if (filter == "Todas") null else filter
                                            onFilterSelected(selectedFilter, null) // Llamamos al callback
                                            showPopup = false // Cerramos el popup
                                        }
                                    }
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Aquí aparece el selector de fechas si `showDatePicker` es verdadero
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                onDateSelected = { date ->
                    selectedDate = date
                    Log.d("SelectedDate", "Selected Date: $selectedDate")
                    onFilterSelected(selectedFilter, selectedDate) // Llamamos al callback con la fecha seleccionada
                    showPopup = false // Cierra el popup
                    showDatePicker = false // Cierra el selector de fechas
                }
            )
        }
    }
}

@Composable
fun DatePickerDialog(
    onDismissRequest: () -> Unit,
    onDateSelected: (Long) -> Unit  // Callback con la fecha seleccionada
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    // Usamos un DatePicker para seleccionar fechas
    val datePickerDialog = remember {
        android.app.DatePickerDialog(context).apply {
            setOnDateSetListener { _, year, month, dayOfMonth ->
                // Cuando se selecciona una fecha, podemos manejar el valor.
                calendar.set(year, month, dayOfMonth)
                val selectedDate = calendar.timeInMillis

                // Llamamos al callback con las fechas seleccionadas
                onDateSelected(selectedDate)
            }
        }
    }

    // Mostrar el DatePicker si es necesario
    LaunchedEffect(Unit) {
        datePickerDialog.show()
    }

    // Botón para cerrar el dialogo si es necesario
    Button(onClick = onDismissRequest) {
        Text("Cerrar")
    }
}



@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun AllActividades(
    navController: NavController,
    selectedFilter: String?,
    searchQuery: String,
    selectedDate: Long? // Solo una fecha, no un rango
) {
    val actividades = remember { mutableStateListOf<ActividadResponse>() }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // Función para convertir la fecha de String a Long (milisegundos) pero solo con la parte de la fecha (sin hora)
    fun stringToDate(dateString: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = format.parse(dateString)
        return date?.time ?: 0L // Si no puede parsear, devuelve 0L
    }

    // Lógica de filtrado
    fun filterActividades(actividadesList: List<ActividadResponse>): List<ActividadResponse> {
        return actividadesList.filter { actividad ->
            val matchesFilter = selectedFilter?.let {
                actividad.estado.equals(it, ignoreCase = true)
            } ?: true

            val matchesSearchQuery = actividad.titulo.contains(searchQuery, ignoreCase = true)

            val matchesDate = selectedDate?.let {
                // Convertir la fecha de "fini" y "ffin" a milisegundos (sin hora)
                val activityStartDate = stringToDate(actividad.fini)
                val activityEndDate = stringToDate(actividad.ffin)

                // Asegurarse de que ambas fechas no tengan hora, solo el día
                // Eliminar horas, minutos, segundos y milisegundos de las fechas
                val selectedDateNoTime = selectedDate!! / (24 * 60 * 60 * 1000) * (24 * 60 * 60 * 1000) // Eliminar la parte de la hora
                val activityStartDateNoTime = activityStartDate / (24 * 60 * 60 * 1000) * (24 * 60 * 60 * 1000) // Solo la parte del día

                val isSameDay = selectedDateNoTime == activityStartDateNoTime

                Log.d("DateFilter", "Comparing dates: activity start = $activityStartDate, selected = $selectedDateNoTime, isSameDay = $isSameDay")
                Log.d("DateFilter", "Activity start date: $activityStartDate, Activity end date: $activityEndDate, Selected date: $selectedDate")

                isSameDay
            } ?: true // Si no hay fecha seleccionada, no filtramos por fecha

            matchesFilter && matchesSearchQuery && matchesDate
        }
    }

    // Actualizamos la carga de actividades y filtrado aquí
    LaunchedEffect(selectedFilter, searchQuery, selectedDate) {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getActividades().execute()
                if (response.isSuccessful) {
                    val filteredActividades = filterActividades(response.body() ?: emptyList())
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
                        activityDate = actividad.fini, // Asegúrate de que 'fini' sea un campo válido
                        activityStatus = actividad.estado,
                        index = actividad.id, // Usamos el ID para navegar a la pantalla de detalles
                        navController = navController
                    )
                }
            }
        }
    }
}




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
                Text(text = errorMessage.value ?: "Error desconocido", color = Color.Red)
            }
        } else {
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(actividades.size) { index ->
                    val actividad = actividades[index]
                    // Usamos el componente ActivityCardItem
                    ActivityCardItem(
                        activityName = actividad.titulo,
                        activityDate = actividad.fini , // Asegúrate de que 'fecha' sea un campo válido
                        activityStatus = actividad.estado,
                        index = actividad.id, // Usamos el ID para navegar a la pantalla de detalles
                        navController = navController
                    )
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
