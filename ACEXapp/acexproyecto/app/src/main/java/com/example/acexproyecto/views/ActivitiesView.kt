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
import com.example.acexproyecto.model.GrupoParticipanteResponse
import com.example.acexproyecto.objetos.Usuario
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


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
    var selectedDate by remember { mutableStateOf<Long?>(null) }  // Para el filtro de fecha (un solo día)
    var selectedCourse by remember { mutableStateOf<String?>(null) } // Ahora es String, ya que el curso es un nombre (no Long)

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
                        onFilterSelected = { filter, date, course ->
                            selectedFilter = filter  // Actualiza el filtro de texto
                            selectedDate = date  // Actualiza la fecha seleccionada (solo un día)
                            selectedCourse = course  // Actualiza el curso seleccionado
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Box para Mis Actividades (arriba)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.5f) // Esto asegura que ocupe la mitad superior de la pantalla
                    ) {
                        AllActividades(navController, selectedFilter, searchQuery, selectedDate,
                            selectedCourse
                        )
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
    onFilterSelected: (String?, Long?, String?) -> Unit  // Callback para recibir el filtro seleccionado
) {
    var searchText by remember { mutableStateOf("") }
    val filterOptions = listOf("Por Fecha", "Por Curso", "Por Estado", "Todas")
    var selectedFilter by remember { mutableStateOf<String?>(null) }  // Valor por defecto
    var showPopup by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }  // Fecha seleccionada para el filtro de fechas
    var showCoursePicker by remember { mutableStateOf(false) }  // Mostrar diálogo de selección de curso
    var selectedCourse by remember { mutableStateOf<String?>(null) } // Curso seleccionado
    var showStatePicker by remember { mutableStateOf(false) }  // Mostrar diálogo de selección de estado
    var selectedState by remember { mutableStateOf<String?>(null) }  // Estado seleccionado

    // Lista de cursos disponibles (puedes obtener esto desde la base de datos)
    val cursos = listOf(
        "ASIR1", "ASIR2", "AYF1", "AYF2", "BACH1", "BACH2", "DAM1", "DAM2", "DAW1", "DAW2",
        "DPFM1", "DPFM2", "ESO1", "ESO2", "ESO3", "ESO4", "FPBFM1", "FPBFM2", "FPBIC1", "FPBIC2",
        "GAD1", "GAD2", "MEC1", "MEC2", "PPFM1", "PPFM2", "SMR1", "SMR2"
    )

    // Lista de estados posibles
    val estadoOptions = listOf("SOLICITADA", "DENEGADA", "APROBADA", "REALIZADA", "REALIZANDOSE", "CANCELADA")

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
                                        when (filter) {
                                            "Por Curso" -> {
                                                // Activar el selector de curso
                                                showCoursePicker = true
                                            }
                                            "Por Fecha" -> {
                                                // Activar el selector de fecha
                                                showDatePicker = true
                                            }
                                            "Por Estado" -> {
                                                // Activar el selector de estado
                                                showStatePicker = true
                                            }
                                            else -> {
                                                selectedFilter = if (filter == "Todas") null else filter
                                                onFilterSelected(selectedFilter, null, null) // Llamamos al callback
                                                showPopup = false // Cerramos el popup
                                            }
                                        }
                                    }
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    fontWeight = if (filter == "Todas") FontWeight.Bold else FontWeight.Normal
                                ),
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }

        // Aquí aparece el selector de estado si `showStatePicker` es verdadero
        if (showStatePicker) {
            StatePickerDialog(
                estadoOptions = estadoOptions,
                onDismissRequest = { showStatePicker = false },
                onStateSelected = { state ->
                    selectedState = state
                    onFilterSelected(selectedFilter, selectedDate, selectedState) // Pasamos el estado seleccionado
                    showPopup = false // Cierra el popup
                    showStatePicker = false // Cierra el selector de estado
                }
            )
        }

        // Aquí aparece el selector de cursos si `showCoursePicker` es verdadero
        if (showCoursePicker) {
            CoursePickerDialog(
                cursos = cursos,
                onDismissRequest = { showCoursePicker = false },
                onCourseSelected = { course ->
                    selectedCourse = course
                    onFilterSelected(selectedFilter, selectedDate, selectedCourse) // Pasamos el curso seleccionado
                    showPopup = false // Cierra el popup
                    showCoursePicker = false // Cierra el selector de cursos
                }
            )
        }

        // Aquí aparece el selector de fechas si `showDatePicker` es verdadero
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                onDateSelected = { date ->
                    selectedDate = date
                    onFilterSelected(selectedFilter, selectedDate, selectedCourse) // Llamamos al callback con la fecha seleccionada
                    showPopup = false // Cierra el popup
                    showDatePicker = false // Cierra el selector de fechas
                }
            )
        }
    }
}

