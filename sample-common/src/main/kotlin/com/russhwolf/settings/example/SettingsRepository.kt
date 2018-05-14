package com.russhwolf.settings.example

import kotlin.properties.ReadWriteProperty

import com.russhwolf.settings.Settings
import com.russhwolf.settings.boolean
import com.russhwolf.settings.double
import com.russhwolf.settings.float
import com.russhwolf.settings.int
import com.russhwolf.settings.long
import com.russhwolf.settings.string

class SettingsRepository(private var settings: Settings) {
    val mySettings: List<SettingConfig<*>> = listOf(
        StringSettingConfig(settings, "MY_STRING"),
        IntSettingConfig(settings, "MY_INT"),
        LongSettingConfig(settings, "MY_LONG"),
        FloatSettingConfig(settings, "MY_FLOAT"),
        DoubleSettingConfig(settings, "MY_DOUBLE"),
        BooleanSettingConfig(settings, "MY_BOOLEAN")
    )

    fun clear() = settings.clear()
}

open class SettingConfig<T : Any>(
    private val settings: Settings,
    val key: String,
    defaultValue: T,
    delegate: Settings.(String, T) -> ReadWriteProperty<Any?, T>,
    private val toType: String.() -> T
) {
    private var value: T by settings.delegate(key, defaultValue)

    fun remove(): Unit = settings.remove(key)
    fun exists(): Boolean = settings.contains(key)
    fun get(): String = value.toString()
    fun set(value: String): Boolean {
        return try {
            this.value = value.toType()
            true
        } catch (exception: Exception) {
            false
        }
    }

    override fun toString() = key
}

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
