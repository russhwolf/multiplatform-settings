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

package com.russhwolf.settings.coroutines

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

/**
 * Wraps this [Settings] in the [SuspendSettings] interface.
 */
@ExperimentalSettingsApi
public fun Settings.toSuspendSettings(): SuspendSettings = SuspendSettingsWrapper(this)

/**
 * Wraps this [ObservableSettings] in the [FlowSettings] interface.
 */
@ExperimentalSettingsApi
@ExperimentalCoroutinesApi
public fun ObservableSettings.toFlowSettings(): FlowSettings = FlowSettingsWrapper(this)

@ExperimentalSettingsApi
private open class SuspendSettingsWrapper(private val delegate: Settings) : SuspendSettings {
    public override suspend fun keys(): Set<String> = delegate.keys
    public override suspend fun size(): Int = delegate.size
    public override suspend fun clear() = delegate.clear()
    public override suspend fun remove(key: String) = delegate.remove(key)
    public override suspend fun hasKey(key: String): Boolean = delegate.hasKey(key)

    public override suspend fun putInt(key: String, value: Int) = delegate.putInt(key, value)
    public override suspend fun getInt(key: String, defaultValue: Int): Int = delegate.getInt(key, defaultValue)
    public override suspend fun getIntOrNull(key: String): Int? = delegate.getIntOrNull(key)

    public override suspend fun putLong(key: String, value: Long) = delegate.putLong(key, value)
    public override suspend fun getLong(key: String, defaultValue: Long): Long =
        delegate.getLong(key, defaultValue)

    public override suspend fun getLongOrNull(key: String): Long? = delegate.getLongOrNull(key)

    public override suspend fun putString(key: String, value: String) = delegate.putString(key, value)
    public override suspend fun getString(key: String, defaultValue: String): String =
        delegate.getString(key, defaultValue)

    public override suspend fun getStringOrNull(key: String): String? = delegate.getStringOrNull(key)

    public override suspend fun putFloat(key: String, value: Float) = delegate.putFloat(key, value)
    public override suspend fun getFloat(key: String, defaultValue: Float): Float =
        delegate.getFloat(key, defaultValue)

    public override suspend fun getFloatOrNull(key: String): Float? = delegate.getFloatOrNull(key)

    public override suspend fun putDouble(key: String, value: Double) = delegate.putDouble(key, value)
    public override suspend fun getDouble(key: String, defaultValue: Double): Double =
        delegate.getDouble(key, defaultValue)

    public override suspend fun getDoubleOrNull(key: String): Double? = delegate.getDoubleOrNull(key)

    public override suspend fun putBoolean(key: String, value: Boolean) = delegate.putBoolean(key, value)
    public override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        delegate.getBoolean(key, defaultValue)

    public override suspend fun getBooleanOrNull(key: String): Boolean? = delegate.getBooleanOrNull(key)
}

@ExperimentalSettingsApi
@ExperimentalCoroutinesApi
private class FlowSettingsWrapper(private val delegate: ObservableSettings) :
    SuspendSettingsWrapper(delegate), FlowSettings {

    public override fun getIntFlow(key: String, defaultValue: Int): Flow<Int> = delegate.getIntFlow(key, defaultValue)
    public override fun getIntOrNullFlow(key: String): Flow<Int?> = delegate.getIntOrNullFlow(key)

    public override fun getLongFlow(key: String, defaultValue: Long): Flow<Long> =
        delegate.getLongFlow(key, defaultValue)

    public override fun getLongOrNullFlow(key: String): Flow<Long?> = delegate.getLongOrNullFlow(key)

    public override fun getStringFlow(key: String, defaultValue: String): Flow<String> =
        delegate.getStringFlow(key, defaultValue)

    public override fun getStringOrNullFlow(key: String): Flow<String?> = delegate.getStringOrNullFlow(key)

    public override fun getFloatFlow(key: String, defaultValue: Float): Flow<Float> =
        delegate.getFloatFlow(key, defaultValue)

    public override fun getFloatOrNullFlow(key: String): Flow<Float?> = delegate.getFloatOrNullFlow(key)

    public override fun getDoubleFlow(key: String, defaultValue: Double): Flow<Double> =
        delegate.getDoubleFlow(key, defaultValue)

    public override fun getDoubleOrNullFlow(key: String): Flow<Double?> = delegate.getDoubleOrNullFlow(key)

    public override fun getBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean> =
        delegate.getBooleanFlow(key, defaultValue)

    public override fun getBooleanOrNullFlow(key: String): Flow<Boolean?> = delegate.getBooleanOrNullFlow(key)

    // Prefer the SuspendSettingsWrapper implementation to the FlowSettings one which calls getXXXFlow().first()

    public override suspend fun getInt(key: String, defaultValue: Int): Int =
        super<SuspendSettingsWrapper>.getInt(key, defaultValue)

    public override suspend fun getIntOrNull(key: String): Int? =
        super<SuspendSettingsWrapper>.getIntOrNull(key)

    public override suspend fun getLong(key: String, defaultValue: Long): Long =
        super<SuspendSettingsWrapper>.getLong(key, defaultValue)

    public override suspend fun getLongOrNull(key: String): Long? =
        super<SuspendSettingsWrapper>.getLongOrNull(key)

    public override suspend fun getString(key: String, defaultValue: String): String =
        super<SuspendSettingsWrapper>.getString(key, defaultValue)

    public override suspend fun getStringOrNull(key: String): String? =
        super<SuspendSettingsWrapper>.getStringOrNull(key)

    public override suspend fun getFloat(key: String, defaultValue: Float): Float =
        super<SuspendSettingsWrapper>.getFloat(key, defaultValue)

    public override suspend fun getFloatOrNull(key: String): Float? =
        super<SuspendSettingsWrapper>.getFloatOrNull(key)

    public override suspend fun getDouble(key: String, defaultValue: Double): Double =
        super<SuspendSettingsWrapper>.getDouble(key, defaultValue)

    public override suspend fun getDoubleOrNull(key: String): Double? =
        super<SuspendSettingsWrapper>.getDoubleOrNull(key)

    public override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        super<SuspendSettingsWrapper>.getBoolean(key, defaultValue)

    public override suspend fun getBooleanOrNull(key: String): Boolean? =
        super<SuspendSettingsWrapper>.getBooleanOrNull(key)
}
