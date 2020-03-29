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

import kotlin.jvm.Synchronized

@ExperimentalListener
class ObservableInMemory : InMemory(), ObservableSettings {
    private data class Listener(val key: String, val callback: () -> Unit)
    private val listeners = mutableSetOf<Listener>()

    @Synchronized
    override fun addListener(key: String, callback: () -> Unit): SettingsListener {
        val l = Listener(key, callback)
        listeners.add(l)
        return object : SettingsListener {
            override fun deactivate() {
                removeListener(l)
            }
        }
    }

    @Synchronized
    private fun removeListener(listener: Listener) {
        listeners.remove(listener)
    }

    private fun notify(key: String) {
        listeners.forEach { if (it.key == key) it.callback() }
    }

    override fun clear() {
        super.clear()
        listeners.forEach { it.callback() }
    }

    override fun remove(key: String) {
        super.remove(key)
        notify(key)
    }

    override fun putInt(key: String, value: Int) {
        super.putInt(key, value)
        notify(key)
    }

    override fun putLong(key: String, value: Long) {
        super.putLong(key, value)
        notify(key)
    }

    override fun putString(key: String, value: String) {
        super.putString(key, value)
        notify(key)
    }

    override fun putFloat(key: String, value: Float) {
        super.putFloat(key, value)
        notify(key)
    }

    override fun putDouble(key: String, value: Double) {
        super.putDouble(key, value)
        notify(key)
    }

    override fun putBoolean(key: String, value: Boolean) {
        super.putBoolean(key, value)
        notify(key)
    }
}