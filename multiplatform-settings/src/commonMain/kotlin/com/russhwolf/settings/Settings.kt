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
 * This interface allows storage of values with the [Int], [Long], [String], [Float], [Double], or [Boolean] types, using a
 * [String] reference as a key. Values will be persisted across app launches.
 *
 * Operator extensions are defined in order to simplify usage. In addition, property delegates are provided for cleaner
 * syntax and better type-safety when interacting with values stored in a `Settings` instance.
 */
public interface Settings {

    /**
     * A factory that can produce [Settings] instances.
     */
    public interface Factory {
        /**
         * Creates a [Settings] object associated with the provided [name].
         *
         * Multiple `Settings` instances created with the same `name` parameter will be backed by the same persistent
         * data, while distinct `name`s will use different data.
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
     * Returns the `Int` value stored at [key], or `null` if no value was stored. If a value of a different type was
     * stored at `key`, the behavior is not defined.
     */
    public fun getIntOrNull(key: String): Int?

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
     * Returns the `Long` value stored at [key], or `null` if no value was stored. If a value of a different type was
     * stored at `key`, the behavior is not defined.
     */
    public fun getLongOrNull(key: String): Long?

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
     * Returns the `String` value stored at [key], or `null` if no value was stored. If a value of a different type was
     * stored at `key`, the behavior is not defined.
     */
    public fun getStringOrNull(key: String): String?

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
     * Returns the `Float` value stored at [key], or `null` if no value was stored. If a value of a different type was
     * stored at `key`, the behavior is not defined.
     */
    public fun getFloatOrNull(key: String): Float?

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
     * Returns the `Double` value stored at [key], or `null` if no value was stored. If a value of a different type was
     * stored at `key`, the behavior is not defined.
     */
    public fun getDoubleOrNull(key: String): Double?

    /**
     * Stores the `Boolean` [value] at [key].
     */
    public fun putBoolean(key: String, value: Boolean)

    /**
     * Returns the `Boolean` value stored at [key], or [defaultValue] if no value was stored. If a value of a different
     * type was stored at `key`, the behavior is not defined.
     */
    public fun getBoolean(key: String, defaultValue: Boolean = false): Boolean

    /**
     * Returns the `Boolean` value stored at [key], or `null` if no value was stored. If a value of a different type was
     * stored at `key`, the behavior is not defined.
     */
    public fun getBooleanOrNull(key: String): Boolean?
}

/**
 * An extension to the [Settings] interface to include update listener functionality
 */
@ExperimentalListener
public interface ObservableSettings : Settings {
    /**
     * Adds a listener which will call the supplied [callback] anytime the value at [key] changes. A [SettingsListener]
     * reference is returned which should be passed to [removeListener] when you no longer need it so that the
     * associated platform resources can be cleaned up.
     *
     * A strong reference should be held to the `SettingsListener` returned by this method in order to avoid it being
     * garbage-collected on Android.
     *
     * No attempt is made in the current implementation to safely handle multithreaded interaction with the listener, so
     * it's recommended that interaction with the listener APIs be confined to the main UI thread.
     */
    @ExperimentalListener
    public fun addListener(key: String, callback: () -> Unit): SettingsListener

    /**
     * Unsubscribes the [listener] from receiving updates to the value at the key it monitors
     */
    @ExperimentalListener
    public fun removeListener(listener: SettingsListener)

}

@Deprecated(
    "ListenableSettings has been renamed ObservableSettings",
    replaceWith = ReplaceWith("ObservableSettings", "com.russhwolf.settings.ObservableSettings"),
    level = DeprecationLevel.WARNING
)
@ExperimentalListener
@Suppress("UNUSED", "KDocMissingDocumentation")
public typealias ListenableSettings = ObservableSettings

/**
 * A handle to a listener instance returned by [ObservableSettings.addListener] so it can be passed to
 * [ObservableSettings.removeListener].
 */
@ExperimentalListener
public interface SettingsListener
