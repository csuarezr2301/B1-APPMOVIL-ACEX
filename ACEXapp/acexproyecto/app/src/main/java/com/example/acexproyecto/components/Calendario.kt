/**
 * Aplicación de gestión de actividades extraescolares
 * Realizada por el grupo 1 de DAM2
 * Santiago Tamayo
 * Carmen Suarez
 */

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.example.acexproyecto.ui.theme.TopAppBarBackground
import com.microsoft.graph.models.Event
import com.microsoft.graph.requests.GraphServiceClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.concurrent.CompletableFuture

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Calendario(accessToken: String, calendarId: String) {
    var events by remember { mutableStateOf<List<Event>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    LaunchedEffect(currentMonth) {
        isLoading = true
        events = fetchCalendarEvents(accessToken, calendarId) ?: emptyList()
        isLoading = false
    }

    Surface(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .heightIn(min = 245.dp, max = 290.dp)
            .shadow(16.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            CalendarView(currentMonth, events, onMonthChange = { newMonth ->
                currentMonth = newMonth
            })
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarView(currentMonth: YearMonth, events: List<Event>, onMonthChange: (YearMonth) -> Unit) {
    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfMonth = (currentMonth.atDay(1).dayOfWeek.value + 6) % 7
    val totalDays = firstDayOfMonth + daysInMonth
    val numberOfRows = if (totalDays > 35) 6 else 5
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")
    val today = LocalDate.now()

    val monthNames = listOf(
        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
    )

    var showDialog by remember { mutableStateOf(false) }
    var selectedEvent by remember { mutableStateOf<Event?>(null) }

    Column (
        modifier = Modifier.wrapContentSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { onMonthChange(currentMonth.minusMonths(1)) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Mes Anterior")
            }
            Text(text = "${monthNames[currentMonth.monthValue - 1]} ${currentMonth.year}", style = MaterialTheme.typography.headlineSmall)
            IconButton(onClick = { onMonthChange(currentMonth.plusMonths(1)) }) {
                Icon(Icons.Default.ArrowForward, contentDescription = "Mes Siguiente")
            }
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            listOf("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom").forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
            }
        }

        Column {
            var day = 1
            for (week in 0 until numberOfRows) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    for (weekday in 0..6) {
                        if (week == 0 && weekday < firstDayOfMonth || day > daysInMonth) {
                            Box(modifier = Modifier.weight(1f).aspectRatio(1.3f))
                        } else {
                            val date = currentMonth.atDay(day)
                            val event = events.find { event ->
                                val eventDate = LocalDate.parse(event.start?.dateTime, formatter)
                                eventDate == date
                            }
                            val isToday = date == today
                            val isPast = date.isBefore(today)
                            val backgroundColor = if (day % 2 == 0) TopAppBarBackground else MaterialTheme.colorScheme.secondary.copy(alpha = 0.05f)
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .aspectRatio(1.3f)
                                    .background(backgroundColor.copy(alpha = 0.13f))
                                    .clickable {
                                        if (event != null) {
                                            selectedEvent = event
                                            showDialog = true
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .background(
                                                if (isToday) Color.Blue.copy(alpha = 0.3f) else Color.Transparent,
                                                shape = CircleShape
                                            )
                                            .border(BorderStroke(2.dp, if (event != null) Color.Blue else Color.Transparent), shape = CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = day.toString(),
                                            style = MaterialTheme.typography.bodyLarge,
                                            textDecoration = if (isPast) TextDecoration.LineThrough else null
                                        )
                                    }
                                }
                            }
                            day++
                        }
                    }
                }
            }
        }
    }

    if (showDialog && selectedEvent != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(text = "Event Details") },
            text = { Text(text = selectedEvent?.subject ?: "No Title") },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text(text = "Aceptar", color = Color(0xFF007AFF))
                }
            }
        )
    }
}
suspend fun fetchCalendarEvents(accessToken: String, calendarId: String): List<Event>? {
    return try {
        Log.e("fetchCalendarEvents", "Iniciando fetchCalendarEvents de $calendarId")
        withContext(Dispatchers.IO) {
            val graphClient = GraphServiceClient
                .builder()
                .authenticationProvider { CompletableFuture.completedFuture(accessToken) }
                .buildClient()

            val events = graphClient.me().calendars(calendarId).events().buildRequest()?.get()?.currentPage
            Log.d("fetchCalendarEvents", "Events: $events")
            events
        }
    } catch (e: Exception) {
        Log.e("fetchCalendarEvents", "Error fetching calendar events $calendarId  --", e)
        null
    }
}