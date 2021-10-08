package com.example.bact.util

import android.content.Context
import android.content.SharedPreferences

object CommonUtil {

    fun getSp(context: Context): SharedPreferences {
        return context.getSharedPreferences("data", Context.MODE_PRIVATE)
    }

}