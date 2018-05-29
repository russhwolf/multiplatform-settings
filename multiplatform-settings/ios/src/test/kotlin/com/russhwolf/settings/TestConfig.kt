/*
 * Copyright 2018 Russell Wolf
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

import platform.Foundation.NSUserDefaults
import platform.Foundation.NSString

actual fun configureTestSettings() = Settings(MockDefaults())

private class MockDefaults : UserDefaultsWrapper {
    private val storage = mutableMapOf<String, Any?>()

    override fun removePersistentDomainForName(domainName: String) = storage.clear()

    override fun removeObjectForKey(defaultName: String) {
        storage.remove(defaultName)
    }

    override fun setObject(value: Any?, forKey: String) {
        if (value != null) {
            storage[forKey] = value
        } else {
            storage.remove(forKey)
        }
    }

    override fun objectForKey(defaultName: String): Any? = storage[defaultName]
    override fun stringForKey(defaultName: String): String? = storage[defaultName] as? String

    override fun setInteger(value: Long, forKey: String) {
        storage[forKey] = value
    }

    override fun integerForKey(defaultName: String): Long = storage[defaultName] as? Long ?: 0

    override fun setFloat(value: Float, forKey: String) {
        storage[forKey] = value
    }

    override fun floatForKey(defaultName: String): Float = storage[defaultName] as? Float ?: 0f

    override fun setDouble(value: Double, forKey: String) {
        storage[forKey] = value
    }

    override fun doubleForKey(defaultName: String): Double = storage[defaultName] as? Double ?: 0.0

    override fun setBool(value: Boolean, forKey: String) {
        storage[forKey] = value
    }

    override fun boolForKey(defaultName: String): Boolean = storage[defaultName] as? Boolean ?: false
}

