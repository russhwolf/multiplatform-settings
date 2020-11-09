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

import com.russhwolf.settings.Settings

// TODO this doesn't NEED to live in a module that pulls in kotlinx dependency...
public interface SuspendSettings {

    public companion object;

    public suspend fun keys(): Set<String>
    public suspend fun size(): Int
    public suspend fun clear()
    public suspend fun remove(key: String)
    public suspend fun hasKey(key: String): Boolean

    public suspend fun putInt(key: String, value: Int)
    public suspend fun getInt(key: String, defaultValue: Int = 0): Int
    public suspend fun getIntOrNull(key: String): Int?

    public suspend fun putLong(key: String, value: Long)
    public suspend fun getLong(key: String, defaultValue: Long = 0): Long
    public suspend fun getLongOrNull(key: String): Long?

    public suspend fun putString(key: String, value: String)
    public suspend fun getString(key: String, defaultValue: String = ""): String
    public suspend fun getStringOrNull(key: String): String?

    public suspend fun putFloat(key: String, value: Float)
    public suspend fun getFloat(key: String, defaultValue: Float = 0f): Float
    public suspend fun getFloatOrNull(key: String): Float?

    public suspend fun putDouble(key: String, value: Double)
    public suspend fun getDouble(key: String, defaultValue: Double = 0.0): Double
    public suspend fun getDoubleOrNull(key: String): Double?

    public suspend fun putBoolean(key: String, value: Boolean)
    public suspend fun getBoolean(key: String, defaultValue: Boolean = false): Boolean
    public suspend fun getBooleanOrNull(key: String): Boolean?
}


public fun Settings.toSuspendSettings(): SuspendSettings = object : SuspendSettings {
    private val settings inline get() = this@toSuspendSettings

    public override suspend fun keys(): Set<String> = settings.keys
    public override suspend fun size(): Int = settings.size
    public override suspend fun clear() = settings.clear()
    public override suspend fun remove(key: String) = settings.remove(key)
    public override suspend fun hasKey(key: String): Boolean = settings.hasKey(key)

    public override suspend fun putInt(key: String, value: Int) = settings.putInt(key, value)
    public override suspend fun getInt(key: String, defaultValue: Int): Int = settings.getInt(key, defaultValue)
    public override suspend fun getIntOrNull(key: String): Int? = settings.getIntOrNull(key)

    public override suspend fun putLong(key: String, value: Long) = settings.putLong(key, value)
    public override suspend fun getLong(key: String, defaultValue: Long): Long = settings.getLong(key, defaultValue)
    public override suspend fun getLongOrNull(key: String): Long? = settings.getLongOrNull(key)

    public override suspend fun putString(key: String, value: String) = settings.putString(key, value)
    public override suspend fun getString(key: String, defaultValue: String): String =
        settings.getString(key, defaultValue)

    public override suspend fun getStringOrNull(key: String): String? = settings.getStringOrNull(key)

    public override suspend fun putFloat(key: String, value: Float) = settings.putFloat(key, value)
    public override suspend fun getFloat(key: String, defaultValue: Float): Float = settings.getFloat(key, defaultValue)
    public override suspend fun getFloatOrNull(key: String): Float? = settings.getFloatOrNull(key)

    public override suspend fun putDouble(key: String, value: Double) = settings.putDouble(key, value)
    public override suspend fun getDouble(key: String, defaultValue: Double): Double =
        settings.getDouble(key, defaultValue)

    public override suspend fun getDoubleOrNull(key: String): Double? = settings.getDoubleOrNull(key)

    public override suspend fun putBoolean(key: String, value: Boolean) = settings.putBoolean(key, value)
    public override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        settings.getBoolean(key, defaultValue)

    public override suspend fun getBooleanOrNull(key: String): Boolean? = settings.getBooleanOrNull(key)
}

