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

import kotlin.test.assertEquals

@Suppress("KDocMissingDocumentation")
class ListenerVerifier {
    val listener: () -> Unit = { invokeCount++ }

    private var invokeCount = 0

    fun assertInvoked(times: Int = 1, message: String? = null) {
        assertEquals(times, invokeCount, message)
        invokeCount = 0
    }

    fun assertNotInvoked(message: String? = null) {
        assertInvoked(0, message)
    }
}
