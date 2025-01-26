/**
 * Aplicación de gestión de actividades extraescolares
 * Realizada por el grupo 1 de DAM2
 * Santiago Tamayo
 * Carmen Suarez
 */

package com.example.acexproyecto.views

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.acexproyecto.objetos.Usuario
import com.example.appacex.model.ActividadResponse
import com.example.appacex.model.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun ActividadesListView( navController: NavHostController ) {
    val actividades = remember { mutableStateListOf<ActividadResponse>() }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    var filteredActividades by remember { mutableStateOf<List<ActividadResponse>>(emptyList()) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val uniqueActivityIds = mutableSetOf<Int>()

                val response = RetrofitClient.instance.getActividades().execute()
                if (response.isSuccessful) {
                    val approvedActividades = response.body()?.filter { it.estado == "APROBADA" } ?: emptyList()
                    actividades.addAll(approvedActividades)
                } else {
                    errorMessage.value = "Error: ${response.code()}"
                }
                val responseProfes = RetrofitClient.instance.getProfesoresparticipantes().execute()
                if (responseProfes.isSuccessful) {
                    val profesorParticipantes = responseProfes.body() ?: emptyList()
                    filteredActividades = actividades.filter { actividad ->
                        profesorParticipantes.any {
                            it.actividad.id == actividad.id && it.profesor.uuid == Usuario.profesor?.uuid
                        }.also { matches ->
                            if (matches) uniqueActivityIds.add(actividad.id)
                        }
                    }
                }

                val responseProfesResponsables = RetrofitClient.instance.getProfesoresResponsables().execute()
                if (responseProfesResponsables.isSuccessful) {
                    val profesorParticipantes = responseProfesResponsables.body() ?: emptyList()
                    filteredActividades += actividades.filter { actividad ->
                        profesorParticipantes.any {
                            it.actividad.id == actividad.id && it.profesor.uuid == Usuario.profesor?.uuid
                        } && !uniqueActivityIds.contains(actividad.id)
                    }.onEach { actividad ->
                        uniqueActivityIds.add(actividad.id)
                    }
                }
            } catch (e: Exception) {
                errorMessage.value = "Exception: ${e.message}"
            } finally {
                withContext(Dispatchers.Main) {
                    isLoading.value = false
                }
            }
        }
    }
    Scaffold(
        topBar = { TopBar(navController) },

        content = { paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isLoading.value) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else if (errorMessage.value != null) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(text = errorMessage.value ?: "Unknown error", color = Color.Red)
                    }
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        if (Usuario.profesor?.rol == "ED" || Usuario.profesor?.rol == "ADM") {
                            filteredActividades =  actividades
                        }

                        items(filteredActividades) { actividad ->
                            ActividadCard(actividad, navController)
                        }
                    }
                }
            }
        },
        bottomBar = { BottomDetailBar(navController) },
    )


}

@Composable
fun ActividadCard(actividad: ActividadResponse, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { navController.navigate("chat/${actividad.id}") },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = actividad.titulo, style = MaterialTheme.typography.titleLarge)
            Text(text = actividad.descripcion, style = MaterialTheme.typography.bodyMedium)
            Text(text = "Fecha: ${actividad.fini} - ${actividad.ffin}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "Estado: ${actividad.estado}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}