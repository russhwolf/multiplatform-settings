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
public fun Settings.int(key: String, defaultValue: Int = 0): ReadWriteProperty<Any?, Int> =
    IntDelegate(this, key, defaultValue)

/**
 * Returns a [Long] property delegate, backed by this [Settings] instance using the provided [key], with initial value [defaultValue].
 */
public fun Settings.long(key: String, defaultValue: Long = 0): ReadWriteProperty<Any?, Long> =
    LongDelegate(this, key, defaultValue)

/**
 * Returns a [String] property delegate, backed by this [Settings] instance using the provided [key], with initial value [defaultValue].
 */
public fun Settings.string(key: String, defaultValue: String = ""): ReadWriteProperty<Any?, String> =
    StringDelegate(this, key, defaultValue)

/**
 * Returns a [Float] property delegate, backed by this [Settings] instance using the provided [key], with initial value [defaultValue].
 */
public fun Settings.float(key: String, defaultValue: Float = 0f): ReadWriteProperty<Any?, Float> =
    FloatDelegate(this, key, defaultValue)

/**
 * Returns a [Double] property delegate, backed by this [Settings] instance using the provided [key], with initial value [defaultValue].
 */
public fun Settings.double(key: String, defaultValue: Double = 0.0): ReadWriteProperty<Any?, Double> =
    DoubleDelegate(this, key, defaultValue)

/**
 * Returns a [Boolean] property delegate, backed by this [Settings] instance using the provided [key], with initial value [defaultValue].
 */
public fun Settings.boolean(key: String, defaultValue: Boolean = false): ReadWriteProperty<Any?, Boolean> =
    BooleanDelegate(this, key, defaultValue)

/**
 * Returns a nullable [Int] property delegate, backed by this [Settings] instance using the provided [key], with initial value `null`
 */
public fun Settings.nullableInt(key: String): ReadWriteProperty<Any?, Int?> = NullableIntDelegate(this, key)

/**
 * Returns a nullable [Long] property delegate, backed by this [Settings] instance using the provided [key], with initial value `null`
 */
public fun Settings.nullableLong(key: String): ReadWriteProperty<Any?, Long?> = NullableLongDelegate(this, key)

/**
 * Returns a nullable [String] property delegate, backed by this [Settings] instance using the provided [key], with initial value `null`
 */
public fun Settings.nullableString(key: String): ReadWriteProperty<Any?, String?> = NullableStringDelegate(this, key)

/**
 * Returns a nullable [Float] property delegate, backed by this [Settings] instance using the provided [key], with initial value `null`
 */
public fun Settings.nullableFloat(key: String): ReadWriteProperty<Any?, Float?> = NullableFloatDelegate(this, key)

/**
 * Returns a nullable [Double] property delegate, backed by this [Settings] instance using the provided [key], with initial value `null`
 */
public fun Settings.nullableDouble(key: String): ReadWriteProperty<Any?, Double?> = NullableDoubleDelegate(this, key)

/**
 * Returns a nullable [Boolean] property delegate, backed by this [Settings] instance using the provided [key], with initial value `null`
 */
public fun Settings.nullableBoolean(key: String): ReadWriteProperty<Any?, Boolean?> = NullableBooleanDelegate(this, key)

private class IntDelegate(
    private val settings: Settings,
    private val key: String,
    private val defaultValue: Int
) : ReadWriteProperty<Any?, Int> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Int = settings[key, defaultValue]
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int) {
        settings[key] = value
    }
}

private class LongDelegate(
    private val settings: Settings,
    private val key: String,
    private val defaultValue: Long
) : ReadWriteProperty<Any?, Long> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Long = settings[key, defaultValue]
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Long) {
        settings[key] = value
    }
}

private class StringDelegate(
    private val settings: Settings,
    private val key: String,
    private val defaultValue: String
) : ReadWriteProperty<Any?, String> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): String = settings[key, defaultValue]
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String) {
        settings[key] = value
    }
}

private class FloatDelegate(
    private val settings: Settings,
    private val key: String,
    private val defaultValue: Float
) : ReadWriteProperty<Any?, Float> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Float = settings[key, defaultValue]
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Float) {
        settings[key] = value
    }
}

private class DoubleDelegate(
    private val settings: Settings,
    private val key: String,
    private val defaultValue: Double
) : ReadWriteProperty<Any?, Double> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Double = settings[key, defaultValue]
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Double) {
        settings[key] = value
    }
}

private class BooleanDelegate(
    private val settings: Settings,
    private val key: String,
    private val defaultValue: Boolean
) : ReadWriteProperty<Any?, Boolean> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean = settings[key, defaultValue]
    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean) {
        settings[key] = value
    }
}

private class NullableIntDelegate(
    private val settings: Settings,
    private val key: String
) : ReadWriteProperty<Any?, Int?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Int? {
        return if (key in settings) settings[key, 0] else null
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Int?) =
        if (value != null) settings[key] = value else settings -= key
}

private class NullableLongDelegate(
    private val settings: Settings,
    private val key: String
) : ReadWriteProperty<Any?, Long?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Long? =
        if (key in settings) settings[key, 0L] else null

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Long?) =
        if (value != null) settings[key] = value else settings -= key
}

private class NullableStringDelegate(
    private val settings: Settings,
    private val key: String
) : ReadWriteProperty<Any?, String?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): String? =
        if (key in settings) settings[key, ""] else null

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: String?) =
        if (value != null) settings[key] = value else settings -= key
}

private class NullableFloatDelegate(
    private val settings: Settings,
    private val key: String
) : ReadWriteProperty<Any?, Float?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Float? =
        if (key in settings) settings[key, 0f] else null

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Float?) =
        if (value != null) settings[key] = value else settings -= key
}

private class NullableDoubleDelegate(
    private val settings: Settings,
    private val key: String
) : ReadWriteProperty<Any?, Double?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Double? =
        if (key in settings) settings[key, 0.0] else null

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Double?) =
        if (value != null) settings[key] = value else settings -= key
}

private class NullableBooleanDelegate(
    private val settings: Settings,
    private val key: String
) : ReadWriteProperty<Any?, Boolean?> {
    override fun getValue(thisRef: Any?, property: KProperty<*>): Boolean? =
        if (key in settings) settings[key, false] else null

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: Boolean?) =
        if (value != null) settings[key] = value else settings -= key
}

