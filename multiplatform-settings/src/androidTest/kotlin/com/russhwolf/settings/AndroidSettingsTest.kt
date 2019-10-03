package com.russhwolf.settings

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import kotlin.test.Test
import kotlin.test.assertEquals

private val context: Context = ApplicationProvider.getApplicationContext()
private val factory = AndroidSettings.Factory(context)

@RunWith(AndroidJUnit4::class)
class AndroidSettingsTest : BaseSettingsTest(factory) {

    @Test
    fun constructor_sharedPreferences() {
        val preferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val settings = AndroidSettings(preferences)

        preferences.edit().putInt("a", 3).apply()
        assertEquals(3, settings["a", 0])
    }

    @Test
    fun factory_name() {
        val preferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val settings = factory.create("Settings")

        preferences.edit().putInt("a", 3).apply()
        assertEquals(3, settings["a", 0])
    }

    @Test
    fun factory_noName() {
        val preferences = context.getSharedPreferences("com.russhwolf.settings.test_preferences", Context.MODE_PRIVATE)
        val settings = factory.create()

        preferences.edit().putInt("a", 3).apply()
        assertEquals(3, settings["a", 0])
    }
}
