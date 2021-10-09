package com.example.bact.util

import android.content.Context
import androidx.annotation.ColorInt
import androidx.core.graphics.ColorUtils

object DisplayUtil {

    fun px2dp(context: Context, px: Float): Float {
        val scale = context.resources.displayMetrics.density
        return px / scale + 0.5f
    }

    fun dp2px(context: Context, dp: Float): Float {
        val scale = context.resources.displayMetrics.density
        return dp * scale + 0.5f
    }

    fun isLightColor(@ColorInt color: Int): Boolean {
        return ColorUtils.calculateLuminance(color) >= 0.5
    }

}