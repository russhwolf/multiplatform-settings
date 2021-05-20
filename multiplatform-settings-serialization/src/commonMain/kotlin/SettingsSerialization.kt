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

@file:Suppress("NOTHING_TO_INLINE")

package com.russhwolf.settings.serialization

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import com.russhwolf.settings.contains
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.nullable
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
 * Encode a structured value to this [Settings] via kotlinx.serialization.
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
 * For example, consider an instance `user` of a class defined as
 * ```kotlin
 * @Serializable
 * class User(val nickname: String?)
 * ```
 * Calling `encodeValue(User.serializer(), "user", user)` is equivalent to
 * ```kotlin
 * if (user.nickname != null) putString("user.nickname", user.nickname) else remove("user.nickname")
 * putBoolean("user.nickname?", user.nickname != null)
 * ```
 *
 * Note that because the `Settings` API is not transactional, it's possible for a failed operation to result in
 * inconsistent data being saved to disk. If you need greater reliability for more complex structured data, prefer a
 * database such as sqlite to this API.
 */
@ExperimentalSerializationApi
@ExperimentalSettingsApi
public fun <T> Settings.encodeValue(
    serializer: KSerializer<T>,
    key: String,
    value: T,
    serializersModule: SerializersModule = EmptySerializersModule
): Unit =
    serializer.serialize(SettingsEncoder(this, key, serializersModule), value)

/**
 * Decode a structured value using the data in this [Settings] via kotlinx.serialization. If all expected data for that
 * value is not present, then [defaultValue] will be returned instead.
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
 * For example, consider a class defined as
 * ```kotlin
 * @Serializable
 * class User(val nickname: String?)
 * ```
 * A function `fun getUser() = decodeValue(User.serializer(), "user", defaultValue)` is equivalent to
 * ```kotlin
 * fun getUser() = User(
 *     nickname = when(getBooleanOrNull("user.nickname?")) {
 *         true -> getStringOrNull("user.nickname") ?: return defaultValue
 *         false -> null
 *         null -> return defaultValue
 *     }
 * )
 * ```
 *
 * Note that because the `Settings` API is not transactional, it's possible for a failed operation to result in
 * inconsistent data being deserialized. If you need greater reliability for more complex structured data, prefer a
 * database such as sqlite to this API.
 */
@ExperimentalSerializationApi
@ExperimentalSettingsApi
public fun <T> Settings.decodeValue(
    serializer: KSerializer<T>,
    key: String,
    defaultValue: T,
    serializersModule: SerializersModule = EmptySerializersModule
): T = deserializeOrElse(defaultValue) { serializer.deserialize(SettingsDecoder(this, key, serializersModule)) }

/**
 * Decode a structured value using the data in this [Settings] via kotlinx.serialization. If all expected data for that
 * value is not present, then `null` will be returned instead.`
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
 * For example, consider a class defined as
 * ```kotlin
 * @Serializable
 * class User(val nickname: String?)
 * ```
 * A function `fun getUser() = decodeValueOrNull(User.serializer(), "user")` is equivalent to
 * ```kotlin
 * fun getUser() = User(
 *     nickname = when(getBooleanOrNull("user.nickname?")) {
 *         true -> getStringOrNull("user.nickname") ?: return null
 *         false -> null
 *         null -> return null
 *     }
 * )
 * ```
 *
 * Note that because the `Settings` API is not transactional, it's possible for a failed operation to result in
 * inconsistent data being deserialized. If you need greater reliability for more complex structured data, prefer a
 * database such as sqlite to this API.
 */
@ExperimentalSerializationApi
@ExperimentalSettingsApi
public fun <T> Settings.decodeValueOrNull(
    serializer: KSerializer<T>,
    key: String,
    serializersModule: SerializersModule = EmptySerializersModule
): T? = deserializeOrElse(null) { serializer.deserialize(SettingsDecoder(this, key, serializersModule)) }

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
    context: SerializersModule = EmptySerializersModule
): ReadWriteProperty<Any?, T> =
    SettingsSerializationDelegate(this, serializer, key, defaultValue, context)

/**
 * Returns a property delegate backed by this [Settings] via kotlinx.serialization. It reads and writes values using the
 * same logic as [encodeValue] and [decodeValueOrNull], and returns `null` on reads when not all data is present.
 */
