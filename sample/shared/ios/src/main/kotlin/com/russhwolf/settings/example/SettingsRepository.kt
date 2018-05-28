package com.russhwolf.settings.example

import com.russhwolf.settings.Settings

actual class SettingsFactory() {
    actual fun create(): Settings = Settings()
}
