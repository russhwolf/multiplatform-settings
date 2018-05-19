package com.russhwolf.settings

expect class Settings {

    fun clear()

    fun remove(key: String)
    fun hasKey(key: String): Boolean

    fun putInt(key: String, value: Int)
    fun getInt(key: String, defaultValue: Int = 0): Int

    fun putLong(key: String, value: Long)
    fun getLong(key: String, defaultValue: Long = 0): Long

    fun putString(key: String, value: String)
    fun getString(key: String, defaultValue: String = ""): String

    fun putFloat(key: String, value: Float)
    fun getFloat(key: String, defaultValue: Float = 0f): Float

    fun putDouble(key: String, value: Double)
    fun getDouble(key: String, defaultValue: Double = 0.0): Double

    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean

}

operator fun Settings.contains(key: String) = hasKey(key)
operator fun Settings.minusAssign(key: String) = remove(key)
operator fun Settings.get(key: String, defaultValue: Int) = getInt(key, defaultValue)
operator fun Settings.get(key: String, defaultValue: Long) = getLong(key, defaultValue)
operator fun Settings.get(key: String, defaultValue: String) = getString(key, defaultValue)
operator fun Settings.get(key: String, defaultValue: Float) = getFloat(key, defaultValue)
operator fun Settings.get(key: String, defaultValue: Double) = getDouble(key, defaultValue)
operator fun Settings.get(key: String, defaultValue: Boolean) = getBoolean(key, defaultValue)
operator fun Settings.set(key: String, value: Int) = putInt(key, value)
operator fun Settings.set(key: String, value: Long) = putLong(key, value)
operator fun Settings.set(key: String, value: String) = putString(key, value)
operator fun Settings.set(key: String, value: Float) = putFloat(key, value)
operator fun Settings.set(key: String, value: Double) = putDouble(key, value)
operator fun Settings.set(key: String, value: Boolean) = putBoolean(key, value)
