/*
 * Copyright 2020 Russell Wolf
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

import com.russhwolf.settings.ObservableSettings
import kotlinx.coroutines.flow.Flow

/**
 * An extension to the [ObservableSettings] interface to include [Flow] APIs
 */
public interface FlowSettings : SuspendSettings {

    public companion object;

    /**
     * Returns a [Flow] containing the `Int` value stored at [key], or [defaultValue] if no value was stored. If a value
     * of a different type is stored at `key`, the behavior is not defined.
     */
    public fun getIntFlow(key: String, defaultValue: Int = 0): Flow<Int>

    /**
     * Returns a [Flow] containing the `Int` value stored at [key], or `null` if no value was stored. If a value of a
     * different type was stored at `key`, the behavior is not defined.
     */
    public fun getIntOrNullFlow(key: String): Flow<Int?>

    /**
     * Returns a [Flow] containing the `Long` value stored at [key], or [defaultValue] if no value was stored. If a
     * value of a different type is stored at `key`, the behavior is not defined.
     */
    public fun getLongFlow(key: String, defaultValue: Long = 0): Flow<Long>

    /**
     * Returns a [Flow] containing the `Long` value stored at [key], or `null` if no value was stored. If a value of a
     * different type was stored at `key`, the behavior is not defined.
     */
    public fun getLongOrNullFlow(key: String): Flow<Long?>

    /**
     * Returns a [Flow] containing the `String` value stored at [key], or [defaultValue] if no value was stored. If a
     * value of a different type is stored at `key`, the behavior is not defined.
     */
    public fun getStringFlow(key: String, defaultValue: String = ""): Flow<String>

    /**
     * Returns a [Flow] containing the `String` value stored at [key], or `null` if no value was stored. If a value of a
     * different type was stored at `key`, the behavior is not defined.
     */
    public fun getStringOrNullFlow(key: String): Flow<String?>

    /**
     * Returns a [Flow] containing the `Float` value stored at [key], or [defaultValue] if no value was stored. If a
     * value of a different type is stored at `key`, the behavior is not defined.
     */
    public fun getFloatFlow(key: String, defaultValue: Float = 0f): Flow<Float>

    /**
     * Returns a [Flow] containing the `Float` value stored at [key], or `null` if no value was stored. If a value of a
     * different type was stored at `key`, the behavior is not defined.
     */
    public fun getFloatOrNullFlow(key: String): Flow<Float?>

    /**
     * Returns a [Flow] containing the `Doublw` value stored at [key], or [defaultValue] if no value was stored. If a
     * value of a different type is stored at `key`, the behavior is not defined.
     */
    public fun getDoubleFlow(key: String, defaultValue: Double = 0.0): Flow<Double>

    /**
     * Returns a [Flow] containing the `Doublw` value stored at [key], or `null` if no value was stored. If a value of a
     * different type was stored at `key`, the behavior is not defined.
     */
    public fun getDoubleOrNullFlow(key: String): Flow<Double?>

    /**
     * Returns a [Flow] containing the `Boolean` value stored at [key], or [defaultValue] if no value was stored. If a
     * value of a different type is stored at `key`, the behavior is not defined.
     */
    public fun getBooleanFlow(key: String, defaultValue: Boolean = false): Flow<Boolean>

    /**
     * Returns a [Flow] containing the `Boolean` value stored at [key], or `null` if no value was stored. If a value of
     * a different type was stored at `key`, the behavior is not defined.
     */
    public fun getBooleanOrNullFlow(key: String): Flow<Boolean?>
}
