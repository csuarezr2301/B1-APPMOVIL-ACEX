package com.example.acexproyecto.views

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.acexproyecto.R
import com.example.acexproyecto.objetos.Usuario
import com.example.acexproyecto.ui.theme.TextPrimary
import com.example.appacex.model.ActividadResponse
import com.example.appacex.model.RetrofitClient
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ChatView(
    navController: NavController,
    activityId: String,
) {
    val userColors = remember { mutableStateMapOf<String, Color>() }
    val colors = listOf(
        Color(0xFF6A5ACD), // SlateBlue
        Color(0xFF4682B4), // SteelBlue
        Color(0xFF708090), // SlateGray
        Color(0xFF556B2F), // DarkOliveGreen
        Color(0xFF8B4513), // SaddleBrown
        Color(0xFF2F4F4F), // DarkSlateGray
        Color(0xFF4B0082), // Indigo
        Color(0xFF800000), // Maroon
        Color(0xFF483D8B), // DarkSlateBlue
        Color(0xFF2E8B57), // SeaGreen
        Color(0xFF3CB371), // MediumSeaGreen
        Color(0xFF8B0000)  // DarkRed
    )

    var showProfessorDialog by remember { mutableStateOf(false) }
    var activity by remember { mutableStateOf<ActividadResponse?>(null) }

    LaunchedEffect(activityId) {
        checkFirebaseConnection()
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getActividades().execute()
                if (response.isSuccessful) {
                    activity = response.body()?.find { it.id == activityId.toInt() }
                }
            } catch (e: Exception) {
                Log.e("ChatView", "Error fetching activity details", e)
            }
        }
    }

    Scaffold(
        topBar = { TopBar(navController) }, // Barra superior con el logo

        content = { paddingValues ->
            Column(
                modifier = Modifier.fillMaxSize()
                    .padding(paddingValues),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                activity?.let {
                    Box(
                        modifier = Modifier
                            .padding(12.dp)
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(Color(0xFFFFFFFF).copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                                .padding(8.dp).clickable { showProfessorDialog = true }
                        ) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_back),
                                    contentDescription = "Back"
                                )
                            }
                            Text(
                                text = it.titulo,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontSize = if (it.titulo.length > 20) 16.sp else 20.sp
                                ),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                }
                ChatScreen(activityId, Usuario.displayName, userColors, colors)
            }
        },
        bottomBar = { BottomDetailBar(navController) }, // Barra inferior
    )

    if (showProfessorDialog) {
        AlertDialog(
            onDismissRequest = { showProfessorDialog = false },
            title = { Text(text = "Profesores Responsables y Organizadores") },
            text = {
                Column {
                    activity?.solicitante?.let {
                        Text(text = "Responsable: ${it.nombre} ${it.apellidos}")
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showProfessorDialog = false }) {
                    Text(text = "Cerrar")
                }
            }
        )
    }
}

