package com.russhwolf.settings

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SettingsTest {
    private lateinit var settings: Settings

    @BeforeTest
    fun setup() {
        settings = configureTestSettings()
    }

    @Test
    fun clear() {
        settings.putInt("a", 5)
        settings.clear()
        assertEquals(-1, settings.getInt("a", -1))
    }

    @Test
    fun remove() {
        settings.putInt("a", 3)
        settings.remove("a")
        assertEquals(-1, settings.getInt("a", -1))
    }

    @Test
    fun contains() {
        assertFalse(settings.contains("a"))
        settings.putString("a", "value")
        assertTrue(settings.contains("a"))
    }

    @Test
    fun intDelegate() {
        var a by settings.int("Int", 5)
        assertEquals(5, a)
        a = 2
        assertEquals(2, a)
        a = 0
        assertEquals(0, a)
    }

    @Test
    fun longDelegate() {
        var a by settings.long("Long", 5)
        assertEquals(5, a)
        a = 2
        assertEquals(2, a)
        a = 0
        assertEquals(0, a)
    }

    @Test
    fun stringDelegate() {
        var a by settings.string("String", "default")
        assertEquals("default", a)
        a = "value"
        assertEquals("value", a)
    }

    @Test
    fun floatDelegate() {
        var a by settings.float("Float", 5f)
        assertEquals(5f, a)
        a = 2f
        assertEquals(2f, a)
        a = 0f
        assertEquals(0f, a)
    }

    @Test
    fun doubleDelegate() {
        var a by settings.double("Double", 5.0)
        assertEquals(5.0, a)
        a = 2.0
        assertEquals(2.0, a)
        a = 0.0
        assertEquals(0.0, a)
    }

    @Test
    fun booleanDelegate() {
        var a by settings.boolean("Boolean", true)
        assertEquals(true, a)
        a = false
        assertEquals(false, a)
    }

    @Test
    fun nullableIntDelegate() {
        var a by settings.nullableInt("Nullable Int")
        assertEquals(null, a)
        a = 2
        assertEquals(2, a)
        a = 0
        assertEquals(0, a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun nullableLongDelegate() {
        var a by settings.nullableLong("Nullable Long")
        assertEquals(null, a)
        a = 2
        assertEquals(2, a)
        a = 0
        assertEquals(0, a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun nullableStringDelegate() {
        var a by settings.nullableString("Nullable String")
        assertEquals(null, a)
        a = "value"
        assertEquals("value", a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun nullableFloatDelegate() {
        var a by settings.nullableFloat("Nullable Float")
        assertEquals(null, a)
        a = 2f
        assertEquals(2f, a)
        a = 0f
        assertEquals(0f, a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun nullableDoubleDelegate() {
        var a by settings.nullableDouble("Nullable Double")
        assertEquals(null, a)
        a = 2.0
        assertEquals(2.0, a)
        a = 0.0
        assertEquals(0.0, a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun nullableBooleanDelegate() {
        var a by settings.nullableBoolean("Nullable Boolean")
        assertEquals(null, a)
        a = true
        assertEquals(true, a)
        a = null
        assertEquals(null, a)
    }
}

expect fun configureTestSettings(): Settings
