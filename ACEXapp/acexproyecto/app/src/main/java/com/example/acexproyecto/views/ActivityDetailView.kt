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
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.example.acexproyecto.ui.theme.ButtonPrimary
import com.example.acexproyecto.ui.theme.TextPrimary
import com.example.appacex.model.ActividadResponse
import com.example.appacex.model.ProfesorParticipanteResponse
import com.example.appacex.model.ProfesorResponse
import com.example.appacex.model.RetrofitClient
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.withContext
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import com.example.acexproyecto.model.GrupoParticipanteResponse
import com.example.acexproyecto.model.GrupoResponse
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

var activity by mutableStateOf<ActividadResponse?>(null)
var allGrupos by mutableStateOf<List<GrupoResponse>>(emptyList())
var numeroAlumnos by mutableStateOf(0)
var grupoParticipantes by mutableStateOf<List<GrupoParticipanteResponse>>(emptyList())
var profAsistentes by mutableStateOf<List<ProfesorParticipanteResponse>>(emptyList())
var selectedImages by mutableStateOf<List<Uri>>(emptyList())
var deletedGrupoParticipantes by mutableStateOf<List<GrupoParticipanteResponse>>(emptyList())
var deletedProfesores by mutableStateOf<List<ProfesorParticipanteResponse>>(emptyList())

@Composable
fun ActivityDetailView(navController: NavController, activityId: String) {
    var isDataChanged by remember { mutableStateOf(false) }

    LaunchedEffect(activityId) {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getActividades().execute()
                if (response.isSuccessful) {
                    activity = response.body()?.find { it.id == activityId.toInt() }
                }
                val grupoResponse = RetrofitClient.instance.getGrupoParticipantes().execute()
                if (grupoResponse.isSuccessful) {
                    grupoParticipantes = grupoResponse.body()?.filter { it.actividades.id == activityId.toInt() } ?: emptyList()
                    val totalParticipantes = grupoParticipantes.sumOf { it.numParticipantes }
                    withContext(Dispatchers.Main) {
                        numeroAlumnos = totalParticipantes
                    }
                }
                val responseAsistentes = RetrofitClient.instance.getProfesoresparticipantes().execute()
                if (responseAsistentes.isSuccessful) {
                    profAsistentes = responseAsistentes.body()?.filter { it.actividad.id == activityId.toInt() } ?: emptyList()
                }
                val responseAllGrupos = RetrofitClient.instance.getGrupos().execute()
                if (responseAllGrupos.isSuccessful) {
                    allGrupos = responseAllGrupos.body() ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("ActivityDetailView", "Error fetching activity details", e)
            }
        }
    }

    Scaffold(
        topBar = { TopBar(navController) },
        content = { paddingValues ->
            ActivityDetailContent(
                navController = navController,
                modifier = Modifier.padding(paddingValues),
                onDataChanged = { isDataChanged = it }
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
            ) {
                BotonGuardar(isEnabled = isDataChanged) {
                    isDataChanged = false
                }
            }
        },
        bottomBar = { BottomDetailBar(navController) }
    )
}


