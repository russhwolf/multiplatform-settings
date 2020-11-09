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

import com.russhwolf.settings.ExperimentalListener
import com.russhwolf.settings.ObservableSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

public interface FlowSettings {

    public companion object;

    public suspend fun keys(): Set<String>
    public suspend fun size(): Int
    public suspend fun clear()
    public suspend fun remove(key: String)
    public suspend fun hasKey(key: String): Boolean

    public suspend fun putInt(key: String, value: Int)
    public fun getIntFlow(key: String, defaultValue: Int = 0): Flow<Int>
    public fun getIntOrNullFlow(key: String): Flow<Int?>

    public suspend fun putLong(key: String, value: Long)
    public fun getLongFlow(key: String, defaultValue: Long = 0): Flow<Long>
    public fun getLongOrNullFlow(key: String): Flow<Long?>

    public suspend fun putString(key: String, value: String)
    public fun getStringFlow(key: String, defaultValue: String = ""): Flow<String>
    public fun getStringOrNullFlow(key: String): Flow<String?>

    public suspend fun putFloat(key: String, value: Float)
    public fun getFloatFlow(key: String, defaultValue: Float = 0f): Flow<Float>
    public fun getFloatOrNullFlow(key: String): Flow<Float?>

    public suspend fun putDouble(key: String, value: Double)
    public fun getDoubleFlow(key: String, defaultValue: Double = 0.0): Flow<Double>
    public fun getDoubleOrNullFlow(key: String): Flow<Double?>

    public suspend fun putBoolean(key: String, value: Boolean)
    public fun getBooleanFlow(key: String, defaultValue: Boolean = false): Flow<Boolean>
    public fun getBooleanOrNullFlow(key: String): Flow<Boolean?>
}

@ExperimentalListener
@ExperimentalCoroutinesApi
public fun ObservableSettings.toFlowSettings(): FlowSettings = object : FlowSettings {
    private val settings inline get() = this@toFlowSettings

    public override suspend fun keys(): Set<String> = settings.keys
    public override suspend fun size(): Int = settings.size
    public override suspend fun clear() = settings.clear()
    public override suspend fun remove(key: String) = settings.remove(key)
    public override suspend fun hasKey(key: String): Boolean = settings.hasKey(key)

    public override suspend fun putInt(key: String, value: Int) = settings.putInt(key, value)
    public override fun getIntFlow(key: String, defaultValue: Int): Flow<Int> = settings.intFlow(key, defaultValue)
    public override fun getIntOrNullFlow(key: String): Flow<Int?> = settings.intOrNullFlow(key)

    public override suspend fun putLong(key: String, value: Long) = settings.putLong(key, value)
    public override fun getLongFlow(key: String, defaultValue: Long): Flow<Long> = settings.longFlow(key, defaultValue)
    public override fun getLongOrNullFlow(key: String): Flow<Long?> = settings.longOrNullFlow(key)

    public override suspend fun putString(key: String, value: String) = settings.putString(key, value)
    public override fun getStringFlow(key: String, defaultValue: String): Flow<String> =
        settings.stringFlow(key, defaultValue)

    public override fun getStringOrNullFlow(key: String): Flow<String?> = settings.stringOrNullFlow(key)

    public override suspend fun putFloat(key: String, value: Float) = settings.putFloat(key, value)
    public override fun getFloatFlow(key: String, defaultValue: Float): Flow<Float> =
        settings.floatFlow(key, defaultValue)

    public override fun getFloatOrNullFlow(key: String): Flow<Float?> = settings.floatOrNullFlow(key)

    public override suspend fun putDouble(key: String, value: Double) = settings.putDouble(key, value)
    public override fun getDoubleFlow(key: String, defaultValue: Double): Flow<Double> =
        settings.doubleFlow(key, defaultValue)

    public override fun getDoubleOrNullFlow(key: String): Flow<Double?> = settings.doubleOrNullFlow(key)

    public override suspend fun putBoolean(key: String, value: Boolean) = settings.putBoolean(key, value)
    public override fun getBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean> =
        settings.booleanFlow(key, defaultValue)

    public override fun getBooleanOrNullFlow(key: String): Flow<Boolean?> = settings.booleanOrNullFlow(key)
}

public fun FlowSettings.toSuspendSettings(): SuspendSettings = object : SuspendSettings {
    private val flowSettings inline get() = this@toSuspendSettings

    override suspend fun keys(): Set<String> = flowSettings.keys()
    override suspend fun size(): Int = flowSettings.size()
    override suspend fun clear() = flowSettings.clear()
    override suspend fun remove(key: String) = flowSettings.remove(key)
    override suspend fun hasKey(key: String): Boolean = flowSettings.hasKey(key)

    override suspend fun putInt(key: String, value: Int) = flowSettings.putInt(key, value)
    override suspend fun getInt(key: String, defaultValue: Int): Int = getIntFlow(key, defaultValue).first()
    override suspend fun getIntOrNull(key: String): Int? = getIntOrNullFlow(key).first()

    override suspend fun putLong(key: String, value: Long) = flowSettings.putLong(key, value)
    override suspend fun getLong(key: String, defaultValue: Long): Long = getLongFlow(key, defaultValue).first()
    override suspend fun getLongOrNull(key: String): Long? = getLongOrNullFlow(key).first()

    override suspend fun putString(key: String, value: String) = flowSettings.putString(key, value)
    override suspend fun getString(key: String, defaultValue: String): String = getStringFlow(key, defaultValue).first()
    override suspend fun getStringOrNull(key: String): String? = getStringOrNullFlow(key).first()

    override suspend fun putFloat(key: String, value: Float) = flowSettings.putFloat(key, value)
    override suspend fun getFloat(key: String, defaultValue: Float): Float = getFloatFlow(key, defaultValue).first()
    override suspend fun getFloatOrNull(key: String): Float? = getFloatOrNullFlow(key).first()

    override suspend fun putDouble(key: String, value: Double) = flowSettings.putDouble(key, value)
    override suspend fun getDouble(key: String, defaultValue: Double): Double = getDoubleFlow(key, defaultValue).first()
    override suspend fun getDoubleOrNull(key: String): Double? = getDoubleOrNullFlow(key).first()

    override suspend fun putBoolean(key: String, value: Boolean) = flowSettings.putBoolean(key, value)
    override suspend fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        getBooleanFlow(key, defaultValue).first()

    override suspend fun getBooleanOrNull(key: String): Boolean? = getBooleanOrNullFlow(key).first()

}
