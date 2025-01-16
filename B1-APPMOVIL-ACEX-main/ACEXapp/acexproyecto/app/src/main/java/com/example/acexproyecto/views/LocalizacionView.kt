package com.example.acexproyecto.views

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.acexproyecto.R
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.example.acexproyecto.ui.theme.*
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LocalizacionView(navController: NavController, isDarkMode: Boolean) {
    // Estructura principal con la barra inferior
    val isDarkTheme = isDarkMode
    val colors = if (isDarkTheme) {
        darkColorScheme()
    } else {
        lightColorScheme()
    }

    Scaffold(
        topBar = { TopBar(navController) }, // Barra superior con el logo

        content = { paddingValues ->
            // Pasamos los valores de padding a la vista del mapa para que no quede debajo de la barra inferior
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

    // Usamos un Box para organizar el mapa
    Box(modifier = modifier.fillMaxSize()) {
        // Mapa que ocupa toda la pantalla menos la barra inferior
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(mapStyleOptions = if (isDarkTheme) mapStyleOptions else null)
        ) {
            Marker(
                state = com.google.maps.android.compose.rememberMarkerState(position = torrelavega),
                title = "Torrelavega",
                snippet = "Cantabria"
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    val navController = rememberNavController()
    LocalizacionView(navController = navController, isDarkMode = true)
}