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
import kotlinx.cinterop.memScoped
import platform.Foundation.NSDictionary
import platform.Foundation.NSThread
import platform.Foundation.NSUserDefaults
import kotlin.concurrent.AtomicInt
import kotlin.concurrent.AtomicReference
import kotlin.native.concurrent.ObsoleteWorkersApi
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class NSUserDefaultsSettingsBackgroundTest {

    private val settings = NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)

    private val incrementedOnMainThread = AtomicReference<Boolean?>(null)

    private fun observeThread() {
        (settings as ObservableSettings).addIntListener("key", 0) {
            incrementedOnMainThread.value = NSThread.isMainThread
        }
    }

    private fun incrementValue() {
        settings["key"] = settings["key", 0] + 1
    }

    @Test
    fun foreground() {
        observeThread()
        incrementValue()
        assertEquals(true, incrementedOnMainThread.value)
    }

    @Test
    fun background_single() {
        doInBackground {
            observeThread()
            incrementValue()
        }
        assertEquals(false, incrementedOnMainThread.value)
    }

    @Test
    fun background_multiple() {
        doInBackground {
            observeThread()
        }
        doInBackground {
            incrementValue()
        }
        assertEquals(false, incrementedOnMainThread.value)
    }

    @Test
    fun observe_background() {
        doInBackground {
            observeThread()
        }
        incrementValue()
        assertEquals(true, incrementedOnMainThread.value)
    }

    @Test
    fun increment_background() {
        observeThread()
        doInBackground {
            incrementValue()
        }
        assertEquals(false, incrementedOnMainThread.value)
    }

    @Test
    fun nonprimitive_value_across_threads(): Unit = memScoped {
        val mutableState = AtomicInt(0)
        val userDefaults = NSUserDefaults.standardUserDefaults
        val settings = NSUserDefaultsSettings(userDefaults)

        settings.addIntListener("key", 0) { mutableState.addAndGet(1) }
        val data = mapOf("foo" to "bar") as NSDictionary

        doInBackground {
            userDefaults.setObject(data, "key")
        }
        userDefaults.setObject("hello", "key")

        assertEquals(2, mutableState.value)
    }

    @Test
    fun deactivate_listener_in_background() {
        val settings = NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
        val listener = settings.addIntListener("key", 0) { fail() }
        doInBackground {
            listener.deactivate()
        }
        settings["key"] = "value"
    }
}

@OptIn(ObsoleteWorkersApi::class)
private fun <T> doInBackground(block: () -> T): T {
    val worker = Worker.start()
    val result = worker.execute(TransferMode.SAFE, { block }, { it.invoke() }).result
    worker.requestTermination()
    return result
}
