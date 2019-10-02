/*
 * Copyright 2019 Russell Wolf
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

import com.russhwolf.settings.JvmPreferencesSettings.Factory
import java.util.Properties
import java.util.prefs.PreferenceChangeEvent
import java.util.prefs.PreferenceChangeListener
import java.util.prefs.Preferences

@Deprecated(
    message = "Use JvmPreferencesSettings or JvmPropertiesSettings to disambiguate usage.",
    replaceWith = ReplaceWith("JvmPreferencesSettings", "com.russhwolf.settings.JvmPreferencesSettings")
)
@ExperimentalJvm
@Suppress("UNUSED", "KDocMissingDocumentation")
typealias JvmSettings = JvmPreferencesSettings

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
 * On the JVM platform, this class can be created by passing a [Properties] instance which will be used as a delegate.
 * Since the [Properties] doesn't perform the serialization and writing of the data by itself, a callback [onModify] can
 * be added which will allow serialization or other work to occur after any write operation is performed.
 *
 * Unlike the implementations on Android and iOS, `JvmPropertiesSettings` does not include a [Settings.Factory] because
 * the `Properties` API does not provide a natural way to create multiple named instances.
 *
 * This class is experimental as marked by the [ExperimentalJvm] annotation.
 *
 * The experimental listener APIs are not implemented in `JvmPropertiesSettings`.
 */
@ExperimentalJvm
public class JvmPropertiesSettings public constructor(
    private val delegate: Properties,
    private val onModify: (Properties) -> Unit = {}
) : Settings {

    public override fun clear(): Unit {
        delegate.clear()
        onModify(delegate)
    }

    public override fun remove(key: String) {
        delegate.remove(key)
        onModify(delegate)
    }

    public override fun hasKey(key: String): Boolean = delegate[key] != null

    public override fun putInt(key: String, value: Int) {
        delegate.setProperty(key, value.toString())
        onModify(delegate)
    }

    public override fun getInt(key: String, defaultValue: Int): Int =
        delegate.getProperty(key)?.toInt() ?: defaultValue

    public override fun getIntOrNull(key: String): Int? =
        delegate.getProperty(key)?.toInt()

    public override fun putLong(key: String, value: Long) {
        delegate.setProperty(key, value.toString())
        onModify(delegate)
    }

    public override fun getLong(key: String, defaultValue: Long): Long =
        delegate.getProperty(key)?.toLong() ?: defaultValue

    public override fun getLongOrNull(key: String): Long? =
        delegate.getProperty(key)?.toLong()

    public override fun putString(key: String, value: String) {
        delegate.setProperty(key, value)
        onModify(delegate)
    }

    public override fun getString(key: String, defaultValue: String): String =
        delegate.getProperty(key) ?: defaultValue

    public override fun getStringOrNull(key: String): String? =
        delegate.getProperty(key)

    public override fun putFloat(key: String, value: Float) {
        delegate.setProperty(key, value.toString())
        onModify(delegate)
    }

    public override fun getFloat(key: String, defaultValue: Float): Float =
        delegate.getProperty(key)?.toFloat() ?: defaultValue

    public override fun getFloatOrNull(key: String): Float? =
        delegate.getProperty(key)?.toFloat()

    public override fun putDouble(key: String, value: Double) {
        delegate.setProperty(key, value.toString())
        onModify(delegate)
    }

    public override fun getDouble(key: String, defaultValue: Double): Double =
        delegate.getProperty(key)?.toDouble() ?: defaultValue

    public override fun getDoubleOrNull(key: String): Double? =
        delegate.getProperty(key)?.toDouble()

    public override fun putBoolean(key: String, value: Boolean) {
        delegate.setProperty(key, value.toString())
        onModify(delegate)
    }

    public override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        delegate.getProperty(key)?.toBoolean() ?: defaultValue

    public override fun getBooleanOrNull(key: String): Boolean? =
        delegate.getProperty(key)?.toBoolean()
}

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
 * or via a [Factory]
 *
 * This class is experimental as marked by the [ExperimentalJvm] annotation.
 */
@ExperimentalJvm
@UseExperimental(ExperimentalListener::class)
public class JvmPreferencesSettings public constructor(
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
    class Factory(private val rootPreferences: Preferences = Preferences.userRoot()) : Settings.Factory {
        public override fun create(name: String?): Settings {
            val preferences = if (name != null) rootPreferences.node(name) else rootPreferences
            return JvmPreferencesSettings(preferences)
        }
    }

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

    @ExperimentalListener
    public override fun addListener(key: String, callback: () -> Unit): SettingsListener {
        val cache = Listener.Cache(delegate.get(key, null))
        val callerThread = Thread.currentThread()

        val prefsListener =
            PreferenceChangeListener { event: PreferenceChangeEvent ->
                val updatedKey = event.key
                if (updatedKey != key) return@PreferenceChangeListener

                /*
                 We'll get called here on any update to the underlying Preferences delegate. We use a cache to determine
                 whether the value at this listener's key changed before calling the user-supplied callback.
                 */
                val prev = cache.value
                val current = event.newValue
                if (prev != current) {
                    cache.value = current
                    /*
                    PreferenceChangeListeners are called from a different thread. To match behavior of other platforms,
                    we try to switch back to the original caller thread to execute the callback.
                     */
                    val callbackThread = if (callerThread.isAlive) callerThread else Thread.currentThread()
                    callbackThread.run { callback() }
                }
            }
        delegate.addPreferenceChangeListener(prefsListener)
        return Listener(prefsListener)
    }

    @ExperimentalListener
    public override fun removeListener(listener: SettingsListener) {
        val platformListener = listener as? Listener ?: return
        val listenerDelegate = platformListener.delegate
        try {
            delegate.removePreferenceChangeListener(listenerDelegate)
        } catch (e: IllegalArgumentException) {
            // Ignore error due to unregistered listener to match behavior of other platforms
        }
    }

    /**
     * A handle to a listener instance created in [addListener] so it can be passed to [removeListener]
     *
     * On the JVM platform, this is a wrapper around [PreferenceChangeListener].
     */
    @ExperimentalListener
    public class Listener internal constructor(
        internal val delegate: PreferenceChangeListener
    ) : SettingsListener {
        internal class Cache(var value: Any?)
    }
}
