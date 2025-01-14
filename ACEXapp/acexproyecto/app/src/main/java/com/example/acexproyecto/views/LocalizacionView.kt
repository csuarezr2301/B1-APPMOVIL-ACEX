package com.example.acexproyecto.views

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun LocalizacionView(navController: NavController) {
    // Estructura principal con la barra inferior
    Scaffold(
        topBar = { TopBar(navController) }, // Barra superior con el logo

        content = { paddingValues ->
            // Pasamos los valores de padding a la vista del mapa para que no quede debajo de la barra inferior
            LocalView(modifier = Modifier.padding(paddingValues))
        },
        bottomBar = { BottomDetailBar(navController) }
    )
}

@Composable
fun LocalView(modifier: Modifier = Modifier) {
    val torrelavega = LatLng(43.353, -4.064)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(torrelavega, 10f)
    }

    // Usamos un Box para organizar el mapa
    Box(modifier = modifier.fillMaxSize()) {
        // Mapa que ocupa toda la pantalla menos la barra inferior
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

@Preview(showBackground = true)
@Composable
fun MapScreenPreview() {
    val navController = rememberNavController()
    LocalizacionView(navController = navController)
}
