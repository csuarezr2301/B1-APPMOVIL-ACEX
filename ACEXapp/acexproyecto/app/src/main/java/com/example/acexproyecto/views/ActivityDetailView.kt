package com.example.acexproyecto.views

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.acexproyecto.R
import com.example.acexproyecto.camara.CamaraView
import com.example.acexproyecto.ui.theme.ButtonPrimary
import com.example.acexproyecto.ui.theme.TextPrimary
import com.example.acexproyecto.utils.SharedViewModel
import com.example.appacex.model.ActividadResponse
import com.example.appacex.model.ProfesorParticipanteResponse
import com.example.appacex.model.ProfesorResponse
import com.example.appacex.model.RetrofitClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acexproyecto.objetos.Loading.isLoading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

@Composable
fun ActivityDetailView(navController: NavController, activityId: String) {
    var activity by remember { mutableStateOf<ActividadResponse?>(null) }

    LaunchedEffect(activityId) {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getActividades().execute()
                if (response.isSuccessful) {
                    activity = response.body()?.find { it.id == activityId.toInt() }
                }
            } catch (e: Exception) {
                Log.e("ActivityDetailView", "Error fetching activity details", e)
            }
        }
    }

    //cambiar la top bar para añadir el boton de regreso
    Scaffold(
        topBar = { TopBar(navController) },
        content = { paddingValues ->
            ActivityDetailContent(
                navController = navController,
                modifier = Modifier.padding(paddingValues),
                actividad = activity
            )
        },
        bottomBar = { BottomDetailBar(navController) }
    )
}



@Composable
fun ActivityDetailContent(navController: NavController, modifier: Modifier = Modifier, actividad : ActividadResponse?) {
    var activityName by remember { mutableStateOf("Nombre de la actividad") }
    var activityDescription by remember { mutableStateOf("Descripción de la actividad. Aquí va la información detallada sobre la actividad, los objetivos y lo que ofrece.") }
    var isDialogVisible by remember { mutableStateOf(false) }

    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val getImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImages = selectedImages + it
        }
    }

    var isPopupVisible by remember { mutableStateOf(false) }
    var isCameraVisible by remember { mutableStateOf(false) }
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            BotonGuardar()
        }
        item {
            // Nombre de la actividad con icono para editar
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = actividad?.titulo ?: "Nombre de la actividad",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = {
                    isDialogVisible = true
                }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar",
                        tint = TextPrimary
                    )
                }
            }
        }

        item {
<<<<<<< Updated upstream
            Text(text = "Fecha: ${actividad?.fini} - ${actividad?.ffin}", color = TextPrimary)
=======
            Text(text = "Fecha: ${actividad?.fini} a ${actividad?.ffin}", color = TextPrimary)
>>>>>>> Stashed changes
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Fotos de la actividad",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            // LazyRow para mostrar fotos de la actividad
            LazyRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                items(selectedImages.size + 1) { index ->
                    if (index == 0) {
                        Icon(
                            painter = painterResource(id = R.drawable.foto),
                            contentDescription = "Seleccionar foto",
                            tint = ButtonPrimary,
                            modifier = Modifier
                                .size(120.dp)
                                .padding(end = 8.dp)
                                .clickable {
                                    isPopupVisible = true
                                }
                        )
                    } else {
                        Image(
                            painter = rememberImagePainter(selectedImages[index - 1]),
                            contentDescription = "Foto $index",
                            modifier = Modifier
                                .size(120.dp)
                                .padding(end = 8.dp)
                        )
                    }
                }
            }
        }

        item {
            // Descripción de la actividad
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .padding(vertical = 8.dp)
            ) {
                item {
                    Text(
                        text = "Descripción de la actividad",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                item {
                    Text(
                        text = actividad?.descripcion ?: "Descricion no encontrada",
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                }
            }
        }

        item {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .padding(vertical = 8.dp)
            ) {
                var activity by remember { mutableStateOf<ActividadResponse?>(null) }
                AlumnosAsistentes()
                ProfesoresAsistentes()
                Spacer(modifier = Modifier.height(15.dp))
                Observaciones(actividad = activity)
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Localización de la actividad",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        item {
            Spacer(modifier = Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .padding(4.dp)
                    .background(Color.Gray)
            ) {
                // Aquí se agregaría el mapa
            }
        }
    }

    if (isDialogVisible) {
        EditActivityDialog(
            activityName = activityName,
            activityDescription = activityDescription,
            onNameChange = { newName -> /* Handle Name Change */ },
            onDescriptionChange = { newDescription -> /* Handle Description Change */ },
            onDismiss = { isDialogVisible = false }
        )
    }

    if (isPopupVisible) {
        PopupMenu(
            onDismissRequest = { isPopupVisible = false },
            onSelectGallery = {
                getImageLauncher.launch("image/*")
                isPopupVisible = false
            },
            onSelectCamera = {
                isPopupVisible = false
                isCameraVisible = true
            }
        )
    }

    if (isCameraVisible) {
        CamaraView(
            onPhotoTaken = { uri ->
                selectedImages = selectedImages + uri
                isCameraVisible = false
                Toast.makeText(navController.context, "Foto tomada: $uri", Toast.LENGTH_SHORT).show()
            },
            context = navController.context,
            navController = navController
        )
    }
}


