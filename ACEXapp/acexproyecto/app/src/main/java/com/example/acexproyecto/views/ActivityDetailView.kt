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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.acexproyecto.model.GrupoParticipanteResponse
import com.example.acexproyecto.model.GrupoResponse
import com.example.acexproyecto.objetos.Loading.isLoading
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response

@Composable
fun ActivityDetailView(navController: NavController, activityId: String) {
    var activity by remember { mutableStateOf<ActividadResponse?>(null) }
    var allGrupos by remember { mutableStateOf<List<GrupoResponse>>(emptyList()) }
    var numeroAlumnos by remember { mutableStateOf(0) }
    var grupoParticipantes by remember { mutableStateOf<List<GrupoParticipanteResponse>>(emptyList()) }
    var profAsistentes by remember { mutableStateOf<List<ProfesorParticipanteResponse>>(emptyList()) }
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

    //cambiar la top bar para añadir el boton de regreso
    Scaffold(
        topBar = { TopBar(navController) },
        content = { paddingValues ->
            ActivityDetailContent(
                navController = navController,
                modifier = Modifier.padding(paddingValues),
                actividad = activity,
                numAlumnos = numeroAlumnos,
                gruposParticipantes = grupoParticipantes,
                profAsistentes = profAsistentes,
                onDataChanged = { isDataChanged = it },
                allGrupos = allGrupos
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(paddingValues)
            ) {
                BotonGuardar(isEnabled = isDataChanged)
            }
        },
        bottomBar = { BottomDetailBar(navController) }
    )
}



@Composable
fun ActivityDetailContent(navController: NavController, modifier: Modifier = Modifier, actividad : ActividadResponse?, numAlumnos: Int, gruposParticipantes: List<GrupoParticipanteResponse>, profAsistentes: List<ProfesorParticipanteResponse>, onDataChanged: (Boolean) -> Unit, allGrupos: List<GrupoResponse>) {
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
            //BotonGuardar(isEnabled = isDataChanged)
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
            Text(text = "Fecha: ${actividad?.fini} a ${actividad?.ffin}", color = TextPrimary)
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
            ) {
                var activity by remember { mutableStateOf<ActividadResponse?>(null) }
                AlumnosAsistentes(gruposParticipantes, onDataChanged, allGrupos, actividad)
                actividad?.let { ProfesoresAsistentes(profAsistentes, actividad, onDataChanged) }
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
fun AlumnosAsistentes(gruposParticipantes: List<GrupoParticipanteResponse>, onDataChanged: (Boolean) -> Unit, allGrupos: List<GrupoResponse>, actividad : ActividadResponse? ) {
    var currentlyEditingId by remember { mutableStateOf<Int?>(null) }
    var totalParticipantes by remember { mutableStateOf(0) }
    var exNumParticipantes by remember { mutableStateOf(0) }
    var isPopupVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedGrupos by remember { mutableStateOf(setOf<String>()) }
    var asistentes by remember { mutableStateOf(gruposParticipantes) }


    LaunchedEffect(gruposParticipantes) {
        asistentes = gruposParticipantes
        selectedGrupos = gruposParticipantes.map { "${it.grupo}" }.toSet()
    }

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

    asistentes.forEach { grupoParticipante ->
        var editedNumParticipantes by remember { mutableStateOf(grupoParticipante.numParticipantes.toString()) }
        val isEditing = currentlyEditingId == grupoParticipante.id

        LaunchedEffect(editedNumParticipantes) {
            totalParticipantes = gruposParticipantes.sumOf { it.numParticipantes }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            IconButton(onClick = {
                asistentes = asistentes.filterNot { it.id == grupoParticipante.id }
                onDataChanged(true)
            }) {
                Icon(
                    imageVector = Icons.Default.Clear,
                    contentDescription = "Quitar Group",
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
                        editedNumParticipantes = newValue
                        grupoParticipante.numParticipantes = newValue.toIntOrNull() ?: 0
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
            title = { Text("Seleccionar Profesores") },
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
                                val isSelected = selectedGrupos.contains(codigo)

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
                        val updatedGruposAsistentes = allGrupos.filter {
                            selectedGrupos.contains("${it.codGrupo}")
                        }.map { grupos ->
                            (gruposParticipantes.firstOrNull()?.actividades?.id ?: actividad)?.let {
                                actividad?.let { it1 ->
                                    GrupoParticipanteResponse(
                                        id = it1.id,
                                        actividades = it1,
                                        grupo = grupos,
                                        numParticipantes = grupos.numAlumnos,
                                        comentario = it1.comentarios
                                    )
                                }
                            }
                        }.filterNotNull()

                        if (!areGruposEqual(updatedGruposAsistentes, asistentes)) {
                            asistentes = updatedGruposAsistentes
                            onDataChanged(true)
                        }

                        Log.d("AlumnosAsistentes", "gruposParticipantes: $asistentes")
                        Log.e("AlumnosAsistentes", "updatedGruposAsistentes: $updatedGruposAsistentes")

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
fun ProfesoresAsistentes(profAsistentes: List<ProfesorParticipanteResponse>, actividad : ActividadResponse?, onDataChanged: (Boolean) -> Unit) {
    var profesoresLista by remember { mutableStateOf<List<ProfesorResponse>>(emptyList()) }
    var isLoadingProfesores by remember { mutableStateOf(true) }
    var isLoadingAsistentes by remember { mutableStateOf(true) }

    var searchQuery by remember { mutableStateOf("") }
    var selectedProfesores by remember { mutableStateOf(setOf<String>()) }
    var isDialogVisible by remember { mutableStateOf(false) }

    var asistentes by remember { mutableStateOf(profAsistentes) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(profAsistentes) {
        asistentes = profAsistentes
        selectedProfesores = profAsistentes.map { "${it.profesor.nombre} ${it.profesor.apellidos}" }.toSet()
    }

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

        if (asistentes.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                asistentes?.forEach { profesor ->
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
                                val nombreCompleto = "${profesor.nombre} ${profesor.apellidos}"
                                val isSelected = selectedProfesores.contains(nombreCompleto)

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable {
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
                        val updatedProfAsistentes = profesoresLista.filter {
                            selectedProfesores.contains("${it.nombre} ${it.apellidos}")
                        }.map { profesor ->
                            (profAsistentes.firstOrNull()?.actividad ?: actividad)?.let {
                                actividad?.let { it1 ->
                                    ProfesorParticipanteResponse(
                                        id = it1.id,
                                        profesor = profesor,
                                        actividad = it
                                    )
                                }
                            }
                        }.filterNotNull()

                        if (!areProfessorsEqual(updatedProfAsistentes, asistentes)) {
                            asistentes = updatedProfAsistentes
                            onDataChanged(true)
                        }
                        if (selectedProfesores.isNotEmpty() && asistentes.isEmpty()) {
                            asistentes = updatedProfAsistentes
                            onDataChanged(true)
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
fun BotonGuardar(isEnabled: Boolean) {
    Row(
        modifier = Modifier.fillMaxWidth(). padding(top = 12.dp, end = 12.dp), // Hace que el Row ocupe todo el ancho disponible
        horizontalArrangement = Arrangement.End, // Alinea el contenido (el botón) a la derecha
        verticalAlignment = Alignment.CenterVertically // Opcional: centra el botón verticalmente
    ) {
        Button(
            onClick = {
                // Acción al presionar el botón
            },
            enabled = isEnabled, // Habilita o deshabilita el botón
            colors = ButtonDefaults.buttonColors(
                containerColor = ButtonPrimary, // Cambia el color de fondo del botón
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