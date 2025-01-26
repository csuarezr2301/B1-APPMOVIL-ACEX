/**
 * Aplicación de gestión de actividades extraescolares
 * Realizada por el grupo 1 de DAM2
 * Santiago Tamayo
 * Carmen Suarez
 */

package com.example.acexproyecto.views

import Calendario
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.acexproyecto.R
import com.example.acexproyecto.objetos.Usuario
import com.example.acexproyecto.ui.theme.*
import com.example.appacex.model.ActividadResponse
import com.example.appacex.model.RetrofitClient
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.exception.MsalException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.Locale

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun HomeView(navController: NavController, onLoadingComplete: () -> Unit) {
    LaunchedEffect(Unit) {
        onLoadingComplete()
    }

    Scaffold(
        topBar = { TopBar(navController) },

        content = { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                ContentDetailView(navController)
            }
        },
        bottomBar = { BottomDetailBar(navController) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(navController: NavController) {
    var showDialog by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logosinnombre),
                    contentDescription = "Logo de la App",
                    modifier = Modifier
                        .size(100.dp)
                )
            }
        },
        navigationIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.faq),
                    contentDescription = "FAQ",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = {
                MsalAppHolder.msalApp?.signOut(object : ISingleAccountPublicClientApplication.SignOutCallback {
                    override fun onSignOut() {
                        navController.navigate("principal")
                    }

                    override fun onError(exception: MsalException) {
                        Log.e("TopBar", "Error during sign out", exception)
                    }
                })
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.logout),
                    contentDescription = "Logout",
                    tint = Color.White
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
    )

    if (showDialog) {
        FAQDialog(onDismiss = { showDialog = false })
    }
}