@Composable
fun AlumnosAsistentes() {
    var isDialogVisible by remember { mutableStateOf(false) }
    var numeroAlumnos by remember { mutableStateOf("10") }

    // Fila para Alumnos Asistentes
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Alumnos Asistentes",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = numeroAlumnos,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.weight(0.5f)
        )
        IconButton(onClick = { isDialogVisible = true }) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Editar",
                tint = TextPrimary
            )
        }
    }
    Spacer(modifier = Modifier.height(8.dp))

    // Diálogo para editar número de alumnos
    if (isDialogVisible) {
        AlertDialog(
            onDismissRequest = { isDialogVisible = false },
            title = { Text(text = "Editar número de alumnos") },
            text = {
                TextField(
                    value = numeroAlumnos,
                    onValueChange = { numeroAlumnos = it },
                    label = { Text("Número de alumnos") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = { isDialogVisible = false }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                Button(
                    onClick = { isDialogVisible = false }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ProfesoresAsistentes() {
    // Estado para los profesores generales, profesores asistentes y sus estados de carga
    var profesoresLista by remember { mutableStateOf<List<ProfesorResponse>>(emptyList()) }
    var profesoresAsistentes by remember { mutableStateOf<List<ProfesorParticipanteResponse>>(emptyList()) }
    var isLoadingProfesores by remember { mutableStateOf(true) }
    var isLoadingAsistentes by remember { mutableStateOf(true) }

    // Estado para la búsqueda y los profesores seleccionados (asociados a la actividad)
    var searchQuery by remember { mutableStateOf("") }
    var selectedProfesores by remember { mutableStateOf(setOf<String>()) }
    var isDialogVisible by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Llamada a la API para obtener los datos de los profesores y los asistentes
    LaunchedEffect(Unit) {
        // Ejecutamos las llamadas a la API en el Dispatcher.IO para evitar el error de red en el hilo principal
        try {
            withContext(Dispatchers.IO) {
                // Llamada a la API para obtener la lista de profesores
                val responseProfesores: Response<List<ProfesorResponse>> = RetrofitClient.instance.getProfesores().execute()
                if (responseProfesores.isSuccessful) {
                    profesoresLista = responseProfesores.body() ?: emptyList()
                    Log.d("ProfesoresAsistentes", "Profesores obtenidos: ${profesoresLista.size}")
                } else {
                    errorMessage = "Error al obtener la lista de profesores: ${responseProfesores.errorBody()}"
                }

                // Llamada a la API para obtener los profesores asistentes
                val responseAsistentes: Response<List<ProfesorParticipanteResponse>> = RetrofitClient.instance.getProfesoresparticipantes().execute()
                if (responseAsistentes.isSuccessful) {
                    profesoresAsistentes = responseAsistentes.body() ?: emptyList()
                    Log.d("ProfesoresAsistentes", "Profesores asistentes obtenidos: ${profesoresAsistentes.size}")
                } else {
                    errorMessage = "Error al obtener profesores asistentes: ${responseAsistentes.errorBody()}"
                }
            }
        } catch (e: Exception) {
            errorMessage = "Error al hacer la llamada a la API: ${e.message}"
            Log.e("ProfesoresAsistentes", "Error al hacer la llamada a la API", e)
        } finally {
            // Finalmente, actualizamos los estados de carga en el hilo principal
            withContext(Dispatchers.Main) {
                isLoadingProfesores = false
                isLoadingAsistentes = false
            }
        }
    }

    // Filtrar la lista de profesores generales según la búsqueda
    val filteredProfesores = profesoresLista.filter {
        it.nombre.contains(searchQuery, ignoreCase = true) || it.apellidos.contains(searchQuery, ignoreCase = true)
    }

    // Filtrar los profesores asistentes según la lista de profesores seleccionados
    val profesoresAsistentesFiltrados = profesoresLista.filter { profesor ->
        profesoresAsistentes.any { asistente ->
            asistente.profesor.uuid == profesor.uuid  // Compara UUIDs de los profesores
        }
    }

    // Mostrar un indicador de carga mientras se obtiene la respuesta
    if (isLoadingProfesores || isLoadingAsistentes) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator() // Indicador de carga
        }
    }

    // Fila para Profesores Asistentes
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Profesores Asistentes",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { isDialogVisible = true }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar",
                    tint = TextPrimary
                )
            }
        }

        // Mostrar la lista de profesores de la actividad debajo del título
        if (profesoresAsistentesFiltrados.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                profesoresAsistentesFiltrados.forEach { profesor ->
                    // Mostrar nombre y apellido de los profesores
                    Text(
                        text = "${profesor.nombre} ${profesor.apellidos}",
                        fontSize = 16.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp)
                    )
                }
            }
        } else {
            // Si no hay profesores asignados, mostrar un mensaje
            Text(
                text = "No hay profesores asignados.",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }

    // Mostrar el popup con la lista de profesores
    if (isDialogVisible) {
        AlertDialog(
            onDismissRequest = { isDialogVisible = false },
            title = { Text("Seleccionar Profesores") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Barra de búsqueda
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it }, // 'it' hace referencia al nuevo valor de searchQuery
                        label = { Text("Buscar Profesor") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    // Mostrar lista de profesores filtrados
                    if (filteredProfesores.isNotEmpty()) {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(filteredProfesores) { profesor ->
                                val nombreCompleto = "${profesor.nombre} ${profesor.apellidos}"
                                val isSelected = selectedProfesores.contains(nombreCompleto)

                                // Caja de selección (Checkbox) dentro de cada ítem
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            // Alternar entre agregar o quitar el profesor de la lista de seleccionados
                                            selectedProfesores = if (isSelected) {
                                                selectedProfesores - nombreCompleto
                                            } else {
                                                selectedProfesores + nombreCompleto
                                            }
                                        }
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { checked ->
                                            selectedProfesores = if (checked) {
                                                selectedProfesores + nombreCompleto
                                            } else {
                                                selectedProfesores - nombreCompleto
                                            }
                                        }
                                    )

                                    // Mostrar el nombre completo del profesor
                                    Text(
                                        text = nombreCompleto,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                            }
                        }
                    } else {
                        Text(
                            text = "No se encontraron resultados para la búsqueda.",
                            color = Color.Gray,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        // Guardar la lista de seleccionados
                        isDialogVisible = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(onClick = { isDialogVisible = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}



@Composable
fun Observaciones(actividad : ActividadResponse?) {
    var observaciones by remember { mutableStateOf(actividad?.incidencias ?: "No hay observaciones e incidencias") }
    var isDialogVisible by remember { mutableStateOf(false) }

    // Fila para Observaciones
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Observaciones",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = { isDialogVisible = true }) {
            Icon(
                imageVector = Icons.Filled.Edit,
                contentDescription = "Editar",
                tint = TextPrimary
            )
        }
    }
    Text(
        text = actividad?.incidencias ?: "No hay Observaciones e Incidencias",
        fontSize = 16.sp,
        color = TextPrimary
    )

    // Popup de edición de observaciones
    if (isDialogVisible) {
        AlertDialog(
            onDismissRequest = { isDialogVisible = false },
            title = { Text("Editar Observaciones") },
            text = {
                TextField(
                    value = observaciones,
                    onValueChange = { observaciones = it },
                    label = { Text("Observaciones") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5,
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                    keyboardActions = KeyboardActions.Default
                )
            },
            confirmButton = {
                Button(
                    onClick = { isDialogVisible = false } // Confirmar y cerrar el diálogo
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(
                    onClick = { isDialogVisible = false } // Cancelar y cerrar el diálogo
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}



@Composable
fun EditActivityDialog(
    activityName: String,
    activityDescription: String,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Editar actividad") },
        text = {
            Column {
                // Campo para el nombre de la actividad
                OutlinedTextField(
                    value = activityName,
                    onValueChange = onNameChange,
                    label = { Text("Nombre de la actividad") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Campo para la descripción de la actividad
                OutlinedTextField(
                    value = activityDescription,
                    onValueChange = onDescriptionChange,
                    label = { Text("Descripción de la actividad") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Guardar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

/*
@Composable
fun MapaActividad(modifier: Modifier = Modifier) {
    val torrelavega = LatLng(43.353, -4.064)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(torrelavega, 10f)
    }

    // Usamos un Box para organizar el mapa
    Box(modifier = modifier.fillMaxSize()) {

        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = com.google.maps.android.compose.rememberMarkerState(position = torrelavega),
                title = "Torrelavega",
                snippet = "Cantabria"
            )
        }
    }
}

*/



// Popup con opciones para seleccionar la foto
@Composable
fun PopupMenu(
    onDismissRequest: () -> Unit,
    onSelectGallery: () -> Unit,
    onSelectCamera: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = { Text("Seleccionar foto") },
            text = {
                Column {
                    TextButton(onClick = onSelectGallery) {
                        Text("Subir foto desde la galería")
                    }
                    TextButton(onClick = onSelectCamera) {
                        Text("Tomar foto")
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
}

@Composable
fun BotonGuardar() {
    Row(
        modifier = Modifier.fillMaxWidth(), // Hace que el Row ocupe todo el ancho disponible
        horizontalArrangement = Arrangement.End, // Alinea el contenido (el botón) a la derecha
        verticalAlignment = Alignment.CenterVertically // Opcional: centra el botón verticalmente
    ) {
        Button(
            onClick = {
                // Acción al presionar el botón
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = ButtonPrimary , // Cambia el color de fondo del botón
                contentColor = TextPrimary // Cambia el color del texto
            )
        ) {
            Text("Guardar")
        }
    }
}



@Preview(showBackground = true)
@Composable
fun ActivityScreenPreview() {
    val navController = rememberNavController()
    ActivityDetailView(navController, "11")
}