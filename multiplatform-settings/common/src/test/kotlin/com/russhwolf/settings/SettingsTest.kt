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

// TODO We shouldn't need this, but common module test sources aren't making it to the IDE test scope at the moment
@file:Suppress("KDocMissingDocumentation")

package com.russhwolf.settings

import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

expect val settingsFactory: Settings.Factory

@RunWith(RobolectricTestRunner::class)
class SettingsTest {
    private lateinit var settings: Settings

    @BeforeTest
    fun setup() {
        settings = settingsFactory.create()
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
        assertFalse(settings.hasKey("a"))
        settings.putString("a", "value")
        assertTrue(settings.hasKey("a"))
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
        assertEquals(2.asNullableType(), a)
        a = 0
        assertEquals(0.asNullableType(), a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun nullableLongDelegate() {
        var a by settings.nullableLong("Nullable Long")
        assertEquals(null, a)
        a = 2
        assertEquals(2L.asNullableType(), a)
        a = 0
        assertEquals(0L.asNullableType(), a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun nullableStringDelegate() {
        var a by settings.nullableString("Nullable String")
        assertEquals(null, a)
        a = "value"
        assertEquals("value".asNullableType(), a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun nullableFloatDelegate() {
        var a by settings.nullableFloat("Nullable Float")
        assertEquals(null, a)
        a = 2f
        assertEquals(2f.asNullableType(), a)
        a = 0f
        assertEquals(0f.asNullableType(), a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun nullableDoubleDelegate() {
        var a by settings.nullableDouble("Nullable Double")
        assertEquals(null, a)
        a = 2.0
        assertEquals(2.0.asNullableType(), a)
        a = 0.0
        assertEquals(0.0.asNullableType(), a)
        a = null
        assertEquals(null, a)
    }

    @Test
    fun nullableBooleanDelegate() {
        var a by settings.nullableBoolean("Nullable Boolean")
        assertEquals(null, a)
        a = true
        assertEquals(true.asNullableType(), a)
        a = null
        assertEquals(null, a)
    }

    @Test
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
    fun listener() {
        var invokationCount = 0
        val listener = { invokationCount += 1 }

        // No invocation for call before listener was set
        settings["a"] = 1
        settings.addListener("a", listener)
        assertEquals(0, invokationCount)

        // New invocation on set
        settings["a"] = 0
        assertEquals(1, invokationCount)

        // New invocation on value change
        settings["a"] = 1
        assertEquals(2, invokationCount)

        // No invocation if value unchanged
        settings["a"] = 1
        assertEquals(2, invokationCount)

        // New invocation on remove
        settings -= "a"
        assertEquals(3, invokationCount)

        // New invocation on re-add
        settings["a"] = 0
        assertEquals(4, invokationCount)

        // No invocation on other key change
        settings["b"] = 1
        assertEquals(4, invokationCount)

        // Second listener at the same key clobbers first one
        settings.addListener("a") { /* Do nothing */ }
        settings["a"] = 3
        assertEquals(4, invokationCount)

        // Set correct listener and verify once more
        settings.addListener("a", listener)
        settings["a"] = 1
        assertEquals(5, invokationCount)

        // No invocation after listener is removed
        settings.removeListener("a")
        settings["a"] = 2
        assertEquals(5, invokationCount)
    }

    @Test
    fun unregisterInvalidListener() {
        settings.removeListener("a")
        // Don't crash
    }

}

/**
 * Cast a value of type `T` to `T?`
 *
 * Helper function to avoid deprecated smart-casts when asserting non-null values of nullable delegates
 */
private fun <T : Any> T.asNullableType() = this as T?
