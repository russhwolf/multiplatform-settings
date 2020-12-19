package com.russhwolf.settings.coroutines

import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import platform.Foundation.NSUserDefaults

class AppleSettingsCoroutineExtensionsTest : BaseCoroutineExtensionsTest() {
    @OptIn(ExperimentalSettingsApi::class)
    override val settings: ObservableSettings = AppleSettings(NSUserDefaults.standardUserDefaults)
}
