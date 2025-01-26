package com.example.acexproyecto.views

import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import com.example.acexproyecto.model.GrupoParticipanteResponse
import com.example.acexproyecto.model.GrupoResponse
import com.example.acexproyecto.model.PhotoResponse
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import com.google.maps.android.compose.rememberMarkerState
import java.io.File
import java.io.InputStream
import retrofit2.Call
import retrofit2.Callback
import java.io.ByteArrayOutputStream
import java.util.Calendar
import androidx.compose.material3.ExperimentalMaterial3Api
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.pm.PackageManager
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.ui.graphics.ColorFilter
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.example.acexproyecto.objetos.Usuario
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.MapProperties
import java.text.SimpleDateFormat
import java.util.Locale

var activity by mutableStateOf<ActividadResponse?>(null)
var allGrupos by mutableStateOf<List<GrupoResponse>>(emptyList())
var numeroAlumnos by mutableStateOf(0)
var grupoParticipantes by mutableStateOf<List<GrupoParticipanteResponse>>(emptyList())
var profAsistentes by mutableStateOf<List<ProfesorParticipanteResponse>>(emptyList())
var selectedImages by mutableStateOf<List<Uri>>(emptyList())
var deletedGrupoParticipantes by mutableStateOf<List<GrupoParticipanteResponse>>(emptyList())
var deletedProfesores by mutableStateOf<List<ProfesorParticipanteResponse>>(emptyList())
var imagesActividad by mutableStateOf<List<PhotoResponse>>(emptyList())
var deletedFotos by mutableStateOf<List<PhotoResponse>>(emptyList())

