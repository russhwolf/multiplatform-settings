package com.russhwolf.settings

import platform.Foundation.NSUserDefaults
import kotlin.test.Test
import kotlin.test.assertEquals

private val factory = AppleSettings.Factory()

class AppleSettingsTest : BaseSettingsTest(factory) {
    @Test
    fun constructor_userDefaults() {
        val userDefaults = NSUserDefaults(suiteName = "Settings")
        val settings = AppleSettings(userDefaults)

        userDefaults.setObject("value", forKey = "a")
        assertEquals("value", settings["a", ""])
    }

    @Test
    fun factory_name() {
        val userDefaults = NSUserDefaults(suiteName = "Settings")
        val settings = factory.create("Settings")

        userDefaults.setObject("value", forKey = "a")
        assertEquals("value", settings["a", ""])
    }

    @Test
    fun factory_noName() {
        val userDefaults = NSUserDefaults.standardUserDefaults
        val settings = factory.create()

        userDefaults.setObject("value", forKey = "a")
        assertEquals("value", settings["a", ""])
    }
}
