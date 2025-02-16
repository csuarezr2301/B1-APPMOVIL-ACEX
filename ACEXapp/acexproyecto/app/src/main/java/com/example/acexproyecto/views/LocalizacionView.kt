/**
 * Aplicación de gestión de actividades extraescolares
 * Realizada por el grupo 1 de DAM2
 * Santiago Tamayo
 * Carmen Suarez
 */

package com.example.acexproyecto.views

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.example.acexproyecto.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.example.appacex.model.ActividadResponse
import com.example.appacex.model.RetrofitClient
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LocalizacionView(navController: NavController, isDarkMode: Boolean) {
    val isDarkTheme = isDarkMode

    Scaffold(
        topBar = { TopBar(navController) },

        content = { paddingValues ->
            LocalView(modifier = Modifier.padding(paddingValues),
                isDarkTheme = isDarkTheme) },
        bottomBar = { BottomDetailBar(navController) }
    )
}

@Composable
fun LocalView(modifier: Modifier = Modifier, isDarkTheme: Boolean) {
    val context = LocalContext.current
    val mapStyleOptions = remember {
        MapStyleOptions.loadRawResourceStyle(context, R.raw.map_style_dark)
    }
    val torrelavega = LatLng(43.353, -4.064)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(torrelavega, 10f)
    }

    val activities = remember { mutableStateOf<List<ActividadResponse>>(emptyList()) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val response: List<ActividadResponse> = RetrofitClient.instance.getActividades().execute().body() ?: emptyList()
                withContext(Dispatchers.Main) {
                    activities.value = response.filter { it.latitud != null && it.longitud != null }
                }
            } catch (e: Exception) {
                Log.e("LocalView", "Error fetching activities", e)
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapStyleOptions = if (isDarkTheme) mapStyleOptions else null)
        ) {
            activities.value.forEach { activity ->
                Marker(
                    state = com.google.maps.android.compose.rememberMarkerState(position = LatLng(activity.latitud!!, activity.longitud!!)),
                    title = activity.titulo,
                    snippet = activity.comentarios
                )
                Marker(
                    state = com.google.maps.android.compose.rememberMarkerState(position = torrelavega),
                    title = "IES Miguel Herrero Pereda",
                    snippet = "Torrelavega",
                    icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
            }
        }
    }
}
