package com.example.acexproyecto.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.acexproyecto.objetos.Loading
import com.example.acexproyecto.views.ActivitiesView
import com.example.acexproyecto.views.ActivityDetailView
import com.example.acexproyecto.views.HomeView
import com.example.acexproyecto.views.LoginView
import com.example.acexproyecto.views.LocalizacionView
import com.example.acexproyecto.views.SettingsView
import com.example.acexproyecto.views.chatView


@Composable
fun NavManager(navController: NavHostController, isDarkMode: Boolean, onThemeChanged: (Boolean) -> Unit) {
    NavHost(
        navController = navController,
        startDestination = "principal"
    ) {
        // Pantalla de login
        composable("principal") {
            LoginView(navController)
        }

        // Pantalla principal (Home)

        composable("home") {
            HomeView(navController) {
                Loading.isLoading = false
            }
        }

        // Pantalla de localización (Mapa)
        composable("maps") {
            LocalizacionView(navController)
        }

        // Pantalla de ajustes y perfil
        composable("settingsandprofile") {
            SettingsView(navController, isDarkMode, onThemeChanged)
        }

        // Pantalla de actividades
        composable("activities") {
            ActivitiesView(navController)
        }

        // Pantalla de chat
        composable("chat") {
            chatView(navController)
        }

        // Pantalla de actividad detalles
        composable("detalle_actividad_screen") {
            ActivityDetailView(navController)
        }
    }
}




