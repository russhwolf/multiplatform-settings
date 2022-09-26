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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

/**
 * `FlowSettings` augments [SuspendSettings] with [Flow]-based APIs.
 */
@ExperimentalSettingsApi
public interface FlowSettings : SuspendSettings {
    // There's a bunch of explicit overrides in here that look unnecessary but they make the default method order
    //  more consistent between SuspendSettings and FlowSettings when creating a new implementation

    public companion object;

    public override suspend fun keys(): Set<String>
    public override suspend fun size(): Int
    public override suspend fun clear()
    public override suspend fun remove(key: String)
    public override suspend fun hasKey(key: String): Boolean

    public override suspend fun putInt(key: String, value: Int)

    /**
     * Returns a [Flow] containing the `Int` value stored at [key], or [defaultValue] if no value was stored. If a value
     * of a different type is stored at `key`, the behavior is not defined.
     */
    public fun getIntFlow(key: String, defaultValue: Int): Flow<Int>
    public override suspend fun getInt(key: String, defaultValue: Int): Int = getIntFlow(key, defaultValue).first()

    /**
     * Returns a [Flow] containing the `Int` value stored at [key], or `null` if no value was stored. If a value of a
     * different type was stored at `key`, the behavior is not defined.
     */
    public fun getIntOrNullFlow(key: String): Flow<Int?>
    public override suspend fun getIntOrNull(key: String): Int? = getIntOrNullFlow(key).first()

    public override suspend fun putLong(key: String, value: Long)

    /**
     * Returns a [Flow] containing the `Long` value stored at [key], or [defaultValue] if no value was stored. If a
     * value of a different type is stored at `key`, the behavior is not defined.
     */
    public fun getLongFlow(key: String, defaultValue: Long): Flow<Long>
    public override suspend fun getLong(key: String, defaultValue: Long): Long = getLongFlow(key, defaultValue).first()

    /**
     * Returns a [Flow] containing the `Long` value stored at [key], or `null` if no value was stored. If a value of a
     * different type was stored at `key`, the behavior is not defined.
     */
    public fun getLongOrNullFlow(key: String): Flow<Long?>
    public override suspend fun getLongOrNull(key: String): Long? = getLongOrNullFlow(key).first()

    public override suspend fun putString(key: String, value: String)

    /**
     * Returns a [Flow] containing the `String` value stored at [key], or [defaultValue] if no value was stored. If a
     * value of a different type is stored at `key`, the behavior is not defined.
     */
    public fun getStringFlow(key: String, defaultValue: String): Flow<String>
    public override suspend fun getString(key: String, defaultValue: String): String =
        getStringFlow(key, defaultValue).first()

    /**
     * Returns a [Flow] containing the `String` value stored at [key], or `null` if no value was stored. If a value of a
     * different type was stored at `key`, the behavior is not defined.
     */
    public fun getStringOrNullFlow(key: String): Flow<String?>
    public override suspend fun getStringOrNull(key: String): String? = getStringOrNullFlow(key).first()

    public override suspend fun putFloat(key: String, value: Float)

    /**
     * Returns a [Flow] containing the `Float` value stored at [key], or [defaultValue] if no value was stored. If a
     * value of a different type is stored at `key`, the behavior is not defined.
     */
    public fun getFloatFlow(key: String, defaultValue: Float): Flow<Float>
    public override suspend fun getFloat(key: String, defaultValue: Float): Float =
        getFloatFlow(key, defaultValue).first()

    /**
     * Returns a [Flow] containing the `Float` value stored at [key], or `null` if no value was stored. If a value of a
     * different type was stored at `key`, the behavior is not defined.
     */
    public fun getFloatOrNullFlow(key: String): Flow<Float?>
    public override suspend fun getFloatOrNull(key: String): Float? = getFloatOrNullFlow(key).first()

    public override suspend fun putDouble(key: String, value: Double)

    /**
     * Returns a [Flow] containing the `Double` value stored at [key], or [defaultValue] if no value was stored. If a
     * value of a different type is stored at `key`, the behavior is not defined.
     */
    public fun getDoubleFlow(key: String, defaultValue: Double): Flow<Double>
    public override suspend fun getDouble(key: String, defaultValue: Double): Double =
        getDoubleFlow(key, defaultValue).first()

    /**
     * Returns a [Flow] containing the `Double` value stored at [key], or `null` if no value was stored. If a value of a
     * different type was stored at `key`, the behavior is not defined.
     */
    public fun getDoubleOrNullFlow(key: String): Flow<Double?>
    public override suspend fun getDoubleOrNull(key: String): Double? = getDoubleOrNullFlow(key).first()

    public override suspend fun putBoolean(key: String, value: Boolean)

    /**
     * Returns a [Flow] containing the `Boolean` value stored at [key], or [defaultValue] if no value was stored. If a
     * value of a different type is stored at `key`, the behavior is not defined.
     */
    public fun getBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean>
    public override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        getBooleanFlow(key, defaultValue).first()

    /**
     * Returns a [Flow] containing the `Boolean` value stored at [key], or `null` if no value was stored. If a value of
     * a different type was stored at `key`, the behavior is not defined.
     */
    public fun getBooleanOrNullFlow(key: String): Flow<Boolean?>
    public override suspend fun getBooleanOrNull(key: String): Boolean? = getBooleanOrNullFlow(key).first()
}
