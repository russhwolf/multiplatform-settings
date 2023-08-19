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

import com.russhwolf.settings.RegistrySettings.Factory
import kotlinx.cinterop.CVariable
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.sizeOf
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import kotlinx.cinterop.wcstr
import platform.windows.DWORDVar
import platform.windows.ERROR_FILE_NOT_FOUND
import platform.windows.ERROR_MORE_DATA
import platform.windows.ERROR_NO_MORE_ITEMS
import platform.windows.ERROR_SUCCESS
import platform.windows.FORMAT_MESSAGE_ALLOCATE_BUFFER
import platform.windows.FORMAT_MESSAGE_FROM_SYSTEM
import platform.windows.FORMAT_MESSAGE_IGNORE_INSERTS
import platform.windows.FormatMessageW
import platform.windows.HKEY
import platform.windows.HKEYVar
import platform.windows.HKEY_CURRENT_USER
import platform.windows.KEY_READ
import platform.windows.KEY_WRITE
import platform.windows.LANG_NEUTRAL
import platform.windows.LPWSTRVar
import platform.windows.LocalFree
import platform.windows.REG_DWORD
import platform.windows.REG_OPTION_NON_VOLATILE
import platform.windows.REG_QWORD
import platform.windows.REG_SZ
import platform.windows.RegCloseKey
import platform.windows.RegCreateKeyExW
import platform.windows.RegDeleteValueW
import platform.windows.RegEnumValueW
import platform.windows.RegQueryInfoKeyW
import platform.windows.RegQueryValueExW
import platform.windows.RegSetValueExW
import platform.windows.SUBLANG_DEFAULT
import platform.windows.ULONGLONGVar
import platform.windows.ULONGVar
import platform.windows.WCHARVar

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
 * On the Windows platform, this class can be created by passing a `String` which will be used as the name of the parent
 * registry key, or via a [Factory].
 */
@ExperimentalSettingsImplementation
public class RegistrySettings public constructor(private val rootKeyName: String) : Settings {

    /**
     * A factory that can produce [Settings] instances.
     *
     * On the Windows platform, this class creates `Settings` objects backed by the Windows registry. The factory must
     * be supplied with a string which will be interpreted as a subkey of [HKEY_CURRENT_USER/SOFTWARE][HKEY_CURRENT_USER].
     * Instances created by this factory will be subkeys of that key.
     */
    public class Factory(private val parentKeyName: String) : Settings.Factory {
        public override fun create(name: String?): RegistrySettings {
            val key = "SOFTWARE\\$parentKeyName" + if (name != null) "\\$name" else ""
            return RegistrySettings(key)
        }
    }

    public override val keys: Set<String>
        get() = registryOperation { rootKey ->
            val nameLength = alloc<DWORDVar>()
            RegQueryInfoKeyW(
                rootKey,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                nameLength.ptr,
                null,
                null,
                null
            ).checkWinApiSuccess { "Unable to get key info" }

            val maxNameLength = nameLength.value + 1u // +1 to hold null-terminator
            val keys = mutableListOf<String>()
            var index = 0u
            while (true) {
                // Apparently things only seem to work if we reallocate this each time
                @Suppress("NAME_SHADOWING")
                val nameLength = alloc<DWORDVar> { value = maxNameLength }
                val nameBuffer = allocArray<WCHARVar>(maxNameLength.toInt())
                val error = RegEnumValueW(
                    rootKey,
                    index++,
                    nameBuffer,
                    nameLength.ptr,
                    null,
                    null,
                    null,
                    null
                )
                if (error == ERROR_NO_MORE_ITEMS) break
                error.checkWinApiSuccess(ERROR_MORE_DATA) { "Error enumerating keys" }
                val key = nameBuffer.toKString()
                keys.add(key)
            }
            keys.toSet()
        }

