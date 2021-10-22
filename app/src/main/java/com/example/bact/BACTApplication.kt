package com.example.bact

import android.app.Application
import android.content.Context
import com.example.bact.model.database.ImageInfoRoomDatabase
import okhttp3.OkHttpClient

class BACTApplication : Application() {

    val database: ImageInfoRoomDatabase by lazy { ImageInfoRoomDatabase.getDatabase() }

    companion object {
        lateinit var appContext: Context
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}