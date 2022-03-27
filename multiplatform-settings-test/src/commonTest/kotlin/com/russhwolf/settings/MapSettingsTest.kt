/*
 * Copyright 2020 Russell Wolf
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

import kotlin.test.Test
import kotlin.test.assertEquals

class MapSettingsTest : BaseSettingsTest(MapSettings.Factory()) {

    @Test
    fun mapConstructor() {
        val delegate = mutableMapOf<String, Any>("a" to 1, "b" to "value", "c" to false)

        val settings1 = MapSettings(delegate)
        assertEquals(1, settings1["a", 0])
        assertEquals("value", settings1["b", ""])
        assertEquals(false, settings1["c", true])

        val settings2 = MapSettings(delegate)
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
        val settings = MapSettings("a" to 1, "b" to "value", "c" to false)

        assertEquals(1, settings["a", 0])
        assertEquals("value", settings["b", ""])
        assertEquals(false, settings["c", true])
    }

    @Test
    fun factoryConstructor_readFromCache_vararg() {
        val factory = MapSettings.Factory()
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
        val factory = MapSettings.Factory()
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
}