    public override val size: Int
        get() = registryOperation { rootKey ->
            val size = alloc<DWORDVar>()
            RegQueryInfoKeyW(
                rootKey,
                null,
                null,
                null,
                null,
                null,
                null,
                size.ptr,
                null,
                null,
                null,
                null
            ).checkWinApiSuccess { "Unable to get key info" }
            size.value.toInt()
        }

    public override fun clear(): Unit = keys.forEach { remove(it) }

    public override fun remove(key: String): Unit = registryOperation { rootKey ->
        RegDeleteValueW(rootKey, key)
            .checkWinApiSuccess(ERROR_FILE_NOT_FOUND) { "Unable to remove key \"$key\"" }
    }

    public override fun hasKey(key: String): Boolean = registryOperation { rootKey ->
        val type = alloc<DWORDVar>()
        val error = RegQueryValueExW(
            rootKey,
            key,
            null,
            type.ptr,
            null,
            null
        )
        error.checkWinApiSuccess(ERROR_FILE_NOT_FOUND) { "Error checking if key \"$key\" is present" }
        error == ERROR_SUCCESS
    }

    public override fun putInt(key: String, value: Int): Unit =
        putRegistryValue(key, REG_DWORD) { alloc<ULONGVar> { this.value = value.toUInt() } }

    public override fun getInt(key: String, defaultValue: Int): Int =
        getIntOrNull(key) ?: defaultValue

    public override fun getIntOrNull(key: String): Int? = getRegistryValue<ULONGVar>(key, REG_DWORD)?.value?.toInt()

    public override fun putLong(key: String, value: Long): Unit =
        putRegistryValue(key, REG_QWORD) { alloc<ULONGLONGVar> { this.value = value.toULong() } }

    public override fun getLong(key: String, defaultValue: Long): Long =
        getLongOrNull(key) ?: defaultValue

    public override fun getLongOrNull(key: String): Long? =
        getRegistryValue<ULONGLONGVar>(key, REG_QWORD)?.value?.toLong()

    public override fun putString(key: String, value: String): Unit = registryOperation { rootKey ->
        // Variable length so this behaves a little differently than primitives
        val cValue = value.wcstr
        RegSetValueExW(
            rootKey,
            key,
            0u,
            REG_SZ.toUInt(),
            cValue.ptr.reinterpret(),
            cValue.size.toUInt()
        ).checkWinApiSuccess { "Unable to put value for key \"$key\"" }
    }

    public override fun getString(key: String, defaultValue: String): String =
        getStringOrNull(key) ?: defaultValue

    public override fun getStringOrNull(key: String): String? = registryOperation { rootKey ->
        // Variable length so this behaves a little differently than primitives

        val length = alloc<DWORDVar>()
        val type = alloc<DWORDVar>()
        val error = RegQueryValueExW(
            rootKey,
            key,
            null,
            type.ptr,
            null,
            length.ptr
        )
        if (error == ERROR_FILE_NOT_FOUND) return@registryOperation null
        error.checkWinApiSuccess(ERROR_FILE_NOT_FOUND) { "Unable to query value length for key \"$key\"" }
        checkType(REG_SZ, type, key)

        val value = allocArray<WCHARVar>(length.value.toInt())
        RegQueryValueExW(
            rootKey,
            key,
            null,
            null,
            value.reinterpret(),
            length.ptr
        ).checkWinApiSuccess { "Unable to query value for key \"$key\"" }

        value.toKString()
    }

    public override fun putFloat(key: String, value: Float): Unit =
        putRegistryValue(key, REG_DWORD) { alloc<ULONGVar> { this.value = value.toRawBits().toUInt() } }

    public override fun getFloat(key: String, defaultValue: Float): Float =
        getFloatOrNull(key) ?: defaultValue

    public override fun getFloatOrNull(key: String): Float? =
        getRegistryValue<ULONGVar>(key, REG_DWORD)?.value?.toInt()?.let { Float.fromBits(it) }

