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
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.core.remove
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.SuspendSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

public class DataStoreSuspendSettings(private val datastore: DataStore<Preferences>) : SuspendSettings {
    override suspend fun keys(): Set<String> = datastore.data.first().asMap().keys.map { it.name }.toSet()
    override suspend fun size(): Int = datastore.data.first().asMap().size
    override suspend fun clear() = keys().forEach { remove(it) }

    override suspend fun remove(key: String) {
        datastore.edit { it.remove(preferencesKey<String>(key)) }
    }

    override suspend fun hasKey(key: String): Boolean = datastore.data.first().contains(preferencesKey<String>(key))

    override suspend fun putInt(key: String, value: Int) {
        datastore.edit { it[preferencesKey(key)] = value }
    }

    override suspend fun getInt(key: String, defaultValue: Int): Int = getIntOrNull(key) ?: defaultValue
    override suspend fun getIntOrNull(key: String): Int? = datastore.data.first()[preferencesKey(key)]

    override suspend fun putLong(key: String, value: Long) {
        datastore.edit { it[preferencesKey(key)] = value }
    }

    override suspend fun getLong(key: String, defaultValue: Long): Long = getLongOrNull(key) ?: defaultValue
    override suspend fun getLongOrNull(key: String): Long? = datastore.data.first()[preferencesKey(key)]

    override suspend fun putString(key: String, value: String) {
        datastore.edit { it[preferencesKey(key)] = value }
    }

    override suspend fun getString(key: String, defaultValue: String): String = getStringOrNull(key) ?: defaultValue
    override suspend fun getStringOrNull(key: String): String? = datastore.data.first()[preferencesKey(key)]

    override suspend fun putFloat(key: String, value: Float) {
        datastore.edit { it[preferencesKey(key)] = value }
    }

    override suspend fun getFloat(key: String, defaultValue: Float): Float = getFloatOrNull(key) ?: defaultValue
    override suspend fun getFloatOrNull(key: String): Float? = datastore.data.first()[preferencesKey(key)]

    override suspend fun putDouble(key: String, value: Double) {
        datastore.edit { it[preferencesKey(key)] = value }
    }

    override suspend fun getDouble(key: String, defaultValue: Double): Double = getDoubleOrNull(key) ?: defaultValue
    override suspend fun getDoubleOrNull(key: String): Double? = datastore.data.first()[preferencesKey(key)]

    override suspend fun putBoolean(key: String, value: Boolean) {
        datastore.edit { it[preferencesKey(key)] = value }
    }

    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean = getBooleanOrNull(key) ?: defaultValue
    override suspend fun getBooleanOrNull(key: String): Boolean? = datastore.data.first()[preferencesKey(key)]
}

public class DataStoreFlowSettings(private val datastore: DataStore<Preferences>) : FlowSettings {
    override suspend fun keys(): Set<String> = datastore.data.first().asMap().keys.map { it.name }.toSet()
    override suspend fun size(): Int = datastore.data.first().asMap().size
    override suspend fun clear() = keys().forEach { remove(it) }

    override suspend fun remove(key: String) {
        datastore.edit { it.remove(preferencesKey<String>(key)) }
    }

    override suspend fun hasKey(key: String): Boolean = datastore.data.first().contains(preferencesKey<String>(key))

    override suspend fun putInt(key: String, value: Int) {
        datastore.edit { it[preferencesKey(key)] = value }
    }

    override fun getIntFlow(key: String, defaultValue: Int): Flow<Int> =
        datastore.data.map { it[preferencesKey(key)] ?: defaultValue }

    override fun getIntOrNullFlow(key: String): Flow<Int?> = datastore.data.map { it[preferencesKey(key)] }

    override suspend fun putLong(key: String, value: Long) {
        datastore.edit { it[preferencesKey(key)] = value }
    }

    override fun getLongFlow(key: String, defaultValue: Long): Flow<Long> =
        datastore.data.map { it[preferencesKey(key)] ?: defaultValue }

    override fun getLongOrNullFlow(key: String): Flow<Long?> = datastore.data.map { it[preferencesKey(key)] }

    override suspend fun putString(key: String, value: String) {
        datastore.edit { it[preferencesKey(key)] = value }
    }

    override fun getStringFlow(key: String, defaultValue: String): Flow<String> =
        datastore.data.map { it[preferencesKey(key)] ?: defaultValue }

    override fun getStringOrNullFlow(key: String): Flow<String?> = datastore.data.map { it[preferencesKey(key)] }

    override suspend fun putFloat(key: String, value: Float) {
        datastore.edit { it[preferencesKey(key)] = value }
    }

    override fun getFloatFlow(key: String, defaultValue: Float): Flow<Float> =
        datastore.data.map { it[preferencesKey(key)] ?: defaultValue }

    override fun getFloatOrNullFlow(key: String): Flow<Float?> = datastore.data.map { it[preferencesKey(key)] }

    override suspend fun putDouble(key: String, value: Double) {
        datastore.edit { it[preferencesKey(key)] = value }
    }

    override fun getDoubleFlow(key: String, defaultValue: Double): Flow<Double> =
        datastore.data.map { it[preferencesKey(key)] ?: defaultValue }

    override fun getDoubleOrNullFlow(key: String): Flow<Double?> = datastore.data.map { it[preferencesKey(key)] }

    override suspend fun putBoolean(key: String, value: Boolean) {
        datastore.edit { it[preferencesKey(key)] = value }
    }

    override fun getBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean> =
        datastore.data.map { it[preferencesKey(key)] ?: defaultValue }

    override fun getBooleanOrNullFlow(key: String): Flow<Boolean?> = datastore.data.map { it[preferencesKey(key)] }
}
