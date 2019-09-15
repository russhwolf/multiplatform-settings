package com.russhwolf.settings.coroutines

import com.russhwolf.settings.ExperimentalJvm
import com.russhwolf.settings.ExperimentalListener
import com.russhwolf.settings.JvmPreferencesSettings
import com.russhwolf.settings.ObservableSettings
import java.util.prefs.Preferences

class JvmPreferencesSettingsCoroutineExtensionsTest : CoroutineExtensionsTest() {
    @OptIn(ExperimentalListener::class, ExperimentalJvm::class)
    override val settings: ObservableSettings = JvmPreferencesSettings(Preferences.userRoot())
}
