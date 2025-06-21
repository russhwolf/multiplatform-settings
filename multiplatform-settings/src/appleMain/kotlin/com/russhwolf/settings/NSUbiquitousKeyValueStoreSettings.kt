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

import platform.Foundation.NSNotification
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSUbiquitousKeyValueStore
import platform.Foundation.NSUbiquitousKeyValueStoreDidChangeExternallyNotification
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
 * On the iOS and macOS platforms, this class can be created by passing a [NSUbiquitousKeyValueStore] instance which will be used as a
 * delegate, or via a [Factory].
 */
public class NSUbiquitousKeyValueStoreSettings public constructor(
    private val delegate: NSUbiquitousKeyValueStore = NSUbiquitousKeyValueStore.defaultStore
) : ObservableSettings {

    @Suppress("UNCHECKED_CAST")
    public override val keys: Set<String> get() = delegate.dictionaryRepresentation().keys as Set<String>
    public override val size: Int get() = delegate.dictionaryRepresentation().keys.count()

    public override fun clear() {
        for (key in delegate.dictionaryRepresentation().keys) {
            remove(key as String)
        }
    }

    public override fun remove(key: String): Unit = delegate.removeObjectForKey(key)

    public override fun hasKey(key: String): Boolean = delegate.objectForKey(key) != null

    public override fun putInt(key: String, value: Int): Unit = delegate.setInt(value, key)

    public override fun getInt(key: String, defaultValue: Int): Int =
        if (hasKey(key)) delegate.intForKey(key) else defaultValue

    public override fun getIntOrNull(key: String): Int? =
        if (hasKey(key)) delegate.intForKey(key) else null

    public override fun putLong(key: String, value: Long): Unit = delegate.setLong(value, key)

    public override fun getLong(key: String, defaultValue: Long): Long =
        if (hasKey(key)) delegate.longForKey(key) else defaultValue

    public override fun getLongOrNull(key: String): Long? =
        if (hasKey(key)) delegate.longForKey(key) else null

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

    public override fun addIntListener(
        key: String,
        defaultValue: Int,
        callback: (Int) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getInt(key, defaultValue)) }

    public override fun addLongListener(
        key: String,
        defaultValue: Long,
        callback: (Long) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getLong(key, defaultValue)) }

    public override fun addStringListener(
        key: String,
        defaultValue: String,
        callback: (String) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getString(key, defaultValue)) }

    public override fun addFloatListener(
        key: String,
        defaultValue: Float,
        callback: (Float) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getFloat(key, defaultValue)) }

    public override fun addDoubleListener(
        key: String,
        defaultValue: Double,
        callback: (Double) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getDouble(key, defaultValue)) }

    public override fun addBooleanListener(
        key: String,
        defaultValue: Boolean,
        callback: (Boolean) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getBoolean(key, defaultValue)) }

    public override fun addIntOrNullListener(
        key: String,
        callback: (Int?) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getIntOrNull(key)) }

    public override fun addLongOrNullListener(
        key: String,
        callback: (Long?) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getLongOrNull(key)) }

    public override fun addStringOrNullListener(
        key: String,
        callback: (String?) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getStringOrNull(key)) }

    public override fun addFloatOrNullListener(
        key: String,
        callback: (Float?) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getFloatOrNull(key)) }

    public override fun addDoubleOrNullListener(
        key: String,
        callback: (Double?) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getDoubleOrNull(key)) }

    public override fun addBooleanOrNullListener(
        key: String,
        callback: (Boolean?) -> Unit
    ): SettingsListener =
        addListener(key) { callback(getBooleanOrNull(key)) }

    private fun addListener(key: String, callback: () -> Unit): SettingsListener {
        val block = { _: NSNotification? ->
            callback.invoke()
        }
        val observer = NSNotificationCenter.defaultCenter.addObserverForName(
            name = NSUbiquitousKeyValueStoreDidChangeExternallyNotification,
            `object` = delegate,
            queue = null,
            usingBlock = block
        )
        return Listener(observer)
    }

    /**
     * A handle to a listener instance returned by one of the addListener methods of [ObservableSettings], so it can be
     * deactivated as needed.
     *
     * On the iOS and macOS platforms, this is a wrapper around the object returned by [NSNotificationCenter.addObserverForName]
     */
    public class Listener internal constructor(
        private val delegate: NSObjectProtocol
    ) : SettingsListener {
        public override fun deactivate() {
            NSNotificationCenter.defaultCenter.removeObserver(delegate)
        }
    }
}

internal expect fun NSUbiquitousKeyValueStore.intForKey(defaultName: String): Int
internal expect fun NSUbiquitousKeyValueStore.setInt(value: Int, forKey: String)
internal expect fun NSUbiquitousKeyValueStore.longForKey(defaultName: String): Long
internal expect fun NSUbiquitousKeyValueStore.setLong(value: Long, forKey: String)
internal expect fun NSUbiquitousKeyValueStore.setFloat(value: Float, forKey: String)
internal expect fun NSUbiquitousKeyValueStore.floatForKey(defaultName: String): Float
