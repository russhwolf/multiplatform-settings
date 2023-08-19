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
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

/**
 * Wraps this [Settings] in the [SuspendSettings] interface.
 */
@ExperimentalSettingsApi
public fun Settings.toSuspendSettings(
    dispatcher: CoroutineDispatcher = converterDefaultDispatcher
): SuspendSettings =
    SuspendSettingsWrapper(this, dispatcher)

/**
 * Wraps this [ObservableSettings] in the [FlowSettings] interface.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.toFlowSettings(
    dispatcher: CoroutineDispatcher = converterDefaultDispatcher
): FlowSettings =
    FlowSettingsWrapper(this, dispatcher)

@ExperimentalSettingsApi
private open class SuspendSettingsWrapper(
    private val delegate: Settings,
    private val dispatcher: CoroutineDispatcher
) : SuspendSettings {
    public override suspend fun keys(): Set<String> = withContext(dispatcher) { delegate.keys }
    public override suspend fun size(): Int = withContext(dispatcher) { delegate.size }
    public override suspend fun clear() = withContext(dispatcher) { delegate.clear() }
    public override suspend fun remove(key: String) = withContext(dispatcher) { delegate.remove(key) }
    public override suspend fun hasKey(key: String): Boolean = withContext(dispatcher) { delegate.hasKey(key) }

    public override suspend fun putInt(key: String, value: Int) = withContext(dispatcher) {
        delegate.putInt(key, value)
    }

    public override suspend fun getInt(key: String, defaultValue: Int): Int = withContext(dispatcher) {
        delegate.getInt(key, defaultValue)
    }

    public override suspend fun getIntOrNull(key: String): Int? = withContext(dispatcher) {
        delegate.getIntOrNull(key)
    }

    public override suspend fun putLong(key: String, value: Long) = withContext(dispatcher) {
        delegate.putLong(key, value)
    }

    public override suspend fun getLong(key: String, defaultValue: Long): Long = withContext(dispatcher) {
        delegate.getLong(key, defaultValue)
    }

    public override suspend fun getLongOrNull(key: String): Long? = withContext(dispatcher) {
        delegate.getLongOrNull(key)
    }

    public override suspend fun putString(key: String, value: String) = withContext(dispatcher) {
        delegate.putString(key, value)
    }

    public override suspend fun getString(key: String, defaultValue: String): String = withContext(dispatcher) {
        delegate.getString(key, defaultValue)
    }

    public override suspend fun getStringOrNull(key: String): String? = withContext(dispatcher) {
        delegate.getStringOrNull(key)
    }

    public override suspend fun putFloat(key: String, value: Float) = withContext(dispatcher) {
        delegate.putFloat(key, value)
    }

    public override suspend fun getFloat(key: String, defaultValue: Float): Float = withContext(dispatcher) {
        delegate.getFloat(key, defaultValue)
    }

    public override suspend fun getFloatOrNull(key: String): Float? = withContext(dispatcher) {
        delegate.getFloatOrNull(key)
    }

    public override suspend fun putDouble(key: String, value: Double) = withContext(dispatcher) {
        delegate.putDouble(key, value)
    }

    public override suspend fun getDouble(key: String, defaultValue: Double): Double = withContext(dispatcher) {
        delegate.getDouble(key, defaultValue)
    }

    public override suspend fun getDoubleOrNull(key: String): Double? = withContext(dispatcher) {
        delegate.getDoubleOrNull(key)
    }

    public override suspend fun putBoolean(key: String, value: Boolean) = withContext(dispatcher) {
        delegate.putBoolean(key, value)
    }

    public override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean = withContext(dispatcher) {
        delegate.getBoolean(key, defaultValue)
    }

    public override suspend fun getBooleanOrNull(key: String): Boolean? = withContext(dispatcher) {
        delegate.getBooleanOrNull(key)
    }
}

@ExperimentalSettingsApi
private class FlowSettingsWrapper(
    private val delegate: ObservableSettings,
    private val dispatcher: CoroutineDispatcher
) : SuspendSettingsWrapper(delegate, dispatcher), FlowSettings {

    public override fun getIntFlow(key: String, defaultValue: Int): Flow<Int> =
        delegate.getIntFlow(key, defaultValue).flowOn(dispatcher)

    public override fun getIntOrNullFlow(key: String): Flow<Int?> =
        delegate.getIntOrNullFlow(key).flowOn(dispatcher)

    public override fun getLongFlow(key: String, defaultValue: Long): Flow<Long> =
        delegate.getLongFlow(key, defaultValue).flowOn(dispatcher)

    public override fun getLongOrNullFlow(key: String): Flow<Long?> =
        delegate.getLongOrNullFlow(key).flowOn(dispatcher)

    public override fun getStringFlow(key: String, defaultValue: String): Flow<String> =
        delegate.getStringFlow(key, defaultValue).flowOn(dispatcher)

    public override fun getStringOrNullFlow(key: String): Flow<String?> =
        delegate.getStringOrNullFlow(key).flowOn(dispatcher)

    public override fun getFloatFlow(key: String, defaultValue: Float): Flow<Float> =
        delegate.getFloatFlow(key, defaultValue).flowOn(dispatcher)

    public override fun getFloatOrNullFlow(key: String): Flow<Float?> =
        delegate.getFloatOrNullFlow(key).flowOn(dispatcher)

    public override fun getDoubleFlow(key: String, defaultValue: Double): Flow<Double> =
        delegate.getDoubleFlow(key, defaultValue).flowOn(dispatcher)

    public override fun getDoubleOrNullFlow(key: String): Flow<Double?> =
        delegate.getDoubleOrNullFlow(key).flowOn(dispatcher)

    public override fun getBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean> =
        delegate.getBooleanFlow(key, defaultValue).flowOn(dispatcher)

    public override fun getBooleanOrNullFlow(key: String): Flow<Boolean?> =
        delegate.getBooleanOrNullFlow(key).flowOn(dispatcher)

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
