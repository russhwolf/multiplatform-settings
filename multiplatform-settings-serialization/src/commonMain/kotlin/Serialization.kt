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

@file:JvmMultifileClass
@file:JvmName("SettingsSerializationKt")

package com.russhwolf.settings.serialization

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.Settings
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.modules.EmptySerializersModule
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer
import kotlin.jvm.JvmMultifileClass
import kotlin.jvm.JvmName

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
    serializersModule: SerializersModule = EmptySerializersModule()
): Unit =
    serializer.serialize(SettingsEncoder(this, key, serializersModule), value)

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
 * Note that because the `Settings` API is not transactional, it's possible for a failed operation to result in
 * inconsistent data being saved to disk. If you need greater reliability for more complex structured data, prefer a
 * database such as sqlite to this API.
 *
 * @throws SerializationException If serializer cannot be created (provided T or its type argument is not serializable).
 */
@ExperimentalSerializationApi
@ExperimentalSettingsApi
public inline fun <reified T> Settings.encodeValue(
    key: String,
    value: T,
    serializersModule: SerializersModule = EmptySerializersModule()
): Unit =
    encodeValue(serializer<T>(), key, value, serializersModule)

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
    serializersModule: SerializersModule = EmptySerializersModule()
): T = serializer.deserializeOrElse(SettingsDecoder(this, key, serializersModule), defaultValue)

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
 * Note that because the `Settings` API is not transactional, it's possible for a failed operation to result in
 * inconsistent data being deserialized. If you need greater reliability for more complex structured data, prefer a
 * database such as sqlite to this API.
 *
 * @throws SerializationException If serializer cannot be created (provided T or its type argument is not serializable).
 */
@ExperimentalSerializationApi
@ExperimentalSettingsApi
public inline fun <reified T> Settings.decodeValue(
    key: String,
    defaultValue: T,
    serializersModule: SerializersModule = EmptySerializersModule()
): T =
    decodeValue(serializer<T>(), key, defaultValue, serializersModule)

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
    serializersModule: SerializersModule = EmptySerializersModule()
): T? =
    serializer.deserializeOrElse(SettingsDecoder(this, key, serializersModule), null)

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
 * Note that because the `Settings` API is not transactional, it's possible for a failed operation to result in
 * inconsistent data being deserialized. If you need greater reliability for more complex structured data, prefer a
 * database such as sqlite to this API.
 *
 * @throws SerializationException If serializer cannot be created (provided T or its type argument is not serializable).
 */
@ExperimentalSerializationApi
@ExperimentalSettingsApi
public inline fun <reified T> Settings.decodeValueOrNull(
    key: String,
    serializersModule: SerializersModule = EmptySerializersModule()
): T? =
    decodeValueOrNull(serializer<T>(), key, serializersModule)

/**
 * Remove all data that encodes a structured value in this [Settings] via kotlinx.serialization. If not all expected
 * data for that value is present, then [ignorePartial] determined whether the values that do exist are removed.
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
 * Calling `removeValue(User.serializer(), "user")` is equivalent to
 * ```kotlin
 * remove("user.nickname?")
 * remove("user.nickname")
 * ```
 *
 * Note that because the `Settings` API is not transactional, it's possible for a failed operation to result in
 * inconsistent data being removed. If you need greater reliability for more complex structured data, prefer a
 * database such as sqlite to this API.
 */
@ExperimentalSerializationApi
@ExperimentalSettingsApi
public fun <T> Settings.removeValue(
    serializer: KSerializer<T>,
    key: String,
    ignorePartial: Boolean = false,
    serializersModule: SerializersModule = EmptySerializersModule(),
) {
    // TODO this can probably be optimized so we don't need to go through decoder twice
    if (ignorePartial && !containsValue(serializer, key)) {
        return
    }
    val enumerator = SettingsRemover(this, key, serializersModule)
    serializer.deserialize(enumerator)
    enumerator.removeKeys()
}

/**
 * Remove all data that encodes a structured value in this [Settings] via kotlinx.serialization. If not all expected
 * data for that value is present, then [ignorePartial] determined whether the values that do exist are removed.
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
 * Note that because the `Settings` API is not transactional, it's possible for a failed operation to result in
 * inconsistent data being removed. If you need greater reliability for more complex structured data, prefer a
 * database such as sqlite to this API.
 *
 * @throws SerializationException If serializer cannot be created (provided T or its type argument is not serializable).
 */
@ExperimentalSerializationApi
@ExperimentalSettingsApi
public inline fun <reified T> Settings.removeValue(
    key: String,
    ignorePartial: Boolean = false,
    serializersModule: SerializersModule = EmptySerializersModule(),
): Unit =
    removeValue(serializer<T>(), key, ignorePartial, serializersModule)

/**
 * Reports whether data that encodes a structured value is present in this [Settings] via kotlinx.serialization. If data
 * is only partially present, returns `false`.
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
 * Calling `containsValue(User.serializer(), "user")` is equivalent to
 * ```kotlin
 * when (getBooleanOrNull("user.nickname?")) {
 *     true -> contains("user.nickname")
 *     false -> true
 *     null -> false
 * }
 * ```
 */
@ExperimentalSerializationApi
@ExperimentalSettingsApi
public fun <T> Settings.containsValue(
    serializer: KSerializer<T>,
    key: String,
    serializersModule: SerializersModule = EmptySerializersModule()
): Boolean =
    decodeValueOrNull(serializer, key, serializersModule) != null

/**
 * Reports whether data that encodes a structured value is present in this [Settings] via kotlinx.serialization. If data
 * is only partially present, returns `false`.
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
 * @throws SerializationException If serializer cannot be created (provided T or its type argument is not serializable).
 */
@ExperimentalSerializationApi
@ExperimentalSettingsApi
public inline fun <reified T> Settings.containsValue(
    key: String,
    serializersModule: SerializersModule = EmptySerializersModule()
): Boolean = containsValue(serializer<T>(), key, serializersModule)

