package com.example.bact.util

import android.util.Log

object ExceptionUtil {

    const val TAG = "ExceptionUtil"

    fun wrapException(action: () -> Unit) {
        try {
            action()
        } catch (e: Throwable) {
        }
    }

    inline fun <reified T> dealException(
        onError: () -> T = { T::class.java.newInstance() },
        block: () -> T
    ): T {
        try {
            return block()
        } catch (e: Exception) {
            Log.d(TAG, "网络出现问题, $e")
            onError()
        }
        return block()
    }
}