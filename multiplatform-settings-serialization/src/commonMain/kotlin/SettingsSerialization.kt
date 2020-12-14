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

package com.russhwolf.settings.serialization

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.contains
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

/**
 * Serialize a structured value to this [Settings].
 *
 * Primitive properties are serialized by combining the [key] parameter with the property name. Non-primitive properties
 * recurse through their structure to find primitives.
 *
 * Nullable properties add an additional `Boolean` value, whose key is the name of that property with "?" appended. This
 * will be set to `true` if the property was present and `false` if it was `null`.
 *
 * Similarly, collection properties encode an additional `Int` to represent the collection size, whose key is the
 * property's name with `.size` appended.
 *
 * For example, consider an instance `myClass` of a class defined as
 * ```kotlin
 * @Serializable
 * class MyClass(val myProperty: Int?)
 * ```
 * Calling `serializeValue(MyClass.serializer(), "myClass", myClass)` is equivalent to
 * ```kotlin
 * if (myClass.myProperty != null) putInt("myClass.myProperty", myClass.myProperty)
 * putBoolean("myClass.myProperty?", myClass.myProperty != null)
 * ```
 *
 * Note that because the `Settings` API is not transactional, it's possible for a failed operation to result in
 * inconsistent data being saved to disk. If you need greater reliability for more complex structured data, prefer a
 * sqlite database to this API.
 */
@ExperimentalSerializationApi
@ExperimentalSettingsApi
public fun <T> Settings.serializeValue(
    serializer: KSerializer<T>,
    key: String,
    value: T,
    serializersModule: SerializersModule = EmptySerializersModule
): Unit =
    serializer.serialize(SettingsEncoder(this, key, serializersModule), value)

/**
 * Deserialize a structured value using the data in this [Settings].
 *
 * Primitive properties are serialized by combining the [key] parameter with the property name. Non-primitive properties
 * recurse through their structure to find primitives.
 *
 * Nullable properties first read an additional `Boolean` value, whose key is the key for that property with "?"
 * appended. If this value is true, then the value at the property key will be used for deserialization.
 *
 * Similarly, collection properties read from an additional `Int` to represent the collection size, whose key is the
 * property's key with `.size` appended.
 *
 * For example, consider an instance `myClass` of a class defined as
 * ```kotlin
 * @Serializable
 * class MyClass(val myProperty: Int?)
 * ```
 * Calling `val myClass = deserializeValue(MyClass.serializer(), "myClass")` is equivalent to
 * ```kotlin
 * val myClass = MyClass(
 *     myProperty = if (getBoolean("myClass.myProperty?")) {
 *         getInt("myClass.myProperty")
 *     } else {
 *         null
 *     }
 * )
 * ```
 *
 * Note that because the `Settings` API is not transactional, it's possible for a failed operation to result in
 * inconsistent data being deserialized. If you need greater reliability for more complex structured data, prefer a
 * sqlite database to this API.
 */
@ExperimentalSerializationApi
@ExperimentalSettingsApi
public fun <T> Settings.deserializeValue(
    serializer: KSerializer<T>,
    key: String,
    serializersModule: SerializersModule = EmptySerializersModule
): T =
    serializer.deserialize(SettingsDecoder(this, key, serializersModule))

/**
 * Returns a property delegate backed by this [Settings]. It reads and writes values using the same logic as
 * [serializeValue] and [deserializeValue].
 */
@ExperimentalSerializationApi
@ExperimentalSettingsApi
public fun <T> Settings.serializationDelegate(
    serializer: KSerializer<T>,
    key: String? = null,
    context: SerializersModule = EmptySerializersModule
): ReadWriteProperty<Any?, T> =
    SettingsSerializationDelegate(this, serializer, key, context)

@ExperimentalSerializationApi
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

@ExperimentalSerializationApi
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
    public override fun encodeNull() {
        settings.remove(getKey())
        settings.putBoolean("${getKey()}?", false)
    }

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

@ExperimentalSerializationApi
private class SettingsDecoder(
    private val settings: Settings,
    private val key: String,
    public override val serializersModule: SerializersModule
) : AbstractDecoder() {

    // Stacks of keys and indices so we can track index at arbitrary levels
    private val keyStack = ArrayDeque<String>().apply { add(key) }
    private val indexStack = ArrayDeque<Int>().apply { add(0) }
    private fun getKey(): String = keyStack.joinToString(".")

    public override fun decodeElementIndex(descriptor: SerialDescriptor): Int {
        if (keyStack.size > depth) {
            keyStack.removeLast()
            indexStack.removeLast()
        }

        // Can usually ask descriptor for a size, except for collections
        val size = when (descriptor.kind) {
            StructureKind.LIST -> decodeCollectionSize(descriptor)
            StructureKind.MAP -> 2 * decodeCollectionSize(descriptor) // Maps look like lists [k1, v1, k2, v2, ...]
            else -> descriptor.elementsCount
        }

        return getNextIndex(descriptor, size)
    }

    @Suppress("UNUSED_PARAMETER")
    private tailrec fun getNextIndex(descriptor: SerialDescriptor, size: Int): Int {
        val index = indexStack.last()
        indexStack[indexStack.lastIndex] = index + 1

        return when {
            index >= size -> DECODE_DONE
            isMissingAndOptional(descriptor, index) -> getNextIndex(descriptor, size)
            else -> {
                keyStack.add(descriptor.getElementName(index))
                indexStack.add(0)
                index
            }
        }
    }

    private fun isMissingAndOptional(descriptor: SerialDescriptor, index: Int): Boolean {
        val key = "${getKey()}.${descriptor.getElementName(index)}"
        return descriptor.isElementOptional(index) && key !in settings && settings.getBooleanOrNull("$key?") != true
    }

    private var depth = 0

    public override fun beginStructure(descriptor: SerialDescriptor): CompositeDecoder {
        depth++
        return super.beginStructure(descriptor)
    }

    public override fun endStructure(descriptor: SerialDescriptor) {
        depth--
        keyStack.removeLast()
        indexStack.removeLast()
        if (keyStack.isEmpty()) {
            // We've reached the end of everything, so reset for potential decoder reuse
            keyStack.add(key)
            indexStack.add(0)
        }
    }

    public override fun decodeCollectionSize(descriptor: SerialDescriptor): Int = settings.getInt("${getKey()}.size")

    public override fun decodeNotNullMark(): Boolean = settings.getBoolean("${getKey()}?")
    public override fun decodeNull(): Nothing? = null

    // NB These gets will be 0/""/false if the class has no default value set and no data is present.
    // TODO should this be configurable? Maybe we'd rather throw than get bad data.
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
