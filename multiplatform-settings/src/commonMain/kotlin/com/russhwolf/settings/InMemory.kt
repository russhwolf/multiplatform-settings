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

open class InMemory: Settings {
    private val container = mutableMapOf<String, Any>()

    @Synchronized
    override fun clear() {
        container.clear()
    }

    @Synchronized
    override fun remove(key: String) {
        container.remove(key)
    }

    @Synchronized
    override fun hasKey(key: String): Boolean {
        return container.containsKey(key)
    }

    @Synchronized
    override fun putInt(key: String, value: Int) {
        container.put(key, value)
    }

    @Synchronized
    override fun getInt(key: String, defaultValue: Int): Int {
        return container.get(key) as? Int ?: defaultValue
    }

    @Synchronized
    override fun getIntOrNull(key: String): Int? {
        return container.get(key) as? Int
    }

    @Synchronized
    override fun putLong(key: String, value: Long) {
        container.put(key, value)
    }

    @Synchronized
    override fun getLong(key: String, defaultValue: Long): Long {
        return container.get(key) as? Long ?: defaultValue
    }

    @Synchronized
    override fun getLongOrNull(key: String): Long? {
        return container.get(key) as? Long
    }

    @Synchronized
    override fun putString(key: String, value: String) {
        container.put(key, value)
    }

    @Synchronized
    override fun getString(key: String, defaultValue: String): String {
        return container.get(key) as? String ?: defaultValue
    }

    @Synchronized
    override fun getStringOrNull(key: String): String? {
        return container.get(key) as? String
    }

    @Synchronized
    override fun putFloat(key: String, value: Float) {
        container.put(key, value)
    }

    @Synchronized
    override fun getFloat(key: String, defaultValue: Float): Float {
        return container.get(key) as? Float ?: defaultValue
    }

    @Synchronized
    override fun getFloatOrNull(key: String): Float? {
        return container.get(key) as? Float
    }

    @Synchronized
    override fun putDouble(key: String, value: Double) {
        container.put(key, value)
    }

    @Synchronized
    override fun getDouble(key: String, defaultValue: Double): Double {
        return container.get(key) as? Double ?: defaultValue
    }

    @Synchronized
    override fun getDoubleOrNull(key: String): Double? {
        return container.get(key) as? Double
    }

    @Synchronized
    override fun putBoolean(key: String, value: Boolean) {
        container.put(key, value)
    }

    @Synchronized
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return container.get(key) as? Boolean ?: defaultValue
    }

    @Synchronized
    override fun getBooleanOrNull(key: String): Boolean? {
        return container.get(key) as? Boolean
    }
}