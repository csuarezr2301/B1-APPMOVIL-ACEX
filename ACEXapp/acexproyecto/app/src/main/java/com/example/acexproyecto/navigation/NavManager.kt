/**
 * Aplicación de gestión de actividades extraescolares
 * Realizada por el grupo 1 de DAM2
 * Santiago Tamayo
 * Carmen Suarez
 */

package com.example.acexproyecto.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.acexproyecto.objetos.Loading
import com.example.acexproyecto.views.ActividadesListView
import com.example.acexproyecto.views.ActivitiesView
import com.example.acexproyecto.views.ActivityDetailView
import com.example.acexproyecto.views.ChatView
import com.example.acexproyecto.views.HomeView
import com.example.acexproyecto.views.LoginView
import com.example.acexproyecto.views.LocalizacionView
import com.example.acexproyecto.views.SettingsView


@Composable
fun NavManager(navController: NavHostController, isDarkMode: Boolean, onThemeChanged: (Boolean) -> Unit) {
    NavHost(
        navController = navController,
        startDestination = "principal"
    ) {
        composable("principal") {
            LoginView(navController)
        }

        composable("home") {
            HomeView(navController) {
                Loading.isLoading = false
            }
        }

        composable("maps") {
            LocalizacionView(navController, isDarkMode)
        }

        composable("settingsandprofile") {
            SettingsView(navController, isDarkMode, onThemeChanged)
        }

        composable("activities") {
            ActivitiesView(navController)
        }

        composable("chat") {
            ActividadesListView(navController)
        }

        composable("detalle_actividad_screen/{activityId}") { backStackEntry ->
            val activityId = backStackEntry.arguments?.getString("activityId") ?: return@composable
            ActivityDetailView(navController = navController, activityId = activityId, isDarkMode)
        }

        composable("chat/{activityId}") { backStackEntry ->
            val activityId = backStackEntry.arguments?.getString("activityId") ?: return@composable
            ChatView(
                navController = navController,
                activityId = activityId
            )
        }
    }
}




