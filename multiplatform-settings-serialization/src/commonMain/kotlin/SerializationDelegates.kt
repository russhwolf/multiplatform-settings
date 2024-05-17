/*
 * Copyright 2024 Russell Wolf
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

@file:JvmMultifileClass
@file:JvmName("SettingsSerializationKt")

package com.russhwolf.settings.serialization

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.nullable
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Returns a property delegate backed by this [Settings] via kotlinx.serialization. It reads and writes values using the
 * same logic as [encodeValue] and [decodeValue], and returns [defaultValue] on reads when not all data is present.
 */
@ExperimentalSerializationApi
@ExperimentalSettingsApi
public fun <T> Settings.serializedValue(
    serializer: KSerializer<T>,
    key: String? = null,
    defaultValue: T,
    context: SerializersModule = EmptySerializersModule()
): ReadWriteProperty<Any?, T> =
    SettingsSerializationDelegate(this, serializer, key, defaultValue, context)

/**
 * Returns a property delegate backed by this [Settings] via kotlinx.serialization. It reads and writes values using the
 * same logic as [encodeValue] and [decodeValue], and returns [defaultValue] on reads when not all data is present.
 *
 * @throws DeserializationException If serializer cannot be created (provided T or its type argument is not serializable).
 */
@ExperimentalSerializationApi
@ExperimentalSettingsApi
public inline fun <reified T> Settings.serializedValue(
    key: String? = null,
    defaultValue: T,
    context: SerializersModule = EmptySerializersModule()
): ReadWriteProperty<Any?, T> =
    serializedValue(serializer<T>(), key, defaultValue, context)

/**
 * Returns a property delegate backed by this [Settings] via kotlinx.serialization. It reads and writes values using the
 * same logic as [encodeValue] and [decodeValueOrNull], and returns `null` on reads when not all data is present.
 */
@ExperimentalSerializationApi
@ExperimentalSettingsApi
public fun <T : Any> Settings.nullableSerializedValue(
    serializer: KSerializer<T>,
    key: String? = null,
    context: SerializersModule = EmptySerializersModule()
): ReadWriteProperty<Any?, T?> =
    SettingsSerializationDelegate(this, serializer.nullable, key, null, context)

/**
 * Returns a property delegate backed by this [Settings] via kotlinx.serialization. It reads and writes values using the
 * same logic as [encodeValue] and [decodeValueOrNull], and returns `null` on reads when not all data is present.
 *
 * @throws DeserializationException If serializer cannot be created (provided T or its type argument is not serializable).
 */
@ExperimentalSerializationApi
@ExperimentalSettingsApi
public inline fun <reified T : Any> Settings.nullableSerializedValue(
    key: String? = null,
    context: SerializersModule = EmptySerializersModule()
): ReadWriteProperty<Any?, T?> = nullableSerializedValue(serializer<T>(), key, context)

@ExperimentalSerializationApi
private open class SettingsSerializationDelegate<T>(
    private val settings: Settings,
    private val serializer: KSerializer<T>,
    private val key: String?,
    private val defaultValue: T,
    private val context: SerializersModule
) : ReadWriteProperty<Any?, T> {
    /*
    A general note on the structure of this class:
    If we specified a nonnull key on init, it's easy to cache de/encoder which use this key.
    If we initialize with a null key, it's extremely likely we will set it once and then it will be constant. In that
    case, it makes sense to cache de/encoder. But this will have wrong behavior if delegate is reused for multiple
    properties, so we verify whether key has changed in checkKey() and clear de/encoder if necessary.

    TODO is it better to just ignore that case and document that you shouldn't reuse delegates?
     */

    private var innerKey: String? = key
    private var decoder: SettingsDecoder? = key?.let { key -> SettingsDecoder(settings, key, context) }
    private var encoder: SettingsEncoder? = key?.let { key -> SettingsEncoder(settings, key, context) }
    private fun checkKey(newKey: String) {
        if (key != null) return // If we specified a key, we never need to reset coders
        if (newKey != innerKey) {
            decoder = null
            encoder = null
        }
        innerKey = newKey
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): T {
        checkKey(property.name)
        val decoder = decoder ?: SettingsDecoder(settings, property.name, context).also { decoder = it }
        return serializer.deserializeOrElse(decoder, defaultValue)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        checkKey(property.name)
        val encoder = encoder ?: SettingsEncoder(settings, property.name, context).also { encoder = it }
        serializer.serialize(encoder, value)
    }
}