@Composable
fun ActivityDetailView(navController: NavController, activityId: String, isDarkTheme: Boolean) {
    var isDataChanged by remember { mutableStateOf(false) }
    var isAdminOrSolicitante by remember { mutableStateOf(false) }

    LaunchedEffect(activityId) {
        selectedImages = emptyList()
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getActividades().execute()
                if (response.isSuccessful) {
                    activity = response.body()?.find { it.id == activityId.toInt() }
                    isAdminOrSolicitante = Usuario.profesor?.rol in listOf("ADM", "ED") || Usuario.profesor?.uuid == activity?.solicitante?.uuid
                    Log.d("ActivityDetailView", "Activity: $isAdminOrSolicitante")
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
                val responseFotos = RetrofitClient.instance.getFotos().execute()
                if (responseFotos.isSuccessful) {
                    imagesActividad = responseFotos.body()?.filter { it.actividad.id == activityId.toInt() } ?: emptyList()
                    for (photo in imagesActividad) {
                        Log.d("ActivityDetailView", "Photo: ${photo.urlFoto}")
                    }
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
                actividad = activity,
                navController = navController,
                modifier = Modifier.padding(paddingValues),
                onDataChanged = { isDataChanged = it },
                isDarkTheme = isDarkTheme,
                canEdit = isAdminOrSolicitante
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
fun ActivityDetailContent(
    actividad: ActividadResponse?,
    navController: NavController,
    modifier: Modifier = Modifier,
    onDataChanged: (Boolean) -> Unit,
    isDarkTheme: Boolean,
    canEdit: Boolean
) {
    // Variables de estado y configuración
    var isDialogVisible by remember { mutableStateOf(false) }
    var isPopupVisible by remember { mutableStateOf(false) }
    var isCameraVisible by remember { mutableStateOf(false) }
    val getImageLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                selectedImages = selectedImages + it
                onDataChanged(true)
            }
        }
    var titulo by remember { mutableStateOf(actividad?.titulo ?: "") }
    var descripcion by remember { mutableStateOf(actividad?.descripcion ?: "") }
    var showImagePopup by remember { mutableStateOf<Pair<Boolean, PhotoResponse?>>(false to null) }
    var showSelectedImagePopup by remember { mutableStateOf<Pair<Boolean, Uri?>>(false to null) }
    var updatedDescription by remember { mutableStateOf("") }

    LaunchedEffect(actividad) {
        titulo = actividad?.titulo ?: ""
        descripcion = actividad?.descripcion ?: ""
    }
    val baseUrl = "http://4.233.223.75:8080/imagenes/actividad/"
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(35.dp))
        }
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = titulo ?: "Nombre de la actividad",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                if (canEdit) {
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
            Spacer(modifier = Modifier.height(4.dp))
        }

        item {
            Text(
                text = "Solicitante: ${actividad?.solicitante?.nombre} ${actividad?.solicitante?.apellidos}",
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(8.dp))
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
                    text = "${actividad?.tipo}",
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "Estado: ${actividad?.estado}",
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )
            }
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
            Spacer(modifier = Modifier.height(8.dp))
        }

        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                if (canEdit) {
                    item {
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
                    }
                }

                items(imagesActividad) { photo ->
                    val imageUrl = photo.urlFoto?.let {
                        baseUrl + photo.actividad.titulo.replace(
                            " ",
                            "_"
                        ) + "/" + it.substringAfterLast("\\").replace(" ", "_")
                    } ?: ""
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .padding(end = 8.dp)
                            .clickable {
                                if (canEdit) {
                                    updatedDescription = photo.descripcion ?: ""
                                    showImagePopup = true to photo
                                }
                            }
                    ) {
                        Image(
                            painter = rememberImagePainter(imageUrl),
                            contentDescription = "Foto subida",
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray)
                        )
                    }
                }

                items(selectedImages) { uri ->
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .padding(end = 8.dp)
                            .clickable {
                                showSelectedImagePopup = true to uri
                            }
                    ) {
                        Image(
                            painter = rememberImagePainter(uri),
                            contentDescription = "Foto seleccionada",
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Gray)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
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
                        text = descripcion ?: "Descricion no encontrada",
                        fontSize = 16.sp,
                        modifier = Modifier.padding(16.dp),
                        color = TextPrimary
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                AlumnosAsistentes(gruposAsistentes = grupoParticipantes, onDataChanged, canEdit)
                Spacer(modifier = Modifier.height(8.dp))
                ProfesoresAsistentes(profAsistentes, onDataChanged, canEdit)
                Spacer(modifier = Modifier.height(15.dp))
                Observaciones(actividad = activity, onUpdateActividad = { updatedActividad ->
                    // Update the actividad state with the new values
                    activity = updatedActividad
                    onDataChanged(true)
                },
                    canEdit = canEdit
                )
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
                MapaActividad(
                    actividad = activity,
                    modifier = Modifier.fillMaxSize(),
                    onDataChanged = onDataChanged,
                    isDarkTheme = isDarkTheme,
                    canEdit = canEdit
                )
            }
        }
    }

    if (isDialogVisible) {
        EditActivityDialog(
            actividad = activity,
            onNameChange = { newName -> /* Handle Name Change */ },
            onDescriptionChange = { newDescription -> /* Handle Description Change */ },
            onDismiss = { isDialogVisible = false },
            onUpdateActividad = { updatedActividad ->
                activity = updatedActividad
                onDataChanged(true)
            }
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
                onDataChanged(true)
                Toast.makeText(navController.context, "Foto tomada: $uri", Toast.LENGTH_SHORT)
                    .show()
            },
            context = navController.context,
            navController = navController
        )
    }


    // Popup para fotos
    // Verifica si el primer valor de 'showImagePopup' es true, indicando que el popup de la imagen debe mostrarse.
    if (showImagePopup.first) {
        val photo = showImagePopup.second
        val imageUrl = photo?.urlFoto?.let {
            baseUrl + photo.actividad.titulo.replace(" ", "_") + "/" + it.substringAfterLast("\\")
                .replace(" ", "_")
        } ?: ""

        Log.d("PopupImage", "URL de imagen: $imageUrl")

        AlertDialog(
            onDismissRequest = { showImagePopup = false to null },
            title = { Text("Vista de Foto") },
            text = {
                Column {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "Foto ampliada",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(bottom = 16.dp),
                        onError = {
                            Log.e(
                                "PopupImageError",
                                "Error cargando imagen en popup: $imageUrl"
                            )
                        }
                    )
                    OutlinedTextField(
                        value = updatedDescription,
                        onValueChange = { updatedDescription = it },
                        label = { Text("Descripción de la foto") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Column {
                    TextButton(
                        onClick = {
                            // Verificamos que la foto no sea nula
                            photo?.let {
                                // Actualizamos la descripción de la foto
                                it.descripcion = updatedDescription

                                // Actualizamos la lista de imágenes en el estado
                                imagesActividad = imagesActividad.map { photoItem ->
                                    if (photoItem.id == it.id) {
                                        // Si el ID coincide, actualizamos la descripción
                                        photoItem.copy(descripcion = updatedDescription)
                                    } else {
                                        // Si no coincide, dejamos la foto sin cambios
                                        photoItem
                                    }
                                }.toMutableList() // Convertimos a MutableList para asegurarnos de que sea mutable

                                // Llamamos a onDataChanged para notificar que los datos han cambiado
                                onDataChanged(true)

                                // Cerramos el popup de la imagen
                                showImagePopup = false to null
                            }
                        }
                    ) {
                        Text("Guardar")
                    }

                    Button(
                        onClick = {
                            // Elimina la foto de imagesActividad y la agrega a deletedFotos
                            photo?.let {
                                imagesActividad = imagesActividad.filter { it.id != photo.id }
                                deletedFotos =
                                    deletedFotos + it // Se agrega a deletedFotos para ser eliminada de la base de datos
                                onDataChanged(true)
                                showImagePopup = false to null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Eliminar Foto", color = Color.White)
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showImagePopup = false to null }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
fun AlumnosAsistentes(gruposAsistentes:  List<GrupoParticipanteResponse>,onDataChanged: (Boolean) -> Unit, canEdit: Boolean) {
    var currentlyEditingId by remember { mutableStateOf<Int?>(null) }
    var totalParticipantes by remember { mutableStateOf(0) }
    var exNumParticipantes by remember { mutableStateOf(0) }
    var isPopupVisible by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedGrupos by remember { mutableStateOf(setOf<String>()) }
    var editedNumParticipantesMap by remember { mutableStateOf<Map<Int, String>>(emptyMap()) }

    LaunchedEffect(gruposAsistentes) {
        totalParticipantes = grupoParticipantes.sumOf { it.numParticipantes }
    }

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
        if (canEdit) {
            IconButton(onClick = { isPopupVisible = true }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Group",
                    tint = TextPrimary,
                    modifier = Modifier.weight(0.05f)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp))

    grupoParticipantes.forEach { grupoParticipante ->
        val editedNumParticipantes = editedNumParticipantesMap[grupoParticipante.id] ?: grupoParticipante.numParticipantes.toString()
        val isEditing = currentlyEditingId == grupoParticipante.id

        LaunchedEffect(editedNumParticipantes) {
            totalParticipantes = grupoParticipantes.sumOf { it.numParticipantes }
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (canEdit) {
                IconButton(onClick = {
                    deletedGrupoParticipantes = deletedGrupoParticipantes + grupoParticipante
                    grupoParticipantes =
                        grupoParticipantes.filterNot { it.grupo.id == grupoParticipante.grupo.id }
                    selectedGrupos = selectedGrupos - grupoParticipante.grupo.codGrupo
                    totalParticipantes = grupoParticipantes.sumOf { it.numParticipantes }
                    editedNumParticipantesMap = editedNumParticipantesMap - grupoParticipante.id
                    onDataChanged(true)
                }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Quitar Grupo",
                        tint = TextPrimary,
                        modifier = Modifier.weight(0.05f)
                    )
                }
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
                            editedNumParticipantesMap = editedNumParticipantesMap + (grupoParticipante.id to newValue)
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
            if (canEdit) {
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
                                            if (grupoParticipantes.any { it.grupo.codGrupo == codigo }) {
                                                deletedGrupoParticipantes = deletedGrupoParticipantes + grupoParticipantes.find { it.grupo.codGrupo == codigo }!!
                                                grupoParticipantes = grupoParticipantes.filterNot { it.grupo.codGrupo == codigo }
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

                        // Merge the new groups with the existing ones
                        val mergedAsistentes = grupoParticipantes.toMutableList().apply {
                            addAll(updatedGruposAsistentes.filter { newGroup ->
                                none { it.grupo.id == newGroup.grupo.id }
                            })
                        }

                        if (!areGruposEqual(mergedAsistentes, grupoParticipantes)) {
                            grupoParticipantes = mergedAsistentes
                            totalParticipantes = grupoParticipantes.sumOf { it.numParticipantes }
                            onDataChanged(true)
                        }

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
fun ProfesoresAsistentes(profesAsistentes: List<ProfesorParticipanteResponse>, onDataChanged: (Boolean) -> Unit, canEdit: Boolean) {
    var profesoresLista by remember { mutableStateOf<List<ProfesorResponse>>(emptyList()) } // todos los profesores
    //var profesoresAsistentes by remember {mutableStateOf(profAsistentes)}
    var isLoadingProfesores by remember { mutableStateOf(true) }
    var isLoadingAsistentes by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedProfesores by remember { mutableStateOf<List<ProfesorParticipanteResponse>>(emptyList()) } // lista de profesores seleccionados
    var isDialogVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(profesAsistentes) {
        selectedProfesores = profAsistentes
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
            if (canEdit) {
                IconButton(onClick = { isDialogVisible = true }) {
                    Icon(
                        imageVector = Icons.Filled.Edit,
                        contentDescription = "Editar",
                        tint = TextPrimary
                    )
                }
            }
        }

        if (selectedProfesores.isNotEmpty()) {
            Column(modifier = Modifier.fillMaxWidth()) {
                selectedProfesores.forEach { profesor ->
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
                                        .clickable { }
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Checkbox(
                                        checked = isSelected,
                                        onCheckedChange = { checked ->
                                            Log.d("ActivityDetailView", "Profesores Asistentes 1: ${selectedProfesores.size}")
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
                                            Log.d("ActivityDetailView", "Profesores Asistentes 2: ${selectedProfesores.size}")
                                            //profAsistentes = selectedProfesores
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
                        Log.d("ActivityDetailView", "Profesores Asistentes Guardar: ${profAsistentes.size}")

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

                        val mergedProfes = profAsistentes.toMutableList().apply {
                            addAll(updatedProfAsistentes.filter { newGroup ->
                                none { it.profesor.uuid == newGroup.profesor.uuid }
                            })
                        }

                        profAsistentes = mergedProfes
                        onDataChanged(true)

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
fun Observaciones(actividad: ActividadResponse?, onUpdateActividad: (ActividadResponse) -> Unit, canEdit: Boolean) {
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
        if (canEdit) {
            IconButton(onClick = { isDialogVisible = true }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar",
                    tint = TextPrimary
                )
            }
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


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditActivityDialog(
    actividad: ActividadResponse?,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onUpdateActividad: (ActividadResponse) -> Unit
) {
    var activityName by remember { mutableStateOf("") }
    var activityDescription by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }

    LaunchedEffect(actividad) {
        activityName = actividad?.titulo ?: ""
        activityDescription = actividad?.descripcion ?: ""
        startDate = actividad?.fini ?: ""
        endDate = actividad?.ffin ?: ""
        startTime = actividad?.hini ?: ""
        endTime = actividad?.hfin ?: ""
    }

    val context = LocalContext.current
    val calendar = Calendar.getInstance()

    val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    val startDatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            startDate = dateFormat.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val endDatePickerDialog = DatePickerDialog(
        context,
        { _, year, month, dayOfMonth ->
            calendar.set(year, month, dayOfMonth)
            endDate = dateFormat.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    val startTimePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            startTime = timeFormat.format(calendar.time)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    val endTimePickerDialog = TimePickerDialog(
        context,
        { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            endTime = timeFormat.format(calendar.time)
        },
        calendar.get(Calendar.HOUR_OF_DAY),
        calendar.get(Calendar.MINUTE),
        true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Editar actividad") },
        text = {
            Column {
                OutlinedTextField(
                    value = activityName,
                    onValueChange = {
                        activityName = it
                        onNameChange(it)
                    },
                    label = { Text("Nombre de la actividad") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(8.dp))
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
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = "$startDate $startTime",
                    onValueChange = {},
                    label = { Text("Fecha de inicio") },
                    readOnly = true,
                    trailingIcon = {
                        Row {
                            IconButton(onClick = { startDatePickerDialog.show() }) {
                                Icon(imageVector = Icons.Default.DateRange,
                                    contentDescription = "Seleccionar fecha de inicio")
                            }
                            IconButton(onClick = { startTimePickerDialog.show() }) {
                                Image(
                                    painter = painterResource(id = R.drawable.reloj),  // Carga la imagen desde recursos
                                    contentDescription = "Seleccionar fecha de inicio",
                                    modifier = Modifier.size(24.dp),  // Puedes ajustar el tamaño si lo necesitas
                                    colorFilter = ColorFilter.tint(Color.DarkGray)
                                    )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = "$endDate $endTime",
                    onValueChange = {},
                    label = { Text("Fecha de fin") },
                    readOnly = true,
                    trailingIcon = {
                        Row {
                            IconButton(onClick = { endDatePickerDialog.show() }) {
                                Icon(imageVector = Icons.Default.DateRange, contentDescription = "Seleccionar fecha de fin")
                            }
                            IconButton(onClick = { startTimePickerDialog.show() }) {
                                Image(
                                    painter = painterResource(id = R.drawable.reloj),  // Carga la imagen desde recursos
                                    contentDescription = "Seleccionar fecha de inicio",
                                    modifier = Modifier.size(24.dp),  // Puedes ajustar el tamaño si lo necesitas
                                    colorFilter = ColorFilter.tint(Color.DarkGray)
                                    )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val updatedActividad = actividad?.copy(
                    titulo = activityName.takeIf { it.isNotEmpty() } ?: actividad.titulo,
                    descripcion = activityDescription.takeIf { it.isNotEmpty() } ?: actividad.descripcion,
                    fini = startDate.takeIf { it.isNotEmpty() } ?: actividad.fini,
                    ffin = endDate.takeIf { it.isNotEmpty() } ?: actividad.ffin,
                    hini = startTime.takeIf { it.isNotEmpty() } ?: actividad.hini,
                    hfin = endTime.takeIf { it.isNotEmpty() } ?: actividad.hfin
                )

                if (updatedActividad != null) {
                    onUpdateActividad(updatedActividad)
                }

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
fun MapaActividad(
    actividad: ActividadResponse?,
    modifier: Modifier = Modifier,
    onDataChanged: (Boolean) -> Unit,
    isDarkTheme: Boolean,
    canEdit: Boolean
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    val activityLocation = if (actividad?.latitud != null && actividad.longitud != null) {
        LatLng(actividad.latitud, actividad.longitud)
    } else {
        LatLng(43.353, -4.064)
    }

    var markerPosition by remember { mutableStateOf(activityLocation) }
    Log.d("MapaActividad", "Marker position: $activityLocation")
    var isMarkerDraggable by remember { mutableStateOf(false) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(markerPosition, 10f)
    }
    val markerState = rememberMarkerState(position = markerPosition)
    val mapStyleOptions = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
    }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapStyleOptions = if (isDarkTheme) mapStyleOptions else null)
        ) {
            Marker(
                state = markerState,
                title = actividad?.titulo ?: "IES Miguel Herrero Pereda",
                snippet = actividad?.descripcion ?: "Torrelavega",
                draggable = isMarkerDraggable,
                onClick = {
                    if (isMarkerDraggable) {
                        markerPosition = it.position
                        markerState.position = it.position
                    }
                    true
                }
            )
        }

        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp)
        ) {
            if (canEdit) {
                IconButton(onClick = {
                    isMarkerDraggable = !isMarkerDraggable
                    if (isMarkerDraggable) {
                        Toast.makeText(
                            context,
                            "Ahora puede mover el marcador del mapa, pulse el botón de nuevo para terminar",
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        actividad?.latitud = markerPosition.latitude
                        actividad?.longitud = markerPosition.longitude
                        onDataChanged(true)
                    }
                }) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f))
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Añadir/Editar Marker",
                            tint = if (isMarkerDraggable) Color.Red else TextPrimary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                IconButton(onClick = {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                            location?.let {
                                markerPosition = LatLng(it.latitude, it.longitude)
                                markerState.position = LatLng(it.latitude, it.longitude)
                                actividad?.latitud = it.latitude
                                actividad?.longitud = it.longitude
                                cameraPositionState.position =
                                    CameraPosition.fromLatLngZoom(markerPosition, 10f)
                                Toast.makeText(
                                    context,
                                    "Localización: ${it.latitude}, ${it.longitude}",
                                    Toast.LENGTH_SHORT
                                ).show()
                                onDataChanged(true)
                            } ?: run {
                                Toast.makeText(
                                    context,
                                    "No se pudo obtener la ubicación",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else {
                        ActivityCompat.requestPermissions(
                            context as Activity,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            1
                        )
                    }
                }) {
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f))
                            .padding(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Usar Mi Localización",
                            tint = TextPrimary
                        )
                    }
                }
            }
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
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp, end = 12.dp),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = {
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

                        val photoResponses = selectedImages.map { uri ->
                            PhotoResponse(
                                id = 0,
                                urlFoto = uri.toString(),
                                descripcion = activity?.titulo ?: "",
                                actividad = activity!!
                            )
                        }

                        uploadPhotoResponses(context, photoResponses, activity!!.id, activity!!.titulo)

                        // Delete photos in deletedFotos
                        deletedFotos.forEach { photo ->
                            val response = RetrofitClient.instance.deleteFoto(photo.id)
                            if (!response.isSuccessful) {
                                Log.e("ActivityDetailView", "Error deleting photo: ${response.code()}")
                            }
                        }
                        deletedFotos = emptyList()

                        // Subir la foto actualizadas
                        imagesActividad.forEach { photo ->
                            // Aquí guardamos las fotos con las descripciones actualizadas
                            val response = RetrofitClient.instance.updateFoto(photo.id, photo)
                            if (!response.isSuccessful) {
                                Log.e("ActivityDetailView", "Error updating photo: ${response.code()}")
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

fun photoResponseToMultipart(context: Context, photo: PhotoResponse): MultipartBody.Part {
    val file = compressImage(context, Uri.parse(photo.urlFoto))
    Log.d("Upload", "Compressed file: ${file.absolutePath}, size: ${file.length()} bytes")
    val requestFile = RequestBody.create("image/jpeg".toMediaTypeOrNull(), file)
    return MultipartBody.Part.createFormData("fotos", file.name, requestFile)
}

fun uploadPhotoResponses(context: Context, photoResponses: List<PhotoResponse>, idActividad: Int, descripcion: String) {
    val multipartPhotos = photoResponses.map { photoResponseToMultipart(context, it) }

    if (multipartPhotos.isNotEmpty()) {
        Log.d("Upload", "Uploading ${multipartPhotos.size} photos")

        val response = RetrofitClient.instance.uploadPhotos(multipartPhotos, idActividad, descripcion)
        response.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("Upload", "Photos uploaded successfully")
                } else {
                    Log.e("Upload", "Failed to upload photos: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Upload", "Error uploading photos", t)
            }
        })
    } else {
        Log.e("Upload", "No photos to upload")
    }
}

fun compressImage(context: Context, uri: Uri): File {
    val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
    val bitmap = BitmapFactory.decodeStream(inputStream)
    val outputStream = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream) // Compress to 50% quality
    val byteArray = outputStream.toByteArray()
    val tempFile = File.createTempFile("compressed", ".jpg", context.cacheDir)
    tempFile.writeBytes(byteArray)
    return tempFile
}

@Preview(showBackground = true)
@Composable
fun ActivityScreenPreview() {
    val navController = rememberNavController()
    ActivityDetailView(navController, "11", false)
}