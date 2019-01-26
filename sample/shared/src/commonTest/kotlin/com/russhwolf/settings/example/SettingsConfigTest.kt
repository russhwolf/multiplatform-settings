package com.russhwolf.settings.example

import com.russhwolf.settings.Settings
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
        val settings = object : StubSettings() {
            override fun getString(key: String, defaultValue: String): String = if (key == KEY) VALUE else defaultValue
            override fun hasKey(key: String): Boolean = key == KEY
        }

        val config1 = StringSettingConfig(settings, KEY)
        val nullableConfig1 = NullableStringSettingConfig(settings, KEY)
        assertEquals(VALUE, config1.get())
        assertEquals(VALUE, nullableConfig1.get())

        val config2 = StringSettingConfig(settings, "Other Key")
        val nullableConfig2 = NullableStringSettingConfig(settings, "Other Key")
        assertEquals("", config2.get())
        assertEquals("null", nullableConfig2.get())
    }

    @Test
    fun stringConfig_set() {
        var verifier: Pair<String?, String?> = null to null
        val settings = object : StubSettings() {
            override fun putString(key: String, value: String) {
                verifier = key to value
            }
        }

        val config = StringSettingConfig(settings, KEY)
        config.set(VALUE)
        assertEquals(KEY to VALUE, verifier)
    }
}

open class StubSettings: Settings {
    override fun clear() = STUB
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = STUB
    override fun getDouble(key: String, defaultValue: Double): Double = STUB
    override fun getFloat(key: String, defaultValue: Float): Float = STUB
    override fun getInt(key: String, defaultValue: Int): Int = STUB
    override fun getLong(key: String, defaultValue: Long): Long = STUB
    override fun getString(key: String, defaultValue: String): String = STUB
    override fun hasKey(key: String): Boolean = STUB
    override fun putBoolean(key: String, value: Boolean): Unit = STUB
    override fun putDouble(key: String, value: Double): Unit = STUB
    override fun putFloat(key: String, value: Float): Unit = STUB
    override fun putInt(key: String, value: Int): Unit = STUB
    override fun putLong(key: String, value: Long): Unit = STUB
    override fun putString(key: String, value: String): Unit = STUB
    override fun remove(key: String): Unit = STUB
}

val STUB: Nothing get() = throw NotImplementedError("Stub!")
