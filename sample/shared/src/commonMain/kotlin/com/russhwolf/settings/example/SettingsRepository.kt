/*
 * Copyright 2020 Russell Wolf
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

package com.russhwolf.settings.example

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SettingsListener

/**
 * This class demonstrates common code exercising all of the functionality of the [Settings] class.
 * The majority of this functionality is delegated to [SettingConfig] subclasses for each supported type.
 */
class SettingsRepository(private val settings: Settings) {

    val mySettings: List<SettingConfig<*>> = listOf(
        StringSettingConfig(settings, "MY_STRING", "default"),
        IntSettingConfig(settings, "MY_INT", -1),
        LongSettingConfig(settings, "MY_LONG", -1),
        FloatSettingConfig(settings, "MY_FLOAT", -1f),
        DoubleSettingConfig(settings, "MY_DOUBLE", -1.0),
        BooleanSettingConfig(settings, "MY_BOOLEAN", true),
        NullableStringSettingConfig(settings, "MY_NULLABLE_STRING"),
        NullableIntSettingConfig(settings, "MY_NULLABLE_INT"),
        NullableLongSettingConfig(settings, "MY_NULLABLE_LONG"),
        NullableFloatSettingConfig(settings, "MY_NULLABLE_FLOAT"),
        NullableDoubleSettingConfig(settings, "MY_NULLABLE_DOUBLE"),
        NullableBooleanSettingConfig(settings, "MY_NULLABLE_BOOLEAN")
    )

    fun clear() = settings.clear()
}

/**
 * This class wraps all of the different operations that might be performed on a given [key], and adds an interface to
 * get and set it as a [String] value.
 */
sealed class SettingConfig<T>(
    private val settings: Settings,
    val key: String,
    private val defaultValue: T
) {
    protected abstract fun getStringValue(settings: Settings, key: String, defaultValue: T): String
    protected abstract fun setStringValue(settings: Settings, key: String, value: String)
    protected abstract fun addListener(
        settings: ObservableSettings,
        key: String,
        defaultValue: T,
        callback: (T) -> Unit
    ): SettingsListener

    private var listener: SettingsListener? = null

    fun remove() = settings.remove(key)
    fun exists(): Boolean = settings.hasKey(key)

    fun get(): String = getStringValue(settings, key, defaultValue)
    fun set(value: String): Boolean {
        return try {
            setStringValue(settings, key, value)
            true
        } catch (exception: Exception) {
            false
        }
    }

    var isLoggingEnabled: Boolean
        get() = listener != null
        set(value) {
            val settings = settings as? ObservableSettings ?: return
            listener = if (value) {
                listener?.deactivate() // just in case
                addListener(settings, key, defaultValue) { println("$key = ${get()}") }
            } else {
                listener?.deactivate()
                null
            }
        }

    override fun toString() = key
}

sealed class NullableSettingConfig<T : Any>(
    settings: Settings,
    key: String
) : SettingConfig<T?>(settings, key, null) {

    protected abstract fun getStringValue(settings: Settings, key: String): String

    final override fun getStringValue(settings: Settings, key: String, defaultValue: T?): String =
        getStringValue(settings, key)

    protected abstract fun addListener(
        settings: ObservableSettings,
        key: String,
        callback: (T?) -> Unit
    ): SettingsListener

    final override fun addListener(
        settings: ObservableSettings,
        key: String,
        defaultValue: T?,
        callback: (T?) -> Unit
    ): SettingsListener =
        addListener(settings, key, null, callback)
}

class StringSettingConfig(settings: Settings, key: String, defaultValue: String) :
    SettingConfig<String>(settings, key, defaultValue) {

    override fun getStringValue(settings: Settings, key: String, defaultValue: String): String =
        settings.getString(key, defaultValue)

    override fun setStringValue(settings: Settings, key: String, value: String) =
        settings.putString(key, value)

    override fun addListener(
        settings: ObservableSettings,
        key: String,
        defaultValue: String,
        callback: (String) -> Unit
    ): SettingsListener =
        settings.addStringListener(key, defaultValue, callback)
}

class IntSettingConfig(settings: Settings, key: String, defaultValue: Int) :
    SettingConfig<Int>(settings, key, defaultValue) {

    override fun getStringValue(settings: Settings, key: String, defaultValue: Int): String =
        settings.getInt(key, defaultValue).toString()

    override fun setStringValue(settings: Settings, key: String, value: String) =
        settings.putInt(key, value.toInt())

    override fun addListener(
        settings: ObservableSettings,
        key: String,
        defaultValue: Int,
        callback: (Int) -> Unit
    ): SettingsListener =
        settings.addIntListener(key, defaultValue, callback)
}

class LongSettingConfig(settings: Settings, key: String, defaultValue: Long) :
    SettingConfig<Long>(settings, key, defaultValue) {

    override fun getStringValue(settings: Settings, key: String, defaultValue: Long): String =
        settings.getLong(key, defaultValue).toString()

    override fun setStringValue(settings: Settings, key: String, value: String) =
        settings.putLong(key, value.toLong())

    override fun addListener(
        settings: ObservableSettings,
        key: String,
        defaultValue: Long,
        callback: (Long) -> Unit
    ): SettingsListener =
        settings.addLongListener(key, defaultValue, callback)
}

