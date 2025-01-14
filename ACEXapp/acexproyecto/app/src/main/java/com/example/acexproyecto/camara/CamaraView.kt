package com.example.acexproyecto.camara

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.core.app.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.example.acexproyecto.views.ActivitiesView

@SuppressLint("RestrictedApi")
@Composable
fun CameraView(onPhotoTaken: (Uri) -> Unit, context: Context) {
    // Configura el uso de CameraX (ImageCapture)
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    val cameraProvider = cameraProviderFuture.get()

    val imageCapture = ImageCapture.Builder().build()

    // Configuración de vista previa
    val preview = Preview.Builder().build()

    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    // Configurar la cámara
    LaunchedEffect(key1 = true) {
        try {
            cameraProvider.unbindAll()

            // Enlazar la cámara con la vista previa
            cameraProvider.bindToLifecycle(
                context as ComponentActivity, cameraSelector, preview, imageCapture
            )

        } catch (e: Exception) {
            Toast.makeText(context, "Error al configurar la cámara", Toast.LENGTH_SHORT).show()
        }
    }

    // Vista de la cámara
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // Vista previa de la cámara aquí (puedes mostrar un Surface o un Composable que contiene la vista previa)
        // Preview setup is simplified, you'll likely use SurfaceView or similar to display camera feed
        Button(
            onClick = {
                takePhoto(imageCapture, context, onPhotoTaken)
            },
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp)
        ) {
            Text("Tomar Foto")
        }
    }
}


fun takePhoto(
    imageCapture: ImageCapture,
    context: Context,
    onPhotoTaken: (Uri) -> Unit
) {
    // Establecer los valores para guardar la imagen en el almacenamiento
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "photo_${System.currentTimeMillis()}")
        put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
    }

    // Obtener la URI del almacenamiento para la imagen
    val imageUri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    // Verificamos si la URI es válida
    if (imageUri != null) {
        // Abrir un OutputStream para la URI
        val outputStream = context.contentResolver.openOutputStream(imageUri)

        // Verificamos si el OutputStream es válido
        if (outputStream != null) {
            // Crear las opciones de archivo para la toma de fotos
            val outputOptions = ImageCapture.OutputFileOptions.Builder(outputStream).build()

            // Tomar la foto y guardarla en el archivo
            imageCapture.takePicture(
                outputOptions,
                ContextCompat.getMainExecutor(context),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        // Foto tomada con éxito, retornamos la URI
                        onPhotoTaken(imageUri)
                    }

                    override fun onError(exception: ImageCaptureException) {
                        // Manejar el error si ocurre
                        Toast.makeText(context, "Error al tomar la foto: ${exception.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        } else {
            // Si no se pudo abrir el OutputStream, mostrar un error
            Toast.makeText(context, "Error al abrir el OutputStream para guardar la imagen", Toast.LENGTH_SHORT).show()
        }
    } else {
        // Si no se pudo crear la URI, mostrar un error
        Toast.makeText(context, "Error al crear URI para la imagen", Toast.LENGTH_SHORT).show()
    }
}

@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val navController = rememberNavController()
    // Llamada a CameraView (no necesita parámetros en este contexto)
    CameraView(onPhotoTaken = { uri -> /* Manejar la URI de la foto */ }, context = navController.context)
}
