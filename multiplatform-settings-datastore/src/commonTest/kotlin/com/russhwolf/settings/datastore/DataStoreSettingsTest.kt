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

@file:OptIn(ExperimentalSettingsApi::class, ExperimentalSettingsImplementation::class)

package com.russhwolf.settings.datastore

import androidx.datastore.core.okio.OkioSerializer
import androidx.datastore.core.okio.OkioStorage
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
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
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import okio.FileSystem
import okio.Path.Companion.toPath
import okio.fakefilesystem.FakeFileSystem
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

expect val preferencesSerializer: OkioSerializer<Preferences>

private var scope: CoroutineScope = CoroutineScope(SupervisorJob())

private val fakeFileSystem = FakeFileSystem()

private val factory = object : Settings.Factory {
    override fun create(name: String?): Settings {
        val storage = OkioStorage(fakeFileSystem, preferencesSerializer) {
            FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "${name ?: "settings"}.preferences_pb".toPath()
        }
        val dataStore = PreferenceDataStoreFactory.create(storage, scope = scope)
        val settings = DataStoreSettings(dataStore)
        return settings.toBlockingObservableSettings(CoroutineScope(Dispatchers.Unconfined))
    }
}

class DataStoreSettingsTest : BaseSettingsTest(
    platformFactory = factory,
    allowsDuplicateInstances = false,
    hasListeners = true
) {
    @AfterTest
    fun tearDown() {
        scope.cancel()
        scope = CoroutineScope(SupervisorJob())
        fakeFileSystem.checkNoOpenFiles()
    }

    @Test
    fun constructor_datastore(): Unit = runBlocking {
        val storage = OkioStorage(fakeFileSystem, preferencesSerializer) {
            FileSystem.SYSTEM_TEMPORARY_DIRECTORY / "test.preferences_pb".toPath()
        }
        val dataStore = PreferenceDataStoreFactory.create(storage, scope = scope)

        val settings = DataStoreSettings(dataStore)

        dataStore.edit { it[intPreferencesKey("a")] = 3 }
        assertEquals(3, settings.getIntOrNull("a"))
    }
}
