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

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

abstract class NoArgTest {
    // clear() inside the lazy block in case subclasses need to do their setup first
    private val settings by lazy {
        @OptIn(ExperimentalSettingsImplementation::class) // IDE doesn't know this is needed for mingw
        Settings().also { it.clear() }
    }

    @AfterTest
    fun tearDown() {
        settings.clear()
    }

    @Test
    fun setValue() {
        settings.putString("key", "value")
        assertEquals("value", getString("key", ""))
    }

    @Test
    fun getValue() {
        settings // lazy-load so we clear() before setString()
        setString("key", "value")
        assertEquals("value", settings.getStringOrNull("key"))
    }

    // Subclasses should implement these methods to directly interact with delegates so test cases will verify that data
    //  is saved in the expected location.
    abstract fun getString(key: String, defaultValue: String): String
    abstract fun setString(key: String, value: String)
}
