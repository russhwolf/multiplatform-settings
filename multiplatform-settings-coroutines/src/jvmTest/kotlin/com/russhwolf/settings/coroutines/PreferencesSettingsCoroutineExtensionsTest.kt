package com.russhwolf.settings.coroutines

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.syncListeners
import java.util.prefs.Preferences

private val preferences = Preferences.userRoot()

class PreferencesSettingsCoroutineExtensionsTest : BaseCoroutineExtensionsTest(
    syncListeners = preferences::syncListeners
) {
    override val settings: ObservableSettings = PreferencesSettings(preferences)
}
