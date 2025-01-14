package com.example.acexproyecto.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.acexproyecto.views.ActivitiesView
import com.example.acexproyecto.views.ActivityDetailView
import com.example.acexproyecto.views.HomeView
import com.example.acexproyecto.views.LoginView
import com.example.acexproyecto.views.LocalizacionView
import com.example.acexproyecto.views.SettingsView
import com.example.acexproyecto.views.chatView
import java.net.URLDecoder
import java.nio.charset.StandardCharsets


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

        composable(
            "home/{displayName}/{photoPath}/{account}",
            arguments = listOf(
                navArgument("displayName") { type = NavType.StringType },
                navArgument("photoPath") { type = NavType.StringType },
                navArgument("account") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val displayName = URLDecoder.decode(backStackEntry.arguments?.getString("displayName") ?: "", StandardCharsets.UTF_8.toString())
            val photoPath = URLDecoder.decode(backStackEntry.arguments?.getString("photoPath") ?: "", StandardCharsets.UTF_8.toString())
            val account = URLDecoder.decode(backStackEntry.arguments?.getString("account") ?: "", StandardCharsets.UTF_8.toString())
            HomeView(navController, displayName, photoPath, account)
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




