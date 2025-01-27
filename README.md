# B1-APPMOVIL-ACEX

# Documentación del Reto

## 📌 Índice
1. [Introducción](#introducción)
2. [Miembros del Equipo](#miembros-del-equipo)
3. [Características Principales](#características-principales)
4. [Producto Obtenido, Mejoras y Pendientes](#producto-obtenido-mejoras-y-pendientes)
5. [Módulos y Tecnologías](#módulos-y-tecnologías)
6. [Estructura del Proyecto](#estructura-del-proyecto)
7. [Funcionalidades del Proyecto](#funcionalidades-del-proyecto)
8. [Configuración y Dependencias](#configuración-y-dependencias)
9. [Instalación y Uso](#instalación-y-uso)
10. [Manual de Usuario](#manual-de-usuario)
11. [Bibliografía y Webgrafía](#bibliografía-y-webgrafía)
---

## 📖 Introducción
**Nombre del reto:**  GESTIÓN DE ACTIVIDADES COMPLEMENTARIAS Y EXTRAECOLARES    
**Descripción corta:**  Desarrollo de un software para la gestión de las
actividades complementarias y realizadas en el
IES Miguel Herrero

---
## 👨‍💻 Miembros del Equipo
Grupo B1  Alumnos:

- Carmen Suárez Riaño
- Miguel Gutiérrez Caso
- Samuel David Sánchez Sinning
- Santiago Manuel Tamayo Arozamena

--- 
## ⭐ Características Principales
- **Registro y gestión de actividades** programadas y realizadas.
- **Subida y visualización de imágenes** de las actividades.
- **Registro de asistencia** de los alumnos.
- **Mapa interactivo** para marcar puntos de interés en actividades extraescolares.
- **Generación de informes estadísticos** sobre actividades realizadas.
- **Gestión de usuarios con diferentes roles** (Profesor, Administrador ACEX, Equipo Directivo).

---

## 🛠 Producto Obtenido

La aplicación desarrollada proporciona una solución para gestionar las **actividades complementarias y extraescolares** en el **IES Miguel Herrero**. La app permite a los profesores gestionar actividades de manera eficiente, realizar solicitudes, visualizar información relevante y registrar asistencia. Además, la aplicación móvil permite tomar fotos de las actividades y registrar comentarios durante su realización.


### 🚀 Mejoras sobre los Requisitos del Producto

1. **Interfaz de usuario mejorada**: Se ha añadido un diseño intuitivo con funcionalidades visuales como un mapa interactivo.
2. **Subida de fotos en tiempo real**: Los profesores pueden tomar y subir fotos durante las actividades. Esta función mejora la visibilidad de las actividades en curso.
3. **Registro de asistencia**: Se ha implementado un sistema de asistencia que permite a los responsables registrar la presencia de los estudiantes de manera rápida y eficiente.


### 🛠 Lo que falta por Implementar


---

## 🔧 Módulos y Tecnologías
| Módulo                 | Tecnologías Usadas              |  
|------------------------|---------------------------------|  
| **Frontend Móvil**     | Kotlin (Android Studio)         |  
| **Backend**            | Java (Spring Boot)               |  
| **Base de Datos**      | SQL Server en Azure             |  
| **Autenticación**      | Microsoft Azure AD              |  
| **Mapa Interactivo**   | Google Maps API                 |  
| **Almacenamiento**     | Azure Storage                   |  
| **Navegación**         | Android Navigation Component   |  
| **Sistema de Fotos**   | CameraX, Glide, Coil           |  
| **Chat**               | Firebase Firestore, MSAL       |  
| **Coroutines**         | Kotlin Coroutines               |  

---
## 🗂 Estructura del Proyecto
Este proyecto sigue una estructura organizada en carpetas. La estructura es la siguiente:
├── 📂 camara
│   └── 📄 CamaraView.kt
├── 📂 components
│   └── 📄 Calendario.kt
├── 📂 model
│   ├── 📄 ActividadResponse.kt
│   ├── 📄 ApiService.kt
│   ├── 📄 CursoResponse.kt
│   ├── 📄 DepartamentoResponse.kt
│   ├── 📄 GrupoParticipanteResponse.kt
│   ├── 📄 GrupoResponse.kt
│   ├── 📄 PhotoResponse.kt
│   ├── 📄 ProfesorParticipanteResponse.kt
│   ├── 📄 ProfesorResponsableResponse.kt
│   ├── 📄 ProfesorResponse.kt
│   └── 📄 RetrofitClient.kt
├── 📂 navigation
│   └── 📄 NavManager.kt
├── 📂 objetos
│   ├── 📄 Loading.kt
│   └── 📄 Usuario.kt
├── 📂 ui.theme
├── 📂 utils
│   ├── 📄 Firebase.kt
│   └── 📄 Utilidades.kt
├── 📂 views
│   ├── 📄 ActividadesListView.kt
│   ├── 📄 ActivitiesView.kt
│   ├── 📄 ActivityDetailView.kt
│   ├── 📄 ChatView.kt
│   ├── 📄 HomeView.kt
│   ├── 📄 LocalizacionView.kt
│   ├── 📄 LoginView.kt
│   ├── 📄 SettingView.kt
│   └── 📄 MainActivity.kt

📂 camara
Esta carpeta se encarga de gestionar las funcionalidades relacionadas con la cámara en la aplicación móvil. En este módulo, el archivo principal CamaraView.kt manejará la interacción de la cámara para que los usuarios puedan tomar fotos durante las actividades y subirlas a la plataforma.

📂 components
En esta carpeta se encuentran los componentes visuales reutilizables que forman parte de la interfaz de usuario (UI). En este caso el calendario de la app.

📂 model
Esta carpeta contiene los modelos de datos y las clases de servicio necesarias para la comunicación con el backend (a través de API). Aquí se definen las estructuras de datos utilizadas en la app.

- **ActividadResponse.kt**: Define la respuesta esperada del servidor para las actividades (información de cada actividad, como nombre, fecha, participantes, etc.).
- **ApiService.kt**: Contiene las interfaces y métodos para hacer peticiones a la API (como obtener información de actividades, registrar asistencia, etc.).
- **CursoResponse.kt**: Modelo que representa la respuesta del servidor para información sobre los cursos relacionados con las actividades.
- **DepartamentoResponse.kt**: Representa los datos relacionados con los departamentos involucrados en las actividades.
- **GrupoParticipanteResponse.kt**: Información sobre los grupos de participantes de las actividades.
- **GrupoResponse.kt**: Representa un grupo específico asociado a las actividades.
- **PhotoResponse.kt**: Responde con la información relacionada a las fotos que los usuarios suben durante las actividades.
- **ProfesorParticipanteResponse.kt**: Información sobre los profesores participantes en una actividad.
- **ProfesorResponsableResponse.kt**: Representa al profesor responsable de la actividad.
- **ProfesorResponse.kt**: Contiene los datos de los profesores, que pueden ser consultados a través de la app.
- **RetrofitClient.kt**: Configura la instancia de Retrofit para las llamadas HTTP hacia el backend.

📂 navigation
La carpeta navigation gestiona la navegación de la aplicación.

📂 objetos
Esta carpeta almacena objetos o clases que representan datos generales o utilitarios utilizados en la app.

📂 ui.theme
Esta carpeta contiene los archivos que definen los temas visuales de la app, incluyendo la personalización de colores, tipografías, y otros aspectos visuales.

📂 utils
Contiene funciones y utilidades que son utilizadas en distintas partes del proyecto. Generalmente se guardan aquí las funciones auxiliares que no encajan en otras categorías.

Firebase.kt: Funciones relacionadas con la integración de Firebase, como autenticación, manejo de bases de datos en tiempo real, y almacenamiento.
Utilidades.kt: Funciones generales de utilidad que pueden ser reutilizadas en diferentes partes de la app, como validaciones, transformaciones de datos, entre otros.

📂 views
Esta carpeta contiene las vistas principales de la aplicación, que son las pantallas con las que los usuarios interactúan.

A continuación, se detalla el propósito de cada módulo dentro de la carpeta views:

### **Settingview**
Este módulo gestiona la **vista de la información del usuario** y permite al usuario cambiar entre los **temas claro y oscuro** de la aplicación. Es donde se encuentran las configuraciones personalizadas del usuario.

### **Loginview**
Es la pantalla **inicial de inicio de sesión**. En esta vista, el usuario introduce sus credenciales para autenticarse en la aplicación. Una vez autenticado, se redirige a la vista principal de la app.

### **Localizacionview**
Este módulo muestra un **mapa interactivo** con los **puntos de interés** de las actividades. Los puntos de interés se marcan en el mapa para que los usuarios puedan ver las ubicaciones relacionadas con las actividades extraescolares.

### **Homeview**
La vista principal de la aplicación, donde se muestra:
- Información básica del **usuario**.
- Un **calendario** con las actividades programadas.
- Un **LazyRow** que muestra las actividades a realizar a partir del **día de hoy**.
- **Cerrar sesión**.
- Acceder a las **Preguntas Frecuentes** donde se puede descargar un documento con las normas relativas a las actividades.

### **Chatview**
En este módulo se encuentra el **chat** con las actividades del usuario registrado. Permite la comunicación con otros usuarios o administradores sobre las actividades en las que está involucrado.

### **Activitydetailview**
Este módulo muestra toda la información detallada de una actividad específica. Aquí se puede:
- **Editar información** de la actividad (si el usuario tiene permisos).
- Ver **nombre**, **alumnos asistentes**, **profesores asistentes**.
- Ver y añadir **fotos** de la actividad.
- Consultar la **descripción** de la actividad.
- Mostrar un **mapa** con la **localización** de la actividad.
- Añadir **observaciones** relacionadas con la actividad.

### **Activityview**
Este módulo muestra un listado de **todas las actividades** disponibles y permite **filtrar** por distintas categorías. Además, se muestran las actividades asociadas al usuario. En la **Top App Bar**, se encuentran opciones para:

---
### 🛠 Características Especiales

- **Sistema de Fotos**: Los usuarios pueden tomar fotos durante las actividades y cargarlas en la aplicación.
- **Chat**: Funcionalidad de chat para interactuar sobre las actividades y mantener la comunicación con otros usuarios.
- **Mapa de Localización**: Los puntos de interés de las actividades están marcados en un mapa interactivo.
- **Navegación**: Utiliza la librería de navegación de Jetpack Compose para facilitar la gestión de las vistas de manera eficiente.

---

## 🧰 Funcionalidades del Proyecto

### **Usuario (Profesor)**

- Ver información sobre actividades realizadas y programadas.
- Visualizar el estado de las actividades a través de un sistema visual de etiquetado.
- Cargar información sobre la participación en actividades como fotografías y asistencia de sus actividades.


### **Usuario (Administrador ACEX)**
- Realizar modificaciones de los datos de todas las actividades.

### **Usuario (Equipo Directivo)**
- Realizar modificaciones de los datos de todas las actividades.

---
## 🔧Configuración y Dependencias
Algunas dependencias utilizadas en el proyecto son:

- **Retrofit** : Para realizar las solicitudes HTTP a la API.
- **Google Maps** : Para mostrar mapas interactivos en la aplicación.
- **Firebase** : Para autenticación y almacenamiento de datos.
- **MSAL** : Para autenticar usuarios con la plataforma de Microsoft.

---
## 🚀 Instalación y Uso

### **📥 Clonar el Repositorio**
```bash
git clone:  https://github.com/csuarezr2301/B1-APPMOVIL-ACEX
```
---

## 📑 Manual de usuario  
Consulta el manual de usuario en PDF para detalles sobre el uso de la app:  
📄 <a href="https://drive.google.com/file/d/1pUmubahHZevSJ5sq94tHa3mf8E9e-VI-" download>Descargar Manual de Usuario</a>

---
## 📚 Bibliografía y Webgrafía

- 🗺️ [API de Google Maps](https://developers.google.com/maps/documentation/android-sdk/overview)

