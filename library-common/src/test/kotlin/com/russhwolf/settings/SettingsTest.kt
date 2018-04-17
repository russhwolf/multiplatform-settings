package com.russhwolf.settings

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
        assertEquals(0, settings.getInt("a", 0))
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
}

expect fun configureTestSettings(): Settings
