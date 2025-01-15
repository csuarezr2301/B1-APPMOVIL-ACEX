package com.example.acexproyecto.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.acexproyecto.objetos.Usuario
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

fun saveImageToFile(context: Context, imageBytes: ByteArray): String {
    val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    val file = File(context.filesDir, "profile_image.jpg")
    FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
    }
    return file.absolutePath
}