@Composable
fun ChatScreen(
    activityId: String,
    displayName: String?,
    userColors: MutableMap<String, Color>,
    colors: List<Color>
) {
    val context = LocalContext.current
    val messages = remember { mutableStateListOf<Message>() }
    val listState = rememberLazyListState()
    var message by remember { mutableStateOf("") }
    var lastReadMessageIndex by rememberSaveable { mutableStateOf(getLastReadMessageIndex(context, activityId)) }
    var unreadMessagesCount by remember { mutableStateOf(0) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(activityId) {
        fetchMessages(activityId) { fetchedMessages ->
            messages.clear()
            messages.addAll(fetchedMessages)
            lastReadMessageIndex = getLastReadMessageIndex(context, activityId).coerceAtLeast(0)
            unreadMessagesCount = messages.size - lastReadMessageIndex - 1
            coroutineScope.launch {
                listState.scrollToItem(lastReadMessageIndex)
            }
        }
        observeMessages(activityId) { updatedMessages ->
            messages.clear()
            messages.addAll(updatedMessages)
            unreadMessagesCount = updatedMessages.size - lastReadMessageIndex - 1
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.scrollToItem(lastReadMessageIndex.coerceAtLeast(0))
            }
        }
    }

    LaunchedEffect(listState.firstVisibleItemIndex) {
        if (listState.firstVisibleItemIndex + listState.layoutInfo.visibleItemsInfo.size >= messages.size) {
            coroutineScope.launch {
                delay(5000)
                lastReadMessageIndex = messages.size - 1
                if (lastReadMessageIndex >= 0) {
                    saveLastReadMessageIndex(context, activityId, lastReadMessageIndex)
                    unreadMessagesCount = 0
                }
            }
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            saveLastReadMessageIndex(context, activityId, lastReadMessageIndex)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.weight(1f)
            ) {
                items(messages) { msg ->
                    MessageBubble(message = msg, isOwnMessage = msg.sender == displayName, userColors = userColors, colors = colors)
                    if (messages.indexOf(msg) == lastReadMessageIndex && unreadMessagesCount > 0) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFADD8E6).copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Mensajes no leidos: $unreadMessagesCount",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
            Row(modifier = Modifier.padding(8.dp)) {
                TextField(
                    value = message,
                    onValueChange = { message = it },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (message.isNotEmpty()) {
                        sendMessage(activityId, Message(sender = displayName ?: "Unknown", content = message, timestamp = System.currentTimeMillis()))
                        message = ""
                    }
                }) {
                    Text("Enviar")
                }
            }
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    isOwnMessage: Boolean,
    userColors: MutableMap<String, Color>,
    colors: List<Color>
) {
    val color = userColors.getOrPut(message.sender) {
        colors[userColors.size % colors.size]
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = if (isOwnMessage) Alignment.CenterEnd else Alignment.CenterStart
    ) {
        Column(
            modifier = Modifier
                .shadow(8.dp, MaterialTheme.shapes.medium)
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(start = 16.dp, end = 16.dp, top = 10.dp, bottom = 6.dp),
        ) {
            Text(
                text = message.sender,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = color,
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyLarge,
                color = TextPrimary,
            )
            Text(
                text = formatTimestamp(message.timestamp),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = if (isOwnMessage) Modifier.align(Alignment.Start).padding(top = 5.dp) else Modifier.align(Alignment.End).padding(top = 5.dp)
            )
        }
    }
}

fun checkFirebaseConnection() {
    val db = FirebaseFirestore.getInstance()
    db.firestoreSettings = FirebaseFirestoreSettings.Builder()
        .setPersistenceEnabled(true)
        .build()

    db.collection("test").addSnapshotListener { snapshot, e ->
        if (e != null) {
            Log.e("FirebaseConnection", "Error connecting to Firebase", e)
            return@addSnapshotListener
        }
        if (snapshot != null && !snapshot.isEmpty) {
            Log.d("FirebaseConnection", "Successfully connected to Firebase")
        } else {
            Log.d("FirebaseConnection", "No data found")
        }
    }
}

fun sendMessage(activityId: String, message: Message) {
    val db = FirebaseFirestore.getInstance()
    db.collection("chats").document(activityId).collection("messages").add(message)
        .addOnSuccessListener {
            Log.d("ChatView", "Message sent successfully")
        }
        .addOnFailureListener { e ->
            Log.e("ChatView", "Error sending message", e)
        }
}

fun fetchMessages(activityId: String, onMessagesFetched: (List<Message>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("chats").document(activityId).collection("messages")
        .orderBy("timestamp")
        .get()
        .addOnSuccessListener { result ->
            val messages = result.map { document ->
                document.toObject(Message::class.java)
            }
            onMessagesFetched(messages)
        }
        .addOnFailureListener { exception ->
            Log.e("ChatView", "Error fetching messages", exception)
        }
}

fun observeMessages(activityId: String, onMessagesUpdated: (List<Message>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("chats").document(activityId).collection("messages")
        .orderBy("timestamp")
        .addSnapshotListener { snapshots, e ->
            if (e != null) {
                Log.e("ChatView", "Listen failed.", e)
                return@addSnapshotListener
            }

            val messages = snapshots?.map { document ->
                document.toObject(Message::class.java)
            } ?: emptyList()
            onMessagesUpdated(messages)
        }
}

fun formatTimestamp(timestamp: Long): String {
    val messageDate = Date(timestamp)
    val currentDate = Date()
    val sameDay = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(messageDate) == SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(currentDate)
    return if (sameDay) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(messageDate)
    } else {
        SimpleDateFormat("dd/MM - HH:mm", Locale.getDefault()).format(messageDate)
    }
}

fun saveLastReadMessageIndex(context: Context, activityId: String, index: Int) {
    if (index >= 0) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("ChatPrefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putInt("lastReadMessageIndex_$activityId", index)
            apply()
        }
        Log.d("ChatView", "Saved lastReadMessageIndex: $index for activityId: $activityId")
    } else {
        Log.e("ChatView", "Attempted to save invalid lastReadMessageIndex: $index for activityId: $activityId")
    }
}

fun getLastReadMessageIndex(context: Context, activityId: String): Int {
    val sharedPreferences: SharedPreferences = context.getSharedPreferences("ChatPrefs", Context.MODE_PRIVATE)
    val index = sharedPreferences.getInt("lastReadMessageIndex_$activityId", 0)
    Log.d("ChatView", "Retrieved lastReadMessageIndex: $index for activityId: $activityId")
    return if (index >= 0) index else 0
}

data class Message(
    val sender: String = "",
    val content: String = "",
    val timestamp: Long = 0L
)

@Preview(showBackground = true)
@Composable
fun ProfileViewPreview() {
    val navController = rememberNavController()
    ChatView(navController, "11")
}