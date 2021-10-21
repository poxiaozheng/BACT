package com.example.bact.util

import java.text.SimpleDateFormat

object CommonUtil {
    fun timeStampToData(timeStamp: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
        return simpleDateFormat.format(timeStamp)
    }
}