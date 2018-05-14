package com.russhwolf.settings

import android.content.SharedPreferences

actual class Settings(private val delegate: SharedPreferences) {

    actual fun clear() = delegate.edit().clear().apply()

    actual fun remove(key: String) = delegate.edit().remove(key).apply()
    actual fun contains(key: String) = delegate.contains(key)

    actual fun putInt(key: String, value: Int) = delegate.edit().putInt(key, value).apply()
    actual fun getInt(key: String, defaultValue: Int): Int = delegate.getInt(key, defaultValue)

    actual fun putLong(key: String, value: Long) = delegate.edit().putLong(key, value).apply()
    actual fun getLong(key: String, defaultValue: Long): Long = delegate.getLong(key, defaultValue)

    actual fun putString(key: String, value: String) = delegate.edit().putString(key, value).apply()
    actual fun getString(key: String, defaultValue: String): String = delegate.getString(key, defaultValue)

    actual fun putFloat(key: String, value: Float) = delegate.edit().putFloat(key, value).apply()
    actual fun getFloat(key: String, defaultValue: Float): Float = delegate.getFloat(key, defaultValue)

    actual fun putDouble(key: String, value: Double) = putLong(key, value.toRawBits())
    actual fun getDouble(key: String, defaultValue: Double): Double =
        Double.fromBits(getLong(key, defaultValue.toRawBits()))

    actual fun putBoolean(key: String, value: Boolean) = delegate.edit().putBoolean(key, value).apply()
    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean = delegate.getBoolean(key, defaultValue)
}
