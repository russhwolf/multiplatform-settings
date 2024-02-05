/*
 * Copyright 2024 Russell Wolf
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

import java.util.concurrent.CountDownLatch
import java.util.prefs.PreferenceChangeEvent
import java.util.prefs.PreferenceChangeListener
import java.util.prefs.Preferences

/**
 * This is a workaround for the fact that Preferences listeners are forcibly executed on a background thread. We call
 * this function before verifying listener calls in our test in order to give that background thread a chance to run
 * first. This should clear out the listener queue because the internal listener here is being added last.
 */
public fun Preferences.syncListeners() {
    val latch = CountDownLatch(1)
    val newValue = 1 + getInt("sync", 0)
    val preferenceChangeListener = object : PreferenceChangeListener {
        override fun preferenceChange(it: PreferenceChangeEvent) {
            if (it.key == "sync" && it.newValue == newValue.toString()) {
                latch.countDown()
                removePreferenceChangeListener(this)
            }
        }
    }
    addPreferenceChangeListener(preferenceChangeListener)
    putInt("sync", newValue)
    latch.await()
}
