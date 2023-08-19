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
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import platform.windows.GetModuleFileNameW
import platform.windows.HKEY_CURRENT_USER
import platform.windows.MAX_PATH
import platform.windows.WCHARVar

/**
 * Returns a default [Settings] instance.
 *
 * On Windows this uses registry entries in a subkey of [HKEY_CURRENT_USER\SOFTWARE][HKEY_CURRENT_USER]. It uses a key
 * based on the name returned by [GetModuleFileNameW], with the directory and extension removed.
 */
@ExperimentalSettingsImplementation
@OptIn(ExperimentalForeignApi::class)
public actual fun Settings(): Settings {
    val name = memScoped {
        val nameArray = allocArray<WCHARVar>(MAX_PATH)
        GetModuleFileNameW(
            null,
            nameArray.reinterpret(),
            MAX_PATH.toUInt()
        )
        nameArray.toKString().takeLastWhile { it != '\\' }.removeSuffix(".exe")
    }
    return RegistrySettings("SOFTWARE\\$name")
}
