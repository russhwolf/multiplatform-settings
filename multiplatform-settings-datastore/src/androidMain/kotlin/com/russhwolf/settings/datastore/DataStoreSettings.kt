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

package com.russhwolf.settings.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

/**
 * `DataStoreSettings` implements [FlowSettings] using the [DataStore] API.
 */
@ExperimentalSettingsImplementation
@ExperimentalSettingsApi
public class DataStoreSettings(private val datastore: DataStore<Preferences>) : FlowSettings {
    public override suspend fun keys(): Set<String> = datastore.data.first().asMap().keys.map { it.name }.toSet()
    public override suspend fun size(): Int = datastore.data.first().asMap().size
    public override suspend fun clear(): Unit = keys().forEach { remove(it) }

    public override suspend fun remove(key: String) {
        datastore.edit { it.remove(stringSetPreferencesKey(key)) }
    }

    public override suspend fun hasKey(key: String): Boolean =
        datastore.data.first().contains(stringSetPreferencesKey(key))

    public override suspend fun putInt(key: String, value: Int) {
        datastore.edit { it[intPreferencesKey(key)] = value }
    }

    public override fun getIntFlow(key: String, defaultValue: Int): Flow<Int> =
        getValue { it[intPreferencesKey(key)] ?: defaultValue }

    public override fun getIntOrNullFlow(key: String): Flow<Int?> =
        getValue { it[intPreferencesKey(key)] }

    public override suspend fun putLong(key: String, value: Long) {
        datastore.edit { it[longPreferencesKey(key)] = value }
    }

    public override fun getLongFlow(key: String, defaultValue: Long): Flow<Long> =
        getValue { it[longPreferencesKey(key)] ?: defaultValue }

    public override fun getLongOrNullFlow(key: String): Flow<Long?> =
        getValue { it[longPreferencesKey(key)] }

    public override suspend fun putString(key: String, value: String) {
        datastore.edit { it[stringPreferencesKey(key)] = value }
    }

    public override fun getStringFlow(key: String, defaultValue: String): Flow<String> =
        getValue { it[stringPreferencesKey(key)] ?: defaultValue }

    public override fun getStringOrNullFlow(key: String): Flow<String?> =
        getValue { it[stringPreferencesKey(key)] }

    public override suspend fun putFloat(key: String, value: Float) {
        datastore.edit { it[floatPreferencesKey(key)] = value }
    }

    public override fun getFloatFlow(key: String, defaultValue: Float): Flow<Float> =
        getValue { it[floatPreferencesKey(key)] ?: defaultValue }

    public override fun getFloatOrNullFlow(key: String): Flow<Float?> =
        getValue { it[floatPreferencesKey(key)] }

    public override suspend fun putDouble(key: String, value: Double) {
        datastore.edit { it[doublePreferencesKey(key)] = value }
    }

    public override fun getDoubleFlow(key: String, defaultValue: Double): Flow<Double> =
        getValue { it[doublePreferencesKey(key)] ?: defaultValue }

    public override fun getDoubleOrNullFlow(key: String): Flow<Double?> =
        getValue { it[doublePreferencesKey(key)] }

    public override suspend fun putBoolean(key: String, value: Boolean) {
        datastore.edit { it[booleanPreferencesKey(key)] = value }
    }

    public override fun getBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean> =
        getValue { it[booleanPreferencesKey(key)] ?: defaultValue }

    public override fun getBooleanOrNullFlow(key: String): Flow<Boolean?> =
        getValue { it[booleanPreferencesKey(key)] }

    private inline fun <T> getValue(crossinline getValue: (Preferences) -> T): Flow<T> =
        datastore.data.map { getValue(it) }.distinctUntilChanged()
}