@Composable
fun ActivityDetailContent(navController: NavController, modifier: Modifier = Modifier, onDataChanged: (Boolean) -> Unit) {
    var isDialogVisible by remember { mutableStateOf(false) }
    var isPopupVisible by remember { mutableStateOf(false) }
    var isCameraVisible by remember { mutableStateOf(false) }
    val getImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            selectedImages = selectedImages + it
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = activity?.titulo ?: "Nombre de la actividad",
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
            Spacer(modifier = Modifier.height(4.dp))
        }

        item {
            Text(text = "Fecha: ${activity?.fini} a ${activity?.ffin}", color = TextPrimary)
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
                        text = activity?.descripcion ?: "Descricion no encontrada",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp),
                        color = TextPrimary
                    )
                }
            }
        }

        item {
            Column (
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                AlumnosAsistentes(onDataChanged)
                Spacer(modifier = Modifier.height(8.dp))
                ProfesoresAsistentes(onDataChanged)
                Spacer(modifier = Modifier.height(15.dp))
                Observaciones(actividad = activity, onUpdateActividad = { updatedActividad ->
                    // Update the actividad state with the new values
                    activity = updatedActividad
                    onDataChanged(true)
                })
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
                MapaActividad(modifier = Modifier.fillMaxSize())
            }
        }
    }

    if (isDialogVisible) {
        EditActivityDialog(
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
fun AlumnosAsistentes(onDataChanged: (Boolean) -> Unit) {
    var currentlyEditingId by remember { mutableStateOf<Int?>(null) }
    var totalParticipantes by remember { mutableStateOf(0) }
    var exNumParticipantes by remember { mutableStateOf(0) }
    var isPopupVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedGrupos by remember { mutableStateOf(setOf<String>()) }

    selectedGrupos = grupoParticipantes.map { "${it.grupo}" }.toSet()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Alumnos Asistentes",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.weight(0.6f)
        )
        Spacer(modifier = Modifier.weight(0.2f))
        Text(
            text = totalParticipantes.toString(),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier
                .weight(0.15f)
        )
        IconButton(onClick = { isPopupVisible = true }) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Group",
                tint = TextPrimary,
                modifier = Modifier.weight(0.05f)
            )
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    grupoParticipantes.forEach { grupoParticipante ->
        var editedNumParticipantes by remember { mutableStateOf(grupoParticipante.numParticipantes.toString()) }
        val isEditing = currentlyEditingId == grupoParticipante.id

        LaunchedEffect(editedNumParticipantes) {
            totalParticipantes = grupoParticipantes.sumOf { it.numParticipantes }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = {
                deletedGrupoParticipantes = deletedGrupoParticipantes + grupoParticipante
                grupoParticipantes = grupoParticipantes.filterNot { it.grupo.id == grupoParticipante.grupo.id }
                selectedGrupos = selectedGrupos - grupoParticipante.grupo.codGrupo
                totalParticipantes = grupoParticipantes.sumOf { it.numParticipantes }
                onDataChanged(true)
            }) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Quitar Grupo",
                    tint = TextPrimary,
                    modifier = Modifier.weight(0.05f)
                )
            }
            Text(
                text = grupoParticipante.grupo.codGrupo,
                fontSize = 16.sp,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            if (isEditing) {
                BasicTextField(
                    value = editedNumParticipantes,
                    onValueChange = { newValue ->
                        val newNum = newValue.toIntOrNull() ?: 0
                        if (newNum in 0..grupoParticipante.grupo.numAlumnos) {
                            editedNumParticipantes = newValue
                            grupoParticipante.numParticipantes = newNum
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                    textStyle = TextStyle(fontSize = 16.sp, lineHeight = 20.sp),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .width(60.dp)
                                .height(40.dp)
                                .background(Color.White)
                                .padding(4.dp)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            innerTextField()
                        }
                    },
                    modifier = Modifier
                        .width(60.dp)
                        .height(40.dp),
                    singleLine = true
                )
            } else {
                Text(
                    text = grupoParticipante.numParticipantes.toString(),
                    fontSize = 16.sp,
                    color = TextPrimary,
                    modifier = Modifier.weight(0.15f)
                )
            }
            Text(
                text = "/",
                fontSize = 16.sp,
                color = TextPrimary,
                modifier = Modifier.weight(0.15f)
            )
            Text(
                text = grupoParticipante.grupo.numAlumnos.toString(),
                fontSize = 16.sp,
                color = TextPrimary,
                modifier = Modifier.weight(0.15f)
            )
            IconButton(onClick = {
                if (isEditing) {
                    if (exNumParticipantes.toString() != editedNumParticipantes) {
                        onDataChanged(true)
                    }
                    currentlyEditingId = null
                } else {
                    exNumParticipantes = grupoParticipante.numParticipantes
                    currentlyEditingId = grupoParticipante.id
                }
            }) {
                Icon(
                    imageVector = if (isEditing) Icons.Default.Check else Icons.Default.Edit,
                    contentDescription = if (isEditing) "Confirm" else "Edit",
                    tint = TextPrimary
                )
            }
        }
    }

    val filteredGrupos = allGrupos.filter {
        it.codGrupo.contains(searchQuery, ignoreCase = true)
    }

    if (isPopupVisible) {
        AlertDialog(
            onDismissRequest = { isPopupVisible = false },
            title = { Text("Seleccionar Grupos") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Buscar Grupo") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (filteredGrupos.isNotEmpty()) {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(filteredGrupos) { grupos ->
                                val codigo = "${grupos.codGrupo}"
                                val isSelected = selectedGrupos.contains(codigo) || grupoParticipantes.any { it.grupo.codGrupo == codigo }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            selectedGrupos = if (isSelected) {
                                                selectedGrupos - codigo
                                            } else {
                                                selectedGrupos + codigo
                                            }
                                        }
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { checked ->
                                            selectedGrupos = if (checked) {
                                                selectedGrupos + codigo
                                            } else {
                                                selectedGrupos - codigo
                                            }
                                        }
                                    )

                                    Text(
                                        text = codigo,
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
                        Log.d("ActivityDetailView", "Selected grupos: ${grupoParticipantes.size}")

                        val updatedGruposAsistentes = allGrupos.filter {
                            selectedGrupos.contains("${it.codGrupo}")
                        }.map { grupos ->
                            activity?.let { it1 ->
                                GrupoParticipanteResponse(
                                    id = 0,
                                    actividades = it1,
                                    grupo = grupos,
                                    numParticipantes = grupos.numAlumnos,
                                    comentario = it1.comentarios
                                )
                            }
                        }.filterNotNull()

                        Log.d("ActivityDetailView", "Updated grupos: ${updatedGruposAsistentes.size}")

                        // Merge the new groups with the existing ones
                        val mergedAsistentes = grupoParticipantes.toMutableList().apply {
                            addAll(updatedGruposAsistentes.filter { newGroup ->
                                none { it.grupo.id == newGroup.grupo.id }
                            })
                        }

                        Log.d("ActivityDetailView", "Merged grupos: ${mergedAsistentes.size}")

                        if (!areGruposEqual(mergedAsistentes, grupoParticipantes)) {
                            grupoParticipantes = mergedAsistentes
                            totalParticipantes = grupoParticipantes.sumOf { it.numParticipantes }
                            onDataChanged(true)
                        }

                        Log.d("ActivityDetailView", "Selected grupos: ${grupoParticipantes.size}")

                        isPopupVisible = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                Button(onClick = { isPopupVisible = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun ProfesoresAsistentes(onDataChanged: (Boolean) -> Unit) {
    var profesoresLista by remember { mutableStateOf<List<ProfesorResponse>>(emptyList()) }
    var isLoadingProfesores by remember { mutableStateOf(true) }
    var isLoadingAsistentes by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedProfesores by remember { mutableStateOf<List<ProfesorParticipanteResponse>>(emptyList()) }
    var isDialogVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    selectedProfesores = profAsistentes

    LaunchedEffect(Unit) {
        try {
            withContext(Dispatchers.IO) {
                val responseProfesores: Response<List<ProfesorResponse>> =
                    RetrofitClient.instance.getProfesores().execute()
                if (responseProfesores.isSuccessful) {
                    profesoresLista = responseProfesores.body() ?: emptyList()
                } else {
                    errorMessage = "Error al obtener la lista de profesores: ${responseProfesores.errorBody()}"
                }
            }
        } catch (e: Exception) {
            errorMessage = "Error al hacer la llamada a la API: ${e.message}"
        } finally {
            withContext(Dispatchers.Main) {
                isLoadingProfesores = false
                isLoadingAsistentes = false
            }
        }
    }

    val filteredProfesores = profesoresLista.filter {
        it.nombre.contains(searchQuery, ignoreCase = true) || it.apellidos.contains(searchQuery, ignoreCase = true)
    }

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

        if (profAsistentes.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                profAsistentes?.forEach { profesor ->
                    Text(
                        text = "${profesor.profesor.nombre} ${profesor.profesor.apellidos}",
                        fontSize = 16.sp,
                        color = TextPrimary,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 4.dp)
                    )
                }
            }
        } else {
            Text(
                text = "No hay profesores asignados.",
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(16.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
    }

    if (isDialogVisible) {
        AlertDialog(
            onDismissRequest = { isDialogVisible = false },
            title = { Text("Seleccionar Profesores") },
            text = {
                Column(modifier = Modifier.fillMaxWidth()) {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Buscar Profesor") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (filteredProfesores.isNotEmpty()) {
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(filteredProfesores) { profesor ->
                                val isSelected = selectedProfesores.any { it.profesor.uuid == profesor.uuid }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
                                            val existingProfesor = selectedProfesores.find { it.profesor.uuid == profesor.uuid }
                                            selectedProfesores = if (existingProfesor != null) {
                                                selectedProfesores
                                            } else {
                                                selectedProfesores + ProfesorParticipanteResponse(
                                                    id = 0,
                                                    profesor = profesor,
                                                    actividad = activity!!
                                                )
                                            }
                                        }
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { checked ->
                                            val existingProfesor = selectedProfesores.find { it.profesor.uuid == profesor.uuid }
                                            selectedProfesores = if (checked) {
                                                if (existingProfesor == null) {
                                                    selectedProfesores + ProfesorParticipanteResponse(
                                                        id = 0,
                                                        profesor = profesor,
                                                        actividad = activity!!
                                                    )
                                                } else {
                                                    selectedProfesores
                                                }
                                            } else {
                                                selectedProfesores.filter { it.profesor.uuid != profesor.uuid }
                                            }
                                            onDataChanged(true)
                                        }
                                    )

                                    Text(
                                        text = "${profesor.nombre} ${profesor.apellidos}",
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
                        Log.d("ActivityDetailView", "Pre Updated profesores: ${profAsistentes.size}")
                        val updatedProfAsistentes = profesoresLista.filter { profesor ->
                            selectedProfesores.any { it.profesor.uuid == profesor.uuid }
                        }.map { profesor ->
                            val existingProf = profAsistentes.find { it.profesor.uuid == profesor.uuid }
                            ProfesorParticipanteResponse(
                                id = existingProf?.id ?: 0,
                                profesor = profesor,
                                actividad = activity!!
                            )
                        }.filterNotNull()

                        val removedProfAsistentes = profAsistentes.filter { oldProf ->
                            updatedProfAsistentes.none { it.profesor.uuid == oldProf.profesor.uuid }
                        }

                        deletedProfesores = deletedProfesores + removedProfAsistentes

                        Log.d("ActivityDetailView", "Updated profesores: ${deletedProfesores.size}")
                        Log.d("ActivityDetailView", "Updated profesores: ${updatedProfAsistentes}")
                        Log.d("ActivityDetailView", "Updated profesores: ${profAsistentes.size}")

                        val mergedProfes = profAsistentes.toMutableList().apply {
                            addAll(updatedProfAsistentes.filter { newGroup ->
                                none { it.profesor.uuid == newGroup.profesor.uuid }
                            })
                        }

                        profAsistentes = mergedProfes
                        onDataChanged(true)

                        if (!areProfessorsEqual(mergedProfes, profAsistentes)) {
                            profAsistentes = mergedProfes
                            onDataChanged(true)
                        }
                        /*if (selectedProfesores.isNotEmpty() && profAsistentes.isEmpty()) {
                            profAsistentes = updatedProfAsistentes
                            onDataChanged(true)
                        }*/
                        Log.d("ActivityDetailView", "Post Updated profesores: ${profAsistentes.size}")
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

fun areProfessorsEqual(
    list1: List<ProfesorParticipanteResponse>,
    list2: List<ProfesorParticipanteResponse>
): Boolean {
    val professors1 = list1.map { it.profesor }
    val professors2 = list2.map { it.profesor }
    return professors1.containsAll(professors2) && professors2.containsAll(professors1)
}

fun areGruposEqual(
    list1: List<GrupoParticipanteResponse>,
    list2: List<GrupoParticipanteResponse>
): Boolean {
    val grupo1 = list1.map { it.grupo }
    val grupo2 = list2.map { it.grupo }
    return grupo1.containsAll(grupo2) && grupo2.containsAll(grupo1)
}

@Composable
fun Observaciones(actividad: ActividadResponse?, onUpdateActividad: (ActividadResponse) -> Unit) {
    var transporte by remember { mutableStateOf(actividad?.comentTransporte ?: "") }
    var alojamiento by remember { mutableStateOf(actividad?.comentAlojamiento ?: "") }
    var comentarios by remember { mutableStateOf(actividad?.comentarios ?: "") }
    var estado by remember { mutableStateOf(actividad?.comentEstado ?: "") }
    var incidencias by remember { mutableStateOf(actividad?.incidencias ?: "") }

    var observaciones by remember { mutableStateOf("") }

    LaunchedEffect(actividad) {
        transporte = actividad?.comentTransporte ?: ""
        alojamiento = actividad?.comentAlojamiento ?: ""
        comentarios = actividad?.comentarios ?: ""
        estado = actividad?.comentEstado ?: ""
        incidencias = actividad?.incidencias ?: ""

        observaciones = listOfNotNull(
            transporte.takeIf { it.isNotEmpty() }?.let { "Comentarios de Transporte: $it" },
            alojamiento.takeIf { it.isNotEmpty() }?.let { "Comentarios de Alojamiento: $it" },
            comentarios.takeIf { it.isNotEmpty() }?.let { "Comentarios: $it" },
            estado.takeIf { it.isNotEmpty() }?.let { "Comentarios de Estado: $it" },
            incidencias.takeIf { it.isNotEmpty() }?.let { "Incidencias: $it" }
        ).joinToString(separator = "\n\n")
    }

    var isDialogVisible by remember { mutableStateOf(false) }

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
        text = if (observaciones.isNotEmpty()) observaciones else "No hay Observaciones e Incidencias",
        fontSize = 16.sp,
        color = TextPrimary,
        modifier = Modifier.padding(8.dp)
    )

    if (isDialogVisible) {
        AlertDialog(
            onDismissRequest = { isDialogVisible = false },
            title = { Text("Editar Observaciones") },
            text = {
                Column {
                    TextField(
                        value = transporte,
                        onValueChange = { transporte = it },
                        label = { Text("Comentarios de Transporte") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                        keyboardActions = KeyboardActions.Default
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = alojamiento,
                        onValueChange = { alojamiento = it },
                        label = { Text("Comentarios de Alojamiento") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                        keyboardActions = KeyboardActions.Default
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = comentarios,
                        onValueChange = { comentarios = it },
                        label = { Text("Comentarios") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                        keyboardActions = KeyboardActions.Default
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = estado,
                        onValueChange = { estado = it },
                        label = { Text("Comentarios de Estado") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                        keyboardActions = KeyboardActions.Default
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = incidencias,
                        onValueChange = { incidencias = it },
                        label = { Text("Incidencias") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 5,
                        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
                        keyboardActions = KeyboardActions.Default
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updatedActividad = actividad?.copy(
                            comentTransporte = transporte,
                            comentAlojamiento = alojamiento,
                            comentarios = comentarios,
                            comentEstado = estado,
                            incidencias = incidencias
                        )
                        if (updatedActividad != null) {
                            onUpdateActividad(updatedActividad)
                        }
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
fun EditActivityDialog(
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var activityName by remember { mutableStateOf(activity?.titulo ?: "") }
    var activityDescription by remember { mutableStateOf(activity?.descripcion ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Editar actividad") },
        text = {
            Column {
                // Campo para el nombre de la actividad
                OutlinedTextField(
                    value = activityName,
                    onValueChange = {
                        activityName = it
                        onNameChange(it)
                    },
                    label = { Text("Nombre de la actividaddd") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
                // Campo para la descripción de la actividad
                OutlinedTextField(
                    value = activityDescription,
                    onValueChange = {
                        activityDescription = it
                        onDescriptionChange(it)
                    },
                    label = { Text("Descripción de la actividad") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 5
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                activity?.titulo = activityName
                activity?.descripcion = activityDescription
                onDismiss()
            }) {
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
fun BotonGuardar(isEnabled: Boolean, onSaveComplete: () -> Unit) {
    val context = LocalContext.current

    Row(
        modifier = Modifier.fillMaxWidth(). padding(top = 12.dp, end = 12.dp),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
                Log.d("ActivityDetailView", "${activity.toString()}")
                Log.d("ActivityDetailView", "${grupoParticipantes.size}")
                Log.d("ActivityDetailView", "${profAsistentes.size}")

                CoroutineScope(Dispatchers.IO).launch {
                    try {
                        activity?.let {
                            val response = RetrofitClient.instance.updateActividad(it.id, it)
                            if (!response.isSuccessful) {
                                Log.e("ActivityDetailView", "Error updating activity: ${response.code()}")
                            }
                        }

                        deletedGrupoParticipantes.forEach { grupoParticipante ->
                            val response = RetrofitClient.instance.deleteGrupoParticipante(grupoParticipante.id)
                            if (!response.isSuccessful) {
                                Log.e("ActivityDetailView", "Error deleting grupoParticipante: ${response.code()}")
                            }
                        }
                        deletedGrupoParticipantes = emptyList()

                        grupoParticipantes.forEach { grupoParticipante ->
                            val response = RetrofitClient.instance.addGrupoParticipante(grupoParticipante)
                            Log.d("ActivityDetailView", "GrupoParticipante: ${grupoParticipante}")
                            if (!response.isSuccessful) {
                                Log.e("ActivityDetailView", "Error adding grupoParticipante: ${response.code()}")
                            }
                        }

                        deletedProfesores.forEach { profParticipante ->
                            val response = RetrofitClient.instance.deleteProfesorParticipante(profParticipante.id)
                            if (!response.isSuccessful) {
                                Log.e("ActivityDetailView", "Error deleting profesorParticipante: ${response.code()}")
                            }
                        }
                        deletedProfesores = emptyList()

                        val newProfAsistentes = profAsistentes.filter { it.id == 0 }
                        newProfAsistentes.forEach { profesorParticipante ->
                            val response = RetrofitClient.instance.addProfesorParticipante(profesorParticipante)
                            if (!response.isSuccessful) {
                                Log.e("ActivityDetailView", "Error adding profesorParticipante: ${response.code()}")
                            }
                        }

                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Datos guardados correctamente", Toast.LENGTH_SHORT).show()
                            onSaveComplete()
                        }
                    } catch (e: Exception) {
                        Log.e("BotonGuardar", "Error saving data", e)
                        withContext(Dispatchers.Main) {
                            Toast.makeText(context, "Error guardando datos", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            enabled = isEnabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = ButtonPrimary,
                contentColor = TextPrimary
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