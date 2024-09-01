/*
 * Copyright 2024 Russell Wolf
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

@file:OptIn(ExperimentalSettingsApi::class)

package com.russhwolf.settings.observable

import com.russhwolf.settings.BaseSettingsTest
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.minusAssign
import com.russhwolf.settings.set
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertNull

/**
 * A `Settings.Factory` that creates `ObservableSettings` which are backed by `MapSettings`, but use the observability
 * of `settings.makeObservable()` rather than the built-in observability of `MapSettings`.
 */
private class MakeObservableSettingsFactory : Settings.Factory {
    val factoryDelegate = MapSettings.Factory()

    override fun create(name: String?): Settings {
        // Create a non-observable `Settings` using the delegate factory, then make it observable via `makeObservable()`
        val settings = object : Settings by factoryDelegate.create(name) {}
        return settings.makeObservable()
    }
}

private val factory = MakeObservableSettingsFactory()

class MakeObservableSettingsTest : BaseSettingsTest(factory) {
    @Test
    fun extension_sameSourceOfTruth() {
        val delegate = object : Settings by MapSettings() {}
        delegate["key"] = "test_value"

        val runtimeObservable = delegate.makeObservable()
        assertEquals("test_value", runtimeObservable["key"])

        delegate -= "key"
        assertNull(runtimeObservable.getStringOrNull("key"))
    }

    @Test
    fun extension_returnsNewInstance() {
        val observableSettings = factory.create()
        val nonObservableSettings = object : Settings by observableSettings {}

        assertNotSame(observableSettings, observableSettings.makeObservable())
        assertNotSame(observableSettings, nonObservableSettings.makeObservable())
    }
}
