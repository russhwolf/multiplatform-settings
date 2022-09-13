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

    public companion object;

    /**
     * A factory that can produce [Settings] instances or derivations thereof.
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
     * Returns a `Set` containing all the keys present in this [Settings].
     */
    public val keys: Set<String>

    /**
     * Returns the number of key-value pairs present in this [Settings].
     */
    public val size: Int

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
    public fun getInt(key: String, defaultValue: Int): Int

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
    public fun getLong(key: String, defaultValue: Long): Long

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
    public fun getString(key: String, defaultValue: String): String

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
    public fun getFloat(key: String, defaultValue: Float): Float

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
    public fun getDouble(key: String, defaultValue: Double): Double

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
    public fun getBoolean(key: String, defaultValue: Boolean): Boolean

    /**
     * Returns the `Boolean` value stored at [key], or `null` if no value was stored. If a value of a different type was
     * stored at `key`, the behavior is not defined.
     */
    public fun getBooleanOrNull(key: String): Boolean?

    @Deprecated(
        message = "Default values for defaultValue parameters are deprecated",
        replaceWith = ReplaceWith("getInt(key, defaultValue = 0)"),
        level = DeprecationLevel.ERROR
    )
    @Suppress("KDocMissingDocumentation")
    public fun getInt(key: String): Int = getInt(key, defaultValue = 0)

    @Deprecated(
        message = "Default values for defaultValue parameters are deprecated",
        replaceWith = ReplaceWith("getLong(key, defaultValue = 0L)"),
        level = DeprecationLevel.ERROR
    )
    @Suppress("KDocMissingDocumentation")
    public fun getLong(key: String): Long = getLong(key, defaultValue = 0L)

    @Deprecated(
        message = "Default values for defaultValue parameters are deprecated",
        replaceWith = ReplaceWith("getString(key, defaultValue = \"\")"),
        level = DeprecationLevel.ERROR
    )
    @Suppress("KDocMissingDocumentation")
    public fun getString(key: String): String = getString(key, defaultValue = "")

    @Deprecated(
        message = "Default values for defaultValue parameters are deprecated",
        replaceWith = ReplaceWith("getFloat(key, defaultValue = 0f)"),
        level = DeprecationLevel.ERROR
    )
    @Suppress("KDocMissingDocumentation")
    public fun getFloat(key: String): Float = getFloat(key, defaultValue = 0f)

    @Deprecated(
        message = "Default values for defaultValue parameters are deprecated",
        replaceWith = ReplaceWith("getDouble(key, defaultValue = 0.0)"),
        level = DeprecationLevel.ERROR
    )
    @Suppress("KDocMissingDocumentation")
    public fun getDouble(key: String): Double = getDouble(key, defaultValue = 0.0)

    @Deprecated(
        message = "Default values for defaultValue parameters are deprecated",
        replaceWith = ReplaceWith("getBoolean(key, defaultValue = false)"),
        level = DeprecationLevel.ERROR
    )
    @Suppress("KDocMissingDocumentation")
    public fun getBoolean(key: String): Boolean = getBoolean(key, defaultValue = false)
}

