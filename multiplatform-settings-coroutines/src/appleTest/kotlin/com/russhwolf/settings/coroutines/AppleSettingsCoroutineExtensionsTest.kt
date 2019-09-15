package com.russhwolf.settings.coroutines

import com.russhwolf.settings.AppleSettings
import com.russhwolf.settings.ExperimentalListener
import com.russhwolf.settings.ObservableSettings
import platform.Foundation.NSUserDefaults

class AppleSettingsCoroutineExtensionsTest : BaseCoroutineExtensionsTest() {
    @OptIn(ExperimentalListener::class)
    override val settings: ObservableSettings = AppleSettings(NSUserDefaults.standardUserDefaults)
}
