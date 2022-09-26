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

package com.russhwolf.settings.coroutines

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SettingsListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

/**
 * Wraps this [SuspendSettings] in the [Settings] interface.
 *
 * Note that this occurs via use of [runBlocking]. Make sure this is what you want! You should only interact with the
 * instance returned by this function from a thread that can be blocked without impacting the rest of your application.
 */
@ExperimentalSettingsApi
public fun SuspendSettings.toBlockingSettings(): Settings = BlockingSuspendSettings(this)

@ExperimentalSettingsApi
private open class BlockingSuspendSettings(private val delegate: SuspendSettings) : Settings {

    public final override val keys: Set<String> get() = runBlocking { delegate.keys() }
    public final override val size: Int get() = runBlocking { delegate.size() }
    public final override fun clear() = runBlocking { delegate.clear() }
    public final override fun remove(key: String) = runBlocking { delegate.remove(key) }
    public final override fun hasKey(key: String): Boolean = runBlocking { delegate.hasKey(key) }

    public final override fun putInt(key: String, value: Int) = runBlocking { delegate.putInt(key, value) }
    public final override fun getInt(key: String, defaultValue: Int): Int =
        runBlocking { delegate.getInt(key, defaultValue) }

    public final override fun getIntOrNull(key: String): Int? = runBlocking { delegate.getIntOrNull(key) }

    public final override fun putLong(key: String, value: Long) = runBlocking { delegate.putLong(key, value) }
    public final override fun getLong(key: String, defaultValue: Long): Long =
        runBlocking { delegate.getLong(key, defaultValue) }

    public final override fun getLongOrNull(key: String): Long? = runBlocking { delegate.getLongOrNull(key) }

    public final override fun putString(key: String, value: String) = runBlocking { delegate.putString(key, value) }
    public final override fun getString(key: String, defaultValue: String): String =
        runBlocking { delegate.getString(key, defaultValue) }

    public final override fun getStringOrNull(key: String): String? = runBlocking { delegate.getStringOrNull(key) }

    public final override fun putFloat(key: String, value: Float) = runBlocking { delegate.putFloat(key, value) }
    public final override fun getFloat(key: String, defaultValue: Float): Float =
        runBlocking { delegate.getFloat(key, defaultValue) }

    public final override fun getFloatOrNull(key: String): Float? = runBlocking { delegate.getFloatOrNull(key) }

    public final override fun putDouble(key: String, value: Double) = runBlocking { delegate.putDouble(key, value) }
    public final override fun getDouble(key: String, defaultValue: Double): Double =
        runBlocking { delegate.getDouble(key, defaultValue) }

    public final override fun getDoubleOrNull(key: String): Double? = runBlocking { delegate.getDoubleOrNull(key) }

    public final override fun putBoolean(key: String, value: Boolean) = runBlocking { delegate.putBoolean(key, value) }
    public final override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        runBlocking { delegate.getBoolean(key, defaultValue) }

    public final override fun getBooleanOrNull(key: String): Boolean? = runBlocking { delegate.getBooleanOrNull(key) }
}

/**
 * Wraps this [FlowSettings] in the [ObservableSettings] interface.
 *
 * Note that listeners are created by launching `Flow`s in [GlobalScope] by default, but you may also supply your own
 * scope which will be used instead.
 */
@ExperimentalSettingsApi
@OptIn(DelicateCoroutinesApi::class)
public fun FlowSettings.toBlockingObservableSettings(scope: CoroutineScope = GlobalScope): ObservableSettings =
    BlockingObservableSettings(this, scope)

@ExperimentalSettingsApi
private class BlockingObservableSettings(
    private val delegate: FlowSettings,
    private val scope: CoroutineScope,
) : BlockingSuspendSettings(delegate), ObservableSettings {

    private class Listener<T>(flow: Flow<T>, scope: CoroutineScope, callback: (T) -> Unit) : SettingsListener {
        // Drop 1, because `FlowSettings` emits the current value immediately, but `ObservableSettings` waits for a new
        // value before the listener is called.
        private val job = flow.drop(1).onEach { callback(it) }.launchIn(scope)

        override fun deactivate() {
            job.cancel()
        }
    }

    override fun addIntListener(key: String, defaultValue: Int, callback: (Int) -> Unit): SettingsListener =
        Listener(delegate.getIntFlow(key, defaultValue), scope, callback)

    override fun addLongListener(key: String, defaultValue: Long, callback: (Long) -> Unit): SettingsListener =
        Listener(delegate.getLongFlow(key, defaultValue), scope, callback)

    override fun addStringListener(key: String, defaultValue: String, callback: (String) -> Unit): SettingsListener =
        Listener(delegate.getStringFlow(key, defaultValue), scope, callback)

    override fun addFloatListener(key: String, defaultValue: Float, callback: (Float) -> Unit): SettingsListener =
        Listener(delegate.getFloatFlow(key, defaultValue), scope, callback)

    override fun addDoubleListener(key: String, defaultValue: Double, callback: (Double) -> Unit): SettingsListener =
        Listener(delegate.getDoubleFlow(key, defaultValue), scope, callback)

    override fun addBooleanListener(key: String, defaultValue: Boolean, callback: (Boolean) -> Unit): SettingsListener =
        Listener(delegate.getBooleanFlow(key, defaultValue), scope, callback)

    override fun addIntOrNullListener(key: String, callback: (Int?) -> Unit): SettingsListener =
        Listener(delegate.getIntOrNullFlow(key), scope, callback)

    override fun addLongOrNullListener(key: String, callback: (Long?) -> Unit): SettingsListener =
        Listener(delegate.getLongOrNullFlow(key), scope, callback)

    override fun addStringOrNullListener(key: String, callback: (String?) -> Unit): SettingsListener =
        Listener(delegate.getStringOrNullFlow(key), scope, callback)

    override fun addFloatOrNullListener(key: String, callback: (Float?) -> Unit): SettingsListener =
        Listener(delegate.getFloatOrNullFlow(key), scope, callback)

    override fun addDoubleOrNullListener(key: String, callback: (Double?) -> Unit): SettingsListener =
        Listener(delegate.getDoubleOrNullFlow(key), scope, callback)

    override fun addBooleanOrNullListener(key: String, callback: (Boolean?) -> Unit): SettingsListener =
        Listener(delegate.getBooleanOrNullFlow(key), scope, callback)

}
