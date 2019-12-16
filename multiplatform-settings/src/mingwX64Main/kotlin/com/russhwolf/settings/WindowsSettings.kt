/*
 * Copyright 2019 Russell Wolf, Andrew Mikhaylov
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

@file:UseExperimental(ExperimentalUnsignedTypes::class)

package com.russhwolf.settings

import kotlinx.cinterop.*
import platform.windows.*

/**
 * A collection of storage-backed key-value data
 *
 * This class allows storage of values with the [Int], [Long], [String], [Float], [Double], or [Boolean] types, using a
 * [String] reference as a key. Values will be persisted across app launches.
 *
 * The specific persistence mechanism is defined using a platform-specific implementation, so certain behavior may vary
 * across platforms. In general, updates will be reflected immediately in-memory, but will be persisted to disk
 * asynchronously.
 *
 * Operator extensions are defined in order to simplify usage. In addition, property delegates are provided for cleaner
 * syntax and better type-safety when interacting with values stored in a `Settings` instance.
 *
 * On the Windows platform, this class can be created by passing a [HKEY] instance which will be used as a
 * delegate, or via a [Factory].
 */
@ExperimentalWinApi
@UseExperimental(ExperimentalListener::class, ExperimentalUnsignedTypes::class)
public class WindowsSettings public constructor(private val hKey: HKEY) : Settings {

    public constructor(companyName: String, appName: String): this(openHKey(companyName, appName))

    public override fun clear() = TODO()

    public override fun remove(key: String) = TODO()

    public override fun hasKey(key: String): Boolean = TODO()

    public override fun putInt(key: String, value: Int) = memScoped {
        val outValue = alloc<DWORDVar> { this.value = value.convert() }
        RegSetValueExW(
            hKey,
            key,
            0,
            REG_DWORD,
            outValue.ptr.reinterpret(),
            sizeOf<DWORDVar>().convert())
            .checkWinApiSuccess { "Unable to put value for key \"$key\"" }
    }

    public override fun getInt(key: String, defaultValue: Int): Int =
        getIntOrNull(key) ?: defaultValue

    override fun getIntOrNull(key: String): Int? = memScoped {
        val outValue = alloc<DWORDVar>()
        val len = alloc<DWORDVar> { value = sizeOf<DWORDVar>().convert() }
        val type = alloc<DWORDVar> { value = 0U }

        RegQueryValueExW(
            hKey,
            key,
            null,
            type.ptr,
            outValue.ptr.reinterpret(),
            len.ptr)
            .checkWinApiSuccess { "Unable to query value for key \"$key\"" }
        checkType(REG_DWORD, type, key)

        return outValue.value.convert()
    }

    public override fun putLong(key: String, value: Long) = memScoped {
        val outValue = alloc<ULONGLONGVar> { this.value = value.convert() }
        RegSetValueExW(
            hKey,
            key,
            0,
            REG_QWORD,
            outValue.ptr.reinterpret(),
            sizeOf<ULONGLONGVar>().convert())
            .checkWinApiSuccess { "Unable to put value for key \"$key\"" }
    }

    public override fun getLong(key: String, defaultValue: Long): Long =
        getLongOrNull(key) ?: defaultValue

    override fun getLongOrNull(key: String): Long? = memScoped {
        val outValue = alloc<ULONGLONGVar>()
        val len = alloc<DWORDVar> { value = sizeOf<ULONGLONGVar>().convert() }
        val type = alloc<DWORDVar> { value = 0U }

        RegQueryValueExW(
            hKey,
            key,
            null,
            type.ptr,
            outValue.ptr.reinterpret(),
            len.ptr)
            .checkWinApiSuccess { "Unable to query value for key \"$key\"" }
        checkType(REG_QWORD, type, key)

        return outValue.value.convert()
    }

    public override fun putString(key: String, value: String) = memScoped {
        val outValue = value.wcstr
        RegSetValueExW(
            hKey,
            key,
            0,
            REG_SZ,
            outValue.ptr.reinterpret(),
            outValue.size.convert())
            .checkWinApiSuccess { "Unable to put value for key \"$key\"" }
    }

    public override fun getString(key: String, defaultValue: String): String =
        getStringOrNull(key) ?: defaultValue

    override fun getStringOrNull(key: String): String? = memScoped {
        val len = alloc<DWORDVar> { value = 0U }
        val type = alloc<DWORDVar> { value = 0U }

        RegQueryValueExW(
            hKey,
            key,
            null,
            type.ptr,
            null,
            len.ptr)
            .checkWinApiSuccess { "Unable to query value length for key \"$key\"" }
        checkType(REG_SZ, type, key)

        val value = allocArray<WCHARVar>(len.value.convert())

        RegQueryValueExW(
            hKey,
            key,
            null,
            type.ptr,
            value.reinterpret(),
            len.ptr)
            .checkWinApiSuccess { "Unable to query value for key \"$key\"" }
        checkType(REG_SZ, type, key)

        return value.toKString()
    }

    public override fun putFloat(key: String, value: Float) = TODO()

    public override fun getFloat(key: String, defaultValue: Float): Float =
        getFloatOrNull(key) ?: defaultValue

    override fun getFloatOrNull(key: String): Float? = TODO()

    public override fun putDouble(key: String, value: Double) = TODO()

    public override fun getDouble(key: String, defaultValue: Double): Double =
        getDoubleOrNull(key) ?: defaultValue

    override fun getDoubleOrNull(key: String): Double? = TODO()

    public override fun putBoolean(key: String, value: Boolean) = TODO()

    public override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        getBooleanOrNull(key) ?: defaultValue

    override fun getBooleanOrNull(key: String): Boolean? = TODO()
}

private fun openHKey(companyName: String, appName: String): HKEY {
    val subKey = "SOFTWARE\\$companyName\\$appName"
    // TODO RegCloseKey somewhere and release memory
    val result = nativeHeap.alloc<HKEYVar>()
    return memScoped {
        RegCreateKeyExW(
            HKEY_CURRENT_USER,
            subKey,
            0U,
            null,
            0U,
            (KEY_READ or KEY_WRITE).convert(),
            null,
            result.ptr,
            null
        )
            .checkWinApiSuccess {
                """Unable to create registry key for "HKCU\$subKey""""
            }
        result.value!!
    }
}

private fun checkType(expected: Int, actual: DWORDVar, key: String) =
    check(actual.value == expected.convert<DWORD>()) {
        "Invalid type stored in registry for key \"$key\": ${actual.value}"
    }
