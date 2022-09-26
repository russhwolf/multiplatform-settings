/*
 * Copyright 2022 Russell Wolf
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

import java.util.Properties

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
 * On the JVM platform, this class can be created by passing a [Properties] instance which will be used as a delegate.
 * Since the [Properties] doesn't perform the serialization and writing of the data by itself, a callback [onModify] can
 * be added which will allow serialization or other work to occur after any write operation is performed.
 *
 * Unlike the implementations on Android and iOS, `JvmPropertiesSettings` does not include a [Settings.Factory] because
 * the `Properties` API does not provide a natural way to create multiple named instances.
 *
 * This class is experimental as marked by the [ExperimentalSettingsImplementation] annotation.
 *
 * The experimental listener APIs are not implemented in `JvmPropertiesSettings`.
 */
public class PropertiesSettings public constructor(
    private val delegate: Properties,
    private val onModify: (Properties) -> Unit = {}
) : Settings {

    @Suppress("UNCHECKED_CAST")
    public override val keys: Set<String> get() = delegate.propertyNames().toList().toSet() as Set<String>
    public override val size: Int get() = delegate.size

    public override fun clear() {
        delegate.clear()
        onModify(delegate)
    }

    public override fun remove(key: String) {
        delegate.remove(key)
        onModify(delegate)
    }

    public override fun hasKey(key: String): Boolean = delegate[key] != null

    public override fun putInt(key: String, value: Int) {
        delegate.setProperty(key, value.toString())
        onModify(delegate)
    }

    public override fun getInt(key: String, defaultValue: Int): Int =
        delegate.getProperty(key)?.toInt() ?: defaultValue

    public override fun getIntOrNull(key: String): Int? =
        delegate.getProperty(key)?.toInt()

    public override fun putLong(key: String, value: Long) {
        delegate.setProperty(key, value.toString())
        onModify(delegate)
    }

    public override fun getLong(key: String, defaultValue: Long): Long =
        delegate.getProperty(key)?.toLong() ?: defaultValue

    public override fun getLongOrNull(key: String): Long? =
        delegate.getProperty(key)?.toLong()

    public override fun putString(key: String, value: String) {
        delegate.setProperty(key, value)
        onModify(delegate)
    }

    public override fun getString(key: String, defaultValue: String): String =
        delegate.getProperty(key) ?: defaultValue

    public override fun getStringOrNull(key: String): String? =
        delegate.getProperty(key)

    public override fun putFloat(key: String, value: Float) {
        delegate.setProperty(key, value.toString())
        onModify(delegate)
    }

    public override fun getFloat(key: String, defaultValue: Float): Float =
        delegate.getProperty(key)?.toFloat() ?: defaultValue

    public override fun getFloatOrNull(key: String): Float? =
        delegate.getProperty(key)?.toFloat()

    public override fun putDouble(key: String, value: Double) {
        delegate.setProperty(key, value.toString())
        onModify(delegate)
    }

    public override fun getDouble(key: String, defaultValue: Double): Double =
        delegate.getProperty(key)?.toDouble() ?: defaultValue

    public override fun getDoubleOrNull(key: String): Double? =
        delegate.getProperty(key)?.toDouble()

    public override fun putBoolean(key: String, value: Boolean) {
        delegate.setProperty(key, value.toString())
        onModify(delegate)
    }

    public override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        delegate.getProperty(key)?.toBoolean() ?: defaultValue

    public override fun getBooleanOrNull(key: String): Boolean? =
        delegate.getProperty(key)?.toBoolean()
}