@Composable
fun ContentDetailView(navController: NavController) {
    val activities = remember { mutableStateListOf<ActividadResponse>() }
    val isLoading = remember { mutableStateOf(true) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.getActividades().execute()
                if (response.code() == 500) {
                    errorMessage.value = "Internal Server Error. Please try again later."
                } else {
                    if (response.isSuccessful) {
                        val allActivities = response.body() ?: emptyList()

                        val currentDate = System.currentTimeMillis()
                        val filteredActivities = allActivities.filter { actividad ->
                            val activityDate = stringToDate(actividad.fini)
                            activityDate >= currentDate
                        }
                        activities.addAll(filteredActivities)
                    } else {
                        errorMessage.value = "Error: ${response.code()}"
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

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 12.dp)
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
            LazyColumn {
                item {
                    UserInformation()
                }
                item {
                    CalendarView()
                }
            }

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(8.dp)
            ) {
                items(activities.size) { index ->
                    val actividad = activities[index]
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

fun stringToDate(dateString: String): Long {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = format.parse(dateString)
    return date?.time ?: 0L
}

@Composable
fun FAQDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Preguntas Frecuentes y Normativa", color = TextPrimary) },
        text = {
            LazyColumn {
                items(faqList) { faq ->
                    ExpandableFAQItem(question = faq.question, answer = faq.answer)
                }
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Cerrar", color = Color.White)
            }
        },
        dismissButton = {
            GenerateFileButton(context = context)
        }
    )
}

@Composable
fun ExpandableFAQItem(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { expanded = !expanded }
    ) {
        Text(
            text = question,
            style = MaterialTheme.typography.bodyMedium ,
            color = TextPrimary,
            fontWeight = FontWeight.Bold
        )
        if (expanded) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = answer,
                style = MaterialTheme.typography.bodySmall,
                color = TextPrimary,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        Divider()
    }
}

val faqList = listOf(
    FAQ(
        "NORMATIVA sobre actividades complementarias y extraescolares (ACEX)",
        "La normativa relativa a las ACEX está recogida en el documento que contiene las Normas de Organización y Funcionamiento (NOF) de nuestro centro. No obstante, en este se recogen, entre otros, los siguientes aspectos:"+
                "• Las actividades complementarias y extraescolares son voluntarias.\n" +
                "• No serán evaluables a efectos académicos.\n" +
                "• Las actividades complementarias se realizan durante la jornada escolar con una duración máxima de una jornada lectiva.\n" +
                "• Las actividades extraescolares se realizan fuera de la jornada escolar salvo autorización del Servicio de Inspección de Educación.\n" +
                "• Se fomentará la coordinación de diferentes departamentos didácticos.\n" +
                "• La programación de ACEX forma parte de la Programación General Anual y requiere aprobación del claustro y consejo escolar.\n" +
                "• Actividades no programadas deben ser aprobadas con un mes de antelación por el departamento correspondiente.\n" +
                "• Los responsables deben informar a los padres/tutores y presentar la relación de alumnado con 24 horas de antelación.\n" +
                "• Se prioriza la participación de profesores del centro como acompañantes."
    ),
    FAQ(
        "¿Cómo inicio la preparación de una actividad extraescolar?",
        "El/la profesor/a responsable puede optar por dos opciones:\n" +
                "• OPCIÓN 1: PROFESORADO USUARIO DE TEAMS (PC/MÓVIL): Entrar en el CANAL ACEX del grupo de TEAMS IES MHP CLAUSTRO. En una de las pestañas hay disponible un formulario Microsoft Forms. Si se enviara el formulario con algún error, se puede enviar otro. Solo el último será tenido en cuenta.\n" +
                "• OPCIÓN 2: PROFESORADO SIN TEAMS EN EL MÓVIL: Escanear QR que dirige al formulario de Microsoft Forms. Si se enviara el formulario con algún error, se puede enviar otro. Solo el último será tenido en cuenta.\n" +
                "Nota: La actividad debe haber sido previamente reflejada en la Programación Anual del Departamento de ACEX, ya que esta ha sido aprobada como parte de la PGA por el Consejo Escolar."
    ),
    FAQ(
        "¿Cuándo inicio la preparación de una actividad extraescolar?",
        "El/la profesor/a responsable enviará el formulario al menos quince días antes de la realización de la actividad para poder solicitar los presupuestos necesarios con margen de tiempo. Si se requiere transporte, la responsable de extraescolares enviará tres presupuestos y el/la profesor/a responsable elegirá el que considere adecuado."
    ),
    FAQ(
        "¿Qué autorización necesitan los alumnos?",
        "Los padres y tutores legales pueden autorizar en el impreso de la matrícula la participación en las ACEX organizadas durante el horario escolar. Es preciso comprobar si los alumnos han sido autorizados previamente. Además, es obligatorio informar a los progenitores de la actividad con la antelación suficiente. Hay disponible una autorización estándar en el grupo de TEAMS para todo el profesorado."
    ),
    FAQ(
        "¿Existe transporte adaptado?",
        "Sí, existen autobuses adaptados. Sin embargo, según la experiencia de años anteriores, RENFE/FEVE no dispone de rampa para el correcto acceso de las sillas de ruedas en los vagones."
    ),
    FAQ(
        "¿Varían los precios dependiendo de la franja horaria del autobús?",
        "Sí, por regla general, las empresas de autobuses proponen un presupuesto inferior en el período comprendido entre segunda hora y quinta hora, ya que los autobuses son empleados para el transporte escolar."
    ),
    FAQ(
        "¿Las excursiones están subvencionadas?",
        "Solo aquellas que requieran transporte, pues la subvención se destina únicamente a cubrir gastos en transporte. En el caso de que la actividad sea subvencionada, se deben descontar los 2 € de la cantidad que se solicite a cada alumno. Además, en el caso de contratar un viaje en tren, Renfe ofrece un billete gratis cada diez pasajeros mayores de doce años."
    ),
    FAQ(
        "¿Cómo se realiza el pago del autobús?",
        "La empresa de autobuses remitirá la factura a la secretaría del centro, que procederá al pago de la misma."
    ),
    FAQ(
        "¿Cómo se realiza el pago del tren?",
        "El/la profesor/a responsable recibirá un archivo PDF con los detalles del viaje y el precio. El número de viajeros puede modificarse una vez en la estación. Deberá presentar el PDF y pagar los gastos de transporte en la estación de tren de cercanías de Torrelavega antes del viaje de ida o en destino si no hubiera taquilla. Se debe solicitar la factura en taquilla, que RENFE enviará por email:\n" +
                "CIF del IES MIGUEL HERRERO: Q3968397D\n" +
                "EMAIL: secretaria.iesmiguelherrero@educantabria.es\n" +
                "El/la profesor/a debe entregar en secretaría:\n" +
                "- El ticket\n" +
                "- El dinero aportado por el alumnado\n" +
                "- Lista de alumnos con la aportación de cada uno\n" +
                "- Memoria de justificación de pago (impreso disponible en secretaría) con el IBAN para autorizar el reembolso."
    ),
    FAQ(
        "¿Qué hacer con el dinero recaudado al alumnado?",
        "El dinero recaudado a cada alumno se entregará en secretaría (despacho de Nuria Celis Nieto), junto con la relación de los alumnos participantes y la aportación económica que ha hecho cada uno."
    ),
    FAQ(
        "¿Cuántos profesores acompañantes necesito?",
        "Se precisa 1 profesor acompañante por cada 20 alumnos. Si la actividad entraña una mayor vigilancia, será 1 profesor por cada 12 alumnos en ESO y FPB, y 1 por cada 15 alumnos en Bachillerato y FPGM/FPGS. En viajes fuera de España irán un mínimo de dos profesores, con una ratio de 1 por cada 12 alumnos."
    ),
    FAQ(
        "¿Cómo comunico qué alumnos asisten a la actividad?",
        "Con al menos 24 horas de antelación, el/la profesor/a responsable deberá colgar en la corchera de extraescolares la relación de alumnos que asisten y dejar una copia en Jefatura de Estudios. Las listas de alumnado por grupos están disponibles en la sala de profesores."
    ),
    FAQ(
        "¿Cómo consulto la programación de ACEX?",
        "La programación de actividades extraescolares puede consultarse de dos formas:\n" +
                "- En la pestaña CALENDARIO del canal ACEX del grupo de TEAMS IES MHP CLAUSTRO.\n" +
                "- Escaneando el CÓDIGO QR del calendario disponible en la sala de profesores."
    )

)

data class FAQ(val question: String, val answer: String)

fun generateTextFile(context: Context, faqList: List<FAQ>) {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "preguntas_frecuentes.txt")
        put(MediaStore.MediaColumns.MIME_TYPE, "text/plain")
        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
    }

    val uri = context.contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)

    uri?.let {
        try {
            val outputStream: OutputStream? = context.contentResolver.openOutputStream(it)

            outputStream?.let { stream ->
                val contentBuilder = StringBuilder()

                contentBuilder.append("Preguntas Frecuentes:\n\n")

                faqList.forEachIndexed { index, faq ->
                    contentBuilder.append("${index + 1}. ${faq.question}\n")
                    contentBuilder.append("${faq.answer}\n\n")
                }

                contentBuilder.append("\nEl archivo se ha descargado en la carpeta 'Descargas'.\n")

                stream.write(contentBuilder.toString().toByteArray())
                stream.close()
            }

            Toast.makeText(context, "Archivo guardado en la carpeta Descargas del dispositivo.", Toast.LENGTH_LONG).show()

        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(context, "Error al guardar el archivo.", Toast.LENGTH_LONG).show()
        }
    }
}

