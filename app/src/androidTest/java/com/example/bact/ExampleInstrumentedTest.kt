package com.example.bact

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.bact.util.DisplayUtil

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.bact", appContext.packageName)
    }

    @Test
    fun dpPxTransform_isCorrect() {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        if (appContext != null) {
            assertEquals(
                100.0f,
                DisplayUtil.px2dp(
                    appContext,
                    DisplayUtil.dp2px(appContext, 100.0f)
                )
            )
        }
    }
}