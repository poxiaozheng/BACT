package com.example.bact

import android.app.Application
import android.content.Context
import com.example.bact.model.database.ImageInfoRoomDatabase
import okhttp3.OkHttpClient

class BACTApplication : Application() {
    companion object {
        val hashMap : HashMap<Int,String> by lazy {
            HashMap<Int,String>().apply {
                this[0] = "无"
                this[1] = "低"
                this[2] = "中"
                this[3] = "高"
            }
        }
        lateinit var appContext: Context
        val database: ImageInfoRoomDatabase by lazy { ImageInfoRoomDatabase.getDatabase() }
    }

    override fun onCreate() {
        super.onCreate()
        appContext = applicationContext
    }
}