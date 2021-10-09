package com.example.bact.util

import android.content.Context

object DisplayUtil {

    fun px2dp(context: Context, px: Float): Float {
        val scale = context.resources.displayMetrics.density
        return px / scale + 0.5f
    }

    fun dp2px(context: Context, dp: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dp * scale + 0.5f
    }

}