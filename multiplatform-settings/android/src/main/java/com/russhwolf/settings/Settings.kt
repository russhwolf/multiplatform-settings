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
import android.preference.PreferenceManager

actual class Settings internal constructor(private val delegate: SharedPreferences) {

    actual class Factory(context: Context) {
        private val appContext = context.applicationContext

        actual fun create(name: String?) = Settings(
            if (name == null) {
                PreferenceManager.getDefaultSharedPreferences(appContext)
            } else {
                appContext.getSharedPreferences(name, MODE_PRIVATE)
            }
        )
    }

    actual fun clear() = delegate.edit().clear().apply()

    actual fun remove(key: String) = delegate.edit().remove(key).apply()
    actual fun hasKey(key: String) = delegate.contains(key)

    actual fun putInt(key: String, value: Int) = delegate.edit().putInt(key, value).apply()
    actual fun getInt(key: String, defaultValue: Int): Int = delegate.getInt(key, defaultValue)

    actual fun putLong(key: String, value: Long) = delegate.edit().putLong(key, value).apply()
    actual fun getLong(key: String, defaultValue: Long): Long = delegate.getLong(key, defaultValue)

    actual fun putString(key: String, value: String) = delegate.edit().putString(key, value).apply()
    actual fun getString(key: String, defaultValue: String): String = delegate.getString(key, defaultValue)

    actual fun putFloat(key: String, value: Float) = delegate.edit().putFloat(key, value).apply()
    actual fun getFloat(key: String, defaultValue: Float): Float = delegate.getFloat(key, defaultValue)

    actual fun putDouble(key: String, value: Double) = putLong(key, value.toRawBits())
    actual fun getDouble(key: String, defaultValue: Double): Double =
        Double.fromBits(getLong(key, defaultValue.toRawBits()))

    actual fun putBoolean(key: String, value: Boolean) = delegate.edit().putBoolean(key, value).apply()
    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean = delegate.getBoolean(key, defaultValue)
}
