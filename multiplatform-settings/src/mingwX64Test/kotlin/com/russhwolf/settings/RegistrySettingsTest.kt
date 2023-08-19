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

package com.russhwolf.settings

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.value
import platform.windows.ERROR_SUCCESS
import platform.windows.HKEYVar
import platform.windows.HKEY_CURRENT_USER
import platform.windows.KEY_READ
import platform.windows.KEY_WRITE
import platform.windows.REG_DWORD
import platform.windows.REG_OPTION_NON_VOLATILE
import platform.windows.RegCloseKey
import platform.windows.RegCreateKeyExW
import platform.windows.RegSetValueExW
import platform.windows.ULONGVar
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

@OptIn(ExperimentalSettingsImplementation::class)
private val factory = RegistrySettings.Factory("multiplatform-settings-test")

@OptIn(ExperimentalSettingsImplementation::class, ExperimentalForeignApi::class)
class RegistrySettingsTest : BaseSettingsTest(
    platformFactory = factory,
    hasListeners = false
) {
    @Test
    fun formatMessageFromSystem() {
        assertEquals(
            "[0x00000000] The operation completed successfully.",
            formatMessageFromSystem(ERROR_SUCCESS)
        )
    }

    @Test
    fun constructor_keyName() {
        val rootKeyName = "SOFTWARE\\multiplatform-settings-test"
        val settings = RegistrySettings(rootKeyName)
        settings -= "key"

        insertTestValue(rootKeyName, "key")

        assertEquals(3, settings["key", 0])
    }

    @Test
    fun factory_name() {
        val settings = factory.create("test")
        settings -= "key"

        insertTestValue("SOFTWARE\\multiplatform-settings-test\\test", "key")

        assertEquals(3, settings["key", 0])
    }

    @Test
    fun factory_noName() {
        val settings = factory.create()
        settings -= "key"

        insertTestValue("SOFTWARE\\multiplatform-settings-test", "key")

        assertEquals(3, settings["key", 0])
    }

    @Test
    fun error_type() {
        val settings = RegistrySettings("SOFTWARE\\multiplatform-settings-test")
        settings["key"] = 3
        assertFailsWith<IllegalStateException> {
            @Suppress("UNUSED_VARIABLE")
            val string: String? = settings["key"]
        }
    }

    private fun insertTestValue(rootKeyName: String, keyName: String) {
        memScoped {
            val hkey = alloc<HKEYVar>()
            RegCreateKeyExW(
                HKEY_CURRENT_USER,
                rootKeyName,
                0u,
                null,
                REG_OPTION_NON_VOLATILE.toUInt(),
                (KEY_READ or KEY_WRITE).toUInt(),
                null,
                hkey.ptr,
                null
            ).also { assertEquals(ERROR_SUCCESS, it) }

            RegSetValueExW(
                hkey.value,
                keyName,
                0u,
                REG_DWORD.toUInt(),
                alloc<ULONGVar> { this.value = 3u }.ptr.reinterpret(),
                sizeOf<ULONGVar>().toUInt()
            ).also { assertEquals(ERROR_SUCCESS, it) }

            RegCloseKey(hkey.value)
                .also { assertEquals(ERROR_SUCCESS, it) }
        }
    }
}
