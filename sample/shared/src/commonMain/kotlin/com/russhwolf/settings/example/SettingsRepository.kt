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

package com.russhwolf.settings.example

import com.russhwolf.settings.ExperimentalListener
import com.russhwolf.settings.ObservableSettings
import kotlin.properties.ReadWriteProperty

import com.russhwolf.settings.Settings
import com.russhwolf.settings.SettingsListener
import com.russhwolf.settings.boolean
import com.russhwolf.settings.contains
import com.russhwolf.settings.double
import com.russhwolf.settings.float
import com.russhwolf.settings.int
import com.russhwolf.settings.long
import com.russhwolf.settings.minusAssign
import com.russhwolf.settings.nullableBoolean
import com.russhwolf.settings.nullableDouble
import com.russhwolf.settings.nullableFloat
import com.russhwolf.settings.nullableInt
import com.russhwolf.settings.nullableLong
import com.russhwolf.settings.nullableString
import com.russhwolf.settings.string

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
 * get and set it as a [String] value..
 */
sealed class SettingConfig<T>(
    private val settings: Settings,
    val key: String,
    defaultValue: T,
    delegate: Settings.(String, T) -> ReadWriteProperty<Any?, T>,
    private val toType: String.() -> T
) {
    private var value: T by settings.delegate(key, defaultValue)

    @ExperimentalListener
    private var listener: SettingsListener? = null

    fun remove() {
        settings -= key
    }

    fun exists(): Boolean = key in settings
    fun get(): String = value.toString()
    fun set(value: String): Boolean {
        return try {
            this.value = value.toType()
            true
        } catch (exception: Exception) {
            false
        }
    }

    @ExperimentalListener
    var isLoggingEnabled: Boolean
        get() = listener != null
        set(value) {
            val settings = settings as? ObservableSettings ?: return
            listener = if (value) {
                settings.addListener(key) { println("$key = ${get()}") }
            } else {
                listener?.let { settings.removeListener(it) }
                null
            }
        }

    override fun toString() = key
}

sealed class NullableSettingConfig<T : Any>(
    settings: Settings,
    key: String,
    delegate: Settings.(String) -> ReadWriteProperty<Any?, T?>,
    toType: String.() -> T
) : SettingConfig<T?>(settings, key, null, { it, _ -> delegate(it) }, toType)

class StringSettingConfig(settings: Settings, key: String, defaultValue: String = "") :
    SettingConfig<String>(settings, key, defaultValue, Settings::string, { this })

class IntSettingConfig(settings: Settings, key: String, defaultValue: Int = 0) :
    SettingConfig<Int>(settings, key, defaultValue, Settings::int, String::toInt)

class LongSettingConfig(settings: Settings, key: String, defaultValue: Long = 0) :
    SettingConfig<Long>(settings, key, defaultValue, Settings::long, String::toLong)

class FloatSettingConfig(settings: Settings, key: String, defaultValue: Float = 0f) :
    SettingConfig<Float>(settings, key, defaultValue, Settings::float, String::toFloat)

class DoubleSettingConfig(settings: Settings, key: String, defaultValue: Double = 0.0) :
    SettingConfig<Double>(settings, key, defaultValue, Settings::double, String::toDouble)

class BooleanSettingConfig(settings: Settings, key: String, defaultValue: Boolean = false) :
    SettingConfig<Boolean>(settings, key, defaultValue, Settings::boolean, String::toBoolean)

class NullableStringSettingConfig(settings: Settings, key: String) :
    NullableSettingConfig<String>(settings, key, Settings::nullableString, { this })

class NullableIntSettingConfig(settings: Settings, key: String) :
    NullableSettingConfig<Int>(settings, key, Settings::nullableInt, String::toInt)

class NullableLongSettingConfig(settings: Settings, key: String) :
    NullableSettingConfig<Long>(settings, key, Settings::nullableLong, String::toLong)

class NullableFloatSettingConfig(settings: Settings, key: String) :
    NullableSettingConfig<Float>(settings, key, Settings::nullableFloat, String::toFloat)

class NullableDoubleSettingConfig(settings: Settings, key: String) :
    NullableSettingConfig<Double>(settings, key, Settings::nullableDouble, String::toDouble)

class NullableBooleanSettingConfig(settings: Settings, key: String) :
    NullableSettingConfig<Boolean>(settings, key, Settings::nullableBoolean, String::toBoolean)
