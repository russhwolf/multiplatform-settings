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

import android.annotation.SuppressLint
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import kotlin.test.Test
import kotlin.test.assertEquals

private val context: Context = ApplicationProvider.getApplicationContext()
private val factory = SharedPreferencesSettings.Factory(context)

@RunWith(AndroidJUnit4::class)
@Config(sdk = [30])
class SharedPreferencesSettingsTest : BaseSettingsTest(factory) {

    @Test
    fun constructor_sharedPreferences() {
        val preferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val settings = SharedPreferencesSettings(preferences)

        preferences.edit().putInt("a", 3).apply()
        assertEquals(3, settings["a", 0])
    }

    @Test
    fun constructor_commit() {
        val preferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val settings = SharedPreferencesSettings(preferences, commit = true)

        settings.putInt("a", 3)
        assertEquals(3, preferences.getInt("a", -1))
    }

    @Test
    fun constructor_noCommit() {
        val preferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val settings = SharedPreferencesSettings(preferences, commit = false)

        settings.putInt("a", 3)
        assertEquals(3, preferences.getInt("a", -1))
    }

    @Test
    fun factory_name() {
        val preferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val settings = factory.create("Settings")

        preferences.edit().putInt("a", 3).apply()
        assertEquals(3, settings["a", 0])
    }

    @Test
    fun factory_noName() {
        val preferences = context.getSharedPreferences("com.russhwolf.settings.test_preferences", Context.MODE_PRIVATE)
        val settings = factory.create()

        preferences.edit().putInt("a", 3).apply()
        assertEquals(3, settings["a", 0])
    }

    @SuppressLint("ApplySharedPref")
    @Test
    fun issue_108() {
        // In Android 11 (SDK level 30), we will get OnSharedPreferenceChangeListener callbacks with a null updatedKey
        // if the user calls clear() on the SharedPreferences.Editor. On Multiplatform Settings versions 0.8.1 and
        // earlier, we assumed updatedKey was nonnull so this would crash.

        val preferences = context.getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val settings = SharedPreferencesSettings(preferences)

        settings.addIntListener("key", 0) { }
        preferences.edit().clear().commit() // This will call OnSharedPreferenceChangeListener with updatedKey = null
    }
}
