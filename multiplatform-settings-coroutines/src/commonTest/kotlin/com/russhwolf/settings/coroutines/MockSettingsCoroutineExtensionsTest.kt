package com.russhwolf.settings.coroutines

import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.ObservableSettings

class MockSettingsCoroutineExtensionsTest : BaseCoroutineExtensionsTest() {
    override val settings: ObservableSettings = MapSettings()
}
