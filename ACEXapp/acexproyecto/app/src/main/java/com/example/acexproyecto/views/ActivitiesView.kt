/**
 * Aplicación de gestión de actividades extraescolares
 * Realizada por el grupo 1 de DAM2
 * Santiago Tamayo
 * Carmen Suarez
 */

package com.example.acexproyecto.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.example.acexproyecto.ui.theme.TextPrimary
import com.example.appacex.model.ActividadResponse
import com.example.appacex.model.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Popup
import com.example.acexproyecto.model.GrupoParticipanteResponse
import com.example.acexproyecto.objetos.Usuario
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


val TertiaryColor = Color(0xFFD0E8F2)
val TextColor = Color(0xFF000000)


@Composable
fun ActivitiesView(navController: NavController) {
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var selectedCourse by remember { mutableStateOf<String?>(null) }
    var selectedState by remember { mutableStateOf<String?>(null) }

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

                    SearchBar(
                        onSearchQueryChanged = { query ->
                            searchQuery = query
                        },
                        onFilterSelected = { filter, date, course, state ->
                            selectedFilter = filter
                            selectedDate = date
                            selectedCourse = course
                            selectedState = state
                        }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.5f)
                    ) {
                        AllActividades(navController, selectedFilter, searchQuery, selectedDate,
                            selectedCourse, selectedState)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
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
    onSearchQueryChanged: (String) -> Unit,
    onFilterSelected: (String?, Long?, String?, String?) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val filterOptions = listOf("Por Fecha", "Por Curso", "Por Estado", "Todas")
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var showPopup by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var showCoursePicker by remember { mutableStateOf(false) }
    var selectedCourse by remember { mutableStateOf<String?>(null) }
    var showStatePicker by remember { mutableStateOf(false) }
    var selectedState by remember { mutableStateOf<String?>(null) }

    val cursos = listOf(
        "ASIR1", "ASIR2", "AYF1", "AYF2", "BACH1", "BACH2", "DAM1", "DAM2", "DAW1", "DAW2",
        "DPFM1", "DPFM2", "ESO1", "ESO2", "ESO3", "ESO4", "FPBFM1", "FPBFM2", "FPBIC1", "FPBIC2",
        "GAD1", "GAD2", "MEC1", "MEC2", "PPFM1", "PPFM2", "SMR1", "SMR2"
    )

    val estadoOptions = listOf("SOLICITADA", "DENEGADA", "APROBADA", "REALIZADA", "REALIZANDOSE", "CANCELADA")

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                onSearchQueryChanged(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            label = { Text("Buscar actividad...", color = TextPrimary) },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Buscar", tint = TextColor)
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                IconButton(onClick = { showPopup = !showPopup }) {
                    Icon(imageVector = Icons.Filled.Menu, contentDescription = "Filtrar")
                }
            }
        )

        if (showPopup) {
            Popup(
                alignment = Alignment.TopStart,
                onDismissRequest = { showPopup = false }
            ) {
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
                                                showCoursePicker = true
                                            }
                                            "Por Fecha" -> {
                                                showDatePicker = true
                                            }
                                            "Por Estado" -> {
                                                showStatePicker = true
                                            }
                                            "Todas" -> {
                                                selectedFilter = null
                                                selectedDate = null
                                                selectedCourse = null
                                                selectedState = null
                                                onFilterSelected(selectedFilter, selectedDate, selectedCourse, selectedState)
                                                showPopup = false
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

        if (showStatePicker) {
            StatePickerDialog(
                estadoOptions = estadoOptions,
                onDismissRequest = { showStatePicker = false },
                onStateSelected = { state ->
                    selectedState = state
                    onFilterSelected(null, null, null, selectedState)
                    showStatePicker = false
                    showPopup = false
                }
            )
        }

        if (showCoursePicker) {
            CoursePickerDialog(
                cursos = cursos,
                onDismissRequest = { showCoursePicker = false },
                onCourseSelected = { course ->
                    selectedCourse = course
                    onFilterSelected(null, null, selectedCourse, null)
                    showCoursePicker = false
                    showPopup = false
                }
            )
        }

        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                onDateSelected = { date ->
                    selectedDate = date
                    onFilterSelected(null, selectedDate, null, null)
                    showDatePicker = false
                    showPopup = false
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
                modifier = Modifier.heightIn(max = 300.dp),
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
            LazyColumn(
                modifier = Modifier.heightIn(max = 300.dp),
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
    onDateSelected: (Long) -> Unit
) {
    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val datePickerDialog = remember {
        android.app.DatePickerDialog(context).apply {
            setOnDateSetListener { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val selectedDate = calendar.timeInMillis

                onDateSelected(selectedDate)
            }
        }
    }

    LaunchedEffect(Unit) {
        datePickerDialog.show()
    }

    Button(onClick = onDismissRequest) {
        Text("Cerrar")
    }
}

@Composable
fun AllActividades(
    navController: NavController,
    selectedFilter: String?,
    searchQuery: String,
    selectedDate: Long?,
    selectedCourse: String?,
    selectedState: String?
) {
    val actividades = remember { mutableStateListOf<ActividadResponse>() }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    fun stringToDate(dateString: String): Long {
        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = format.parse(dateString)
        return date?.time ?: 0L
    }

    fun filterActividades(
        actividadesList: List<ActividadResponse>,
        gruposParticipantesList: List<GrupoParticipanteResponse>,
        selectedCourse: String?,
        selectedState: String?
    ): List<ActividadResponse> {
        return actividadesList.filter { actividad ->

            val matchesState = selectedState?.let {
                actividad.estado?.equals(it, ignoreCase = true) == true
            } ?: true

            val matchesSearchQuery = actividad.titulo.contains(searchQuery, ignoreCase = true)

            val matchesDate = selectedDate?.let {
                val activityStartDate = stringToDate(actividad.fini)
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

            val matchesCourse = selectedCourse?.let {
                val gruposDelCurso = gruposParticipantesList.filter { grupoParticipante ->
                    grupoParticipante.grupo.curso.codCurso == it
                }
                gruposDelCurso.any { grupoParticipante ->
                    grupoParticipante.actividades.id == actividad.id
                }
            } ?: true

            matchesState && matchesSearchQuery && matchesCourse && matchesDate
        }
    }

    LaunchedEffect(selectedFilter, searchQuery, selectedDate, selectedCourse, selectedState) {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getActividades().execute()
                val groupsResponse = RetrofitClient.instance.getGrupoParticipantes().execute()
                if (response.isSuccessful && groupsResponse.isSuccessful) {
                    val filteredActividades = filterActividades(
                        response.body() ?: emptyList(),
                        groupsResponse.body() ?: emptyList(),
                        selectedCourse,
                        selectedState
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

    if (isLoading.value) {
        CircularProgressIndicator(modifier = Modifier.fillMaxSize())
    } else if (actividades.isEmpty() && errorMessage.value == null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "No hay actividades con ese filtro.",
                color = Color.Gray,
                style = MaterialTheme.typography.titleLarge
            )
        }
    } else if (errorMessage.value != null) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = errorMessage.value ?: "",
                color = Color.Red,
                style = MaterialTheme.typography.titleLarge
            )
        }
    } else {
        LazyColumn {
            items(actividades) { actividad ->
                // Mostrar cada actividad
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

    val profesorId = Usuario.profesor?.uuid

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val actividadesResponse = RetrofitClient.instance.getActividades().execute()
                val profesorParticipantesResponse = RetrofitClient.instance.getProfesoresparticipantes().execute() // Asegúrate de tener un endpoint para obtener esto

                if (actividadesResponse.isSuccessful && profesorParticipantesResponse.isSuccessful) {
                    val allActividades = actividadesResponse.body() ?: emptyList()
                    val profesorParticipantes = profesorParticipantesResponse.body() ?: emptyList()

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
            color = TextPrimary
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
                    ActivityCardItem(
                        activityName = actividad.titulo,
                        activityDate = actividad.fini ,
                        activityStatus = actividad.estado,
                        index = actividad.id,
                        navController = navController
                    )
                }
            }
        }
    }
}
