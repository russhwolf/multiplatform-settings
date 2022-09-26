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
import com.russhwolf.settings.Settings

/**
 * A collection of storage-backed key-value data. It differs from [Settings] in that all functions use a suspending API.
 *
 * This interface allows storage of values with the [Int], [Long], [String], [Float], [Double], or [Boolean] types,
 * using a [String] reference as a key. Values will be persisted across app launches.
 */
// TODO strictly speaking this interface doesn't NEED to live in a module that pulls in the kotlinx dependency...
@ExperimentalSettingsApi
public interface SuspendSettings {

    public companion object;

    /**
     * Returns a `Set` containing all the keys present in this [SuspendSettings].
     */
    public suspend fun keys(): Set<String>

    /**
     * Returns the number of key-value pairs present in this [SuspendSettings].
     */
    public suspend fun size(): Int

    /**
     * Clears all values stored in this [SuspendSettings] instance.
     */
    public suspend fun clear()

    /**
     * Removes the value stored at [key].
     */
    public suspend fun remove(key: String)

    /**
     * Returns `true` if there is a value stored at [key], or `false` otherwise.
     */
    public suspend fun hasKey(key: String): Boolean

    /**
     * Stores the `Int` [value] at [key].
     */
    public suspend fun putInt(key: String, value: Int)

    /**
     * Returns the `Int` value stored at [key], or [defaultValue] if no value was stored. If a value of a different
     * type was stored at `key`, the behavior is not defined.
     */
    public suspend fun getInt(key: String, defaultValue: Int): Int

    /**
     * Returns the `Int` value stored at [key], or `null` if no value was stored. If a value of a different type was
     * stored at `key`, the behavior is not defined.
     */
    public suspend fun getIntOrNull(key: String): Int?

    /**
     * Stores the `Long` [value] at [key].
     */
    public suspend fun putLong(key: String, value: Long)

    /**
     * Returns the `Long` value stored at [key], or [defaultValue] if no value was stored. If a value of a different
     * type was stored at `key`, the behavior is not defined.
     */
    public suspend fun getLong(key: String, defaultValue: Long): Long

    /**
     * Returns the `Long` value stored at [key], or `null` if no value was stored. If a value of a different type was
     * stored at `key`, the behavior is not defined.
     */
    public suspend fun getLongOrNull(key: String): Long?

    /**
     * Stores the `String` [value] at [key].
     */
    public suspend fun putString(key: String, value: String)

    /**
     * Returns the `String` value stored at [key], or [defaultValue] if no value was stored. If a value of a different
     * type was stored at `key`, the behavior is not defined.
     */
    public suspend fun getString(key: String, defaultValue: String): String

    /**
     * Returns the `String` value stored at [key], or `null` if no value was stored. If a value of a different type was
     * stored at `key`, the behavior is not defined.
     */
    public suspend fun getStringOrNull(key: String): String?

    /**
     * Stores the `Float` [value] at [key].
     */
    public suspend fun putFloat(key: String, value: Float)

    /**
     * Returns the `Float` value stored at [key], or [defaultValue] if no value was stored. If a value of a different
     * type was stored at `key`, the behavior is not defined.
     */
    public suspend fun getFloat(key: String, defaultValue: Float): Float

    /**
     * Returns the `Float` value stored at [key], or `null` if no value was stored. If a value of a different type was
     * stored at `key`, the behavior is not defined.
     */
    public suspend fun getFloatOrNull(key: String): Float?

    /**
     * Stores the `Double` [value] at [key].
     */
    public suspend fun putDouble(key: String, value: Double)

    /**
     * Returns the `Double` value stored at [key], or [defaultValue] if no value was stored. If a value of a different
     * type was stored at `key`, the behavior is not defined.
     */
    public suspend fun getDouble(key: String, defaultValue: Double): Double

    /**
     * Returns the `Double` value stored at [key], or `null` if no value was stored. If a value of a different type was
     * stored at `key`, the behavior is not defined.
     */
    public suspend fun getDoubleOrNull(key: String): Double?

    /**
     * Stores the `Boolean` [value] at [key].
     */
    public suspend fun putBoolean(key: String, value: Boolean)

    /**
     * Returns the `Boolean` value stored at [key], or [defaultValue] if no value was stored. If a value of a different
     * type was stored at `key`, the behavior is not defined.
     */
    public suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean

    /**
     * Returns the `Boolean` value stored at [key], or `null` if no value was stored. If a value of a different type was
     * stored at `key`, the behavior is not defined.
     */
    public suspend fun getBooleanOrNull(key: String): Boolean?
}

