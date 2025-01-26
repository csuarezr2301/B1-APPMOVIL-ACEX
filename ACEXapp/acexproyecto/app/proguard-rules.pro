# Mantener las clases y métodos de la aplicación
-keep class com.example.** { *; }

# Mantener las clases y métodos de Retrofit
-keep class retrofit2.** { *; }
-dontwarn retrofit2.**

# Mantener las clases y métodos de Gson
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**

# Mantener las clases y métodos de Firebase
-keep class com.google.firebase.** { *; }
-dontwarn com.google.firebase.**

# Mantener las clases y métodos de Room
-keep class androidx.room.** { *; }
-dontwarn androidx.room.**

# Mantener las clases y métodos de Glide
-keep class com.bumptech.glide.** { *; }
-dontwarn com.bumptech.glide.**

# Mantener las clases y métodos de Dagger
-keep class dagger.** { *; }
-dontwarn dagger.**

# Mantener las clases y métodos de Hilt
-keep class dagger.hilt.** { *; }
-dontwarn dagger.hilt.**

# Mantener las clases y métodos de Jetpack Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

-dontwarn org.xmlpull.v1.**
-dontwarn org.kxml2.io.**
-dontwarn android.content.res.**
-dontwarn org.slf4j.impl.StaticLoggerBinder

-keep class org.xmlpull.** { *; }
-keepclassmembers class org.xmlpull.** { *; }

-dontwarn java.awt.datatransfer.Transferable
-dontwarn reactor.blockhound.integration.BlockHoundIntegration