package com.example.acexproyecto.views

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberImagePainter
import com.example.acexproyecto.R
import com.example.acexproyecto.ui.theme.TextPrimary
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun ActivityDetailView(navController: NavController) {
   //cambiar la top bar para añadir el boton de regreso
    Scaffold(
        topBar = { TopBar(navController) },
        content = { paddingValues ->
            ActivityDetailContent(navController, modifier = Modifier.padding(paddingValues))
        },
        bottomBar = { BottomDetailBar(navController) }
    )
}

@Composable
fun ActivityDetailContent(navController: NavController, modifier: Modifier = Modifier) {
    var activityName by remember { mutableStateOf("Nombre de la actividad") }
    var activityDescription by remember { mutableStateOf("Descripción de la actividad. Aquí va la información detallada sobre la actividad, los objetivos y lo que ofrece.") }
    var isDialogVisible by remember { mutableStateOf(false) }

    // Aquí guardaremos las imágenes seleccionadas
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    // Lanzador para seleccionar una imagen desde la galería
    val getImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            // Si se seleccionó una imagen, la agregamos a la lista de imágenes seleccionadas
            selectedImages = selectedImages + it
        }
    }

    // Estado para manejar el pop-up
    var isPopupVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Nombre de la actividad con icono para editar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = activityName,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = {
                // Mostrar el cuadro de diálogo de edición
                isDialogVisible = true
            }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "Editar",
                    tint = TextPrimary
                )
            }
        }

        Text(text = "Fecha de Actividad", color = TextPrimary)

        // Descripción de la actividad
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)  // Limitar la altura de la descripción
                .padding(vertical = 8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)  // Espacio interno
            ) {
                item {
                    Text(
                        text = activityDescription,
                        fontSize = 16.sp,
                        color = TextPrimary
                    )
                }
            }
        }

        // Fotos de la actividad
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
            IconButton(
                onClick = {
                    // Acción para tomar foto
                    Toast.makeText(navController.context, "Tomar foto", Toast.LENGTH_SHORT).show()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Phone,
                    contentDescription = "Hacer foto",
                    tint = TextPrimary
                )
            }
        }

        // LazyRow para mostrar fotos de la actividad
        LazyRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(selectedImages.size + 1) { index -> // Cambiar el número según la cantidad de fotos
                if (index == 0) {
                    // Primer elemento: ícono de galería
                    Image(
                        painter = painterResource(id = R.drawable.camarasubir), // Usa tu imagen de galería
                        contentDescription = "Seleccionar foto",
                        modifier = Modifier
                            .size(120.dp)  // Tamaño de la imagen
                            .padding(end = 8.dp)
                            .clickable {
                                // Mostrar el popup al hacer clic en la imagen
                                isPopupVisible = true
                            }
                    )
                } else {
                    // Para las otras fotos, mostrar las imágenes seleccionadas
                    Image(
                        painter = rememberImagePainter(selectedImages[index - 1]), // Mostrar la imagen seleccionada
                        contentDescription = "Foto $index",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(end = 8.dp)
                    )
                }
            }
        }

        // Espacio para el mapa con la ubicación
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Localización de la actividad",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(14.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(4.dp)
                .background(Color.Gray) // Simulación de mapa
        ) {
            // Aquí se agregaría el mapa
            // MapaActividad()
        }
    }

    // Diálogo de edición para el nombre y la descripción
    if (isDialogVisible) {
        EditActivityDialog(
            activityName = activityName,
            activityDescription = activityDescription,
            onNameChange = { newName -> activityName = newName },
            onDescriptionChange = { newDescription -> activityDescription = newDescription },
            onDismiss = { isDialogVisible = false }
        )
    }

    // Mostrar el Popup cuando se hace clic en el primer ícono
    if (isPopupVisible) {
        PopupMenu(
            onDismissRequest = { isPopupVisible = false },
            onSelectGallery = {
                getImageLauncher.launch("image/*")
                isPopupVisible = false
            },
            onSelectCamera = {
                //takePictureLauncher.launch()
                isPopupVisible = false
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

@Preview(showBackground = true)
@Composable
fun ActivityScreenPreview() {
    val navController = rememberNavController()
    ActivityDetailView(navController)
}
