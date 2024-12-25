/*
 * Copyright 2025 Russell Wolf
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

import kotlinx.cinterop.ExperimentalForeignApi
import kotlin.test.Test
import kotlin.test.assertEquals

@Suppress("DEPRECATION")
@OptIn(
    ExperimentalSettingsImplementation::class,
    ExperimentalSettingsApi::class,
    ExperimentalForeignApi::class
)
class LegacyKeychainSettingsTest {
    @Test
    fun migrateLegacyKeychainSettings() {
        val legacySettings = KeychainSettings("LegacySettingsTest")
        legacySettings.clear()

        legacySettings.putString("Foo", "Bar")

        val migratedSettings = KeychainSettings("LegacySettingsTest", KeychainSettings.Accessibility.AfterFirstUnlock)
        migratedSettings.migrateLegacyKeys()

        assertEquals("Bar", migratedSettings.getStringOrNull("Foo"))
    }
}
