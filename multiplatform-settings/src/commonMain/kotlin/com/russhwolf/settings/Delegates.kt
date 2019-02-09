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

@file:Suppress("RedundantVisibilityModifier")

package com.russhwolf.settings

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/** Returns an [Int] property delegate, backed by this [Settings] instance using the provided [key], with initial value [defaultValue] */
public fun Settings.int(key: String? = null, defaultValue: Int = 0): ReadWriteProperty<Any?, Int> =
        SettingsDelegate( key, defaultValue, getter(), setter() )

/** Returns a [Long] property delegate, backed by this [Settings] instance using the provided [key], with initial value [defaultValue] */
public fun Settings.long(key: String? = null, defaultValue: Long = 0): ReadWriteProperty<Any?, Long> =
        SettingsDelegate( key, defaultValue, getter(), setter() )

/** Returns a [String] property delegate, backed by this [Settings] instance using the provided [key], with initial value [defaultValue] */
public fun Settings.string(key: String? = null, defaultValue: String = ""): ReadWriteProperty<Any?, String> =
        SettingsDelegate( key, defaultValue, getter(), setter() )

/** Returns a [Float] property delegate, backed by this [Settings] instance using the provided [key], with initial value [defaultValue] */
public fun Settings.float(key: String? = null, defaultValue: Float = 0f): ReadWriteProperty<Any?, Float> =
        SettingsDelegate( key, defaultValue, getter(), setter() )

/** Returns a [Double] property delegate, backed by this [Settings] instance using the provided [key], with initial value [defaultValue] */
public fun Settings.double(key: String? = null, defaultValue: Double = 0.0): ReadWriteProperty<Any?, Double> =
        SettingsDelegate( key, defaultValue, getter(), setter() )

/** Returns a [Boolean] property delegate, backed by this [Settings] instance using the provided [key], with initial value [defaultValue] */
public fun Settings.boolean(key: String? = null, defaultValue: Boolean = false): ReadWriteProperty<Any?, Boolean> =
        SettingsDelegate( key, defaultValue, getter(), setter() )

/** Returns a nullable [Int] property delegate, backed by this [Settings] instance using the provided [key], with initial value `null */
public fun Settings.nullableInt(key: String? = null): ReadWriteProperty<Any?, Int?> =
        OptSettingsDelegate<Int?>( key, getter(), setter() )

/** Returns a nullable [Long] property delegate, backed by this [Settings] instance using the provided [key], with initial value `null */
public fun Settings.nullableLong(key: String? = null): ReadWriteProperty<Any?, Long?> =
        OptSettingsDelegate<Long?>( key, getter(), setter() )

/** Returns a nullable [String] property delegate, backed by this [Settings] instance using the provided [key], with initial value `null */
public fun Settings.nullableString(key: String? = null): ReadWriteProperty<Any?, String?> =
        OptSettingsDelegate<String?>( key, getter(), setter() )

/** Returns a nullable [Float] property delegate, backed by this [Settings] instance using the provided [key], with initial value `null */
public fun Settings.nullableFloat(key: String? = null): ReadWriteProperty<Any?, Float?> =
        OptSettingsDelegate<Float?>( key, getter(), setter() )

/** Returns a nullable [Double] property delegate, backed by this [Settings] instance using the provided [key], with initial value `null */
public fun Settings.nullableDouble(key: String? = null): ReadWriteProperty<Any?, Double?> =
        OptSettingsDelegate<Double?>( key, getter(), setter() )

/** Returns a nullable [Boolean] property delegate, backed by this [Settings] instance using the provided [key], with initial value `null */
public fun Settings.nullableBoolean(key: String? = null): ReadWriteProperty<Any?, Boolean?> =
        OptSettingsDelegate<Boolean?>( key, getter(), setter() )


/** Returns an [Int] property delegate, backed by this [Settings] instance using the provided [key], with initial value [defaultValue] */
public inline operator fun <reified V: Any> Settings.invoke( key: String? = null, defaultValue: V ): ReadWriteProperty<Any?, V> =
        SettingsDelegate( key, defaultValue , getter(), setter() )

/**
 * Returns a nullable [V] property delegate, backed by this [Settings] instance using the provided [key], with initial value `null`
 * @throws IllegalArgumentException if [V] is not nullable.
 */
public inline operator fun <reified V: Any?> Settings.invoke( key: String? = null ): ReadWriteProperty<Any?, V?> {
    if ( null !is V /* T is not nullable */ )
        throw IllegalArgumentException(
                "A default value must be declared if return type `${V::class.qualifiedName}` is not nullable"
        )
    return OptSettingsDelegate<V>( key , getter(), setter() )
}

@PublishedApi
internal inline fun <reified V: Any?> Settings.getter(): (String) -> V? = { key -> get<V>( key ) }
@PublishedApi
internal inline fun <reified V: Any?> Settings.setter(): (String, V) -> Unit = { key, value -> set( key, value ) }

/**
 * An [OptKeyDelegate] for a NOT NULLABLE [V] value of [Settings]. Injected [getter] and [setter] are required because
 * class is not inlined, so it can't hold a *reified* [V], which a function can.
 */
@PublishedApi
internal class SettingsDelegate<V: Any>(
        key: String?,
        private val default: V,
        private val getter: (String) -> V?,
        private val setter: (String, V) -> Unit
): OptKeyDelegate<V>( key ) {

    override fun getValue( key: String ): V = getter( key ) ?: default
    override fun setValue( key: String, value: V ) {
        setter( key, value )
    }
}

/**
 * An [OptKeyDelegate] for a NULLABLE [V] value of [Settings]. Injected [getter] and [setter] are required because
 * class is not inlined, so it can't hold a *reified* [V], which a function can.
 */
@PublishedApi
internal open class OptSettingsDelegate<V: Any?>(
        key: String?,
        private val getter: (String) -> V?,
        private val setter: (String, V?) -> Unit
): OptKeyDelegate<V?>( key ) {

    override fun getValue( key: String ): V? = getter( key )
    override fun setValue( key: String, value: V? ) {
        setter( key, value )
    }
}

/**
 * A [ReadWriteProperty] that `get` and `set` through a [String] key.
 * @param _key an OPTIONAl key [String], if this value is null, the name [KProperty.name] will be used as key.
 */
internal abstract class OptKeyDelegate<T: Any?>( _key: String? ): ReadWriteProperty<Any?, T> {
    private var finalKey: String? = _key
    private val KProperty<*>.key: String get() {
        if ( finalKey == null ) finalKey = name
        return finalKey!!
    }

    abstract fun getValue( key: String ): T
    abstract fun setValue( key: String, value: T )

    override fun getValue( thisRef: Any?, property: KProperty<*> ): T = getValue( property.key )
    override fun setValue( thisRef: Any?, property: KProperty<*>, value: T ) {
        setValue( property.key, value )
    }
}