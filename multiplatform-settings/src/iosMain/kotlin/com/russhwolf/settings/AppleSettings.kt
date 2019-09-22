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

import com.russhwolf.settings.AppleSettings.Factory
import kotlinx.cinterop.convert
import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSUserDefaults
import platform.Foundation.NSUserDefaultsDidChangeNotification
import platform.darwin.NSObjectProtocol

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
 * On the iOS and macOS platforms, this class can be created by passing a [NSUserDefaults] instance which will be used as a
 * delegate, or via a [Factory].
 */
@UseExperimental(ExperimentalListener::class)
public class AppleSettings public constructor(private val delegate: NSUserDefaults) : ObservableSettings {

    /**
     * A factory that can produce [Settings] instances.
     *
     * This class can only be instantiated via a platform-specific constructor. It's purpose is so that `Settings`
     * objects can be created in common code, so that the only platform-specific behavior necessary in order to use
     * multiple `Settings` objects is the one-time creation of a single `Factory`.
     *
     * On the iOS and macOS platforms, this class creates `Settings` objects backed by [NSUserDefaults].
     */
    public class Factory : Settings.Factory {

        /**
         * Creates a [Settings] object associated with the provided [name].
         *
         * Multiple `Settings` instances created with the same `name` parameter will be backed by the same persistent
         * data, while distinct `name`s will use different data. If `name` is `null` then a platform-specific default
         * will be used.
         *
         * On the iOS and macOS platforms, this is implemented by calling [NSUserDefaults.init] and passing [name]. If `name` is
         * `null` then [NSUserDefaults.standardUserDefaults] will be used instead.
         */
        public override fun create(name: String?): Settings {
            val delegate = if (name == null) NSUserDefaults.standardUserDefaults else NSUserDefaults(suiteName = name)
            return AppleSettings(delegate)
        }
    }

    public override fun clear() {
        for (key in delegate.dictionaryRepresentation().keys) {
            remove(key as String)
        }
    }

    public override fun remove(key: String): Unit = delegate.removeObjectForKey(key)

    public override fun hasKey(key: String): Boolean = delegate.objectForKey(key) != null

    public override fun putInt(key: String, value: Int): Unit = delegate.setInteger(value.convert(), key)

    public override fun getInt(key: String, defaultValue: Int): Int =
        if (hasKey(key)) delegate.integerForKey(key).convert() else defaultValue

    public override fun getIntOrNull(key: String): Int? =
        if (hasKey(key)) delegate.integerForKey(key).convert() else null

    public override fun putLong(key: String, value: Long): Unit = delegate.setInteger(value.convert(), key)

    public override fun getLong(key: String, defaultValue: Long): Long =
        if (hasKey(key)) delegate.integerForKey(key).convert() else defaultValue

    public override fun getLongOrNull(key: String): Long? =
        if (hasKey(key)) delegate.integerForKey(key).convert() else null

    public override fun putString(key: String, value: String): Unit = delegate.setObject(value, key)

    public override fun getString(key: String, defaultValue: String): String =
        delegate.stringForKey(key) ?: defaultValue

    public override fun getStringOrNull(key: String): String? = delegate.stringForKey(key)

    public override fun putFloat(key: String, value: Float): Unit = delegate.setFloat(value, key)

    public override fun getFloat(key: String, defaultValue: Float): Float =
        if (hasKey(key)) delegate.floatForKey(key) else defaultValue

    public override fun getFloatOrNull(key: String): Float? =
        if (hasKey(key)) delegate.floatForKey(key) else null

    public override fun putDouble(key: String, value: Double): Unit = delegate.setDouble(value, key)

    public override fun getDouble(key: String, defaultValue: Double): Double =
        if (hasKey(key)) delegate.doubleForKey(key) else defaultValue

    public override fun getDoubleOrNull(key: String): Double? =
        if (hasKey(key)) delegate.doubleForKey(key) else null

    public override fun putBoolean(key: String, value: Boolean): Unit = delegate.setBool(value, key)

    public override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        if (hasKey(key)) delegate.boolForKey(key) else defaultValue

    public override fun getBooleanOrNull(key: String): Boolean? =
        if (hasKey(key)) delegate.boolForKey(key) else null

    @ExperimentalListener
    public override fun addListener(key: String, callback: () -> Unit): SettingsListener {
        val cache = Listener.Cache(delegate.objectForKey(key))

        val block = { _: NSNotification? ->
            /*
             We'll get called here on any update to the underlying NSUserDefaults delegate. We use a cache to determine
             whether the value at this listener's key changed before calling the user-supplied callback.
             */
            val prev = cache.value
            val current = delegate.objectForKey(key)
            if (prev != current) {
                callback()
                cache.value = current
            }
        }
        val observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = NSUserDefaultsDidChangeNotification,
            `object` = delegate,
            queue = null,
            usingBlock = block
        )
        return Listener(observer)
    }

    @ExperimentalListener
    public override fun removeListener(listener: SettingsListener) {
        val platformListener = listener as? Listener ?: return
        val listenerDelegate = platformListener.delegate
        NSNotificationCenter.defaultCenter.removeObserver(listenerDelegate)
    }

    /**
     * A handle to a listener instance created in [addListener] so it can be passed to [removeListener]
     *
     * On the iOS and macOS platforms, this is a wrapper around the object returned by [NSNotificationCenter.addObserverForName]
     */
    @ExperimentalListener
    public class Listener internal constructor(internal val delegate: NSObjectProtocol) : SettingsListener {
        internal class Cache(var value: Any?)
    }
}