@Composable
fun GenerateFileButton(context: Context) {
    Button(onClick = { generateTextFile(context, faqList) }) {
        Text("Descargar Documento")
    }
}

@Composable
fun UserInformation() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp, start = 48.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(30.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                if (Usuario.photoPath.isNullOrEmpty()) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(Color.Gray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = getInitials(Usuario.displayName),
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                    }
                } else {
                    Image(
                        painter = rememberImagePainter(data = Usuario.photoPath),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(Usuario.displayName, fontSize = 20.sp, color = TextPrimary)
                    Text(Usuario.account, fontSize = 16.sp, color = TextPrimary)
                }
            }
        }
    }
}

fun getInitials(name: String?): String {
    return name?.split(" ")?.mapNotNull { it.firstOrNull()?.toString() }?.take(2)?.joinToString("")?.uppercase() ?: "?"
}

@Composable
fun CalendarView() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Calendario de Actividades",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Calendario(Usuario.msalToken, Usuario.calendarId)

    }
}

@Composable
fun ActivityCardItem(
    activityName: String,
    activityDate: String,
    activityStatus: String,
    index: Int,
    navController: NavController
) {
    val iconColor = when (activityStatus) {
        "APROBADA" -> TextPrimary
        "REALIZADA" -> TextPrimary
        "CANCELADA" -> TextPrimary
        else -> TextPrimary
    }

    Card(
        modifier = Modifier
            .padding(8.dp)
            .width(150.dp)
            .height(110.dp)
            .clickable {
                navController.navigate("detalle_actividad_screen/${index}")
            }
            .shadow(8.dp, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                activityName,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Text(activityDate, color = TextPrimary, fontSize = 14.sp)


            when (activityStatus) {
                "APROBADA" -> {
                    Icon(
                        painter = painterResource(id = R.drawable.aprobada),
                        contentDescription = "Pendiente",
                        tint = iconColor,
                        modifier = Modifier.size(30.dp)
                    )
                }
                "REALIZADA" -> {
                    Icon(
                        imageVector = Icons.Filled.CheckCircle,
                        contentDescription = "Aprobada",
                        tint = iconColor,
                        modifier = Modifier.size(30.dp)
                    )
                }
                "CANCELADA" -> {
                    Icon(
                        painter = painterResource(id = R.drawable.cancelada),
                        contentDescription = "Cancelada",
                        tint = iconColor,
                        modifier = Modifier.size(30.dp)
                    )
                }
                else -> {
                    Icon(
                        painter = painterResource(id = R.drawable.reloj),
                        contentDescription = "Pendiente",
                        tint = iconColor,
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }
    }
}


@Composable
fun BottomDetailBar(navController: NavController) {
    BottomAppBar(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(0.dp),
    ) {
        val currentRoute = navController.currentBackStackEntry?.destination?.route

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            IconButton(
                onClick = { if (currentRoute != "maps") navController.navigate("maps") },
                modifier = Modifier.size(65.dp)
            ) {
                Icon(imageVector = Icons.Filled.LocationOn,
                    contentDescription = "Maps",
                    tint = TextPrimary,
                    modifier = Modifier.shadow(elevation = if (currentRoute == "maps") 68.dp else 0.dp,
                        spotColor = MaterialTheme.colorScheme.primary))
            }

            IconButton(
                onClick = { if (currentRoute != "activities") navController.navigate("activities") },
                modifier = Modifier.size(65.dp)
            ) {
                Icon(imageVector = Icons.Filled.Star,
                    contentDescription = "Activities",
                    tint = TextPrimary,
                    modifier = Modifier.shadow(if (currentRoute == "activities") 68.dp else 0.dp,
                        spotColor = MaterialTheme.colorScheme.primary))
            }

            IconButton(
                onClick = { if (currentRoute != "home") navController.navigate("home") },
                modifier = Modifier.size(65.dp)
            ) {
                Icon(imageVector = Icons.Filled.Home,
                    contentDescription = "Home",
                    tint = TextPrimary,
                    modifier = Modifier.shadow(if (currentRoute == "home") 68.dp else 0.dp,
                        spotColor = MaterialTheme.colorScheme.primary))
            }

            IconButton(
                onClick = { if (currentRoute != "chat") navController.navigate("chat") },
                modifier = Modifier.size(65.dp)
            ) {
                Icon(imageVector = Icons.Filled.Email ,
                    contentDescription = "Chat",
                    tint = TextPrimary,
                    modifier = Modifier.shadow(if (currentRoute == "chat") 68.dp else 0.dp,
                        spotColor = MaterialTheme.colorScheme.primary))
            }

            IconButton(
                onClick = { if (currentRoute != "settingsandprofile") navController.navigate("settingsandprofile") },
                modifier = Modifier.size(65.dp)
            ) {
                Icon(imageVector = Icons.Filled.Person ,
                    contentDescription = "Perfil",
                    tint = TextPrimary,
                    modifier = Modifier.shadow(if (currentRoute == "settingsandprofile") 68.dp else 0.dp,
                        spotColor = MaterialTheme.colorScheme.primary))
            }
        }
    }
}
