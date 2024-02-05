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

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SettingsListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged

@ExperimentalSettingsApi
private inline fun <T> ObservableSettings.createFlow(
    key: String,
    defaultValue: T,
    crossinline getter: Settings.(String, T) -> T,
    crossinline addListener: ObservableSettings.(String, T, (T) -> Unit) -> SettingsListener
): Flow<T> = callbackFlow {
    send(getter(key, defaultValue))
    val listener = addListener(key, defaultValue) {
        trySend(it)
    }
    awaitClose {
        listener.deactivate()
    }
}.distinctUntilChanged()

@ExperimentalSettingsApi
private inline fun <T> ObservableSettings.createNullableFlow(
    key: String,
    crossinline getter: Settings.(String) -> T?,
    crossinline addListener: ObservableSettings.(String, (T?) -> Unit) -> SettingsListener
): Flow<T?> =
    createFlow<T?>(key, null, { it, _ -> getter(it) }, { it, _, callback -> addListener(it, callback) })

/**
 * Create a new flow, based on observing the given [key] as an `Int`. This flow will immediately emit the current
 * value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * [defaultValue] will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getIntFlow(key: String, defaultValue: Int): Flow<Int> =
    createFlow(key, defaultValue, Settings::getInt, ObservableSettings::addIntListener)

/**
 * Create a new flow, based on observing the given [key] as a `Long`. This flow will immediately emit the current
 * value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * [defaultValue] will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getLongFlow(key: String, defaultValue: Long): Flow<Long> =
    createFlow(key, defaultValue, Settings::getLong, ObservableSettings::addLongListener)

/**
 * Create a new flow, based on observing the given [key] as a `String`. This flow will immediately emit the current
 * value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * [defaultValue] will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getStringFlow(key: String, defaultValue: String): Flow<String> =
    createFlow(key, defaultValue, Settings::getString, ObservableSettings::addStringListener)

/**
 * Create a new flow, based on observing the given [key] as a `Float`. This flow will immediately emit the current
 * value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * [defaultValue] will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getFloatFlow(key: String, defaultValue: Float): Flow<Float> =
    createFlow(key, defaultValue, Settings::getFloat, ObservableSettings::addFloatListener)

/**
 * Create a new flow, based on observing the given [key] as a `Double`. This flow will immediately emit the current
 * value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * [defaultValue] will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getDoubleFlow(key: String, defaultValue: Double): Flow<Double> =
    createFlow(key, defaultValue, Settings::getDouble, ObservableSettings::addDoubleListener)

/**
 * Create a new flow, based on observing the given [key] as a `Boolean`. This flow will immediately emit the current
 * value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * [defaultValue] will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean> =
    createFlow(key, defaultValue, Settings::getBoolean, ObservableSettings::addBooleanListener)

/**
 * Create a new flow, based on observing the given [key] as a nullable `Int`. This flow will immediately emit the
 * current value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * `null` will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getIntOrNullFlow(key: String): Flow<Int?> =
    createNullableFlow(key, Settings::getIntOrNull, ObservableSettings::addIntOrNullListener)

/**
 * Create a new flow, based on observing the given [key] as a nullable `Long`. This flow will immediately emit the
 * current value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * `null` will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getLongOrNullFlow(key: String): Flow<Long?> =
    createNullableFlow(key, Settings::getLongOrNull, ObservableSettings::addLongOrNullListener)

/**
 * Create a new flow, based on observing the given [key] as a nullable `String`. This flow will immediately emit the
 * current value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * `null` will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getStringOrNullFlow(key: String): Flow<String?> =
    createNullableFlow(key, Settings::getStringOrNull, ObservableSettings::addStringOrNullListener)

/**
 * Create a new flow, based on observing the given [key] as a nullable `Float`. This flow will immediately emit the
 * current value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * `null` will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getFloatOrNullFlow(key: String): Flow<Float?> =
    createNullableFlow(key, Settings::getFloatOrNull, ObservableSettings::addFloatOrNullListener)

/**
 * Create a new flow, based on observing the given [key] as a nullable `Double`. This flow will immediately emit the
 * current value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * `null` will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getDoubleOrNullFlow(key: String): Flow<Double?> =
    createNullableFlow(key, Settings::getDoubleOrNull, ObservableSettings::addDoubleOrNullListener)

/**
 * Create a new flow, based on observing the given [key] as a nullable `Boolean`. This flow will immediately emit the
 * current value and then emit any subsequent values when the underlying `Settings` changes. When no value is present,
 * `null` will be emitted instead.
 */
@ExperimentalSettingsApi
public fun ObservableSettings.getBooleanOrNullFlow(key: String): Flow<Boolean?> =
    createNullableFlow(key, Settings::getBooleanOrNull, ObservableSettings::addBooleanOrNullListener)

