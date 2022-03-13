/*
 * Copyright 2019 Russell Wolf
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

import platform.Foundation.NSUserDefaults
import kotlin.test.Test
import kotlin.test.assertEquals

private val factory = NSUserDefaultsSettings.Factory()

class NSUserDefaultsSettingsTest : BaseSettingsTest(factory) {
    @Test
    fun constructor_userDefaults() {
        val userDefaults = NSUserDefaults(suiteName = "Settings")
        val settings = NSUserDefaultsSettings(userDefaults)

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
