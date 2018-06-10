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

actual class Settings public constructor(private val delegate: NSUserDefaults) {

    actual class Factory() {
        actual fun create(name: String?): Settings {
            val userDefaults = if (name == null) NSUserDefaults.standardUserDefaults else NSUserDefaults(name)
            return Settings(userDefaults)
        }
    }

    actual fun clear() {
        for (key in delegate.dictionaryRepresentation().keys) {
            remove(key as String)
        }
    }

    actual fun remove(key: String) = delegate.removeObjectForKey(key)
    actual fun hasKey(key: String) = delegate.objectForKey(key) != null

    actual fun putInt(key: String, value: Int) = delegate.setInteger(value.toLong(), forKey = key)
    actual fun getInt(key: String, defaultValue: Int): Int =
        if (hasKey(key)) delegate.integerForKey(key).toInt() else defaultValue

    actual fun putLong(key: String, value: Long) = delegate.setInteger(value, forKey = key)
    actual fun getLong(key: String, defaultValue: Long): Long =
        if (hasKey(key)) delegate.integerForKey(key) else defaultValue

    actual fun putString(key: String, value: String) = delegate.setObject(value, forKey = key)
    actual fun getString(key: String, defaultValue: String): String =
        delegate.stringForKey(key) ?: defaultValue

    actual fun putFloat(key: String, value: Float) = delegate.setFloat(value, forKey = key)
    actual fun getFloat(key: String, defaultValue: Float): Float =
        if (hasKey(key)) delegate.floatForKey(key) else defaultValue

    actual fun putDouble(key: String, value: Double) = delegate.setDouble(value, forKey = key)
    actual fun getDouble(key: String, defaultValue: Double): Double =
        if (hasKey(key)) delegate.doubleForKey(key) else defaultValue

    actual fun putBoolean(key: String, value: Boolean) = delegate.setBool(value, forKey = key)
    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        if (hasKey(key)) delegate.boolForKey(key) else defaultValue
}
