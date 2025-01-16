package com.example.acexproyecto.views

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.acexproyecto.ui.theme.ButtonPrimary
import com.example.acexproyecto.ui.theme.TextPrimary
import com.example.appacex.model.ActividadResponse
import com.example.appacex.model.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.ui.window.Popup

val PrimaryColor = Color(0xFF79B3BB)   // Primary color (light blue)
val SecondaryColor = Color(0xFF9AE7DF) // Secondary color (lighter blue)
val TertiaryColor = Color(0xFFD0E8F2)  // Tertiary color (light cyan)
val BackgroundColor = Color(0xFFF1F1F1) // Background color (off white)
val TextColor = Color(0xFF000000) // Text color (black)

@Composable
fun ActivitiesView(navController: NavController) {
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { TopBar(navController) },
        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {

                    SearchBar(
                        onSearchQueryChanged = { query -> searchQuery = query },
                        onFilterSelected = { filter -> selectedFilter = filter }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.5f)
                    ) {
                        AllActividades(navController, selectedFilter, searchQuery)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        OtrasActividades(navController)
                    }
                }
            }
        },
        bottomBar = { BottomDetailBar(navController) }
    )
}

@Composable
fun SearchBar(
    onSearchQueryChanged: (String) -> Unit,
    onFilterSelected: (String?) -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val filterOptions = listOf("Aprobada", "Realizada", "Cancelada", "Todas")
    var selectedFilter by remember { mutableStateOf<String?>(null) }
    var showPopup by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = searchText,
            onValueChange = {
                searchText = it
                onSearchQueryChanged(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            label = { Text("Buscar actividad...", color = TextPrimary) },
            leadingIcon = {
                Icon(imageVector = Icons.Filled.Search, contentDescription = "Buscar", tint = TextColor)
            },
            singleLine = true,
            shape = RoundedCornerShape(8.dp),
            trailingIcon = {
                IconButton(onClick = { showPopup = !showPopup }) {
                    Icon(imageVector = Icons.Filled.Menu, contentDescription = "Filtrar")
                }
            }
        )

        if (showPopup) {
            Popup(
                alignment = Alignment.TopStart,
                onDismissRequest = { showPopup = false }
            ) {
                Card(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    colors = CardDefaults.cardColors(containerColor = TertiaryColor)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        filterOptions.forEach { filter ->
                            Text(
                                text = filter,
                                modifier = Modifier
                                    .clickable {
                                        selectedFilter = if (filter == "Todas") null else filter
                                        onFilterSelected(selectedFilter)
                                        showPopup = false
                                    }
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextPrimary
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalFoundationApi::class)
@Composable
fun AllActividades(navController: NavController, selectedFilter: String?, searchQuery: String) {
    val actividades = remember { mutableStateListOf<ActividadResponse>() }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(selectedFilter, searchQuery) {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getActividades().execute()
                if (response.isSuccessful) {
                    val filteredActividades = response.body()?.filter { actividad ->
                        val matchesFilter = selectedFilter?.let { actividad.estado.equals(it, true) } ?: true
                        val matchesSearchQuery = actividad.titulo.contains(searchQuery, true)
                        matchesFilter && matchesSearchQuery
                    } ?: emptyList()

                    actividades.clear()
                    actividades.addAll(filteredActividades)
                } else {
                    errorMessage.value = "Error: ${response.code()}"
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

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Actividades",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = 15.dp)
                .align(Alignment.CenterHorizontally),
            color = TextPrimary
        )

        if (isLoading.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (errorMessage.value != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = errorMessage.value ?: "Error desconocido", color = Color.Red)
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(actividades.size) { index ->
                    val actividad = actividades[index]
                    ActivityCardItem(
                        activityName = actividad.titulo,
                        activityDate = actividad.fini,
                        activityStatus = actividad.estado,
                        index = actividad.id,
                        navController = navController
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun OtrasActividades(navController: NavController) {
    val actividades = remember { mutableStateListOf<ActividadResponse>() }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getActividades().execute()
                if (response.isSuccessful) {
                    val approvedActividades = response.body()?.filter { it.estado == "APROBADA" } ?: emptyList()
                    actividades.addAll(approvedActividades)
                } else {
                    errorMessage.value = "Error: ${response.code()}"
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

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Mis Actividades",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.CenterHorizontally),
            color = TextPrimary
        )

        if (isLoading.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (errorMessage.value != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = errorMessage.value ?: "Error desconocido", color = Color.Red)
            }
        } else {
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                items(actividades.size) { index ->
                    val actividad = actividades[index]
                    ActivityCardItem(
                        activityName = actividad.titulo,
                        activityDate = actividad.fini,
                        activityStatus = actividad.estado,
                        index = actividad.id,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainScreenPreview() {
    val navController = rememberNavController()
    ActivitiesView(navController)
}
