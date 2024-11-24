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

package com.russhwolf.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import androidx.test.platform.app.InstrumentationRegistry

private val factory = object : Settings.Factory {
    override fun create(name: String?): Settings {
        val context: Context = InstrumentationRegistry.getInstrumentation().context
        val sharedPreferences = if (name == null) {
            PreferenceManager.getDefaultSharedPreferences(context)
        } else {
            context.getSharedPreferences(name, Context.MODE_PRIVATE)
        }
        return JniSharedPreferencesSettingsWrapper(sharedPreferences)
    }
}

/**
 * There's not a good way to run android native tests directly, and we wouldn't be able to mock context or JNIEnv. So
 * instead we add another layer of JNI in order to test from an Android test. This consumes a binary created by the
 * `:androidNative-test-helpers` module.
 */
class JniSharedPreferencesSettingsTest : BaseSettingsTest(
    platformFactory = factory,
    hasNamedInstances = true,
    allowsDuplicateInstances = true,
    hasListeners = false
) {
    init {
        System.loadLibrary("androidNativeTestHelpers")
    }
}

private class JniSharedPreferencesSettingsWrapper(private val sharedPreferences: SharedPreferences) : Settings {

    override val keys: Set<String>
        get() = nativeGetKeys(sharedPreferences).toSet()
    override val size: Int
        get() = nativeGetSize(sharedPreferences)

    override fun clear() =
        nativeClear(sharedPreferences)

    override fun remove(key: String) =
        nativeRemove(sharedPreferences, key)

    override fun hasKey(key: String): Boolean =
        nativeHasKey(sharedPreferences, key)

    override fun putInt(key: String, value: Int) =
        nativePutInt(sharedPreferences, key, value)

    override fun getInt(key: String, defaultValue: Int) =
        nativeGetInt(sharedPreferences, key, defaultValue)

    override fun getIntOrNull(key: String): Int? =
        nativeGetIntOrNull(sharedPreferences, key)

    override fun putLong(key: String, value: Long) =
        nativePutLong(sharedPreferences, key, value)

    override fun getLong(key: String, defaultValue: Long): Long =
        nativeGetLong(sharedPreferences, key, defaultValue)

    override fun getLongOrNull(key: String): Long? =
        nativeGetLongOrNull(sharedPreferences, key)

    override fun putString(key: String, value: String) =
        nativePutString(sharedPreferences, key, value)

    override fun getString(key: String, defaultValue: String): String =
        nativeGetString(sharedPreferences, key, defaultValue)

    override fun getStringOrNull(key: String): String? =
        nativeGetStringOrNull(sharedPreferences, key)

    override fun putFloat(key: String, value: Float) =
        nativePutFloat(sharedPreferences, key, value)

    override fun getFloat(key: String, defaultValue: Float): Float =
        nativeGetFloat(sharedPreferences, key, defaultValue)

    override fun getFloatOrNull(key: String): Float? =
        nativeGetFloatOrNull(sharedPreferences, key)

    override fun putDouble(key: String, value: Double) =
        nativePutDouble(sharedPreferences, key, value)

    override fun getDouble(key: String, defaultValue: Double): Double =
        nativeGetDouble(sharedPreferences, key, defaultValue)

    override fun getDoubleOrNull(key: String): Double? =
        nativeGetDoubleOrNull(sharedPreferences, key)

    override fun putBoolean(key: String, value: Boolean) =
        nativePutBoolean(sharedPreferences, key, value)

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        nativeGetBoolean(sharedPreferences, key, defaultValue)

    override fun getBooleanOrNull(key: String): Boolean? =
        nativeGetBooleanOrNull(sharedPreferences, key)
}

external fun nativeGetKeys(sharedPreferences: SharedPreferences): Array<String>
external fun nativeGetSize(sharedPreferences: SharedPreferences): Int
external fun nativeClear(sharedPreferences: SharedPreferences)
external fun nativeRemove(sharedPreferences: SharedPreferences, key: String)
external fun nativeHasKey(sharedPreferences: SharedPreferences, key: String): Boolean
external fun nativePutInt(sharedPreferences: SharedPreferences, key: String, value: Int)
external fun nativeGetInt(sharedPreferences: SharedPreferences, key: String, defaultValue: Int): Int
external fun nativeGetIntOrNull(sharedPreferences: SharedPreferences, key: String): Int?
external fun nativePutLong(sharedPreferences: SharedPreferences, key: String, value: Long)
external fun nativeGetLong(sharedPreferences: SharedPreferences, key: String, defaultValue: Long): Long
external fun nativeGetLongOrNull(sharedPreferences: SharedPreferences, key: String): Long?
external fun nativePutString(sharedPreferences: SharedPreferences, key: String, value: String)
external fun nativeGetString(sharedPreferences: SharedPreferences, key: String, defaultValue: String): String
external fun nativeGetStringOrNull(sharedPreferences: SharedPreferences, key: String): String?
external fun nativePutFloat(sharedPreferences: SharedPreferences, key: String, value: Float)
external fun nativeGetFloat(sharedPreferences: SharedPreferences, key: String, defaultValue: Float): Float
external fun nativeGetFloatOrNull(sharedPreferences: SharedPreferences, key: String): Float?
external fun nativePutDouble(sharedPreferences: SharedPreferences, key: String, value: Double)
external fun nativeGetDouble(sharedPreferences: SharedPreferences, key: String, defaultValue: Double): Double
external fun nativeGetDoubleOrNull(sharedPreferences: SharedPreferences, key: String): Double?
external fun nativePutBoolean(sharedPreferences: SharedPreferences, key: String, value: Boolean)
external fun nativeGetBoolean(sharedPreferences: SharedPreferences, key: String, defaultValue: Boolean): Boolean
external fun nativeGetBooleanOrNull(sharedPreferences: SharedPreferences, key: String): Boolean?
