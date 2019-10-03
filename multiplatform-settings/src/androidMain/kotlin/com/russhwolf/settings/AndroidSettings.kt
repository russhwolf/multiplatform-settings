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

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import com.russhwolf.settings.AndroidSettings.Factory

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
 *
 * On the Android platform, this class can be created by passing a [SharedPreferences] instance which will be used as a
 * delegate. Thus two `Settings` instances created using the same [delegate] will be backed by the same data.
 */
@UseExperimental(ExperimentalListener::class)
public class AndroidSettings public constructor(private val delegate: SharedPreferences) : ObservableSettings {

    /**
     * A factory that can produce [Settings] instances.
     *
     * This class can only be instantiated via a platform-specific constructor. It's purpose is so that `Settings`
     * objects can be created in common code, so that the only platform-specific behavior necessary in order to use
     * multiple `Settings` objects is the one-time creation of a single `Factory`.
     *
     * On the Android platform, this class creates `Settings` objects backed by [SharedPreferences]. It  can only be
     * created by supplying a [Context] instance. The `Factory` will hold onto a reference to the
     * [applicationContext][Context.getApplicationContext] property of the supplied `context` and will use that to
     * create [SharedPreferences] objects.
     */
    public class Factory(context: Context) : Settings.Factory {
        private val appContext = context.applicationContext

        /**
         * Creates a [Settings] object associated with the provided [name].
         *
         * Multiple `Settings` instances created with the same `name` parameter will be backed by the same persistent
         * data, while distinct `name`s will use different data. If `name` is `null` then a platform-specific default
         * will be used.
         *
         * On the Android platform, this is implemented by calling [Context.getSharedPreferences] and passing [name]. If
         * `name` is `null` then a default package-specific name will be used instead.
         */
        public override fun create(name: String?): Settings {
            // For null name, match the behavior of PreferenceManager.getDefaultSharedPreferences()
            val preferencesName = name ?: "${appContext.packageName}_preferences"
            val delegate = appContext.getSharedPreferences(preferencesName, MODE_PRIVATE)
            return AndroidSettings(delegate)
        }
    }

    public override fun clear() {
        // Note: we call remove() on all keys instead of calling clear() in order to match listener behavior to iOS
        // See issue #9
        delegate.edit().apply {
            for (key in delegate.all.keys) {
                remove(key)
            }
        }.apply()
    }

    public override fun remove(key: String): Unit = delegate.edit().remove(key).apply()

    public override fun hasKey(key: String): Boolean = delegate.contains(key)

    public override fun putInt(key: String, value: Int): Unit = delegate.edit().putInt(key, value).apply()

    public override fun getInt(key: String, defaultValue: Int): Int = delegate.getInt(key, defaultValue)

    public override fun getIntOrNull(key: String): Int? =
        if (delegate.contains(key)) delegate.getInt(key, 0) else null

    public override fun putLong(key: String, value: Long): Unit = delegate.edit().putLong(key, value).apply()

    public override fun getLong(key: String, defaultValue: Long): Long = delegate.getLong(key, defaultValue)

    public override fun getLongOrNull(key: String): Long? =
        if (delegate.contains(key)) delegate.getLong(key, 0L) else null

    public override fun putString(key: String, value: String): Unit =
        delegate.edit().putString(key, value).apply()

    public override fun getString(key: String, defaultValue: String): String =
        delegate.getString(key, defaultValue) ?: defaultValue

    public override fun getStringOrNull(key: String): String? =
        if (delegate.contains(key)) delegate.getString(key, "") else null

    public override fun putFloat(key: String, value: Float): Unit = delegate.edit().putFloat(key, value).apply()

    public override fun getFloat(key: String, defaultValue: Float): Float = delegate.getFloat(key, defaultValue)

    public override fun getFloatOrNull(key: String): Float? =
        if (delegate.contains(key)) delegate.getFloat(key, 0f) else null

    public override fun putDouble(key: String, value: Double): Unit =
        delegate.edit().putLong(key, value.toRawBits()).apply()

    public override fun getDouble(key: String, defaultValue: Double): Double =
        Double.fromBits(delegate.getLong(key, defaultValue.toRawBits()))

    public override fun getDoubleOrNull(key: String): Double? =
        if (delegate.contains(key)) Double.fromBits(delegate.getLong(key, 0.0.toRawBits())) else null

    public override fun putBoolean(key: String, value: Boolean): Unit =
        delegate.edit().putBoolean(key, value).apply()

    public override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        delegate.getBoolean(key, defaultValue)

    public override fun getBooleanOrNull(key: String): Boolean? =
        if (delegate.contains(key)) delegate.getBoolean(key, false) else null

    @ExperimentalListener
    public override fun addListener(key: String, callback: () -> Unit): SettingsListener {
        val cache = Listener.Cache(delegate.all[key])

        val prefsListener =
            SharedPreferences.OnSharedPreferenceChangeListener { _: SharedPreferences, updatedKey: String ->
                if (updatedKey != key) return@OnSharedPreferenceChangeListener

                /*
                 According to the OnSharedPreferenceChangeListener contract, we might get called for an update even
                 if the value didn't change. We hold a cache to ensure that the user-supplied callback only updates on
                 changes, in order to ensure that we match iOS behavior
                 */
                val prev = cache.value
                val current = delegate.all[key]
                if (prev != current) {
                    callback()
                    cache.value = current
                }
            }
        delegate.registerOnSharedPreferenceChangeListener(prefsListener)
        return Listener(prefsListener)
    }

    @ExperimentalListener
    public override fun removeListener(listener: SettingsListener) {
        val platformListener = listener as? Listener ?: return
        val listenerDelegate = platformListener.delegate
        delegate.unregisterOnSharedPreferenceChangeListener(listenerDelegate)
    }

    /**
     * A handle to a listener instance created in [addListener] so it can be passed to [removeListener]
     *
     * On the Android platform, this is a wrapper around [SharedPreferences.OnSharedPreferenceChangeListener].
     */
    @ExperimentalListener
    public class Listener internal constructor(
        internal val delegate: SharedPreferences.OnSharedPreferenceChangeListener
    ) : SettingsListener {
        internal class Cache(var value: Any?)
    }
}