    public override fun putDouble(key: String, value: Double): Unit =
        putRegistryValue(key, REG_QWORD) { alloc<ULONGLONGVar> { this.value = value.toRawBits().toULong() } }

    public override fun getDouble(key: String, defaultValue: Double): Double =
        getDoubleOrNull(key) ?: defaultValue

    public override fun getDoubleOrNull(key: String): Double? =
        getRegistryValue<ULONGLONGVar>(key, REG_QWORD)?.value?.toLong()?.let { Double.fromBits(it) }

    public override fun putBoolean(key: String, value: Boolean): Unit =
        putRegistryValue(key, REG_DWORD) { alloc<ULONGVar> { this.value = (if (value) 1u else 0u) } }

    public override fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        getBooleanOrNull(key) ?: defaultValue

    public override fun getBooleanOrNull(key: String): Boolean? =
        getRegistryValue<DWORDVar>(key, REG_DWORD)?.value?.toInt()?.equals(0)?.not()

    private inline fun <reified T : CVariable> getRegistryValue(key: String, expectedType: Int): T? =
        registryOperation { rootKey ->
            val value = alloc<T>()
            val type = alloc<DWORDVar>()
            val error = RegQueryValueExW(
                rootKey,
                key,
                null,
                type.ptr,
                value.ptr.reinterpret(),
                alloc<DWORDVar> { this.value = sizeOf<T>().convert() }.ptr
            )
            error.checkWinApiSuccess(ERROR_FILE_NOT_FOUND) { "Error checking if key \"$key\" is present" }
            if (error == ERROR_FILE_NOT_FOUND) {
                null
            } else {
                checkType(expectedType, type, key)
                value
            }
        }

    private inline fun <reified T : CVariable> putRegistryValue(
        key: String,
        type: Int,
        crossinline getValue: MemScope.() -> T
    ): Unit = registryOperation { rootKey ->
        RegSetValueExW(
            rootKey,
            key,
            0u,
            type.convert(),
            getValue().ptr.reinterpret(),
            sizeOf<T>().convert()
        ).checkWinApiSuccess { "Unable to put value for key \"$key\"" }
    }

    private fun <T> registryOperation(action: MemScope.(rootKey: HKEY) -> T): T = memScoped {
        val hkey = alloc<HKEYVar>()
        return try {
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
            ).checkWinApiSuccess { "Unable to create/open registry key for \"$rootKeyName\"" }

            action(hkey.value!!)
        } finally {
            RegCloseKey(hkey.value).checkWinApiSuccess { "Unable to close registry key for \"$rootKeyName\"" }
        }
    }
}

private fun checkType(expected: Int, actual: DWORDVar, key: String) =
    check(actual.value.toInt() == expected) {
        "Invalid type stored in registry for key \"$key\": ${actual.value}"
    }


private fun Int.checkWinApiSuccess(vararg expectedErrors: Int, message: () -> String) {
    if (this != ERROR_SUCCESS && this !in expectedErrors) error("${message()}: ${formatMessageFromSystem(this)}")
}

internal fun formatMessageFromSystem(errorCode: Int): String = memScoped {
    val errorText = alloc<LPWSTRVar>()

    FormatMessageW(
        (FORMAT_MESSAGE_FROM_SYSTEM or
                FORMAT_MESSAGE_ALLOCATE_BUFFER or
                FORMAT_MESSAGE_IGNORE_INSERTS).toUInt(),
        null,
        errorCode.toUInt(),
        makeLangId(LANG_NEUTRAL, SUBLANG_DEFAULT),
        errorText.reinterpret<WCHARVar>().ptr,
        0u,
        null
    )

    val message = errorText.value?.toKString().orEmpty().trim()
    LocalFree(errorText.value)

    return "[0x${errorCode.toString(16).padStart(8, '0')}] $message"
}

private fun makeLangId(primary: Int, sub: Int) =
    sub.toUInt() shl 10 or primary.toUInt()

