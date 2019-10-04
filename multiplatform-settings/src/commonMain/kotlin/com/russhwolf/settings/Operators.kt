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

@file:Suppress("NOTHING_TO_INLINE")

package com.russhwolf.settings

/** Equivalent to [Settings.hasKey] */
public inline operator fun Settings.contains(key: String): Boolean = hasKey(key)

/** Equivalent to [Settings.remove] */
public inline operator fun Settings.minusAssign(key: String): Unit = remove(key)

/** Equivalent to [Settings.getInt] */
public inline operator fun Settings.get(key: String, defaultValue: Int): Int = getInt(key, defaultValue)

/** Equivalent to [Settings.getLong] */
public inline operator fun Settings.get(key: String, defaultValue: Long): Long = getLong(key, defaultValue)

/** Equivalent to [Settings.getString] */
public inline operator fun Settings.get(key: String, defaultValue: String): String = getString(key, defaultValue)

/** Equivalent to [Settings.getFloat] */
public inline operator fun Settings.get(key: String, defaultValue: Float): Float = getFloat(key, defaultValue)

/** Equivalent to [Settings.getDouble] */
public inline operator fun Settings.get(key: String, defaultValue: Double): Double = getDouble(key, defaultValue)

/** Equivalent to [Settings.getBoolean] */
public inline operator fun Settings.get(key: String, defaultValue: Boolean): Boolean = getBoolean(key, defaultValue)

/** Equivalent to [Settings.putInt] */
public inline operator fun Settings.set(key: String, value: Int): Unit = putInt(key, value)

/** Equivalent to [Settings.putLong] */
public inline operator fun Settings.set(key: String, value: Long): Unit = putLong(key, value)

/** Equivalent to [Settings.putString] */
public inline operator fun Settings.set(key: String, value: String): Unit = putString(key, value)

/** Equivalent to [Settings.putFloat] */
public inline operator fun Settings.set(key: String, value: Float): Unit = putFloat(key, value)

/** Equivalent to [Settings.putDouble] */
public inline operator fun Settings.set(key: String, value: Double): Unit = putDouble(key, value)

/** Equivalent to [Settings.putBoolean] */
public inline operator fun Settings.set(key: String, value: Boolean): Unit = putBoolean(key, value)

/**
 * Get the typed value stored at [key] if present, or return null if not. Throws [IllegalArgumentException] if [T] is
 * not one of `Int`, `Long`, `String`, `Float`, `Double`, or `Boolean`.
 */
public inline operator fun <reified T : Any> Settings.get(key: String): T? = when (T::class) {
    Int::class -> getIntOrNull(key) as T?
    Long::class -> getLongOrNull(key) as T?
    String::class -> getStringOrNull(key) as T?
    Float::class -> getFloatOrNull(key) as T?
    Double::class -> getDoubleOrNull(key) as T?
    Boolean::class -> getBooleanOrNull(key) as T?
    else -> throw IllegalArgumentException("Invalid type!")
}

/**
 * Stores a typed value at [key], or remove what's there if [value] is null. Throws [IllegalArgumentException] if [T] is
 * not one of `Int`, `Long`, `String`, `Float`, `Double`, or `Boolean`.
 */
public inline operator fun <reified T : Any> Settings.set(key: String, value: T?): Unit =
    if (value == null) {
        this -= key
    } else when (T::class) {
        Int::class -> putInt(key, value as Int)
        Long::class -> putLong(key, value as Long)
        String::class -> putString(key, value as String)
        Float::class -> putFloat(key, value as Float)
        Double::class -> putDouble(key, value as Double)
        Boolean::class -> putBoolean(key, value as Boolean)
        else -> throw IllegalArgumentException("Invalid type!")
    }

/** Equivalent to [Settings.remove] */
public inline operator fun Settings.set(key: String, @Suppress("UNUSED_PARAMETER") value: Nothing?): Unit = remove(key)
