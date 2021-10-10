package com.example.bact.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import java.io.File
import java.io.FileInputStream
import java.io.IOException

object CommonUtil {

    fun getSp(context: Context): SharedPreferences {
        return context.getSharedPreferences("data", Context.MODE_PRIVATE)
    }


    fun encodeFileBase64(FilePath: String): String? {
        val file = File(FilePath)
        val fi = FileInputStream(file)
        val length = fi.available()
        val data = ByteArray(length)
        fi.read(data, 0, length)
        fi.close()
        return Base64.encodeToString(data, Base64.DEFAULT)
    }

}