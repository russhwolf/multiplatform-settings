/*
 * Copyright 2018 Russell Wolf
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

package com.russhwolf.settings

/**
 * This class allows storage of values with the [Int], [Long], [String], [Float], [Double], or [Boolean] types, using a
 * [String] reference as a key. Values will be persisted across app launches.
 *
 * The specific persistence mechanism is defined using a platform-specific implementation, so certain behavior may vary
 * across platforms. In general, updates will be reflected immediately in-memory, but will be persisted to disk
 * asynchronously.
 *
 * Operator extensions are defined in order to simplify usage. In addition, property delegates are provided for cleaner
 * syntax and better type-safety when interacting with values stored in a `Settings` instance.
 */
expect class Settings {

    /**
     * Clear all values stored in this [Settings] instance
     */
    fun clear()

    /**
     * Remove the value stored at [key]
     */
    fun remove(key: String)

    /**
     * Returns `true` if there is a value stored at [key], or `false` otherwise
     */
    fun hasKey(key: String): Boolean

    /**
     * Stores the `Int` [value] at [key].
     */
    fun putInt(key: String, value: Int)

    /**
     * Returns the `Int` value stored at [key], or [defaultValue] if no value was stored. If a value
     * of a different type was stored at [key], the behavior is not defined.
     */
    fun getInt(key: String, defaultValue: Int = 0): Int

    /**
     * Stores the `Long` [value] at [key].
     */
    fun putLong(key: String, value: Long)

    /**
     * Returns the `Long` value stored at [key], or [defaultValue] if no value was stored. If a value
     * of a different type was stored at [key], the behavior is not defined.
     */
    fun getLong(key: String, defaultValue: Long = 0): Long

    /**
     * Stores the `String` [value] at [key].
     */
    fun putString(key: String, value: String)

    /**
     * Returns the `String` value stored at [key], or [defaultValue] if no value was stored. If a value
     * of a different type was stored at [key], the behavior is not defined.
     */
    fun getString(key: String, defaultValue: String = ""): String

    /**
     * Stores the `Float` [value] at [key].
     */
    fun putFloat(key: String, value: Float)

    /**
     * Returns the `Float` value stored at [key], or [defaultValue] if no value was stored. If a value
     * of a different type was stored at [key], the behavior is not defined.
     */
    fun getFloat(key: String, defaultValue: Float = 0f): Float

    /**
     * Stores the `Double` [value] at [key].
     */
    fun putDouble(key: String, value: Double)

    /**
     * Returns the `Double` value stored at [key], or [defaultValue] if no value was stored. If a value
     * of a different type was stored at [key], the behavior is not defined.
     */
    fun getDouble(key: String, defaultValue: Double = 0.0): Double

    /**
     * Stores the `Boolean` [value] at [key].
     */
    fun putBoolean(key: String, value: Boolean)

    /**
     * Returns the `Boolean` value stored at [key], or [defaultValue] if no value was stored. If a value
     * of a different type was stored at [key], the behavior is not defined.
     */
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean

}

operator fun Settings.contains(key: String) = hasKey(key)
operator fun Settings.minusAssign(key: String) = remove(key)
operator fun Settings.get(key: String, defaultValue: Int) = getInt(key, defaultValue)
operator fun Settings.get(key: String, defaultValue: Long) = getLong(key, defaultValue)
operator fun Settings.get(key: String, defaultValue: String) = getString(key, defaultValue)
operator fun Settings.get(key: String, defaultValue: Float) = getFloat(key, defaultValue)
operator fun Settings.get(key: String, defaultValue: Double) = getDouble(key, defaultValue)
operator fun Settings.get(key: String, defaultValue: Boolean) = getBoolean(key, defaultValue)
operator fun Settings.set(key: String, value: Int) = putInt(key, value)
operator fun Settings.set(key: String, value: Long) = putLong(key, value)
operator fun Settings.set(key: String, value: String) = putString(key, value)
operator fun Settings.set(key: String, value: Float) = putFloat(key, value)
operator fun Settings.set(key: String, value: Double) = putDouble(key, value)
operator fun Settings.set(key: String, value: Boolean) = putBoolean(key, value)
