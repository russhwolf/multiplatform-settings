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

@file:Suppress("KDocMissingDocumentation")

package com.russhwolf.settings

import kotlin.test.assertEquals
import kotlin.test.fail

class ListenerValueVerifier<T> {
    val listener: (T) -> Unit = {
        if (state != State.Empty) fail("ListenerValueVerifier was invoked a second time without clearing last value")
        state = State.Value(it)
    }

    private var state: State<T?> by threadSafeReference(State.Empty)

    fun assertLastValue(value: T, message: String? = null) {
        assertEquals(State.Value(value), state, message)
        state = State.Empty
    }

    fun assertNoValue(message: String? = null) {
        assertEquals(State.Empty, state, message)
    }

    private sealed class State<out T> {
        object Empty : State<Nothing>() {
            override fun toString(): String = "EMPTY"
        }

        data class Value<T>(private val value: T) : State<T>()
    }
}
