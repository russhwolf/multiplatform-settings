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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalSettingsApi::class)
abstract class BaseCoroutineExtensionsTest(
    private val syncListeners: () -> Unit = {}
) {

    abstract val settings: ObservableSettings

    private suspend fun <T> turbineTest(
        flowBuilder: ObservableSettings.(String, T) -> Flow<T>,
        setter: Settings.(String, T) -> Unit,
        defaultValue: T,
        firstValue: T,
        secondValue: T
    ) {
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
                if (secondValue != defaultValue) { // Usually true, but false for nonnull bool
                    assertEquals(defaultValue, awaitItem())
                }
                expectNoEvents()
            }
    }

    private fun <T> flowTest(
        flowBuilder: ObservableSettings.(String, T) -> Flow<T>,
        setter: Settings.(String, T) -> Unit,
        defaultValue: T,
        firstValue: T,
        secondValue: T
    ) = runTest {
        turbineTest(flowBuilder, setter, defaultValue, firstValue, secondValue)
    }

    private fun <T> stateFlowTest(
        stateFlowBuilder: ObservableSettings.(CoroutineScope, String, T) -> StateFlow<T>,
        setter: Settings.(String, T) -> Unit,
        defaultValue: T,
        firstValue: T,
        secondValue: T
    ) = runTest {
        // We want StataFlow updates in unconfined dispatcher, so we don't need to wait for updates before asserting.
        // If everything runs in UnconfinedTestDispatcher, we deadlock, so just use unconfined for creating StateFlows
        @OptIn(ExperimentalCoroutinesApi::class)
        val stateFlowScope = CoroutineScope(UnconfinedTestDispatcher(testScheduler))
        val stateFlow = settings.stateFlowBuilder(stateFlowScope, "foo", defaultValue)
        assertEquals(defaultValue, stateFlow.value)
        settings.setter("foo", firstValue)
        syncListeners()
        assertEquals(firstValue, stateFlow.value)
        settings.setter("foo", secondValue)
        syncListeners()
        assertEquals(secondValue, stateFlow.value)
        settings.remove("foo")
        syncListeners()
        assertEquals(defaultValue, stateFlow.value)

        turbineTest(
            { key, defaultValue -> stateFlowBuilder(stateFlowScope, key, defaultValue) },
            setter,
            defaultValue,
            firstValue,
            secondValue
        )
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

    private inline fun <reified T : Any> nullableStateFlowTest(
        crossinline stateFlowBuilder: ObservableSettings.(CoroutineScope, String) -> StateFlow<T?>,
        crossinline setter: Settings.(String, T) -> Unit,
        firstValue: T,
        secondValue: T
    ) = stateFlowTest(
        stateFlowBuilder = { coroutineScope, key, _ -> stateFlowBuilder(coroutineScope, key) },
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

    @Test
    fun intStateFlowTest() = stateFlowTest(
        stateFlowBuilder = ObservableSettings::getIntStateFlow,
        setter = Settings::putInt,
        defaultValue = 0,
        firstValue = 3,
        secondValue = 8
    )

    @Test
    fun longStateFlowTest() = stateFlowTest(
        stateFlowBuilder = ObservableSettings::getLongStateFlow,
        setter = Settings::putLong,
        defaultValue = 0L,
        firstValue = 3L,
        secondValue = 8L
    )

    @Test
    fun stringStateFlowTest() = stateFlowTest(
        stateFlowBuilder = ObservableSettings::getStringStateFlow,
        setter = Settings::putString,
        defaultValue = "",
        firstValue = "bar",
        secondValue = "baz"
    )

    @Test
    fun floatStateFlowTest() = stateFlowTest(
        stateFlowBuilder = ObservableSettings::getFloatStateFlow,
        setter = Settings::putFloat,
        defaultValue = 0f,
        firstValue = 3f,
        secondValue = 8f
    )

    @Test
    fun doubleStateFlowTest() = stateFlowTest(
        stateFlowBuilder = ObservableSettings::getDoubleStateFlow,
        setter = Settings::putDouble,
        defaultValue = 0.0,
        firstValue = 3.0,
        secondValue = 8.0
    )

    @Test
    fun booleanStateFlowTest() = stateFlowTest(
        stateFlowBuilder = ObservableSettings::getBooleanStateFlow,
        setter = Settings::putBoolean,
        defaultValue = false,
        firstValue = true,
        secondValue = false
    )

    @Test
    fun intOrNullStateFlowTest() = nullableStateFlowTest(
        stateFlowBuilder = ObservableSettings::getIntOrNullStateFlow,
        setter = Settings::putInt,
        firstValue = 3,
        secondValue = 8
    )

    @Test
    fun longOrNullStateFlowTest() = nullableStateFlowTest(
        stateFlowBuilder = ObservableSettings::getLongOrNullStateFlow,
        setter = Settings::putLong,
        firstValue = 3L,
        secondValue = 8L
    )

    @Test
    fun stringOrNullStateFlowTest() = nullableStateFlowTest(
        stateFlowBuilder = ObservableSettings::getStringOrNullStateFlow,
        setter = Settings::putString,
        firstValue = "bar",
        secondValue = "baz"
    )

    @Test
    fun floatOrNullStateFlowTest() = nullableStateFlowTest(
        stateFlowBuilder = ObservableSettings::getFloatOrNullStateFlow,
        setter = Settings::putFloat,
        firstValue = 3f,
        secondValue = 8f
    )

    @Test
    fun doubleOrNullStateFlowTest() = nullableStateFlowTest(
        stateFlowBuilder = ObservableSettings::getDoubleOrNullStateFlow,
        setter = Settings::putDouble,
        firstValue = 3.0,
        secondValue = 8.0
    )

    @Test
    fun booleanOrNullStateFlowTest() = nullableStateFlowTest(
        stateFlowBuilder = ObservableSettings::getBooleanOrNullStateFlow,
        setter = Settings::putBoolean,
        firstValue = true,
        secondValue = false
    )
}
