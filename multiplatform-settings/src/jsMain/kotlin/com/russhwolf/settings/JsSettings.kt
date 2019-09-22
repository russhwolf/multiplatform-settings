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

import org.w3c.dom.Storage
import org.w3c.dom.get
import org.w3c.dom.set
import kotlin.browser.localStorage

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
 * On the JS platform, this class can be created by passing a [Storage] instance which will be used as a delegate. By
 * default [localStorage] will be used if nothing is provided.
 *
 * Unlike the implementations on Android and iOS, `JsSettings` does not include a [Settings.Factory] because
 * the `Storage` API does not provide a way to create multiple named instances.
 *
 * This class is experimental as marked by the [ExperimentalJs] annotation.
 *
 * The experimental listener APIs are not implemented in `JsSettings`.
 */
@ExperimentalJs
public class JsSettings(private val delegate: Storage = localStorage) : Settings {

    public override fun clear(): Unit = delegate.clear()
    public override fun remove(key: String): Unit = delegate.removeItem(key)
    public override fun hasKey(key: String): Boolean = delegate[key] != null

    public override fun putInt(key: String, value: Int) {
        delegate[key] = value.toString()
    }

    public override fun getInt(key: String, defaultValue: Int): Int =
        delegate[key]?.toInt() ?: defaultValue

    public override fun getIntOrNull(key: String): Int? =
        delegate[key]?.toIntOrNull()

    public override fun putLong(key: String, value: Long) {
        delegate[key] = value.toString()
    }

    public override fun getLong(key: String, defaultValue: Long): Long =
        delegate[key]?.toLong() ?: defaultValue

    public override fun getLongOrNull(key: String): Long? =
        delegate[key]?.toLongOrNull()


    public override fun putString(key: String, value: String) {
        delegate[key] = value
    }

    public override fun getString(key: String, defaultValue: String): String = delegate[key] ?: defaultValue

    public override fun getStringOrNull(key: String): String? = delegate[key]

    public override fun putFloat(key: String, value: Float) {
        delegate[key] = value.toString()
    }

    public override fun getFloat(key: String, defaultValue: Float): Float =
        delegate[key]?.toFloat() ?: defaultValue

    public override fun getFloatOrNull(key: String): Float? =
        delegate[key]?.toFloatOrNull()

    public override fun putDouble(key: String, value: Double) {
        delegate[key] = value.toString()
    }

    public override fun getDouble(key: String, defaultValue: Double): Double =
        delegate[key]?.toDouble() ?: defaultValue

    public override fun getDoubleOrNull(key: String): Double? =
        delegate[key]?.toDoubleOrNull()

    public override fun putBoolean(key: String, value: Boolean) {
        delegate[key] = value.toString()
    }

    public override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        delegate[key]?.toBoolean() ?: defaultValue

    public override fun getBooleanOrNull(key: String): Boolean? =
        delegate[key]?.toBoolean()
}
