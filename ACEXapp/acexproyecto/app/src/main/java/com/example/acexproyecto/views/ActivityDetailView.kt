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
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.acexproyecto.R
import com.example.acexproyecto.camara.CamaraView
import com.example.acexproyecto.camara.takePhoto
import com.example.acexproyecto.ui.theme.ButtonPrimary
import com.example.acexproyecto.ui.theme.TextPrimary
import com.example.appacex.model.ActividadResponse
import com.example.appacex.model.RetrofitClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
            Text(text = "Fecha: ${actividad?.fini} - ${actividad?.ffin}", color = TextPrimary)
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
                AlumnosAsistentes()
                ProfesoresAsistentes()
                Spacer(modifier = Modifier.height(15.dp))
                Observaciones()
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
    var profesores by remember { mutableStateOf(listOf("Profesor 1", "Profesor 2")) }
    var isDialogVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    // Lista de profesores (puedes reemplazarla con datos reales)
    val profesoresLista = listOf(
        "Profesor 1", "Profesor 2", "Profesor 3", "Profesor 4", "Profesor 5",
        "Profesor 6", "Profesor 7", "Profesor 8", "Profesor 9", "Profesor 10"
    )
    // Lista de profesores seleccionados
    var selectedProfesores by remember { mutableStateOf(setOf<String>()) }

    // Filtrar la lista de profesores según la búsqueda
    val filteredProfesores = profesoresLista.filter {
        it.contains(searchQuery, ignoreCase = true)
    }

    // Fila para Profesores Asistentes
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
    Text(
        text = profesores.joinToString(", "),
        fontSize = 16.sp,
        color = TextPrimary
    )
    Spacer(modifier = Modifier.height(8.dp))

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
                        onValueChange = { searchQuery = it },
                        label = { Text("Buscar Profesor") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))



                    LazyColumn(modifier = Modifier.fillMaxWidth()) {
                        items(filteredProfesores) { profesor ->
                            val isSelected = selectedProfesores.contains(profesor)

                            // Caja de selección (Checkbox) dentro de cada ítem
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        // Alternar entre agregar o quitar el profesor de la lista de seleccionados
                                        if (isSelected) {
                                            selectedProfesores =
                                                selectedProfesores - profesor // Eliminar
                                        } else {
                                            selectedProfesores =
                                                selectedProfesores + profesor // Agregar
                                        }
                                    }
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = isSelected,
                                    onCheckedChange = {
                                        // Cambiar el estado al hacer clic en el checkbox
                                        if (it) {
                                            selectedProfesores = selectedProfesores + profesor
                                        } else {
                                            selectedProfesores = selectedProfesores - profesor
                                        }
                                    }
                                )

                                // Mostrar el nombre del profesor
                                Text(
                                    text = profesor,
                                    fontSize = 16.sp,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                        }
                    }

                    // Mostrar la lista de profesores seleccionados
                    if (selectedProfesores.isNotEmpty()) {
                        Text(
                            text = "Profesores seleccionados: ${selectedProfesores.joinToString(", ")}",
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { // Guardar la lista de seleccionados

                        // Actualizar la lista de profesores seleccionados
                        profesores = selectedProfesores.toList()
                        isDialogVisible = false
                    }
                ) {
                    Text("Guardar")
                }
            }
        )
    }
}



@Composable
fun Observaciones() {
    var observaciones by remember { mutableStateOf("Observaciones sobre la actividad") }
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
        text = observaciones,
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