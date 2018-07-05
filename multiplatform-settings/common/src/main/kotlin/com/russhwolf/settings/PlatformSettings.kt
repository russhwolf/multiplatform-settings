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

import com.russhwolf.settings.PlatformSettings.Factory

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
 * This class can be instantiated via a platform-specific constructor or via a [Factory].
 */
public expect class PlatformSettings : Settings {

    /**
     * A factory that can produce [Settings] instances.
     *
     * This class can only be instantiated via a platform-specific constructor. It's purpose is so that `Settings`
     * objects can be created in common code, so that the only platform-specific behavior necessary in order to use
     * multiple `Settings` objects is the one-time creation of a single `Factory`.
     */
    public class Factory : Settings.Factory {
        public override fun create(name: String?): Settings
    }

    public override fun clear()
    public override fun remove(key: String)
    public override fun hasKey(key: String): Boolean
    public override fun putInt(key: String, value: Int)
    public override fun getInt(key: String, defaultValue: Int): Int
    public override fun putLong(key: String, value: Long)
    public override fun getLong(key: String, defaultValue: Long): Long
    public override fun putString(key: String, value: String)
    public override fun getString(key: String, defaultValue: String): String
    public override fun putFloat(key: String, value: Float)
    public override fun getFloat(key: String, defaultValue: Float): Float
    public override fun putDouble(key: String, value: Double)
    public override fun getDouble(key: String, defaultValue: Double): Double
    public override fun putBoolean(key: String, value: Boolean)
    public override fun getBoolean(key: String, defaultValue: Boolean): Boolean
}

