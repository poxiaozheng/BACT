package com.example.bact

import android.app.Application
import okhttp3.OkHttpClient

class BACTApplication : Application() {

    companion object{
        var okHttpClient:OkHttpClient? = null
    }

    override fun onCreate() {
        super.onCreate()
        okHttpClient = OkHttpClient().newBuilder().build()
    }
}