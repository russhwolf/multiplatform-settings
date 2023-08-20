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

package com.russhwolf.settings

import kotlin.test.Test
import kotlin.test.assertEquals

class StorageSettingsTest : BaseSettingsTest(
    platformFactory = object : Settings.Factory {
        override fun create(name: String?): StorageSettings {
            return StorageSettings(localStorage)
        }
    },
    hasNamedInstances = false,
    hasListeners = false
) {
    @Test
    fun constructor_localStorage() {
        val settings = StorageSettings()
        localStorage["a"] = "value"
        assertEquals("value", settings["a", ""])
    }
}
