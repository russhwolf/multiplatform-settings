package com.russhwolf.settings.coroutines

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import platform.Foundation.NSUserDefaults

class NSUserDefaultsSettingsCoroutineExtensionsTest : BaseCoroutineExtensionsTest() {
    @OptIn(ExperimentalSettingsApi::class)
    override val settings: ObservableSettings = NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
}
