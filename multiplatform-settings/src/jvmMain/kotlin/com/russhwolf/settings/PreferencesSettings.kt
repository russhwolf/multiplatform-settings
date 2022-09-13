/*
 * Copyright 2022 Russell Wolf
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

import com.russhwolf.settings.PreferencesSettings.Factory
import java.util.prefs.PreferenceChangeEvent
import java.util.prefs.PreferenceChangeListener
import java.util.prefs.Preferences

@Deprecated(
    message = "JvmPreferencesSettings has been renamed to PreferencesSettings",
    replaceWith = ReplaceWith("PreferencesSettings", "com.russhwolf.settings.PreferencesSettings"),
    level = DeprecationLevel.WARNING
)
@Suppress("KDocMissingDocumentation")
public typealias JvmPreferencesSettings = PreferencesSettings

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
 * On the JVM platform, this class can be created by passing a [Preferences] instance which will be used as a delegate,
 * or via a [Factory].
 *
 * Note that listener callbacks passed to [addListener] will run on a background thread in this implementation
 *
 * This class is experimental as marked by the [ExperimentalSettingsImplementation] annotation.
 */
public class PreferencesSettings public constructor(
    private val delegate: Preferences
) : ObservableSettings {

    /**
     * A factory that can produce [Settings] instances.
     *
     * This class can only be instantiated via a platform-specific constructor. It's purpose is so that `Settings`
     * objects can be created in common code, so that the only platform-specific behavior necessary in order to use
     * multiple `Settings` objects is the one-time creation of a single `Factory`.
     *
     * On the JVM platform, this class creates `Settings` objects backed by [Preferences].
     */
    public class Factory(private val rootPreferences: Preferences = Preferences.userRoot()) : Settings.Factory {
        public override fun create(name: String?): ObservableSettings {
            val preferences = if (name != null) rootPreferences.node(name) else rootPreferences
            return PreferencesSettings(preferences)
        }
    }

    public override val keys: Set<String> get() = delegate.keys().toSet()
    public override val size: Int get() = delegate.keys().size

    public override fun clear(): Unit = delegate.clear()

    public override fun remove(key: String): Unit = delegate.remove(key)

    public override fun hasKey(key: String): Boolean = key in delegate.keys()

    public override fun putInt(key: String, value: Int): Unit = delegate.putInt(key, value)

    public override fun getInt(key: String, defaultValue: Int): Int = delegate.getInt(key, defaultValue)

    public override fun getIntOrNull(key: String): Int? =
        if (key in delegate.keys()) delegate.getInt(key, 0) else null

    public override fun putLong(key: String, value: Long): Unit = delegate.putLong(key, value)

    public override fun getLong(key: String, defaultValue: Long): Long = delegate.getLong(key, defaultValue)

    public override fun getLongOrNull(key: String): Long? =
        if (key in delegate.keys()) delegate.getLong(key, 0L) else null

    public override fun putString(key: String, value: String): Unit = delegate.put(key, value)

    public override fun getString(key: String, defaultValue: String): String = delegate.get(key, defaultValue)

    public override fun getStringOrNull(key: String): String? =
        if (key in delegate.keys()) delegate.get(key, "") else null

    public override fun putFloat(key: String, value: Float): Unit = delegate.putFloat(key, value)

    public override fun getFloat(key: String, defaultValue: Float): Float = delegate.getFloat(key, defaultValue)

    public override fun getFloatOrNull(key: String): Float? =
        if (key in delegate.keys()) delegate.getFloat(key, 0f) else null

    public override fun putDouble(key: String, value: Double): Unit = delegate.putDouble(key, value)

    public override fun getDouble(key: String, defaultValue: Double): Double = delegate.getDouble(key, defaultValue)

    public override fun getDoubleOrNull(key: String): Double? =
        if (key in delegate.keys()) delegate.getDouble(key, 0.0) else null

    public override fun putBoolean(key: String, value: Boolean): Unit = delegate.putBoolean(key, value)

    public override fun getBoolean(key: String, defaultValue: Boolean): Boolean = delegate.getBoolean(key, defaultValue)

    public override fun getBooleanOrNull(key: String): Boolean? =
        if (key in delegate.keys()) delegate.getBoolean(key, false) else null

    @Deprecated(
        "Use typed listener methods instead",
        level = DeprecationLevel.WARNING
    )
    public override fun addListener(key: String, callback: () -> Unit): SettingsListener {
        var prev = delegate.get(key, null)

        val prefsListener =
            PreferenceChangeListener { event: PreferenceChangeEvent ->
                val updatedKey = event.key
                if (updatedKey != key) return@PreferenceChangeListener

                /*
                 We'll get called here on any update to the underlying Preferences delegate. We use a cache to determine
                 whether the value at this listener's key changed before calling the user-supplied callback.
                 */
                val current = event.newValue
                if (prev != current) {
                    callback()
                    prev = current
                }
            }
        delegate.addPreferenceChangeListener(prefsListener)
        return Listener(delegate, prefsListener)
    }

    /**
     * A handle to a listener instance created in [addListener] so it can be passed to [removeListener]
     *
     * On the JVM platform, this is a wrapper around [PreferenceChangeListener].
     */
    public class Listener internal constructor(
        private val preferences: Preferences,
        private val listener: PreferenceChangeListener
    ) : SettingsListener {
        public override fun deactivate() {
            try {
                preferences.removePreferenceChangeListener(listener)
            } catch (e: IllegalArgumentException) {
                // Ignore error due to unregistered listener to match behavior of other platforms
            }
        }
    }
}
