/*
 * Copyright 2018 Russell Wolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.russhwolf.settings

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

expect val platformFactory: Settings.Factory

@RunWith(AndroidJUnit4::class)
class SettingsTest {
    private lateinit var settings: Settings

    private val settingsFactory = object : Settings.Factory {
        override fun create(name: String?): Settings = platformFactory.create(name).also { it.clear() }
    }

    @BeforeTest
    fun setup() {
        settings = settingsFactory.create()
    }

    @Test
    fun clear() {
        settings.putInt("a", 4)
        settings.putString("b", "value")
        settings.clear()
        assertEquals(0, settings.getInt("a"))
        assertEquals("", settings.getString("b"))
    }

    @Test
    fun remove() {
        settings.putInt("a", 3)
        settings.putString("b", "value")
        settings.remove("a")
        assertEquals(0, settings.getInt("a"))
        assertEquals("value", settings.getString("b"))

        settings["a"] = 3
        settings["b"] = "value"
        settings -= "a"
        assertEquals(-1, settings["a", -1])
        assertEquals("value", settings["b", "default"])
    }

    @Test
    fun contains() {
        assertFalse(settings.hasKey("a"))
        assertFalse("a" in settings)
        settings.putString("a", "value")
        assertTrue(settings.hasKey("a"))
        assertTrue("a" in settings)
    }

    @Test
    fun intBasic() {
        assertEquals(0, settings.getInt("a"))
        settings.putInt("a", 2)
        assertEquals(2, settings.getInt("a"))
        settings.putInt("a", Int.MIN_VALUE)
        assertEquals(Int.MIN_VALUE, settings.getInt("a"))
        settings.putInt("a", Int.MAX_VALUE)
        assertEquals(Int.MAX_VALUE, settings.getInt("a"))

        assertEquals(5, settings.getInt("b", 5))
    }

    @Test
    fun intOperator() {
        assertEquals(5, settings["a", 5])
        settings["a"] = 2
        assertEquals(2, settings["a", 5])
        settings["a"] = 0
        assertEquals(0, settings["a", 5])
    }

    @Test
    fun intDelegate() {
        var a by settings.int("a", 5)
        assertEquals(5, a)
        a = 2
        assertEquals(2, a)
        a = 0
        assertEquals(0, a)

        val b by settings.int("b")
        assertEquals(0, b)
    }

    @Test
    fun intNullableDelegate() {
        var a by settings.nullableInt("a")
        assertEquals(null, a)
        a = 2
        assertEquals(2, a)
        a = 0
        assertEquals(0, a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun intDelegateDefaultKey() {
        var a by settings.int(defaultValue = 5)
        assertEquals(5, a)
        a = 2
        assertEquals(2, a)
        a = 0
        assertEquals(0, a)

        val b by settings.int()
        assertEquals(0, b)
    }

    @Test
    fun intNullableDelegateDefaultKey() {
        var a by settings.nullableInt()
        assertEquals(null, a)
        a = 2
        assertEquals(2, a)
        a = 0
        assertEquals(0, a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun longBasic() {
        assertEquals(0, settings.getLong("a"))
        settings.putLong("a", 2)
        assertEquals(2, settings.getLong("a"))
        settings.putLong("a", Long.MIN_VALUE)
        assertEquals(Long.MIN_VALUE, settings.getLong("a"))
        settings.putLong("a", Long.MAX_VALUE)
        assertEquals(Long.MAX_VALUE, settings.getLong("a"))

        assertEquals(5, settings.getLong("b", 5))
    }

    @Test
    fun longOperator() {
        assertEquals(5, settings["a", 5])
        settings["a"] = 2
        assertEquals(2, settings["a", 5])
        settings["a"] = 0
        assertEquals(0, settings["a", 5])
    }

    @Test
    fun longDelegate() {
        var a by settings.long("a", 5)
        assertEquals(5, a)
        a = 2
        assertEquals(2, a)
        a = 0
        assertEquals(0, a)

        val b by settings.long("b")
        assertEquals(0, b)
    }

    @Test
    fun longNullableDelegate() {
        var a by settings.nullableLong("a")
        assertEquals(null, a)
        a = 2
        assertEquals(2, a)
        a = 0
        assertEquals(0, a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun longDelegateDefaultKey() {
        var a by settings.long(defaultValue = 5)
        assertEquals(5, a)
        a = 2
        assertEquals(2, a)
        a = 0
        assertEquals(0, a)

        val b by settings.long()
        assertEquals(0, b)
    }

    @Test
    fun longNullableDelegateDefaultKey() {
        var a by settings.nullableLong()
        assertEquals(null, a)
        a = 2
        assertEquals(2, a)
        a = 0
        assertEquals(0, a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun stringBasic() {
        assertEquals("", settings.getString("a"))
        settings.putString("a", "value")
        assertEquals("value", settings.getString("a"))

        assertEquals("default", settings.getString("b", "default"))
    }

    @Test
    fun stringOperator() {
        assertEquals("default", settings["a", "default"])
        settings["a"] = "value"
        assertEquals("value", settings["a", "default"])
        settings["a"] = ""
        assertEquals("", settings["a", "default"])
    }

    @Test
    fun stringDelegate() {
        var a by settings.string("a", "default")
        assertEquals("default", a)
        a = "value"
        assertEquals("value", a)
        a = ""
        assertEquals("", a)

        val b by settings.string("b")
        assertEquals("", b)
    }

    @Test
    fun stringNullableDelegate() {
        var a by settings.nullableString("a")
        assertEquals(null, a)
        a = "value"
        assertEquals("value", a)
        a = ""
        assertEquals("", a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun stringDelegateDefaultKey() {
        var a by settings.string("a", "default")
        assertEquals("default", a)
        a = "value"
        assertEquals("value", a)
        a = ""
        assertEquals("", a)

        val b by settings.string("b")
        assertEquals("", b)
    }

    @Test
    fun stringNullableDelegateDefaultKey() {
        var a by settings.nullableString("a")
        assertEquals(null, a)
        a = "value"
        assertEquals("value", a)
        a = ""
        assertEquals("", a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun floatBasic() {
        assertEquals(0f, settings.getFloat("a"))
        settings.putFloat("a", 2f)
        assertEquals(2f, settings.getFloat("a"))
        settings.putFloat("a", Float.MIN_VALUE)
        assertEquals(Float.MIN_VALUE, settings.getFloat("a"))
        settings.putFloat("a", Float.MAX_VALUE)
        assertEquals(Float.MAX_VALUE, settings.getFloat("a"))
        settings.putFloat("a", Float.NEGATIVE_INFINITY)
        assertEquals(Float.NEGATIVE_INFINITY, settings.getFloat("a"))
        settings.putFloat("a", Float.POSITIVE_INFINITY)
        assertEquals(Float.POSITIVE_INFINITY, settings.getFloat("a"))
        settings.putFloat("a", Float.NaN)
        assertEquals(Float.NaN, settings.getFloat("a"))

        assertEquals(5f, settings.getFloat("b", 5f))
    }

    @Test
    fun floatOperator() {
        assertEquals(5f, settings["a", 5f])
        settings["a"] = 2f
        assertEquals(2f, settings["a", 5f])
        settings["a"] = 0f
        assertEquals(0f, settings["a", 5f])
    }

    @Test
    fun floatDelegate() {
        var a by settings.float("a", 5f)
        assertEquals(5f, a)
        a = 2f
        assertEquals(2f, a)
        a = 0f
        assertEquals(0f, a)

        val b by settings.float("b")
        assertEquals(0f, b)
    }

    @Test
    fun floatNullableDelegate() {
        var a by settings.nullableFloat("a")
        assertEquals(null, a)
        a = 2f
        assertEquals(2f, a)
        a = 0f
        assertEquals(0f, a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun floatDelegateDefaultKey() {
        var a by settings.float(defaultValue = 5f)
        assertEquals(5f, a)
        a = 2f
        assertEquals(2f, a)
        a = 0f
        assertEquals(0f, a)

        val b by settings.float()
        assertEquals(0f, b)
    }

    @Test
    fun floatNullableDelegateDefaultKey() {
        var a by settings.nullableFloat()
        assertEquals(null, a)
        a = 2f
        assertEquals(2f, a)
        a = 0f
        assertEquals(0f, a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun doubleBasic() {
        assertEquals(0.0, settings.getDouble("a"))
        settings.putDouble("a", 2.0)
        assertEquals(2.0, settings.getDouble("a"))
        settings.putDouble("a", Double.MIN_VALUE)
        assertEquals(Double.MIN_VALUE, settings.getDouble("a"))
        settings.putDouble("a", Double.MAX_VALUE)
        assertEquals(Double.MAX_VALUE, settings.getDouble("a"))
        settings.putDouble("a", Double.NEGATIVE_INFINITY)
        assertEquals(Double.NEGATIVE_INFINITY, settings.getDouble("a"))
        settings.putDouble("a", Double.POSITIVE_INFINITY)
        assertEquals(Double.POSITIVE_INFINITY, settings.getDouble("a"))
        settings.putDouble("a", Double.NaN)
        assertEquals(Double.NaN, settings.getDouble("a"))

        assertEquals(5.0, settings.getDouble("b", 5.0))
    }

    @Test
    fun doubleOperator() {
        assertEquals(5.0, settings["a", 5.0])
        settings["a"] = 2.0
        assertEquals(2.0, settings["a", 5.0])
        settings["a"] = 0.0
        assertEquals(0.0, settings["a", 5.0])
    }

    @Test
    fun doubleDelegate() {
        var a by settings.double("a", 5.0)
        assertEquals(5.0, a)
        a = 2.0
        assertEquals(2.0, a)
        a = 0.0
        assertEquals(0.0, a)

        val b by settings.double("b")
        assertEquals(0.0, b)
    }

    @Test
    fun doubleNullableDelegate() {
        var a by settings.nullableDouble("a")
        assertEquals(null, a)
        a = 2.0
        assertEquals(2.0, a)
        a = 0.0
        assertEquals(0.0, a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun doubleDelegateDefaultKey() {
        var a by settings.double(defaultValue = 5.0)
        assertEquals(5.0, a)
        a = 2.0
        assertEquals(2.0, a)
        a = 0.0
        assertEquals(0.0, a)

        val b by settings.double()
        assertEquals(0.0, b)
    }

    @Test
    fun doubleNullableDelegateDefaultKey() {
        var a by settings.nullableDouble()
        assertEquals(null, a)
        a = 2.0
        assertEquals(2.0, a)
        a = 0.0
        assertEquals(0.0, a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun booleanBasic() {
        assertEquals(false, settings.getBoolean("a"))
        settings.putBoolean("a", true)
        assertEquals(true, settings.getBoolean("a"))

        assertEquals(true, settings.getBoolean("b", true))
    }

    @Test
    fun booleanOperator() {
        assertEquals(true, settings["a", true])
        settings["a"] = false
        assertEquals(false, settings["a", true])
    }

    @Test
    fun booleanDelegate() {
        var a by settings.boolean("a", true)
        assertEquals(true, a)
        a = false
        assertEquals(false, a)

        val b by settings.boolean("b")
        assertEquals(false, b)
    }

    @Test
    fun booleanNullableDelegate() {
        var a by settings.nullableBoolean("a")
        assertEquals(null, a)
        a = false
        assertEquals(false, a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun booleanDelegateDefaultKey() {
        var a by settings.boolean(defaultValue = true)
        assertEquals(true, a)
        a = false
        assertEquals(false, a)

        val b by settings.boolean()
        assertEquals(false, b)
    }

    @Test
    fun booleanNullableDelegateDefaultKey() {
        var a by settings.nullableBoolean()
        assertEquals(null, a)
        a = false
        assertEquals(false, a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun delegateReuseTest() {
        // Is this a reasonable use-case? Might as well protect it for now. That way if we break it later it'll be on
        // purpose instead of by accident
        val delegate = settings.int()
        var a by delegate
        var b by delegate

        a = 1
        b = 2
        assertEquals(1, a)
        assertEquals(2, b)
    }

    @Test
    @JsIgnore // Ignored in JS because name isn't implemented
    @JvmIgnore // Ignored in JVM because name isn't implemented
    fun multipleDifferentInstances() {
        val settingsA = settingsFactory.create("com.russhwolf.multiplatform-settings.test.A")
        val settingsB = settingsFactory.create("com.russhwolf.multiplatform-settings.test.B")
        settingsA["a"] = 1
        assertEquals(1, settingsA["a", -1])
        assertEquals(-1, settingsB["a", -1])
    }

    @Test
    fun multipleSameInstances() {
        val settingsA = settingsFactory.create("com.russhwolf.multiplatform-settings.test.A")
        val settingsB = settingsFactory.create("com.russhwolf.multiplatform-settings.test.A")
        settingsA["a"] = 1
        assertEquals(1, settingsA["a", -1])
        assertEquals(1, settingsB["a", -1])
    }

    @Test
    @JsIgnore // Ignored in JS because listener isn't implemented
    @JvmIgnore // Ignored in JVM because listener isn't implemented
    @UseExperimental(ExperimentalListener::class)
    fun listener() {
        val settings = settings as? ListenableSettings
            ?: throw IllegalStateException("Must implement ListenableSettings or ignore this test")

        var invocationCount = 0
        val callback = { invocationCount += 1 }

        // No invocation for call before listener was set
        settings["a"] = 2
        val listener = settings.addListener("a", callback)
        assertEquals(0, invocationCount)

        // No invocation on set to existing value
        settings["a"] = 2
        assertEquals(0, invocationCount)

        // New invocation on value change
        settings["a"] = 1
        assertEquals(1, invocationCount)

        // No invocation if value unchanged
        settings["a"] = 1
        assertEquals(1, invocationCount)

        // New invocation on remove
        settings -= "a"
        assertEquals(2, invocationCount)

        // New invocation on re-add with same value
        settings["a"] = 1
        assertEquals(3, invocationCount)

        // No invocation on other key change
        settings["b"] = 1
        assertEquals(3, invocationCount)

        // New invocation on clear
        settings.clear()
        assertEquals(4, invocationCount)

        // Second listener at the same key also gets called
        var invokationCount2 = 0
        val callback2 = { invokationCount2 += 1 }
        settings.addListener("a", callback2)
        settings["a"] = 3
        assertEquals(5, invocationCount)
        assertEquals(1, invokationCount2)

        // No invocation on listener which is removed
        settings.removeListener(listener)
        settings["a"] = 2
        assertEquals(5, invocationCount)
        assertEquals(2, invokationCount2)
    }

}
