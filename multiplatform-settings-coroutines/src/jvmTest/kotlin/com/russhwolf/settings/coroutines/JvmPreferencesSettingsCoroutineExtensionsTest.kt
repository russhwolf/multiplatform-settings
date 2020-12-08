package com.russhwolf.settings.coroutines

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.JvmPreferencesSettings
import com.russhwolf.settings.ObservableSettings
import java.util.prefs.Preferences

class JvmPreferencesSettingsCoroutineExtensionsTest : BaseCoroutineExtensionsTest() {
    @OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
    override val settings: ObservableSettings = JvmPreferencesSettings(Preferences.userRoot())
}
