/*
 * Copyright 2019 Russell Wolf
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

@file:OptIn(ExperimentalSerializationApi::class)

package com.russhwolf.settings.serialization

import com.russhwolf.settings.Settings
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.StructureKind
import kotlinx.serialization.encoding.AbstractDecoder
import kotlinx.serialization.encoding.AbstractEncoder
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.CompositeDecoder.Companion.DECODE_DONE
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

public fun <T> Settings.serializeValue(
    serializer: KSerializer<T>,
    key: String,
    obj: T,
    serializersModule: SerializersModule = EmptySerializersModule
): Unit =
    serializer.serialize(SettingsEncoder(this, key, serializersModule), obj)

public fun <T> Settings.deserializeValue(
    serializer: KSerializer<T>,
    key: String,
    serializersModule: SerializersModule = EmptySerializersModule
): T =
    serializer.deserialize(SettingsDecoder(this, key, serializersModule))

public fun <T> Settings.serializationDelegate(
    serializer: KSerializer<T>,
    key: String? = null,
    context: SerializersModule = EmptySerializersModule
): ReadWriteProperty<Any?, T> =
    SettingsSerializationDelegate(this, serializer, key, context)

private class SettingsSerializationDelegate<T>(
    private val settings: Settings,
    private val serializer: KSerializer<T>,
    private val key: String?,
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
        return serializer.deserialize(decoder)
    }

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        checkKey(property.name)
        val encoder = encoder ?: SettingsEncoder(settings, property.name, context).also { encoder = it }
        serializer.serialize(encoder, value)
    }
}

private class SettingsEncoder(
    private val settings: Settings,
    key: String,
    public override val serializersModule: SerializersModule
) : AbstractEncoder() {

    private val keyStack = ArrayDeque<String>().apply { add(key) }
    private fun getKey() = keyStack.joinToString(".")

    public override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        if (keyStack.size > depth) {
            keyStack.removeLast()
        }
        keyStack.add(descriptor.getElementName(index))
        return true
    }

    private var depth = 0

    public override fun beginStructure(descriptor: SerialDescriptor): CompositeEncoder {
        depth++
        return super.beginStructure(descriptor)
    }

    public override fun endStructure(descriptor: SerialDescriptor) {
        depth--
        keyStack.removeLast()
    }

    public override fun beginCollection(descriptor: SerialDescriptor, collectionSize: Int): CompositeEncoder {
        settings.putInt("${getKey()}.size", collectionSize)
        return super.beginCollection(descriptor, collectionSize)
    }

    public override fun encodeNotNullMark() = settings.putBoolean("${getKey()}?", true)
    public override fun encodeNull() = settings.putBoolean("${getKey()}?", false)

    public override fun encodeBoolean(value: Boolean) = settings.putBoolean(getKey(), value)
    public override fun encodeByte(value: Byte) = settings.putInt(getKey(), value.toInt())
    public override fun encodeChar(value: Char) = settings.putInt(getKey(), value.toInt())
    public override fun encodeDouble(value: Double) = settings.putDouble(getKey(), value)
    public override fun encodeEnum(enumDescriptor: SerialDescriptor, index: Int) = settings.putInt(getKey(), index)
    public override fun encodeFloat(value: Float) = settings.putFloat(getKey(), value)
    public override fun encodeInt(value: Int) = settings.putInt(getKey(), value)
    public override fun encodeLong(value: Long) = settings.putLong(getKey(), value)
    public override fun encodeShort(value: Short) = settings.putInt(getKey(), value.toInt())
    public override fun encodeString(value: String) = settings.putString(getKey(), value)
}

private class SettingsDecoder(
    private val settings: Settings,
    private val key: String,
    public override val serializersModule: SerializersModule
) : AbstractDecoder() {

    // Stack of pairs <key, index> so we can track index at arbitrary levels (could probably use some cleanup here)
    private val keyStack = ArrayDeque<Pair<String, Int>>().apply { add(key to 0) }
    private fun getKey(): String = keyStack.joinToString(".") { it.first }

    public override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (keyStack.size > depth) {
            keyStack.removeLast()
        }
        val index = keyStack.last().second
        keyStack[keyStack.lastIndex] = keyStack.last().first to index + 1

        // Can usually ask descriptor for a size, except for collections
        val size = when (descriptor.kind) {
            StructureKind.LIST -> decodeCollectionSize(descriptor)
            StructureKind.MAP -> 2 * decodeCollectionSize(descriptor) // Maps look like lists [k1, v1, k2, v2, ...]
            else -> descriptor.elementsCount
        }
        if (index < size) {
            keyStack.add(descriptor.getElementName(index) to 0)
            return index
        }
        return DECODE_DONE
    }

    private var depth = 0

    public override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        depth++
        return super.beginStructure(descriptor)
    }

    public override fun endStructure(descriptor: SerialDescriptor) {
        depth--
        keyStack.removeLast()
        if (keyStack.isEmpty()) {
            // We've reached the end of everything, so reset for potential decoder reuse
            keyStack.add(key to 0)
        }
    }

    public override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = settings.getInt("${getKey()}.size")

    public override fun decodeNotNullMark(): Boolean = settings.getBoolean("${getKey()}?")
    public override fun decodeNull(): Nothing? = null

    public override fun decodeBoolean(): Boolean = settings.getBoolean(getKey())
    public override fun decodeByte(): Byte = settings.getInt(getKey()).toByte()
    public override fun decodeChar(): Char = settings.getInt(getKey()).toChar()
    public override fun decodeDouble(): Double = settings.getDouble(getKey())
    public override fun decodeEnum(enumDescriptor: SerialDescriptor): Int = settings.getInt(getKey())
    public override fun decodeFloat(): Float = settings.getFloat(getKey())
    public override fun decodeInt(): Int = settings.getInt(getKey())
    public override fun decodeLong(): Long = settings.getLong(getKey())
    public override fun decodeShort(): Short = settings.getInt(getKey()).toShort()
    public override fun decodeString(): String = settings.getString(getKey())
}