/**
 * An extension to the [Settings] interface to include update listener functionality
 */
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
    @Deprecated(
        "Use typed listener methods instead",
        level = DeprecationLevel.WARNING
    )
    public fun addListener(key: String, callback: () -> Unit): SettingsListener

    /**
     * Adds a listener which will call the supplied [callback] anytime the value at [key] changes. A [SettingsListener]
     * reference is returned which can be used to halt callbacks by calling [deactivate()][SettingsListener.deactivate].
     * A strong reference should be held to the `SettingsListener` returned by this method in order to avoid it being
     * garbage-collected.
     */
    @Suppress("DEPRECATION")
    public fun addIntListener(
        key: String,
        defaultValue: Int,
        callback: (Int) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getInt(key, defaultValue)) }

    /**
     * Adds a listener which will call the supplied [callback] anytime the value at [key] changes. A [SettingsListener]
     * reference is returned which can be used to halt callbacks by calling [deactivate()][SettingsListener.deactivate].
     * A strong reference should be held to the `SettingsListener` returned by this method in order to avoid it being
     * garbage-collected.
     */
    @Suppress("DEPRECATION")
    public fun addLongListener(
        key: String,
        defaultValue: Long,
        callback: (Long) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getLong(key, defaultValue)) }

    /**
     * Adds a listener which will call the supplied [callback] anytime the value at [key] changes. A [SettingsListener]
     * reference is returned which can be used to halt callbacks by calling [deactivate()][SettingsListener.deactivate].
     * A strong reference should be held to the `SettingsListener` returned by this method in order to avoid it being
     * garbage-collected.
     */
    @Suppress("DEPRECATION")
    public fun addStringListener(
        key: String,
        defaultValue: String,
        callback: (String) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getString(key, defaultValue)) }

    /**
     * Adds a listener which will call the supplied [callback] anytime the value at [key] changes. A [SettingsListener]
     * reference is returned which can be used to halt callbacks by calling [deactivate()][SettingsListener.deactivate].
     * A strong reference should be held to the `SettingsListener` returned by this method in order to avoid it being
     * garbage-collected.
     */
    @Suppress("DEPRECATION")
    public fun addFloatListener(
        key: String,
        defaultValue: Float,
        callback: (Float) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getFloat(key, defaultValue)) }

    /**
     * Adds a listener which will call the supplied [callback] anytime the value at [key] changes. A [SettingsListener]
     * reference is returned which can be used to halt callbacks by calling [deactivate()][SettingsListener.deactivate].
     * A strong reference should be held to the `SettingsListener` returned by this method in order to avoid it being
     * garbage-collected.
     */
    @Suppress("DEPRECATION")
    public fun addDoubleListener(
        key: String,
        defaultValue: Double,
        callback: (Double) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getDouble(key, defaultValue)) }

    /**
     * Adds a listener which will call the supplied [callback] anytime the value at [key] changes. A [SettingsListener]
     * reference is returned which can be used to halt callbacks by calling [deactivate()][SettingsListener.deactivate].
     * A strong reference should be held to the `SettingsListener` returned by this method in order to avoid it being
     * garbage-collected.
     */
    @Suppress("DEPRECATION")
    public fun addBooleanListener(
        key: String,
        defaultValue: Boolean,
        callback: (Boolean) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getBoolean(key, defaultValue)) }

    /**
     * Adds a listener which will call the supplied [callback] anytime the value at [key] changes. A [SettingsListener]
     * reference is returned which can be used to halt callbacks by calling [deactivate()][SettingsListener.deactivate].
     * A strong reference should be held to the `SettingsListener` returned by this method in order to avoid it being
     * garbage-collected.
     */
    @Suppress("DEPRECATION")
    public fun addIntOrNullListener(
        key: String,
        callback: (Int?) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getIntOrNull(key)) }

    /**
     * Adds a listener which will call the supplied [callback] anytime the value at [key] changes. A [SettingsListener]
     * reference is returned which can be used to halt callbacks by calling [deactivate()][SettingsListener.deactivate].
     * A strong reference should be held to the `SettingsListener` returned by this method in order to avoid it being
     * garbage-collected.
     */
    @Suppress("DEPRECATION")
    public fun addLongOrNullListener(
        key: String,
        callback: (Long?) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getLongOrNull(key)) }

    /**
     * Adds a listener which will call the supplied [callback] anytime the value at [key] changes. A [SettingsListener]
     * reference is returned which can be used to halt callbacks by calling [deactivate()][SettingsListener.deactivate].
     * A strong reference should be held to the `SettingsListener` returned by this method in order to avoid it being
     * garbage-collected.
     */
    @Suppress("DEPRECATION")
    public fun addStringOrNullListener(
        key: String,
        callback: (String?) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getStringOrNull(key)) }

    /**
     * Adds a listener which will call the supplied [callback] anytime the value at [key] changes. A [SettingsListener]
     * reference is returned which can be used to halt callbacks by calling [deactivate()][SettingsListener.deactivate].
     * A strong reference should be held to the `SettingsListener` returned by this method in order to avoid it being
     * garbage-collected.
     */
    @Suppress("DEPRECATION")
    public fun addFloatOrNullListener(
        key: String,
        callback: (Float?) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getFloatOrNull(key)) }

    /**
     * Adds a listener which will call the supplied [callback] anytime the value at [key] changes. A [SettingsListener]
     * reference is returned which can be used to halt callbacks by calling [deactivate()][SettingsListener.deactivate].
     * A strong reference should be held to the `SettingsListener` returned by this method in order to avoid it being
     * garbage-collected.
     */
    @Suppress("DEPRECATION")
    public fun addDoubleOrNullListener(
        key: String,
        callback: (Double?) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getDoubleOrNull(key)) }

    /**
     * Adds a listener which will call the supplied [callback] anytime the value at [key] changes. A [SettingsListener]
     * reference is returned which can be used to halt callbacks by calling [deactivate()][SettingsListener.deactivate].
     * A strong reference should be held to the `SettingsListener` returned by this method in order to avoid it being
     * garbage-collected.
     */
    @Suppress("DEPRECATION")
    public fun addBooleanOrNullListener(
        key: String,
        callback: (Boolean?) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getBooleanOrNull(key)) }

    /**
     * Unsubscribes the [listener] from receiving updates to the value at the key it monitors
     */
    @Deprecated(
        message = "Use SettingsListener.deactivate() instead",
        replaceWith = ReplaceWith("listener.deactivate()"),
        level = DeprecationLevel.HIDDEN
    )
    public fun removeListener(listener: SettingsListener): Unit = listener.deactivate()

}

@Deprecated(
    "ListenableSettings has been renamed ObservableSettings",
    replaceWith = ReplaceWith("ObservableSettings", "com.russhwolf.settings.ObservableSettings"),
    level = DeprecationLevel.HIDDEN
)
@Suppress("UNUSED", "KDocMissingDocumentation")
public typealias ListenableSettings = ObservableSettings

/**
 * A handle to a listener instance returned by [ObservableSettings.addListener] so it can be deactivated as needed
 */
public interface SettingsListener {
    /**
     * Unsubscribes this [SettingsListener] from receiving updates to the value at the key it monitors
     */
    public fun deactivate()
}
