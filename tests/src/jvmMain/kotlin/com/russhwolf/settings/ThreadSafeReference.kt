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

import java.util.concurrent.atomic.AtomicReference
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Test helper atomic delegate.
 */
internal actual fun <T> threadSafeReference(initialValue: T): ReadWriteProperty<Any?, T> =
    object : ReadWriteProperty<Any?, T> {
        private val reference = AtomicReference(initialValue)

        override fun getValue(thisRef: Any?, property: KProperty<*>): T {
            return reference.get()
        }

        override fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
            reference.set(value)
        }
    }
