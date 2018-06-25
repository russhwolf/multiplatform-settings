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
 * A collection of storage-backed key-value data
 *
 * This class allows storage of values with the [Int], [Long], [String], [Float], [Double], or [Boolean] types, using a
 * [String] reference as a key. Values will be persisted across app launches.
 *
 * The specific persistence mechanism is defined using a platform-specific implementation, so certain behavior may vary
 * across platforms. In general, updates will be reflected immediately in-memory, but will be persisted to disk
 * asynchronously.
 *
 * Operator extensions are defined in order to simplify usage. In addition, property delegates are provided for cleaner
 * syntax and better type-safety when interacting with values stored in a `Settings` instance.
 *
 * This class can be instantiated via a platform-specific constructor or via a [Factory].
 */
public expect class Settings {

    /**
     * A factory that can produce [Settings] instances.
     *
     * This class can only be instantiated via a platform-specific constructor. It's purpose is so that `Settings`
     * objects can be created in common code, so that the only platform-specific behavior necessary in order to use
     * multiple `Settings` objects is the one-time creation of a single `Factory`.
     */
    public class Factory {
        /**
         * Creates a [Settings] object associated with the provided [name].
         *
         * Multiple `Settings` instances created with the same `name` parameter will be backed by the same persistent
         * data, while distinct `name`s will use different data. If `name` is `null` then a platform-specific default
         * will be used.
         */
        public fun create(name: String? = null): Settings
    }

    /**
     * Clears all values stored in this [Settings] instance.
     */
    public fun clear()

    /**
     * Removes the value stored at [key].
     */
    public fun remove(key: String)

    /**
     * Returns `true` if there is a value stored at [key], or `false` otherwise.
     */
    public fun hasKey(key: String): Boolean

    /**
     * Stores the `Int` [value] at [key].
     */
    public fun putInt(key: String, value: Int)

    /**
     * Returns the `Int` value stored at [key], or [defaultValue] if no value was stored. If a value of a different
     * type was stored at `key`, the behavior is not defined.
     */
    public fun getInt(key: String, defaultValue: Int = 0): Int

    /**
     * Stores the `Long` [value] at [key].
     */
    public fun putLong(key: String, value: Long)

    /**
     * Returns the `Long` value stored at [key], or [defaultValue] if no value was stored. If a value of a different
     * type was stored at `key`, the behavior is not defined.
     */
    public fun getLong(key: String, defaultValue: Long = 0): Long

    /**
     * Stores the `String` [value] at [key].
     */
    public fun putString(key: String, value: String)

    /**
     * Returns the `String` value stored at [key], or [defaultValue] if no value was stored. If a value of a different
     * type was stored at `key`, the behavior is not defined.
     */
    public fun getString(key: String, defaultValue: String = ""): String

    /**
     * Stores the `Float` [value] at [key].
     */
    public fun putFloat(key: String, value: Float)

    /**
     * Returns the `Float` value stored at [key], or [defaultValue] if no value was stored. If a value of a different
     * type was stored at `key`, the behavior is not defined.
     */
    public fun getFloat(key: String, defaultValue: Float = 0f): Float

    /**
     * Stores the `Double` [value] at [key].
     */
    public fun putDouble(key: String, value: Double)

    /**
     * Returns the `Double` value stored at [key], or [defaultValue] if no value was stored. If a value of a different
     * type was stored at `key`, the behavior is not defined.
     */
    public fun getDouble(key: String, defaultValue: Double = 0.0): Double

    /**
     * Stores the `Boolean` [value] at [key].
     */
    public fun putBoolean(key: String, value: Boolean)

    /**
     * Returns the `Boolean` value stored at [key], or [defaultValue] if no value was stored. If a value of a different
     * type was stored at `key`, the behavior is not defined.
     */
    public fun getBoolean(key: String, defaultValue: Boolean = false): Boolean

}
