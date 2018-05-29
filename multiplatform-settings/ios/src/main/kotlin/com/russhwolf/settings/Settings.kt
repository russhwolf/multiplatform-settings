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
import platform.Foundation.NSBundle

actual class Settings(private val delegate: UserDefaultsWrapper) {

    constructor() : this(NSUserDefaultsWrapper())

    actual fun clear() {
        val appDomain = NSBundle.mainBundle().bundleIdentifier
        delegate.removePersistentDomainForName(appDomain ?: "")
    }

    actual fun remove(key: String) = delegate.removeObjectForKey(key)
    actual fun hasKey(key: String) = delegate.objectForKey(key) != null

    actual fun putInt(key: String, value: Int) = delegate.setInteger(value.toLong(), forKey = key)
    actual fun getInt(key: String, defaultValue: Int): Int =
        if (hasKey(key)) delegate.integerForKey(key).toInt() else defaultValue

    actual fun putLong(key: String, value: Long) = delegate.setInteger(value, forKey = key)
    actual fun getLong(key: String, defaultValue: Long): Long =
        if (hasKey(key)) delegate.integerForKey(key) else defaultValue

    actual fun putString(key: String, value: String) = delegate.setObject(value, forKey = key)
    actual fun getString(key: String, defaultValue: String): String =
        delegate.stringForKey(key) ?: defaultValue

    actual fun putFloat(key: String, value: Float) = delegate.setFloat(value, forKey = key)
    actual fun getFloat(key: String, defaultValue: Float): Float =
        if (hasKey(key)) delegate.floatForKey(key) else defaultValue

    actual fun putDouble(key: String, value: Double) = delegate.setDouble(value, forKey = key)
    actual fun getDouble(key: String, defaultValue: Double): Double =
        if (hasKey(key)) delegate.doubleForKey(key) else defaultValue

    actual fun putBoolean(key: String, value: Boolean) = delegate.setBool(value, forKey = key)
    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean =
        if (hasKey(key)) delegate.boolForKey(key) else defaultValue
}

/**
 * A pass-through facade for NSUserDefaults, to facilitate mocking for tests
 */
interface UserDefaultsWrapper {
    fun removePersistentDomainForName(domainName: String)
    fun removeObjectForKey(defaultName: String)

    fun setObject(value: Any?, forKey: String)
    fun objectForKey(defaultName: String): Any?
    fun stringForKey(defaultName: String): String?

    fun setInteger(value: Long, forKey: String)
    fun integerForKey(defaultName: String): Long

    fun setFloat(value: Float, forKey: String)
    fun floatForKey(defaultName: String): Float

    fun setDouble(value: Double, forKey: String)
    fun doubleForKey(defaultName: String): Double

    fun setBool(value: Boolean, forKey: String)
    fun boolForKey(defaultName: String): Boolean
}

class NSUserDefaultsWrapper(val delegate: NSUserDefaults = NSUserDefaults()) : UserDefaultsWrapper {
    override fun removePersistentDomainForName(domainName: String) = delegate.removePersistentDomainForName(domainName)

    override fun removeObjectForKey(defaultName: String) = delegate.removeObjectForKey(defaultName)

    override fun setObject(value: Any?, forKey: String) = delegate.setObject(value, forKey = forKey)
    override fun objectForKey(defaultName: String) = delegate.objectForKey(defaultName)
    override fun stringForKey(defaultName: String) = delegate.stringForKey(defaultName)

    override fun setInteger(value: Long, forKey: String) = delegate.setInteger(value, forKey = forKey)
    override fun integerForKey(defaultName: String) = delegate.integerForKey(defaultName)

    override fun setFloat(value: Float, forKey: String) = delegate.setFloat(value, forKey = forKey)
    override fun floatForKey(defaultName: String) = delegate.floatForKey(defaultName)

    override fun setDouble(value: Double, forKey: String) = delegate.setDouble(value, forKey = forKey)
    override fun doubleForKey(defaultName: String) = delegate.doubleForKey(defaultName)

    override fun setBool(value: Boolean, forKey: String) = delegate.setBool(value, forKey = forKey)
    override fun boolForKey(defaultName: String) = delegate.boolForKey(defaultName)
}
