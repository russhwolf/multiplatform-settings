package com.russhwolf.settings.coroutines

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import java.util.prefs.Preferences

class PreferencesSettingsCoroutineExtensionsTest : BaseCoroutineExtensionsTest() {
    override val settings: ObservableSettings = PreferencesSettings(Preferences.userRoot())
}
