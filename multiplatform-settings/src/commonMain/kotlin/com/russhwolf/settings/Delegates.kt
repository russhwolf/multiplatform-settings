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

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Returns an [Int] property delegate, backed by this [Settings] instance using the provided [key], with initial value [defaultValue].
 */
public fun Settings.int(key: String? = null, defaultValue: Int = 0): ReadWriteProperty<Any?, Int> =
    IntDelegate(this, key, defaultValue)

/**
 * Returns a [Long] property delegate, backed by this [Settings] instance using the provided [key], with initial value [defaultValue].
 */
public fun Settings.long(key: String? = null, defaultValue: Long = 0): ReadWriteProperty<Any?, Long> =
    LongDelegate(this, key, defaultValue)

/**
 * Returns a [String] property delegate, backed by this [Settings] instance using the provided [key], with initial value [defaultValue].
 */
public fun Settings.string(key: String? = null, defaultValue: String = ""): ReadWriteProperty<Any?, String> =
    StringDelegate(this, key, defaultValue)

/**
 * Returns a [Float] property delegate, backed by this [Settings] instance using the provided [key], with initial value [defaultValue].
 */
public fun Settings.float(key: String? = null, defaultValue: Float = 0f): ReadWriteProperty<Any?, Float> =
    FloatDelegate(this, key, defaultValue)

/**
 * Returns a [Double] property delegate, backed by this [Settings] instance using the provided [key], with initial value [defaultValue].
 */
public fun Settings.double(key: String? = null, defaultValue: Double = 0.0): ReadWriteProperty<Any?, Double> =
    DoubleDelegate(this, key, defaultValue)

/**
 * Returns a [Boolean] property delegate, backed by this [Settings] instance using the provided [key], with initial value [defaultValue].
 */
public fun Settings.boolean(key: String? = null, defaultValue: Boolean = false): ReadWriteProperty<Any?, Boolean> =
    BooleanDelegate(this, key, defaultValue)

/**
 * Returns a nullable [Int] property delegate, backed by this [Settings] instance using the provided [key], with initial value `null`
 */
public fun Settings.nullableInt(key: String? = null): ReadWriteProperty<Any?, Int?> = NullableIntDelegate(this, key)

/**
 * Returns a nullable [Long] property delegate, backed by this [Settings] instance using the provided [key], with initial value `null`
 */
public fun Settings.nullableLong(key: String? = null): ReadWriteProperty<Any?, Long?> = NullableLongDelegate(this, key)

/**
 * Returns a nullable [String] property delegate, backed by this [Settings] instance using the provided [key], with initial value `null`
 */
public fun Settings.nullableString(key: String? = null): ReadWriteProperty<Any?, String?> =
    NullableStringDelegate(this, key)

/**
 * Returns a nullable [Float] property delegate, backed by this [Settings] instance using the provided [key], with initial value `null`
 */
public fun Settings.nullableFloat(key: String? = null): ReadWriteProperty<Any?, Float?> =
    NullableFloatDelegate(this, key)

/**
 * Returns a nullable [Double] property delegate, backed by this [Settings] instance using the provided [key], with initial value `null`
 */
public fun Settings.nullableDouble(key: String? = null): ReadWriteProperty<Any?, Double?> =
    NullableDoubleDelegate(this, key)

/**
 * Returns a nullable [Boolean] property delegate, backed by this [Settings] instance using the provided [key], with initial value `null`
 */
public fun Settings.nullableBoolean(key: String? = null): ReadWriteProperty<Any?, Boolean?> =
    NullableBooleanDelegate(this, key)

private class IntDelegate(
    private val settings: Settings,
    key: String?,
    private val defaultValue: Int
) : OptionalKeyDelegate<Int>(key) {
    override fun getValue(key: String): Int = settings[key, defaultValue]
    override fun setValue(key: String, value: Int) {
        settings[key] = value
    }
}

