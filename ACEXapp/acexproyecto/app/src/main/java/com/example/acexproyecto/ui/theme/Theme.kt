package com.example.acexproyecto.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

// Esquema de color para el modo oscuro
private val DarkColorScheme = darkColorScheme(
    primary = Accent,         // Color principal en modo oscuro
    onPrimary = Color.White,  // Texto sobre el color principal
    secondary = ButtonPrimary, // Elementos secundarios
    onSecondary = Color.White,
    background = Color(0xFF121212), // Fondo oscuro
    onBackground = Color.White, // Texto sobre el fondo oscuro
    surface = Color(0xFF1E1E1E), // Fondo de superficies oscuras
    onSurface = Color.White, // Texto sobre superficies oscuras
    surfaceVariant = Color(0xFF57626B)
)

// Esquema de color para el modo claro
private val LightColorScheme = lightColorScheme(
    primary = ButtonPrimary,  // Color de botones y elementos llamativos
    onPrimary = Color.White,  // Texto sobre el color del botón
    secondary = Accent,       // Elementos secundarios
    onSecondary = Color.White,
    background = Background,  // Fondo general
    onBackground = TextPrimary, // Texto principal sobre el fondo claro
    surface = Color.White,    // Fondo blanco para superficies
    onSurface = TextPrimary,   // Texto sobre superficies claras
    surfaceVariant = TopAppBarBackground
)

@Composable
fun acexproyecto_BaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        // Si el dispositivo soporta colores dinámicos (Android 12+)
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // Si no, se elige el esquema de colores en base al modo de tema
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme, // Se asigna el esquema de colores dinámico o estático
        typography = Typography,   // Tipografía (asegúrate de tener definida la variable Typography)
        content = content          // Contenido de la pantalla
    )
}
