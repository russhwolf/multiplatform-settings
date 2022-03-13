package com.russhwolf.settings.coroutines

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import java.util.prefs.Preferences

class PreferencesSettingsCoroutineExtensionsTest : BaseCoroutineExtensionsTest() {
    @OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)
    override val settings: ObservableSettings = PreferencesSettings(Preferences.userRoot())
}
