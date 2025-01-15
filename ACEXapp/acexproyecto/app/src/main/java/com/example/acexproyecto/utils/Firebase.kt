package com.example.acexproyecto.utils

import com.google.firebase.FirebaseApp
import android.app.Application

class Firebase : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
}