@ExperimentalSerializationApi
@ExperimentalSettingsApi
public fun <T : Any> Settings.nullableSerializedValue(
    serializer: KSerializer<T>,
    key: String? = null,
    context: SerializersModule = EmptySerializersModule
): ReadWriteProperty<Any?, T?> =
    SettingsSerializationDelegate(this, serializer.nullable, key, null, context)

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
        // TODO ??? for some reason jsLegacy delegate tests fail when this uses deserializeOrElse()
        return try {
            serializer.deserialize(decoder)
        } catch (e: DeserializationException) {
            defaultValue
        }
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

    // Stack of keys to track what we're encoding next
    private val keyStack = ArrayDeque<String>().apply { add(key) }
    private fun getKey() = keyStack.joinToString(".")

    // Depth increases with beginStructure() and decreases with endStructure(). Subtly different from keyStack size.
    // This is important so we can tell whether the last key on the stack refers to the current parent or a sibling.
    private var depth = 0

    public override fun encodeElement(descriptor: SerialDescriptor, index: Int): Boolean {
        if (keyStack.size > depth) {
            keyStack.removeLast()
        }
        keyStack.add(descriptor.getElementName(index))
        return true
    }


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
    public override fun encodeChar(value: Char) = settings.putInt(getKey(), value.code)
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

    // Stacks of keys and indices so we can track index at arbitrary levels to know what we're decoding next
    private val keyStack = ArrayDeque<String>().apply { add(key) }
    private val indexStack = ArrayDeque<Int>().apply { add(0) }
    private fun getKey(): String = keyStack.joinToString(".")

    // Depth increases with beginStructure() and decreases with endStructure(). Subtly different from stack sizes.
    // This is important so we can tell whether the last items on the stack refer to the current parent or a sibling.
    private var depth = 0


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

    private tailrec fun getNextIndex(descriptor: SerialDescriptor, size: Int): Int {
        val index = indexStack.removeLast()
        indexStack.addLast(index + 1)

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

    private inline fun isMissingAndOptional(descriptor: SerialDescriptor, index: Int): Boolean {
        val key = "${getKey()}.${descriptor.getElementName(index)}"
        // Descriptor shows key is optional, key is not present, and nullability doesn't indicate key should be present
        return descriptor.isElementOptional(index) && key !in settings && settings.getBooleanOrNull("$key?") != true
    }


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

    public override fun decodeCollectionSize(descriptor: SerialDescriptor): Int =
        settings.getIntOrNull("${getKey()}.size") ?: deserializationError()

    public override fun decodeNotNullMark(): Boolean =
        settings.getBooleanOrNull("${getKey()}?") ?: deserializationError()

    public override fun decodeNull(): Nothing? = null

    // Unfortunately the only way we can interrupt serialization if data is missing is to throw here and catch elsewhere
    public override fun decodeBoolean(): Boolean = settings.getBooleanOrNull(getKey()) ?: deserializationError()
    public override fun decodeByte(): Byte = settings.getIntOrNull(getKey())?.toByte() ?: deserializationError()
    public override fun decodeChar(): Char {
        // TODO ??? for some reason jsLegacy allTypes tests fail when this is an expression function.
        return settings.getIntOrNull(getKey())?.toChar() ?: deserializationError()
    }

    public override fun decodeDouble(): Double = settings.getDoubleOrNull(getKey()) ?: deserializationError()
    public override fun decodeEnum(enumDescriptor: SerialDescriptor): Int =
        settings.getIntOrNull(getKey()) ?: deserializationError()

    public override fun decodeFloat(): Float = settings.getFloatOrNull(getKey()) ?: deserializationError()
    public override fun decodeInt(): Int = settings.getIntOrNull(getKey()) ?: deserializationError()
    public override fun decodeLong(): Long = settings.getLongOrNull(getKey()) ?: deserializationError()
    public override fun decodeShort(): Short = settings.getIntOrNull(getKey())?.toShort() ?: deserializationError()
    public override fun decodeString(): String = settings.getStringOrNull(getKey()) ?: deserializationError()
}

private class DeserializationException : IllegalStateException()

private inline fun deserializationError(): Nothing = throw DeserializationException()
private inline fun <T> deserializeOrElse(defaultValue: T, block: () -> T) =
    try {
        block()
    } catch (_: DeserializationException) {
        defaultValue
    }
