package com.russhwolf.settings

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

fun Settings.int(key: String, defaultValue: Int = 0): ReadWriteProperty<Any?, Int> =
    IntDelegate(this, key, defaultValue)

fun Settings.long(key: String, defaultValue: Long = 0): ReadWriteProperty<Any?, Long> =
    LongDelegate(this, key, defaultValue)

fun Settings.string(key: String, defaultValue: String = ""): ReadWriteProperty<Any?, String> =
    StringDelegate(this, key, defaultValue)

fun Settings.float(key: String, defaultValue: Float = 0f): ReadWriteProperty<Any?, Float> =
    FloatDelegate(this, key, defaultValue)

fun Settings.double(key: String, defaultValue: Double = 0.0): ReadWriteProperty<Any?, Double> =
    DoubleDelegate(this, key, defaultValue)

fun Settings.boolean(key: String, defaultValue: Boolean = false): ReadWriteProperty<Any?, Boolean> =
    BooleanDelegate(this, key, defaultValue)

fun Settings.nullableInt(key: String): ReadWriteProperty<Any?, Int?> = NullableIntDelegate(this, key)

fun Settings.nullableLong(key: String): ReadWriteProperty<Any?, Long?> = NullableLongDelegate(this, key)

fun Settings.nullableString(key: String): ReadWriteProperty<Any?, String?> = NullableStringDelegate(this, key)

fun Settings.nullableFloat(key: String): ReadWriteProperty<Any?, Float?> = NullableFloatDelegate(this, key)

fun Settings.nullableDouble(key: String): ReadWriteProperty<Any?, Double?> = NullableDoubleDelegate(this, key)

fun Settings.nullableBoolean(key: String): ReadWriteProperty<Any?, Boolean?> = NullableBooleanDelegate(this, key)

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
    override fun getValue(thisRef: Any?, property: KProperty<*>): Int? =
        if (key in settings) settings[key, 0] else null

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

