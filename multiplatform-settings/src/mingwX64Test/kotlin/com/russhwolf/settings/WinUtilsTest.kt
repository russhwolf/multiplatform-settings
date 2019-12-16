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

@file:UseExperimental(ExperimentalWinApi::class)

package com.russhwolf.settings

import kotlinx.cinterop.convert
import platform.windows.ERROR_SUCCESS
import kotlin.test.Test
import kotlin.test.assertEquals

class WinUtilsTest {
    @Test
    fun formatMessageFromSystem() {
        assertEquals("[0x00000000] The operation completed successfully.", formatMessageFromSystem(ERROR_SUCCESS.convert()))
    }

    @Test
    fun checkStr() {
        val settings = WindowsSettings("multiplatform-settings", "test")
        settings["akey"] = "avalue"
        assertEquals<String?>("avalue", settings["akey"])
    }
}
