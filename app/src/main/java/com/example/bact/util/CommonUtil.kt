package com.example.bact.util

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.util.Base64
import java.io.File
import java.io.FileInputStream
import java.io.IOException

object CommonUtil {

    fun getSp(context: Context): SharedPreferences {
        return context.getSharedPreferences("data", Context.MODE_PRIVATE)
    }

}