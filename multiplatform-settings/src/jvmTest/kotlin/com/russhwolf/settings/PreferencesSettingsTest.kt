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

import java.util.prefs.Preferences
import kotlin.test.Test
import kotlin.test.assertEquals

private val preferences = Preferences.userRoot()
private val factory = PreferencesSettings.Factory(preferences)

class PreferencesSettingsTest : BaseSettingsTest(
    platformFactory = factory,
    syncListeners = preferences::syncListeners
) {
    @Test
    fun constructor_preferences() {
        val preferences = Preferences.userRoot().node("Settings")
        val settings = PreferencesSettings(preferences)

        preferences.putInt("a", 3)
        assertEquals(3, settings["a", 0])
    }


    @Test
    fun factory_name() {
        val preferences = Preferences.userRoot().node("Settings")
        val settings = factory.create("Settings")

        preferences.putInt("a", 3)
        assertEquals(3, settings["a", 0])
    }

    @Test
    fun factory_noName() {
        val settings = factory.create()

        preferences.putInt("a", 3)
        assertEquals(3, settings["a", 0])
    }
}
