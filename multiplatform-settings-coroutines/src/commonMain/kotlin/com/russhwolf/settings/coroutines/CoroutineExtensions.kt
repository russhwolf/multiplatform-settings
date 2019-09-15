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

package com.russhwolf.settings.coroutines

import com.russhwolf.settings.ExperimentalListener
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@ExperimentalCoroutinesApi
@ExperimentalListener
private inline fun <T> ObservableSettings.createFlow(
    key: String,
    defaultValue: T,
    crossinline getter: Settings.(String, T) -> T
): Flow<T> = callbackFlow {
    offer(getter(key, defaultValue))
    val listener = addListener(key) {
        offer(getter(key, defaultValue))
    }
    awaitClose {
        listener.deactivate()
    }
}

@ExperimentalCoroutinesApi
@ExperimentalListener
private inline fun <T> ObservableSettings.createNullableFlow(
    key: String,
    crossinline getter: Settings.(String) -> T?
): Flow<T?> =
    createFlow<T?>(key, null) { it, _ -> getter(it) }

/**
 * Create a new flow, based on observing the given [key] as an `Int`. This flow will immediately emit the current
 * value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * [defaultValue] will be emitted instead.
 */
@ExperimentalCoroutinesApi
@ExperimentalListener
public fun ObservableSettings.intFlow(key: String, defaultValue: Int = 0): Flow<Int> =
    createFlow(key, defaultValue, Settings::getInt)

/**
 * Create a new flow, based on observing the given [key] as a `Long`. This flow will immediately emit the current
 * value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * [defaultValue] will be emitted instead.
 */
@ExperimentalCoroutinesApi
@ExperimentalListener
public fun ObservableSettings.longFlow(key: String, defaultValue: Long = 0L): Flow<Long> =
    createFlow(key, defaultValue, Settings::getLong)

/**
 * Create a new flow, based on observing the given [key] as a `String`. This flow will immediately emit the current
 * value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * [defaultValue] will be emitted instead.
 */
@ExperimentalCoroutinesApi
@ExperimentalListener
public fun ObservableSettings.stringFlow(key: String, defaultValue: String = ""): Flow<String> =
    createFlow(key, defaultValue, Settings::getString)

/**
 * Create a new flow, based on observing the given [key] as a `Float`. This flow will immediately emit the current
 * value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * [defaultValue] will be emitted instead.
 */
@ExperimentalCoroutinesApi
@ExperimentalListener
public fun ObservableSettings.floatFlow(key: String, defaultValue: Float = 0f): Flow<Float> =
    createFlow(key, defaultValue, Settings::getFloat)

/**
 * Create a new flow, based on observing the given [key] as a `Double`. This flow will immediately emit the current
 * value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * [defaultValue] will be emitted instead.
 */
@ExperimentalCoroutinesApi
@ExperimentalListener
public fun ObservableSettings.doubleFlow(key: String, defaultValue: Double = 0.0): Flow<Double> =
    createFlow(key, defaultValue, Settings::getDouble)

/**
 * Create a new flow, based on observing the given [key] as a `Boolean`. This flow will immediately emit the current
 * value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * [defaultValue] will be emitted instead.
 */
@ExperimentalCoroutinesApi
@ExperimentalListener
public fun ObservableSettings.booleanFlow(key: String, defaultValue: Boolean = false): Flow<Boolean> =
    createFlow(key, defaultValue, Settings::getBoolean)

/**
 * Create a new flow, based on observing the given [key] as an nullable `Int`. This flow will immediately emit the
 * current value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * `null` will be emitted instead.
 */
@ExperimentalCoroutinesApi
@ExperimentalListener
public fun ObservableSettings.intOrNullFlow(key: String): Flow<Int?> =
    createNullableFlow(key, Settings::getIntOrNull)

/**
 * Create a new flow, based on observing the given [key] as a nullable `Long`. This flow will immediately emit the
 * current value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * `null` will be emitted instead.
 */
@ExperimentalCoroutinesApi
@ExperimentalListener
public fun ObservableSettings.longOrNullFlow(key: String): Flow<Long?> =
    createNullableFlow(key, Settings::getLongOrNull)

/**
 * Create a new flow, based on observing the given [key] as a nullable `String`. This flow will immediately emit the
 * current value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * `null` will be emitted instead.
 */
@ExperimentalCoroutinesApi
@ExperimentalListener
public fun ObservableSettings.stringOrNullFlow(key: String): Flow<String?> =
    createNullableFlow(key, Settings::getStringOrNull)

/**
 * Create a new flow, based on observing the given [key] as a nullable `Float`. This flow will immediately emit the
 * current value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * `null` will be emitted instead.
 */
@ExperimentalCoroutinesApi
@ExperimentalListener
public fun ObservableSettings.floatOrNullFlow(key: String): Flow<Float?> =
    createNullableFlow(key, Settings::getFloatOrNull)

/**
 * Create a new flow, based on observing the given [key] as a nullable `Double`. This flow will immediately emit the
 * current value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * `null` will be emitted instead.
 */
@ExperimentalCoroutinesApi
@ExperimentalListener
public fun ObservableSettings.doubleOrNullFlow(key: String): Flow<Double?> =
    createNullableFlow(key, Settings::getDoubleOrNull)

/**
 * Create a new flow, based on observing the given [key] as a nullable `Boolean`. This flow will immediately emit the
 * current value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * `null` will be emitted instead.
 */
@ExperimentalCoroutinesApi
@ExperimentalListener
public fun ObservableSettings.booleanOrNullFlow(key: String): Flow<Boolean?> =
    createNullableFlow(key, Settings::getBooleanOrNull)
