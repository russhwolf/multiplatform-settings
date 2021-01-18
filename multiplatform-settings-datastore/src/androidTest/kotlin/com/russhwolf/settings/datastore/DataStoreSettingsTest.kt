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
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import com.russhwolf.settings.BaseSettingsTest
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SettingsListener
import com.russhwolf.settings.coroutines.toBlockingSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineScope
import org.junit.After
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.fail

private val temporaryFolder = TemporaryFolder()
private val testScope = TestCoroutineScope()

private val factory = object : Settings.Factory {
    override fun create(name: String?): Settings {
        val dataStore: DataStore<Preferences> =
            PreferenceDataStoreFactory.create { File(temporaryFolder.root, "$name.preferences_pb") }
        val settings = DataStoreSettings(dataStore)
        return BlockingDataStoreSettings(settings, dataStore)
    }
}

class DataStoreSettingsTest : BaseSettingsTest(
    platformFactory = factory,
    allowsDuplicateInstances = false,
    syncListeners = { testScope.advanceUntilIdle() }
) {

    @Rule
    fun temporaryFolder() = temporaryFolder

    @Test
    fun constructor_datastore(): Unit = runBlocking {
        val dataStore = PreferenceDataStoreFactory.create { File(temporaryFolder.root, "settings.preferences_pb") }
        val settings = DataStoreSettings(dataStore)

        dataStore.edit { it[intPreferencesKey("a")] = 3 }
        assertEquals(3, settings.getInt("a"))
    }

    @After
    fun tearDown() {
        testScope.cleanupTestCoroutines()
    }
}

// TODO need better tests around Flow APIs instead of this terrible hackery
@OptIn(FlowPreview::class, ExperimentalSettingsApi::class)
private class BlockingDataStoreSettings(
    private val settings: DataStoreSettings,
    private val dataStore: DataStore<Preferences>
) : Settings by settings.toBlockingSettings(), ObservableSettings {
    override fun addListener(key: String, callback: () -> Unit): SettingsListener = object : SettingsListener {

        @Suppress("USELESS_CAST") // There's an implicit cast somewhere that try/catch misses
        private inline fun <reified T : Any> valueOrNull(): T? =
            runBlocking {
                try {
                    dataStore.data.first()[preferencesKey<T>(key)]
                } catch (e: Throwable) {
                    null
                }
            } as? T

        private inline fun <reified T : Any> flowOrNull(): Flow<T?> =
            dataStore.data.map { it[preferencesKey<T>(key)] }.catch {}

        private inline fun <reified T> preferencesKey(key: String): Preferences.Key<T> {
            @Suppress("UNCHECKED_CAST")
            return when (T::class) {
                Int::class -> intPreferencesKey(key)
                Long::class -> longPreferencesKey(key)
                String::class -> stringPreferencesKey(key)
                Float::class -> floatPreferencesKey(key)
                Double::class -> doublePreferencesKey(key)
                Int::class -> booleanPreferencesKey(key)
                else -> fail("invalid type")
            } as Preferences.Key<T>
        }

        private val scope = CoroutineScope(testScope.coroutineContext + SupervisorJob(testScope.coroutineContext[Job]))

        init {
            var prev: Any? = valueOrNull<String>()
                ?: valueOrNull<Int>()
                ?: valueOrNull<Long>()
                ?: valueOrNull<Float>()
                ?: valueOrNull<Double>()
                ?: valueOrNull<Boolean>()

            flowOf(
                flowOrNull<String>(),
                flowOrNull<Int>(),
                flowOrNull<Long>(),
                flowOrNull<Float>(),
                flowOrNull<Double>(),
                flowOrNull<Boolean>()
            ).flattenMerge()
                .onEach { current ->
                    if (prev != current) {
                        callback()
                        prev = current
                    }
                }
                .launchIn(scope)
        }

        override fun deactivate() {
            scope.cancel()
        }
    }
}
