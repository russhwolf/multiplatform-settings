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

@file:OptIn(ExperimentalForeignApi::class)

package com.russhwolf.settings

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import kotlinx.cinterop.wcstr
import platform.windows.DWORDVar
import platform.windows.ERROR_FILE_NOT_FOUND
import platform.windows.ERROR_SUCCESS
import platform.windows.HKEYVar
import platform.windows.HKEY_CURRENT_USER
import platform.windows.KEY_READ
import platform.windows.KEY_WRITE
import platform.windows.REG_OPTION_NON_VOLATILE
import platform.windows.REG_SZ
import platform.windows.RegCloseKey
import platform.windows.RegCreateKeyExW
import platform.windows.RegQueryValueExW
import platform.windows.RegSetValueExW
import platform.windows.WCHARVar
import kotlin.test.assertEquals


class WindowsNoArgTest : NoArgTest() {
    override fun getString(key: String, defaultValue: String): String = getTestValue(key) ?: defaultValue
    override fun setString(key: String, value: String): Unit = insertTestValue(key, value)

    // Note this matches the name set in gradle
    private val subKey = "com.russhwolf.settings.noarg.test"

    private fun getTestValue(keyName: String) = memScoped<String?> {
        val hkey = alloc<HKEYVar>()
        RegCreateKeyExW(
            HKEY_CURRENT_USER,
            "SOFTWARE\\$subKey",
            0u,
            null,
            REG_OPTION_NON_VOLATILE.toUInt(),
            (KEY_READ or KEY_WRITE).toUInt(),
            null,
            hkey.ptr,
            null
        ).also { assertEquals(ERROR_SUCCESS, it) }

        val length = alloc<DWORDVar>()
        val type = alloc<DWORDVar>()
        RegQueryValueExW(
            hkey.value,
            keyName,
            null,
            type.ptr,
            null,
            length.ptr
        ).also {
            if (it == ERROR_FILE_NOT_FOUND) {
                return null
            } else {
                assertEquals(ERROR_SUCCESS, it)
            }
        }

        val value = allocArray<WCHARVar>(length.value.toInt())
        RegQueryValueExW(
            hkey.value,
            keyName,
            null,
            null,
            value.reinterpret(),
            length.ptr
        ).also { assertEquals(ERROR_SUCCESS, it) }

        RegCloseKey(hkey.value)
            .also { assertEquals(ERROR_SUCCESS, it) }

        value.toKString()
    }

    private fun insertTestValue(keyName: String, value: String) {
        memScoped {
            val hkey = alloc<HKEYVar>()
            RegCreateKeyExW(
                HKEY_CURRENT_USER,
                "SOFTWARE\\$subKey",
                0u,
                null,
                REG_OPTION_NON_VOLATILE.toUInt(),
                (KEY_READ or KEY_WRITE).toUInt(),
                null,
                hkey.ptr,
                null
            ).also { assertEquals(ERROR_SUCCESS, it) }

            val cValue = value.wcstr
            RegSetValueExW(
                hkey.value,
                keyName,
                0u,
                REG_SZ.toUInt(),
                cValue.ptr.reinterpret(),
                cValue.size.toUInt()
            ).also { assertEquals(ERROR_SUCCESS, it) }

            RegCloseKey(hkey.value)
                .also { assertEquals(ERROR_SUCCESS, it) }
        }
    }
}
