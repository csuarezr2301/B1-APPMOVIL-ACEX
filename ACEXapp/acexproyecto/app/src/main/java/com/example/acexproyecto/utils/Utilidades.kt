package com.example.acexproyecto.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.http.HttpException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresExtension
import com.example.acexproyecto.objetos.Usuario
import com.example.appacex.model.ProfesorResponse
import com.example.appacex.model.RetrofitClient
import com.microsoft.graph.http.GraphServiceException
import com.microsoft.graph.requests.GraphServiceClient
import com.microsoft.identity.client.IAuthenticationResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.concurrent.CompletableFuture

fun fetchUserProfile(context: Context, authenticationResult: IAuthenticationResult, callback: () -> Unit) {
    val accessToken = authenticationResult.accessToken

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val graphClient = GraphServiceClient
                .builder()
                .authenticationProvider { CompletableFuture.completedFuture(accessToken) }
                .buildClient()

            val user = graphClient.me().buildRequest().get()
            val displayName = user?.displayName ?: "Unknown"

            val inputStream = try {
                graphClient.me().photo().content().buildRequest().get()
            } catch (e: GraphServiceException) {
                if (e.serviceError?.code == "ImageNotFound") {
                    Log.e("LoginDialogFragment", "La imagen no se ha encontrado")
                    null
                } else {
                    throw e
                }
            }

            val photoPath = if (inputStream != null) {
                saveImageToFile(context, inputStream.readBytes())
            } else {
                ""
            }

            Usuario.photoPath = photoPath
            Usuario.displayName = displayName

            withContext(Dispatchers.Main) {
                callback()
            }
        } catch (e: Exception) {
            Log.e("LoginDialogFragment", "Error fetching user profile", e)
            withContext(Dispatchers.Main) {
                callback()
            }
        }
    }
}

@RequiresExtension(extension = Build.VERSION_CODES.S, version = 7)
suspend fun checkProfessorEmail(email: String, callback: (Boolean) -> Unit) {
    withContext(Dispatchers.IO) {
        try {
            val response = RetrofitClient.instance.getProfesores().execute()
            if (response.isSuccessful) {
                val profesores = response.body()
                val profesor : ProfesorResponse? = profesores?.find {
                    it.correo.equals(email, ignoreCase = true) && it.activo == 1
                }
                val isProfessor = profesor != null
                if (isProfessor) {
                    Usuario.profesor = profesor
                }
                callback(isProfessor)
            } else {
                Log.e("API", "Error en la respuesta de la API: ${response.code()}")
                callback(false)
            }
        } catch (e: HttpException) {
            Log.e("API", "Error en la solicitud a la API", e)
            callback(false)
        } catch (e: Exception) {
            Log.e("API", "Error desconocido", e)
            callback(false)
        }
    }
}

fun saveImageToFile(context: Context, imageBytes: ByteArray): String {
    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    val file = File(context.filesDir, "profile_image.jpg")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
    return file.absolutePath
}