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

import platform.Foundation.NSUserDefaults
import platform.Foundation.NSBundle
import platform.Foundation.*

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
 * On the iOS platform, this class can be created by passing a [NSUserDefaults] instance which will be used as a
 * delegate, or via a [Factory].
 */
actual class PlatformSettings public constructor(private val delegate: NSUserDefaults) : Settings {

    /**
     * A factory that can produce [Settings] instances.
     *
     * This class can only be instantiated via a platform-specific constructor. It's purpose is so that `Settings`
     * objects can be created in common code, so that the only platform-specific behavior necessary in order to use
     * multiple `Settings` objects is the one-time creation of a single `Factory`.
     *
     * On the iOS platform, this class creates `Settings` objects backed by [NSUserDefaults].
     */
    actual class Factory() : Settings.Factory {

        /**
         * Creates a [Settings] object associated with the provided [name].
         *
         * Multiple `Settings` instances created with the same `name` parameter will be backed by the same persistent
         * data, while distinct `name`s will use different data. If `name` is `null` then a platform-specific default
         * will be used.
         *
         * On the iOS platform, this is implemented by calling [NSUserDefaults.init] and passing [name]. If `name` is
         * `null` then [NSUserDefaults.standardUserDefaults] will be used instead.
         */
        actual override fun create(name: String?): Settings {
            val delegate = if (name == null) NSUserDefaults.standardUserDefaults else NSUserDefaults(suiteName = name)
            return PlatformSettings(delegate)
        }
    }

    private val observerCache = mutableMapOf<String, Any>()
    private val valueCache = mutableMapOf<String, Any>()

    actual override fun addListener(key: String, listener: () -> Unit) {
        removeListener(key)

        val block = { _: NSNotification? ->
            /*
             We'll get called here on any update to the underlying NSUserDefaults delegate. We use a cache to determine
             whether the value at this listener's key changed before calling the user-supplied callback.
             */
            val prev = valueCache[key]
            val current = delegate.objectForKey(key)
            if (prev != current) {
                listener()
                current?.let { valueCache[key] = it }
            }
        }
        delegate.objectForKey(key)?.let { valueCache[key] = it }
        val observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = NSUserDefaultsDidChangeNotification,
            `object` = delegate,
            queue = null,
            usingBlock = block
        )
        observerCache[key] = observer
    }

    actual override fun removeListener(key: String) {
        observerCache.remove(key)?.let { NSNotificationCenter.defaultCenter.removeObserver(it) }
        valueCache.remove(key)
    }

    /**
     * Clears all values stored in this [Settings] instance.
     */
    actual override fun clear(): Unit {
        for (key in delegate.dictionaryRepresentation().keys) {
            remove(key as String)
        }
    }

    /**
     * Removes the value stored at [key].
     */
    actual override fun remove(key: String): Unit = delegate.removeObjectForKey(key)

    /**
     * Returns `true` if there is a value stored at [key], or `false` otherwise.
     */
    actual override fun hasKey(key: String): Boolean = delegate.objectForKey(key) != null

    /**
     * Stores the `Int` [value] at [key].
     */
    actual override fun putInt(key: String, value: Int): Unit = delegate.setInteger(value.toLong(), key)

    /**
     * Returns the `Int` value stored at [key], or [defaultValue] if no value was stored. If a value of a different
     * type was stored at `key`, the behavior is not defined.
     */
    actual override fun getInt(key: String, defaultValue: Int): Int =
        if (hasKey(key)) delegate.integerForKey(key).toInt() else defaultValue

    /**
     * Stores the `Long` [value] at [key].
     */
    actual override fun putLong(key: String, value: Long) = delegate.setInteger(value, key)

    /**
     * Returns the `Long` value stored at [key], or [defaultValue] if no value was stored. If a value of a different
     * type was stored at `key`, the behavior is not defined.
     */
    actual override fun getLong(key: String, defaultValue: Long): Long =
        if (hasKey(key)) delegate.integerForKey(key) else defaultValue

    /**
     * Stores the `String` [value] at [key].
     */
    actual override fun putString(key: String, value: String) = delegate.setObject(value, key)

    /**
     * Returns the `String` value stored at [key], or [defaultValue] if no value was stored. If a value of a different
     * type was stored at `key`, the behavior is not defined.
     */
    actual override fun getString(key: String, defaultValue: String): String =
        delegate.stringForKey(key) ?: defaultValue

    /**
     * Stores the `Float` [value] at [key].
     */
    actual override fun putFloat(key: String, value: Float) = delegate.setFloat(value, key)

    /**
     * Returns the `Float` value stored at [key], or [defaultValue] if no value was stored. If a value of a different
     * type was stored at `key`, the behavior is not defined.
     */
    actual override fun getFloat(key: String, defaultValue: Float): Float =
        if (hasKey(key)) delegate.floatForKey(key) else defaultValue

    /**
     * Stores the `Double` [value] at [key].
     */
    actual override fun putDouble(key: String, value: Double) = delegate.setDouble(value, key)

    /**
     * Returns the `Double` value stored at [key], or [defaultValue] if no value was stored. If a value of a different
     * type was stored at `key`, the behavior is not defined.
     */
    actual override fun getDouble(key: String, defaultValue: Double): Double =
        if (hasKey(key)) delegate.doubleForKey(key) else defaultValue

    /**
     * Stores the `Boolean` [value] at [key].
     */
    actual override fun putBoolean(key: String, value: Boolean) = delegate.setBool(value, key)

    /**
     * Returns the `Boolean` value stored at [key], or [defaultValue] if no value was stored. If a value of a different
     * type was stored at `key`, the behavior is not defined.
     */
    actual override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        if (hasKey(key)) delegate.boolForKey(key) else defaultValue
}
