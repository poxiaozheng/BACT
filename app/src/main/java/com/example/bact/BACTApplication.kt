package com.example.bact

import android.app.Application
import android.content.Context
import com.example.bact.model.database.ImageInfoRoomDatabase
import okhttp3.OkHttpClient

class BACTApplication : Application() {
    companion object {
        lateinit var appContext: Context
        val database: ImageInfoRoomDatabase by lazy { ImageInfoRoomDatabase.getDatabase() }
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}