package com.russhwolf.settings.coroutines

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.MockSettings
import com.russhwolf.settings.ObservableSettings

class MockSettingsCoroutineExtensionsTest : BaseCoroutineExtensionsTest() {
    @OptIn(ExperimentalSettingsApi::class)
    override val settings: ObservableSettings = MockSettings()
}
