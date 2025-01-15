package com.example.acexproyecto.views

import android.annotation.SuppressLint
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.acexproyecto.R
import com.example.acexproyecto.objetos.Usuario
import com.example.appacex.model.ActividadResponse
import com.example.appacex.model.RetrofitClient
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun chatView(navController: NavController) {
    Scaffold(
        topBar = { TopBar(navController) },
        content = {
            ChatView(
                navController = navController,
                activityId = "11", // Replace with actual activity ID
            )
        },
        bottomBar = { BottomDetailBar(navController) }
    )
}

@Composable
fun ChatView(
    navController: NavController,
    activityId: String,
) {
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
                ChatScreen(activityId, Usuario.displayName)
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
fun ChatScreen(activityId: String, displayName: String?) {
    val messages = remember { mutableStateListOf<Message>() }
    val listState = rememberLazyListState()
    var message by remember { mutableStateOf("") }

    LaunchedEffect(activityId) {
        fetchMessages(activityId) { fetchedMessages ->
            messages.clear()
            messages.addAll(fetchedMessages)
        }
        observeMessages(activityId) { updatedMessages ->
            messages.clear()
            messages.addAll(updatedMessages)
        }
    }

    // Scroll to the bottom when new messages are loaded
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.scrollToItem(messages.size - 1)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f)
        ) {
            items(messages) { msg ->
                MessageBubble(message = msg, isOwnMessage = msg.sender == displayName)
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

@Composable
fun MessageBubble(message: Message, isOwnMessage: Boolean) {
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
                color = if (isOwnMessage) Color.Blue else Color.Red,
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Text(
                text = message.content,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.Black
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

data class Message(
    val sender: String = "",
    val content: String = "",
    val timestamp: Long = 0L
)

@Preview(showBackground = true)
@Composable
fun ProfileViewPreview() {
    val navController = rememberNavController()
    chatView(navController)
}