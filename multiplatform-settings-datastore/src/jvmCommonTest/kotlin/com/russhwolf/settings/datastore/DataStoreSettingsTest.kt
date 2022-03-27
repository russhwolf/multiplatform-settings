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

@file:OptIn(ExperimentalSettingsApi::class, ExperimentalCoroutinesApi::class, ExperimentalSettingsImplementation::class)

package com.russhwolf.settings.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import com.russhwolf.settings.BaseSettingsTest
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.toBlockingObservableSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import kotlin.test.assertEquals

private val temporaryFolder = TemporaryFolder()

// Shim to work around IDE issues. HMPP doesn't support JVM/Android so we can't see java.io.File here.
internal expect fun TemporaryFolder.createDataStore(fileName: String): DataStore<Preferences>

private val factory = object : Settings.Factory {
    override fun create(name: String?): Settings {
        val dataStore: DataStore<Preferences> = temporaryFolder.createDataStore("$name.preferences_pb")
        val settings = DataStoreSettings(dataStore)
        return settings.toBlockingObservableSettings(CoroutineScope(Dispatchers.Unconfined))
    }
}

class DataStoreSettingsTest : BaseSettingsTest(
    platformFactory = factory,
    allowsDuplicateInstances = false,
    hasListeners = true
) {

    @Rule
    fun temporaryFolder() = temporaryFolder

    @Test
    fun constructor_datastore(): Unit = runBlocking {
        val dataStore = temporaryFolder.createDataStore("settings.preferences_pb")
        val settings = DataStoreSettings(dataStore)

        dataStore.edit { it[intPreferencesKey("a")] = 3 }
        assertEquals(3, settings.getInt("a"))
    }
}