class FloatSettingConfig(settings: Settings, key: String, defaultValue: Float) :
    SettingConfig<Float>(settings, key, defaultValue) {

    override fun getStringValue(settings: Settings, key: String, defaultValue: Float): String =
        settings.getFloat(key, defaultValue).toString()

    override fun setStringValue(settings: Settings, key: String, value: String) =
        settings.putFloat(key, value.toFloat())

    override fun addListener(
        settings: ObservableSettings,
        key: String,
        defaultValue: Float,
        callback: (Float) -> Unit
    ): SettingsListener =
        settings.addFloatListener(key, defaultValue, callback)
}

class DoubleSettingConfig(settings: Settings, key: String, defaultValue: Double) :
    SettingConfig<Double>(settings, key, defaultValue) {

    override fun getStringValue(settings: Settings, key: String, defaultValue: Double): String =
        settings.getDouble(key, defaultValue).toString()

    override fun setStringValue(settings: Settings, key: String, value: String) =
        settings.putDouble(key, value.toDouble())

    override fun addListener(
        settings: ObservableSettings,
        key: String,
        defaultValue: Double,
        callback: (Double) -> Unit
    ): SettingsListener =
        settings.addDoubleListener(key, defaultValue, callback)
}

class BooleanSettingConfig(settings: Settings, key: String, defaultValue: Boolean) :
    SettingConfig<Boolean>(settings, key, defaultValue) {

    override fun getStringValue(settings: Settings, key: String, defaultValue: Boolean): String =
        settings.getBoolean(key, defaultValue).toString()

    override fun setStringValue(settings: Settings, key: String, value: String) =
        settings.putBoolean(key, value.toBoolean())

    override fun addListener(
        settings: ObservableSettings,
        key: String,
        defaultValue: Boolean,
        callback: (Boolean) -> Unit
    ): SettingsListener =
        settings.addBooleanListener(key, defaultValue, callback)
}

class NullableStringSettingConfig(settings: Settings, key: String) :
    NullableSettingConfig<String>(settings, key) {

    override fun getStringValue(settings: Settings, key: String): String =
        settings.getStringOrNull(key).toString()

    override fun setStringValue(settings: Settings, key: String, value: String) =
        settings.putString(key, value)

    override fun addListener(settings: ObservableSettings, key: String, callback: (String?) -> Unit): SettingsListener =
        settings.addStringOrNullListener(key, callback)
}

class NullableIntSettingConfig(settings: Settings, key: String) :
    NullableSettingConfig<Int>(settings, key) {

    override fun getStringValue(settings: Settings, key: String): String =
        settings.getIntOrNull(key).toString()

    override fun setStringValue(settings: Settings, key: String, value: String) =
        settings.putInt(key, value.toInt())

    override fun addListener(settings: ObservableSettings, key: String, callback: (Int?) -> Unit): SettingsListener =
        settings.addIntOrNullListener(key, callback)
}

class NullableLongSettingConfig(settings: Settings, key: String) :
    NullableSettingConfig<Long>(settings, key) {

    override fun getStringValue(settings: Settings, key: String): String =
        settings.getLongOrNull(key).toString()

    override fun setStringValue(settings: Settings, key: String, value: String) =
        settings.putLong(key, value.toLong())

    override fun addListener(settings: ObservableSettings, key: String, callback: (Long?) -> Unit): SettingsListener =
        settings.addLongOrNullListener(key, callback)
}

class NullableFloatSettingConfig(settings: Settings, key: String) :
    NullableSettingConfig<Float>(settings, key) {

    override fun getStringValue(settings: Settings, key: String): String =
        settings.getFloatOrNull(key).toString()

    override fun setStringValue(settings: Settings, key: String, value: String) =
        settings.putFloat(key, value.toFloat())

    override fun addListener(settings: ObservableSettings, key: String, callback: (Float?) -> Unit): SettingsListener =
        settings.addFloatOrNullListener(key, callback)
}

class NullableDoubleSettingConfig(settings: Settings, key: String) :
    NullableSettingConfig<Double>(settings, key) {

    override fun getStringValue(settings: Settings, key: String): String =
        settings.getDoubleOrNull(key).toString()

    override fun setStringValue(settings: Settings, key: String, value: String) =
        settings.putDouble(key, value.toDouble())

    override fun addListener(settings: ObservableSettings, key: String, callback: (Double?) -> Unit): SettingsListener =
        settings.addDoubleOrNullListener(key, callback)
}

class NullableBooleanSettingConfig(settings: Settings, key: String) :
    NullableSettingConfig<Boolean>(settings, key) {

    override fun getStringValue(settings: Settings, key: String): String =
        settings.getBooleanOrNull(key).toString()

    override fun setStringValue(settings: Settings, key: String, value: String) =
        settings.putBoolean(key, value.toBoolean())

    override fun addListener(
        settings: ObservableSettings,
        key: String,
        callback: (Boolean?) -> Unit
    ): SettingsListener =
        settings.addBooleanOrNullListener(key, callback)
}
