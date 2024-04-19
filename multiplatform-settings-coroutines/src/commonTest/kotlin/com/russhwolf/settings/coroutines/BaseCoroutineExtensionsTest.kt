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

import app.cash.turbine.test
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalSettingsApi::class)
abstract class BaseCoroutineExtensionsTest {

    abstract val settings: ObservableSettings

    private fun <T> flowTest(
        flowBuilder: ObservableSettings.(String, T) -> Flow<T>,
        setter: Settings.(String, T) -> Unit,
        defaultValue: T,
        firstValue: T,
        secondValue: T
    ) = runTest {
        settings.setter("foo", firstValue)
        settings.flowBuilder("foo", defaultValue)
            .test {
                assertEquals(firstValue, awaitItem())
                expectNoEvents()
                settings.setter("foo", firstValue)
                expectNoEvents()
                settings.setter("bar", firstValue)
                expectNoEvents()
                settings.setter("foo", secondValue)
                assertEquals(secondValue, awaitItem())
                expectNoEvents()
                settings.remove("foo")
                assertEquals(defaultValue, awaitItem())
                expectNoEvents()
            }
    }

    private inline fun <reified T : Any> nullableFlowTest(
        crossinline flowBuilder: ObservableSettings.(String) -> Flow<T?>,
        crossinline setter: Settings.(String, T) -> Unit,
        firstValue: T,
        secondValue: T
    ) = flowTest(
        flowBuilder = { key, _ -> flowBuilder(key) },
        setter = { key, value -> if (value != null) setter(key, value) else remove(key) },
        defaultValue = null,
        firstValue = firstValue,
        secondValue = secondValue
    )

    @Test
    fun intFlowTest() = flowTest(
        flowBuilder = ObservableSettings::getIntFlow,
        setter = Settings::putInt,
        defaultValue = 0,
        firstValue = 3,
        secondValue = 8
    )

    @Test
    fun longFlowTest() = flowTest(
        flowBuilder = ObservableSettings::getLongFlow,
        setter = Settings::putLong,
        defaultValue = 0L,
        firstValue = 3L,
        secondValue = 8L
    )

    @Test
    fun stringFlowTest() = flowTest(
        flowBuilder = ObservableSettings::getStringFlow,
        setter = Settings::putString,
        defaultValue = "",
        firstValue = "bar",
        secondValue = "baz"
    )

    @Test
    fun floatFlowTest() = flowTest(
        flowBuilder = ObservableSettings::getFloatFlow,
        setter = Settings::putFloat,
        defaultValue = 0f,
        firstValue = 3f,
        secondValue = 8f
    )

    @Test
    fun doubleFlowTest() = flowTest(
        flowBuilder = ObservableSettings::getDoubleFlow,
        setter = Settings::putDouble,
        defaultValue = 0.0,
        firstValue = 3.0,
        secondValue = 8.0
    )

    @Test
    fun booleanFlowTest() = flowTest(
        flowBuilder = ObservableSettings::getBooleanFlow,
        setter = Settings::putBoolean,
        defaultValue = false,
        firstValue = true,
        secondValue = false
    )

    @Test
    fun intOrNullFlowTest() = nullableFlowTest(
        flowBuilder = ObservableSettings::getIntOrNullFlow,
        setter = Settings::putInt,
        firstValue = 3,
        secondValue = 8
    )

    @Test
    fun longOrNullFlowTest() = nullableFlowTest(
        flowBuilder = ObservableSettings::getLongOrNullFlow,
        setter = Settings::putLong,
        firstValue = 3L,
        secondValue = 8L
    )

    @Test
    fun stringOrNullFlowTest() = nullableFlowTest(
        flowBuilder = ObservableSettings::getStringOrNullFlow,
        setter = Settings::putString,
        firstValue = "bar",
        secondValue = "baz"
    )

    @Test
    fun floatOrNullFlowTest() = nullableFlowTest(
        flowBuilder = ObservableSettings::getFloatOrNullFlow,
        setter = Settings::putFloat,
        firstValue = 3f,
        secondValue = 8f
    )

    @Test
    fun doubleOrNullFlowTest() = nullableFlowTest(
        flowBuilder = ObservableSettings::getDoubleOrNullFlow,
        setter = Settings::putDouble,
        firstValue = 3.0,
        secondValue = 8.0
    )

    @Test
    fun booleanOrNullFlowTest() = nullableFlowTest(
        flowBuilder = ObservableSettings::getBooleanOrNullFlow,
        setter = Settings::putBoolean,
        firstValue = true,
        secondValue = false
    )
}
