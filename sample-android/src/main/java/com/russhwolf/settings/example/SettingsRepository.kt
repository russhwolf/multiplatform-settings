package com.russhwolf.settings.example

import android.content.Context
import android.preference.PreferenceManager
import com.russhwolf.settings.Settings

actual class SettingsFactory(private val context: Context) {
    actual fun create(): Settings = Settings(PreferenceManager.getDefaultSharedPreferences(context))
}
