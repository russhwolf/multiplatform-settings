package com.russhwolf.settings

import kotlin.test.Test

class SettingsTest : BaseSettingsTest(MockSettings.Factory()) {
    @Test
    fun enable() = Unit // Hack to get the base class tests running on Android
}