@Composable
fun StatePickerDialog(
    estadoOptions: List<String>,
    onDismissRequest: () -> Unit,
    onStateSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Selecciona un estado") },
        text = {
            LazyColumn(
                modifier = Modifier.heightIn(max = 300.dp), // Limitar el tamaño máximo
                contentPadding = PaddingValues(8.dp)
            ) {
                items(estadoOptions) { state ->
                    TextButton(onClick = {
                        onStateSelected(state)
                    }) {
                        Text(state)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
}


@Composable
fun CoursePickerDialog(
    cursos: List<String>,
    onDismissRequest: () -> Unit,
    onCourseSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text("Selecciona un curso") },
        text = {
            // Aquí usamos LazyColumn para hacer la lista de cursos scrollable
            LazyColumn(
                modifier = Modifier.heightIn(max = 300.dp), // Limitar el tamaño máximo
                contentPadding = PaddingValues(8.dp)
            ) {
                items(cursos) { course ->
                    TextButton(onClick = {
                        onCourseSelected(course)
                    }) {
                        Text(course)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Cancelar")
            }
        }
    )
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
    selectedDate: Long?, // Solo una fecha, no un rango
    selectedCourse: String? // Recibe el curso seleccionado
) {
    val actividades = remember { mutableStateListOf<ActividadResponse>() }
    val gruposParticipantes = remember { mutableStateListOf<GrupoParticipanteResponse>() } // Lista de participantes
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // Función para convertir la fecha de String a Long (milisegundos) pero solo con la parte de la fecha (sin hora)
    fun stringToDate(dateString: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = format.parse(dateString)
        return date?.time ?: 0L // Si no puede parsear, devuelve 0L
    }

    // Lógica de filtrado
    fun filterActividades(
        actividadesList: List<ActividadResponse>,
        gruposParticipantesList: List<GrupoParticipanteResponse>,
        selectedCourse: String?,
        selectedState: String?  // Agregamos el parámetro para el estado seleccionado
    ): List<ActividadResponse> {
        return actividadesList.filter { actividad ->

            // Filtro por estado (si se ha seleccionado uno)
            val matchesState = selectedState?.let {
                actividad.estado.equals(it, ignoreCase = true)
            } ?: true // Si no se seleccionó un estado, no se filtra por estado

            // Filtro por título de búsqueda
            val matchesSearchQuery = actividad.titulo.contains(searchQuery, ignoreCase = true)

            // Filtro por fecha
            val matchesDate = selectedDate?.let {
                val activityStartDate = stringToDate(actividad.fini)
                val activityEndDate = stringToDate(actividad.ffin)

                val calendar = Calendar.getInstance()
                calendar.timeInMillis = selectedDate
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val selectedDateNoTime = calendar.timeInMillis

                calendar.timeInMillis = activityStartDate
                calendar.set(Calendar.HOUR_OF_DAY, 0)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                val activityStartDateNoTime = calendar.timeInMillis

                selectedDateNoTime == activityStartDateNoTime
            } ?: true

            // Filtro por curso
            val matchesCourse = selectedCourse?.let {
                val gruposDelCurso = gruposParticipantesList.filter { grupoParticipante ->
                    grupoParticipante.grupo.curso.codCurso == it
                }
                gruposDelCurso.any { grupoParticipante ->
                    grupoParticipante.actividades.id == actividad.id
                }
            } ?: true

            // Todos los filtros
            matchesState && matchesSearchQuery && matchesCourse && matchesDate
        }
    }

    // Carga las actividades y grupos participantes en un hilo secundario
    LaunchedEffect(selectedFilter, searchQuery, selectedDate, selectedCourse) {
        withContext(Dispatchers.IO) {
            try {
                // Realizamos las peticiones a la API
                val response = RetrofitClient.instance.getActividades().execute()
                val groupsResponse = RetrofitClient.instance.getGrupoParticipantes().execute() // Obtener los grupos
                if (response.isSuccessful && groupsResponse.isSuccessful) {
                    // Filtramos las actividades
                    val filteredActividades = filterActividades(
                        response.body() ?: emptyList(),
                        groupsResponse.body() ?: emptyList(),
                        selectedCourse,
                        selectedFilter // Asegúrate de pasar también el estado seleccionado si es necesario
                    )
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


@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun OtrasActividades(navController: NavController) {
    val actividades = remember { mutableStateListOf<ActividadResponse>() }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // Obtener el ID del profesor actual (esto debe estar disponible desde tu sistema de autenticación)
    val profesorId =
        Usuario.profesor?.uuid  // Asegúrate de que este sea el ID correcto del profesor

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                // Obtener todas las actividades
                val actividadesResponse = RetrofitClient.instance.getActividades().execute()
                val profesorParticipantesResponse = RetrofitClient.instance.getProfesoresparticipantes().execute() // Asegúrate de tener un endpoint para obtener esto

                if (actividadesResponse.isSuccessful && profesorParticipantesResponse.isSuccessful) {
                    val allActividades = actividadesResponse.body() ?: emptyList()
                    val profesorParticipantes = profesorParticipantesResponse.body() ?: emptyList()

                    // Filtrar las actividades asociadas al profesor actual
                    val profesorActividades = allActividades.filter { actividad ->
                        profesorParticipantes.any { profesorParticipante ->
                            profesorParticipante.profesor.uuid == profesorId && profesorParticipante.actividad.id == actividad.id
                        }
                    }
                    actividades.addAll(profesorActividades)
                } else {
                    errorMessage.value = "Error: ${actividadesResponse.code()}"
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