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

import platform.Foundation.NSThread
import kotlin.native.concurrent.AtomicReference
import kotlin.native.concurrent.TransferMode
import kotlin.native.concurrent.Worker
import kotlin.native.concurrent.freeze
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalListener::class)
class AppleSettingsBackgroundTest : BaseSettingsTest(object : Settings.Factory {
    val delegate = AppleSettings.Factory()
    override fun create(name: String?): Settings {
        return delegate.create(name).freeze()
    }
}) {

    private val incrementedOnMainThread = AtomicReference<Boolean?>(null)

    private fun observeThread() {
        (settings as ObservableSettings).addListener("key") {
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
}

private fun <T> doInBackground(block: () -> T): T {
    val worker = Worker.start()
    val result = worker.execute(TransferMode.SAFE, { block.freeze() }, { it.invoke() }).result
    worker.requestTermination()
    return result
}
