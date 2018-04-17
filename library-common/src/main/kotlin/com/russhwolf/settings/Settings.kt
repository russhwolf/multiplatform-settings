package com.russhwolf.settings

expect class Settings {

    fun clear()

    fun putInt(key: String, value: Int)
    fun getInt(key: String, defaultValue: Int): Int

    fun putLong(key: String, value: Long)
    fun getLong(key: String, defaultValue: Long): Long

    fun putString(key: String, value: String)
    fun getString(key: String, defaultValue: String): String

    fun putFloat(key: String, value: Float)
    fun getFloat(key: String, defaultValue: Float): Float

    fun putDouble(key: String, value: Double)
    fun getDouble(key: String, defaultValue: Double): Double

    fun putBoolean(key: String, value: Boolean)
    fun getBoolean(key: String, defaultValue: Boolean): Boolean

}
