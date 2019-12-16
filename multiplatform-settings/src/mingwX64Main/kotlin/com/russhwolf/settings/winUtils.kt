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

internal fun formatMessageFromSystem(errorCode: DWORD): String = memScoped {
    val errorText = alloc<LPWSTRVar>()
    val reinterpret = errorText.reinterpret<WCHARVar>()

    FormatMessageW(
        (FORMAT_MESSAGE_FROM_SYSTEM or
                FORMAT_MESSAGE_ALLOCATE_BUFFER or
                FORMAT_MESSAGE_IGNORE_INSERTS).convert(),
        null,
        errorCode,
        MAKELANGID(LANG_NEUTRAL, SUBLANG_DEFAULT).convert(),
        reinterpret.ptr,
        0,
        null)

    val message = errorText.value!!.toKString().trim()
    LocalFree(errorText.value)

    return "[0x${errorCode.toString(16).padStart(8, '0')}] $message"
}

internal fun lastErrorMessage(): String = formatMessageFromSystem(GetLastError())

internal fun MAKELANGID(primary: Int, sub: Int) =
    sub.toUInt() shl 10 or primary.toUInt()

internal fun Int.checkWinApiSuccess(message: (Int) -> String) {
    if (this != ERROR_SUCCESS) error("${message(this)}: ${formatMessageFromSystem(convert())}")
}

internal fun UInt.checkWinApiSuccess(message: (UInt) -> String) {
    if (this != ERROR_SUCCESS.toUInt()) error("${message(this)}: ${formatMessageFromSystem(this)}")
}
