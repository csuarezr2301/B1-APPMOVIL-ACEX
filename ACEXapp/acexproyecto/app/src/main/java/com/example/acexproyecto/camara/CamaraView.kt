/**
 * Aplicación de gestión de actividades extraescolares
 * Realizada por el grupo 1 de DAM2
 * Santiago Tamayo
 * Carmen Suarez
 */

package com.example.acexproyecto.camara

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.app.ComponentActivity
import androidx.navigation.NavController
import coil.compose.rememberImagePainter

@SuppressLint("RestrictedApi")
@Composable
fun CamaraView(onPhotoTaken: (Uri) -> Unit, context: Context, navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { permissions ->
            if (permissions[Manifest.permission.CAMERA] == true &&
                permissions[Manifest.permission.WRITE_EXTERNAL_STORAGE] == true &&
                permissions[Manifest.permission.READ_EXTERNAL_STORAGE] == true
            ) {
                Toast.makeText(context, "Permisos concedidos", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(context, "Permisos denegados", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(key1 = true) {
        permissionLauncher.launch(
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        )
    }

    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    val imageCapture = ImageCapture.Builder().build()
    val preview = Preview.Builder().build()
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AndroidView(
            factory = { context ->
                val previewView = PreviewView(context).apply {
                    preview.setSurfaceProvider(surfaceProvider)
                }
                previewView
            },
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(100.dp), // Padding alrededor del botón
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = {
                    takePhoto(imageCapture, context) { uri ->
                        photoUri = uri
                        showDialog = true
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Tomar Foto")
            }
        }
    }

    if (showDialog && photoUri != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Guardar Foto") },
            text = {
                Column {
                    photoUri?.let {
                        Image(
                            painter = rememberImagePainter(it),
                            contentDescription = "Foto tomada",
                            modifier = Modifier.fillMaxWidth().height(200.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("¿Deseas guardar esta foto?")
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        photoUri?.let { uri ->
                            onPhotoTaken(uri)
                        }
                        showDialog = false
                    }
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        photoUri = null
                    }
                ) {
                    Text("Tomar otra")
                }
            }
        )
    }

    LaunchedEffect(key1 = true) {
        try {
            val cameraProvider = cameraProviderFuture.get()
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(
                context as ComponentActivity, cameraSelector, preview, imageCapture
            )
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Error al configurar la cámara: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
        }
    }
}

fun takePhoto(
    imageCapture: ImageCapture,
    context: Context,
    onPhotoTaken: (Uri) -> Unit
) {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "photo_${System.currentTimeMillis()}")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
    }

    val imageUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    if (imageUri != null) {
        val outputStream = context.contentResolver.openOutputStream(imageUri)

        if (outputStream != null) {
            val outputOptions = ImageCapture.OutputFileOptions.Builder(outputStream).build()

            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        onPhotoTaken(imageUri)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        Toast.makeText(context, "Error al tomar la foto: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        } else {
            Toast.makeText(context, "Error al abrir el OutputStream para guardar la imagen", Toast.LENGTH_SHORT).show()
        }
    } else {
        Toast.makeText(context, "Error al crear URI para la imagen", Toast.LENGTH_SHORT).show()
    }
}