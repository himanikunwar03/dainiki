package com.example.c36b

import android.app.Application
import com.cloudinary.android.MediaManager

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        MediaManager.init(this)
    }
}

