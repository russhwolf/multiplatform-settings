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

import com.russhwolf.settings.ExperimentalListener
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SettingsListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

public fun SuspendSettings.toBlockingSettings(): Settings = object : Settings {
    private val settings inline get() = this@toBlockingSettings

    public override val keys: Set<String> get() = runBlocking { settings.keys() }
    public override val size: Int get() = runBlocking { settings.size() }
    public override fun clear() = runBlocking { settings.clear() }
    public override fun remove(key: String) = runBlocking { settings.remove(key) }
    public override fun hasKey(key: String): Boolean = runBlocking { settings.hasKey(key) }

    public override fun putInt(key: String, value: Int) = runBlocking { settings.putInt(key, value) }
    public override fun getInt(key: String, defaultValue: Int): Int = runBlocking { settings.getInt(key, defaultValue) }
    public override fun getIntOrNull(key: String): Int? = runBlocking { settings.getIntOrNull(key) }

    public override fun putLong(key: String, value: Long) = runBlocking { settings.putLong(key, value) }
    public override fun getLong(key: String, defaultValue: Long): Long =
        runBlocking { settings.getLong(key, defaultValue) }

    public override fun getLongOrNull(key: String): Long? = runBlocking { settings.getLongOrNull(key) }

    public override fun putString(key: String, value: String) = runBlocking { settings.putString(key, value) }
    public override fun getString(key: String, defaultValue: String): String =
        runBlocking { settings.getString(key, defaultValue) }

    public override fun getStringOrNull(key: String): String? = runBlocking { settings.getStringOrNull(key) }

    public override fun putFloat(key: String, value: Float) = runBlocking { settings.putFloat(key, value) }
    public override fun getFloat(key: String, defaultValue: Float): Float =
        runBlocking { settings.getFloat(key, defaultValue) }

    public override fun getFloatOrNull(key: String): Float? = runBlocking { settings.getFloatOrNull(key) }

    public override fun putDouble(key: String, value: Double) = runBlocking { settings.putDouble(key, value) }
    public override fun getDouble(key: String, defaultValue: Double): Double =
        runBlocking { settings.getDouble(key, defaultValue) }

    public override fun getDoubleOrNull(key: String): Double? = runBlocking { settings.getDoubleOrNull(key) }

    public override fun putBoolean(key: String, value: Boolean) = runBlocking { settings.putBoolean(key, value) }
    public override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        runBlocking { settings.getBoolean(key, defaultValue) }

    public override fun getBooleanOrNull(key: String): Boolean? = runBlocking { settings.getBooleanOrNull(key) }
}


@ExperimentalListener
@ExperimentalCoroutinesApi
public fun FlowSettings.toBlockingObservableSettings(
    listenerContext: CoroutineContext = EmptyCoroutineContext
): ObservableSettings = object : ObservableSettings {
    private val settings inline get() = this@toBlockingObservableSettings

    public override val keys: Set<String> get() = runBlocking { settings.keys() }
    public override val size: Int get() = runBlocking { settings.size() }
    public override fun clear() = runBlocking { settings.clear() }
    public override fun remove(key: String) = runBlocking { settings.remove(key) }
    public override fun hasKey(key: String): Boolean = runBlocking { settings.hasKey(key) }

    public override fun putInt(key: String, value: Int) = runBlocking { settings.putInt(key, value) }
    public override fun getInt(key: String, defaultValue: Int): Int =
        runBlocking { settings.getIntFlow(key, defaultValue).first() }

    public override fun getIntOrNull(key: String): Int? = runBlocking { settings.getIntOrNullFlow(key).first() }

    public override fun putLong(key: String, value: Long) = runBlocking { settings.putLong(key, value) }
    public override fun getLong(key: String, defaultValue: Long): Long =
        runBlocking { settings.getLongFlow(key, defaultValue).first() }

    public override fun getLongOrNull(key: String): Long? = runBlocking { settings.getLongOrNullFlow(key).first() }

    public override fun putString(key: String, value: String) = runBlocking { settings.putString(key, value) }
    public override fun getString(key: String, defaultValue: String): String =
        runBlocking { settings.getStringFlow(key, defaultValue).first() }

    public override fun getStringOrNull(key: String): String? =
        runBlocking { settings.getStringOrNullFlow(key).first() }

    public override fun putFloat(key: String, value: Float) = runBlocking { settings.putFloat(key, value) }
    public override fun getFloat(key: String, defaultValue: Float): Float =
        runBlocking { settings.getFloatFlow(key, defaultValue).first() }

    public override fun getFloatOrNull(key: String): Float? = runBlocking { settings.getFloatOrNullFlow(key).first() }

    public override fun putDouble(key: String, value: Double) = runBlocking { settings.putDouble(key, value) }
    public override fun getDouble(key: String, defaultValue: Double): Double =
        runBlocking { settings.getDoubleFlow(key, defaultValue).first() }

    public override fun getDoubleOrNull(key: String): Double? =
        runBlocking { settings.getDoubleOrNullFlow(key).first() }

    public override fun putBoolean(key: String, value: Boolean) = runBlocking { settings.putBoolean(key, value) }
    public override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        runBlocking { settings.getBooleanFlow(key, defaultValue).first() }

    public override fun getBooleanOrNull(key: String): Boolean? =
        runBlocking { settings.getBooleanOrNullFlow(key).first() }

    @ExperimentalListener
    override fun addListener(key: String, callback: () -> Unit): SettingsListener = object : SettingsListener {

        private inline fun <T> tryOrNull(block: () -> T) = runCatching(block).getOrNull()

        private var prev = tryOrNull { getStringOrNull(key) }
            ?: tryOrNull { getIntOrNull(key) }
            ?: tryOrNull { getLongOrNull(key) }
            ?: tryOrNull { getFloatOrNull(key) }
            ?: tryOrNull { getDoubleOrNull(key) }
            ?: tryOrNull { getBooleanOrNull(key) }


        private val scope = CoroutineScope(listenerContext + SupervisorJob(listenerContext[Job]))

        init {
            @OptIn(FlowPreview::class)
            flowOf(
                getStringOrNullFlow(key).catch {},
                getIntOrNullFlow(key).catch {},
                getLongOrNullFlow(key).catch {},
                getFloatOrNullFlow(key).catch {},
                getDoubleOrNullFlow(key).catch {},
                getBooleanOrNullFlow(key).catch {},
            ).flattenMerge()
                .onEach {
                    val current = it
                    if (prev != current) {
                        callback()
                        prev = current
                    }
                }
                .launchIn(scope)
        }

        override fun deactivate() {
            scope.cancel()
        }
    }
}
