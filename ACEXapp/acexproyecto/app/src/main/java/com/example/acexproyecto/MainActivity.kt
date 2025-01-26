/**
 * Aplicación de gestión de actividades extraescolares
 * Realizada por el grupo 1 de DAM2
 * Santiago Tamayo
 * Carmen Suarez
 */

package com.example.acexproyecto

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.rememberNavController
import com.example.acexproyecto.navigation.NavManager
import com.example.acexproyecto.ui.theme.acexproyecto_BaseTheme
import com.example.acexproyecto.views.MsalAppHolder
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        db = FirebaseFirestore.getInstance()
        val onInitialized: () -> Unit = {
            setContent {
                var isDarkMode by remember { mutableStateOf(false) }

                val onThemeChanged: (Boolean) -> Unit = { darkMode ->
                    isDarkMode = darkMode
                }

                val navController = rememberNavController()

                acexproyecto_BaseTheme(darkTheme = isDarkMode) {
                    NavManager(
                        navController = navController,
                        isDarkMode = isDarkMode,
                        onThemeChanged = onThemeChanged
                    )
                }
            }
        }

        MsalAppHolder.initialize(application, onInitialized)
        db.collection("chats").get()
            .addOnSuccessListener { result ->
                if (!result.isEmpty) {
                    Log.d("MainActivity", "DB has data: ${result.documents.size} documents found")
                } else {
                    Log.d("MainActivity", "DB is empty")
                }
            }
            .addOnFailureListener { exception ->
                Log.e("MainActivity", "Error checking DB", exception)
            }
    }
}