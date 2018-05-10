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

    override fun boolForKey(defaultName: String): Boolean =
        storage[defaultName] as? Boolean ?: false
}

