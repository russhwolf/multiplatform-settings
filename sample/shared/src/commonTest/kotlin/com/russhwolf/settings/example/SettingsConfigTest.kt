package com.russhwolf.settings.example

import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import kotlin.test.Test
import kotlin.test.assertEquals

private const val KEY = "Key"
private const val VALUE = "My String Value"

/**
 * Some test cases to demonstrate how [Settings] can be mocked out when writing tests of business logic in a common module
 */
class SettingsConfigTest {
    @Test
    fun stringConfig_get() {
        val settings = MapSettings(KEY to VALUE)

        val config1 = StringSettingConfig(settings, KEY, defaultValue = "")
        val nullableConfig1 = NullableStringSettingConfig(settings, KEY)
        assertEquals(VALUE, config1.get())
        assertEquals(VALUE, nullableConfig1.get())

        val config2 = StringSettingConfig(settings, "Other Key", defaultValue = "")
        val nullableConfig2 = NullableStringSettingConfig(settings, "Other Key")
        assertEquals("", config2.get())
        assertEquals("null", nullableConfig2.get())
    }

    @Test
    fun stringConfig_set() {
        val settings = MapSettings()

        val config = StringSettingConfig(settings, KEY, defaultValue = "")
        config.set(VALUE)
        assertEquals(VALUE, settings[KEY, ""])
    }
}
