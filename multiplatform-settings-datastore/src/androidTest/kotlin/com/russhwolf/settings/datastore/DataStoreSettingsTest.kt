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

@file:OptIn(ExperimentalListener::class, ExperimentalCoroutinesApi::class)

package com.russhwolf.settings.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import com.russhwolf.settings.BaseSettingsTest
import com.russhwolf.settings.ExperimentalListener
import com.russhwolf.settings.Settings
import com.russhwolf.settings.coroutines.toBlockingObservableSettings
import com.russhwolf.settings.coroutines.toBlockingSettings
import com.russhwolf.settings.coroutines.toSuspendSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File


private val temporaryFolder = TemporaryFolder()
private val testScope = TestCoroutineScope()

private val suspendFactory = object : Settings.Factory {
    override fun create(name: String?): Settings {
        val dataStore: DataStore<Preferences> =
            PreferenceDataStoreFactory.create { File(temporaryFolder.root, "$name.preferences_pb") }
        return DataStoreSuspendSettings(dataStore).toBlockingSettings()
    }
}
private val flowFactory = object : Settings.Factory {
    override fun create(name: String?): Settings {
        val dataStore: DataStore<Preferences> =
            PreferenceDataStoreFactory.create { File(temporaryFolder.root, "$name.preferences_pb") }
        return DataStoreFlowSettings(dataStore).toBlockingObservableSettings(testScope.coroutineContext)
    }
}
private val flowToSuspendFactory = object : Settings.Factory {
    override fun create(name: String?): Settings {
        val dataStore: DataStore<Preferences> =
            PreferenceDataStoreFactory.create { File(temporaryFolder.root, "$name.preferences_pb") }
        return DataStoreFlowSettings(dataStore).toSuspendSettings().toBlockingSettings()
    }
}

class DataStoreSuspendSettingsTest : BaseSettingsTest(
    platformFactory = suspendFactory,
    allowsDuplicateInstances = false,
    hasListeners = false
) {
    @Rule
    fun temporaryFolder() = temporaryFolder

    @Test
    fun noop() = Unit
}

class DataStoreFlowSettingsTest : BaseSettingsTest(
    platformFactory = flowFactory,
    allowsDuplicateInstances = false,
    syncListeners = { testScope.advanceUntilIdle() }
) {

    @Rule
    fun temporaryFolder() = temporaryFolder

    @Test
    fun noop() = Unit

    @After
    fun tearDown() {
        testScope.cleanupTestCoroutines()
    }
}

class DataStoreFlowToSuspendSettingsTest : BaseSettingsTest(
    platformFactory = flowToSuspendFactory,
    allowsDuplicateInstances = false,
    hasListeners = false
) {
    @Rule
    fun temporaryFolder() = temporaryFolder

    @Test
    fun noop() = Unit
}
