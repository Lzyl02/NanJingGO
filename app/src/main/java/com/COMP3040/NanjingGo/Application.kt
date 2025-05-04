package com.COMP3040.NanjingGo

import android.app.Application
import com.google.firebase.FirebaseApp

class NanjingGoApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
} 