private class LongDelegate(
    private val settings: Settings,
    key: String?,
    private val defaultValue: Long
) : OptionalKeyDelegate<Long>(key) {
    override fun getValue(key: String): Long = settings[key, defaultValue]
    override fun setValue(key: String, value: Long) {
        settings[key] = value
    }
}

private class StringDelegate(
    private val settings: Settings,
    key: String?,
    private val defaultValue: String
) : OptionalKeyDelegate<String>(key) {
    override fun getValue(key: String): String = settings[key, defaultValue]
    override fun setValue(key: String, value: String) {
        settings[key] = value
    }
}

private class FloatDelegate(
    private val settings: Settings,
    key: String?,
    private val defaultValue: Float
) : OptionalKeyDelegate<Float>(key) {
    override fun getValue(key: String): Float = settings[key, defaultValue]
    override fun setValue(key: String, value: Float) {
        settings[key] = value
    }
}

private class DoubleDelegate(
    private val settings: Settings,
    key: String?,
    private val defaultValue: Double
) : OptionalKeyDelegate<Double>(key) {
    override fun getValue(key: String): Double = settings[key, defaultValue]
    override fun setValue(key: String, value: Double) {
        settings[key] = value
    }
}

private class BooleanDelegate(
    private val settings: Settings,
    key: String?,
    private val defaultValue: Boolean
) : OptionalKeyDelegate<Boolean>(key) {
    override fun getValue(key: String): Boolean = settings[key, defaultValue]
    override fun setValue(key: String, value: Boolean) {
        settings[key] = value
    }
}

private class NullableIntDelegate(
    private val settings: Settings,
    key: String?
) : OptionalKeyDelegate<Int?>(key) {
    override fun getValue(key: String): Int? {
        return if (key in settings) settings[key, 0] else null
    }

    override fun setValue(key: String, value: Int?) =
        if (value != null) settings[key] = value else settings -= key
}

private class NullableLongDelegate(
    private val settings: Settings,
    key: String?
) : OptionalKeyDelegate<Long?>(key) {
    override fun getValue(key: String): Long? =
        if (key in settings) settings[key, 0L] else null

    override fun setValue(key: String, value: Long?) =
        if (value != null) settings[key] = value else settings -= key
}

private class NullableStringDelegate(
    private val settings: Settings,
    key: String?
) : OptionalKeyDelegate<String?>(key) {
    override fun getValue(key: String): String? =
        if (key in settings) settings[key, ""] else null

    override fun setValue(key: String, value: String?) =
        if (value != null) settings[key] = value else settings -= key
}

private class NullableFloatDelegate(
    private val settings: Settings,
    key: String?
) : OptionalKeyDelegate<Float?>(key) {
    override fun getValue(key: String): Float? =
        if (key in settings) settings[key, 0f] else null

    override fun setValue(key: String, value: Float?) =
        if (value != null) settings[key] = value else settings -= key
}

private class NullableDoubleDelegate(
    private val settings: Settings,
    key: String?
) : OptionalKeyDelegate<Double?>(key) {
    override fun getValue(key: String): Double? =
        if (key in settings) settings[key, 0.0] else null

    override fun setValue(key: String, value: Double?) =
        if (value != null) settings[key] = value else settings -= key
}

private class NullableBooleanDelegate(
    private val settings: Settings,
    key: String?
) : OptionalKeyDelegate<Boolean?>(key) {
    override fun getValue(key: String): Boolean? =
        if (key in settings) settings[key, false] else null

    override fun setValue(key: String, value: Boolean?) =
        if (value != null) settings[key] = value else settings -= key
}

/**
 * A [ReadWriteProperty] that coordinates its [getValue] and [setValue] functions via a [String] key. If the [key]
 * is null, then [KProperty.name] will be used as a default.
 */
private abstract class OptionalKeyDelegate<T>(private val key: String?) : ReadWriteProperty<Any?, T> {

    abstract fun getValue(key: String): T
    abstract fun setValue(key: String, value: T)

    override fun getValue(thisRef: Any?, property: KProperty<*>): T = getValue(key ?: property.name)
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        setValue(key ?: property.name, value)
    }
}
