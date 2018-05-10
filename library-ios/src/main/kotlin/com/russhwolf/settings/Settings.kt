package com.russhwolf.settings

import platform.Foundation.NSUserDefaults
import platform.Foundation.NSBundle

actual class Settings constructor(private val delegate: UserDefaultsWrapper) {

    constructor() : this(NSUserDefaultsWrapper(NSUserDefaults.standardUserDefaults))

    actual fun clear() {
        val appDomain = NSBundle.mainBundle().bundleIdentifier
        delegate.removePersistentDomainForName(appDomain ?: "")
    }

    actual fun remove(key: String) = delegate.removeObjectForKey(key)
    actual fun contains(key: String) = delegate.objectForKey(key) != null

    actual fun putInt(key: String, value: Int) = delegate.setInteger(value.toLong(), forKey = key)
    actual fun getInt(key: String, defaultValue: Int): Int {
        if (!contains(key)) return defaultValue
        return delegate.integerForKey(key).toInt()
    }

    actual fun putLong(key: String, value: Long) = delegate.setInteger(value, forKey = key)
    actual fun getLong(key: String, defaultValue: Long): Long {
        if (!contains(key)) return defaultValue
        return delegate.integerForKey(key)
    }

    actual fun putString(key: String, value: String) = delegate.setObject(value, forKey = key)
    actual fun getString(key: String, defaultValue: String): String =
        delegate.stringForKey(key) ?: defaultValue

    actual fun putFloat(key: String, value: Float) = delegate.setFloat(value, forKey = key)
    actual fun getFloat(key: String, defaultValue: Float): Float {
        if (!contains(key)) return defaultValue
        return delegate.floatForKey(key)
    }

    actual fun putDouble(key: String, value: Double) = delegate.setDouble(value, forKey = key)
    actual fun getDouble(key: String, defaultValue: Double): Double {
        if (!contains(key)) return defaultValue
        return delegate.doubleForKey(key)
    }

    actual fun putBoolean(key: String, value: Boolean) = delegate.setBool(value, forKey = key)
    actual fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        if (!contains(key)) return defaultValue
        return delegate.boolForKey(key)
    }
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

class NSUserDefaultsWrapper constructor(val delegate: NSUserDefaults) : UserDefaultsWrapper {
    override fun removePersistentDomainForName(domainName: String) =
        delegate.removePersistentDomainForName(domainName)

    override fun removeObjectForKey(defaultName: String) = delegate.removeObjectForKey(defaultName)

    override fun setObject(value: Any?, forKey: String) = delegate.setObject(value, forKey = forKey)
    override fun objectForKey(defaultName: String) = delegate.objectForKey(defaultName)
    override fun stringForKey(defaultName: String) = delegate.stringForKey(defaultName)

    override fun setInteger(value: Long, forKey: String) =
        delegate.setInteger(value, forKey = forKey)

    override fun integerForKey(defaultName: String) = delegate.integerForKey(defaultName)

    override fun setFloat(value: Float, forKey: String) = delegate.setFloat(value, forKey = forKey)
    override fun floatForKey(defaultName: String) = delegate.floatForKey(defaultName)

    override fun setDouble(value: Double, forKey: String) =
        delegate.setDouble(value, forKey = forKey)

    override fun doubleForKey(defaultName: String) = delegate.doubleForKey(defaultName)

    override fun setBool(value: Boolean, forKey: String) = delegate.setBool(value, forKey = forKey)
    override fun boolForKey(defaultName: String) = delegate.boolForKey(defaultName)
}
