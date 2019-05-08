package com.russhwolf.settings

import kotlin.test.Test
import kotlin.test.assertEquals

@UseExperimental(ExperimentalListener::class)
class MockSettingsTest {

    @Test
    fun mapConstructor() {
        val delegate = mutableMapOf<String, Any>("a" to 1, "b" to "value", "c" to false)

        val settings1 = MockSettings(delegate)
        assertEquals(1, settings1["a", 0])
        assertEquals("value", settings1["b", ""])
        assertEquals(false, settings1["c", true])

        val settings2 = MockSettings(delegate)
        assertEquals(1, settings2["a", 0])
        assertEquals("value", settings2["b", ""])
        assertEquals(false, settings2["c", true])

        delegate["a"] = 2
        assertEquals(2, settings1["a", 0])
        assertEquals(2, settings2["a", 0])

        settings1["b"] = "other value"
        assertEquals("other value", settings1["b", ""])
        assertEquals("other value", settings2["b", ""])
    }

    @Test
    fun varargConstructor() {
        val settings = MockSettings("a" to 1, "b" to "value", "c" to false)

        assertEquals(1, settings["a", 0])
        assertEquals("value", settings["b", ""])
        assertEquals(false, settings["c", true])
    }

    @Test
    fun factoryConstructor_readFromCache_vararg() {
        val factory = MockSettings.Factory()
        factory.setCacheValues("test", "a" to 1, "b" to "value", "c" to false)

        val settings1 = factory.create("test")
        assertEquals(1, settings1["a", 0])
        assertEquals("value", settings1["b", ""])
        assertEquals(false, settings1["c", true])

        val settings2 = factory.create("test")
        assertEquals(1, settings2["a", 0])
        assertEquals("value", settings2["b", ""])
        assertEquals(false, settings2["c", true])

        val settings3 = factory.create("other key")
        assertEquals(0, settings3["a", 0])
        assertEquals("", settings3["b", ""])
        assertEquals(true, settings3["c", true])

        settings2["a"] = 2
        assertEquals(2, settings1["a", 0])
        assertEquals(2, settings2["a", 0])
        assertEquals(0, settings3["a", 0])

        factory.setCacheValues("test")

        assertEquals(0, settings1["a", 0])
        assertEquals("", settings1["b", ""])
        assertEquals(true, settings1["c", true])

        assertEquals(0, settings2["a", 0])
        assertEquals("", settings2["b", ""])
        assertEquals(true, settings2["c", true])

        assertEquals(0, settings3["a", 0])
        assertEquals("", settings3["b", ""])
        assertEquals(true, settings3["c", true])
    }

    @Test
    fun factoryConstructor_readFromCache_map() {
        val factory = MockSettings.Factory()
        factory.setCacheValues("test", mutableMapOf<String, Any>("a" to 1, "b" to "value", "c" to false))

        val settings1 = factory.create("test")
        assertEquals(1, settings1["a", 0])
        assertEquals("value", settings1["b", ""])
        assertEquals(false, settings1["c", true])

        val settings2 = factory.create("test")
        assertEquals(1, settings2["a", 0])
        assertEquals("value", settings2["b", ""])
        assertEquals(false, settings2["c", true])

        val settings3 = factory.create("other key")
        assertEquals(0, settings3["a", 0])
        assertEquals("", settings3["b", ""])
        assertEquals(true, settings3["c", true])

        settings2["a"] = 2
        assertEquals(2, settings1["a", 0])
        assertEquals(2, settings2["a", 0])
        assertEquals(0, settings3["a", 0])

        factory.setCacheValues("test", emptyMap())

        assertEquals(0, settings1["a", 0])
        assertEquals("", settings1["b", ""])
        assertEquals(true, settings1["c", true])

        assertEquals(0, settings2["a", 0])
        assertEquals("", settings2["b", ""])
        assertEquals(true, settings2["c", true])

        assertEquals(0, settings3["a", 0])
        assertEquals("", settings3["b", ""])
        assertEquals(true, settings3["c", true])
    }

    @Test
    fun listener() {
        val settings = MockSettings("a" to 3, "b" to false)
        val verifierA = ListenerVerifier()
        val verifierB = ListenerVerifier()
        val listenerA = settings.addListener("a", verifierA.listener)
        settings.addListener("b", verifierB.listener)

        verifierA.assertNotInvoked()
        verifierB.assertNotInvoked()

        settings.remove("a")
        verifierA.assertInvoked()
        verifierB.assertNotInvoked()

        settings.hasKey("a")
        verifierA.assertNotInvoked()
        verifierB.assertNotInvoked()

        settings.putInt("a", 1)
        verifierA.assertInvoked()
        verifierB.assertNotInvoked()

        settings.getInt("a")
        verifierA.assertNotInvoked()
        verifierB.assertNotInvoked()

        settings.putLong("a", 2)
        verifierA.assertInvoked()
        verifierB.assertNotInvoked()

        settings.getLong("a")
        verifierA.assertNotInvoked()
        verifierB.assertNotInvoked()

        settings.putString("a", "value")
        verifierA.assertInvoked()
        verifierB.assertNotInvoked()

        settings.getString("a")
        verifierA.assertNotInvoked()
        verifierB.assertNotInvoked()

        settings.putFloat("a", 1.5f)
        verifierA.assertInvoked()
        verifierB.assertNotInvoked()

        settings.getFloat("a")
        verifierA.assertNotInvoked()
        verifierB.assertNotInvoked()

        settings.putDouble("a", 2.5)
        verifierA.assertInvoked()
        verifierB.assertNotInvoked()

        settings.getDouble("a")
        verifierA.assertNotInvoked()
        verifierB.assertNotInvoked()

        settings.putBoolean("a", true)
        verifierA.assertInvoked()
        verifierB.assertNotInvoked()

        settings.getBoolean("a")
        verifierA.assertNotInvoked()
        verifierB.assertNotInvoked()

        settings.clear()
        verifierA.assertInvoked()
        verifierB.assertInvoked()

        settings.removeListener(listenerA)

        settings.putInt("a", 3)
        verifierA.assertNotInvoked()
        verifierB.assertNotInvoked()
    }

}
