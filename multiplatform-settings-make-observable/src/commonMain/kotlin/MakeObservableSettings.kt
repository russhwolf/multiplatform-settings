/*
 * Copyright 2024 Russell Wolf
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

package com.russhwolf.settings.observable

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SettingsListener
import com.russhwolf.settings.get
import kotlin.jvm.JvmInline

/**
 * Returns an [ObservableSettings] instance based on this [Settings].
 *
 * WARNING: When this function is used, changes to the underlying storage will not trigger callbacks unless they are
 * made through the same `ObservableSettings` instance being observed.
 */
@ExperimentalSettingsApi
public fun Settings.makeObservable(): ObservableSettings = MakeObservableSettings(this)

/**
 * A wrapper around provided [Settings] instance. It only ensures the callback if the
 * settings are modified through the member functions of [MakeObservableSettings].
 */
private class MakeObservableSettings(
    private val delegate: Settings,
) : Settings by delegate, ObservableSettings {

    private val listenerMap = mutableMapOf<String, MutableSet<() -> Unit>>()

    override fun remove(key: String) {
        delegate.remove(key)
        invokeListeners(key)
    }

    override fun clear() {
        delegate.clear()
        invokeAllListeners()
    }

    override fun putInt(key: String, value: Int) {
        delegate.putInt(key, value)
        invokeListeners(key)
    }

    override fun putLong(key: String, value: Long) {
        delegate.putLong(key, value)
        invokeListeners(key)
    }

    override fun putString(key: String, value: String) {
        delegate.putString(key, value)
        invokeListeners(key)
    }

    override fun putFloat(key: String, value: Float) {
        delegate.putFloat(key, value)
        invokeListeners(key)
    }

    override fun putDouble(key: String, value: Double) {
        delegate.putDouble(key, value)
        invokeListeners(key)
    }

    override fun putBoolean(key: String, value: Boolean) {
        delegate.putBoolean(key, value)
        invokeListeners(key)
    }

    override fun addIntListener(
        key: String,
        defaultValue: Int,
        callback: (Int) -> Unit
    ): SettingsListener = addListener<Int>(key) {
        callback(getInt(key, defaultValue))
    }


    override fun addLongListener(
        key: String,
        defaultValue: Long,
        callback: (Long) -> Unit
    ): SettingsListener = addListener<Long>(key) {
        callback(getLong(key, defaultValue))
    }

    override fun addStringListener(
        key: String,
        defaultValue: String,
        callback: (String) -> Unit
    ): SettingsListener = addListener<String>(key) {
        callback(getString(key, defaultValue))
    }

    override fun addFloatListener(
        key: String,
        defaultValue: Float,
        callback: (Float) -> Unit
    ): SettingsListener = addListener<Float>(key) {
        callback(getFloat(key, defaultValue))
    }

    override fun addDoubleListener(
        key: String,
        defaultValue: Double,
        callback: (Double) -> Unit
    ): SettingsListener = addListener<Double>(key) {
        callback(getDouble(key, defaultValue))
    }

    override fun addBooleanListener(
        key: String,
        defaultValue: Boolean,
        callback: (Boolean) -> Unit
    ): SettingsListener = addListener<Boolean>(key) {
        callback(getBoolean(key, defaultValue))
    }

    override fun addIntOrNullListener(
        key: String, callback: (Int?) -> Unit
    ): SettingsListener = addListener<Int>(key) {
        callback(getIntOrNull(key))
    }

    override fun addLongOrNullListener(
        key: String,
        callback: (Long?) -> Unit
    ): SettingsListener = addListener<Long>(key) {
        callback(getLongOrNull(key))
    }

    override fun addStringOrNullListener(
        key: String,
        callback: (String?) -> Unit
    ): SettingsListener = addListener<String>(key) {
        callback(getStringOrNull(key))
    }

    override fun addFloatOrNullListener(
        key: String,
        callback: (Float?) -> Unit
    ): SettingsListener = addListener<Float>(key) {
        callback(getFloatOrNull(key))
    }

    override fun addDoubleOrNullListener(
        key: String,
        callback: (Double?) -> Unit
    ): SettingsListener = addListener<Double>(key) {
        callback(getDoubleOrNull(key))
    }

    override fun addBooleanOrNullListener(
        key: String,
        callback: (Boolean?) -> Unit
    ): SettingsListener = addListener<Boolean>(key) {
        callback(getBooleanOrNull(key))
    }

    private inline fun <reified T> addListener(
        key: String,
        noinline callback: () -> Unit
    ): SettingsListener {
        var prev: T? = delegate[key]

        val listener = {
            val current: T? = delegate[key]
            if (prev != current) {
                callback()
                prev = current
            }
        }

        val listeners = listenerMap.getOrPut(key) { mutableSetOf() }
        listeners += listener

        return Listener {
            removeListener(key, listener)
        }
    }

    private fun removeListener(key: String, listener: () -> Unit) {
        listenerMap[key]?.also {
            it -= listener
        }
    }

    private fun invokeListeners(key: String) {
        listenerMap[key]?.forEach { callback ->
            callback()
        }
    }

    private fun invokeAllListeners() {
        listenerMap.forEach { entry ->
            entry.value.forEach { callback ->
                callback()
            }
        }
    }

}


/**
 *  A handle to a listener instance returned by one of the addListener methods of [MakeObservableSettings], so it can be
 *  deactivated as needed.
 */
@JvmInline
private value class Listener(
    private val removeListener: () -> Unit
) : SettingsListener {

    override fun deactivate(): Unit = removeListener()
}
