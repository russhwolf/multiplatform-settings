package com.russhwolf.settings.coroutines

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import platform.Foundation.NSUserDefaults

class NSUserDefaultsSettingsCoroutineExtensionsTest : BaseCoroutineExtensionsTest() {
    override val settings: ObservableSettings = NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
}
