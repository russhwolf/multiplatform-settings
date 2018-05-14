package com.russhwolf.settings

expect class Settings {

    fun clear()

    fun remove(key: String)
    fun contains(key: String): Boolean

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
