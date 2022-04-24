/*
 * Copyright 2022 Russell Wolf
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

import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import java.util.Collections

/**
 * A collection of storage-backed key-value data
 *
 * This class allows storage of values with the [Int], [Long], [String], [Float], [Double], or [Boolean] types, using a
 * [String] reference as a key. Values will be persisted across app launches.
 *
 * The specific persistence mechanism is defined using a platform-specific implementation, so certain behavior may vary
 * across platforms. In general, updates will be reflected immediately in-memory, but will be persisted to disk
 * asynchronously.
 *
 * Operator extensions are defined in order to simplify usage. In addition, property delegates are provided for cleaner
 * syntax and better type-safety when interacting with values stored in a `Settings` instance.
 *
 * This class can be instantiated via a platform-specific constructor or via a [Factory].
 *
 * On the Android platform, this class can be created by passing a [SharedPreferences] instance which will be used as a
 * delegate. Thus two `Settings` instances created using the same [delegate] will be backed by the same data.
 *
 * Set the [commit] parameter to true if you want your changes to be immediately committed to the persistent storage
 * (slower, but synchronous).
 *
 * All data stored via this class will be encrypted with a new key that is stored in the keystore, then when the data is
 * is retrieved it is decrypted using the same key that was created when the data was stored.
 */
/* reference https://medium.com/@josiassena/using-the-android-keystore-system-to-store-sensitive-information-3a56175a454b */
@RequiresApi(Build.VERSION_CODES.M)
public class KeystoreSettings(delegate: SharedPreferences, commit : Boolean = false) : Settings {

    /**
     * A factory that can produce [Settings] instances.
     *
     * This class can only be instantiated via a platform-specific constructor. It's purpose is so that `Settings`
     * objects can be created in common code, so that the only platform-specific behavior necessary in order to use
     * multiple `Settings` objects is the one-time creation of a single `Factory`.
     *
     * On the Android platform, this class creates `Settings` objects backed by [SharedPreferences]. It  can only be
     * created by supplying a [Context] instance. The `Factory` will hold onto a reference to the
     * [applicationContext][Context.getApplicationContext] property of the supplied `context` and will use that to
     * create [SharedPreferences] objects.
     */
    public class Factory(context: Context) : Settings.Factory {
        private val appContext = context.applicationContext

        /**
         * Creates a [Settings] object associated with the provided [name].
         *
         * Multiple `Settings` instances created with the same `name` parameter will be backed by the same persistent
         * data, while distinct `name`s will use different data. If `name` is `null` then a platform-specific default
         * will be used.
         *
         * On the Android platform, this is implemented by calling [Context.getSharedPreferences] and passing [name]. If
         * `name` is `null` then a default package-specific name will be used instead.
         */
        public override fun create(name: String?): Settings {
            // For null name, match the behavior of PreferenceManager.getDefaultSharedPreferences()
            val preferencesName = name ?: "${appContext.packageName}_preferences"
            val delegate = appContext.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
            return KeystoreSettings(delegate)
        }
    }

    private val androidSettings = AndroidSettings(delegate, commit)

    private val encryptor = EnCryptor()

    private val decryptor = DeCryptor()

    private val keyStore
        get() = decryptor.keyStore

    override val keys: Set<String> = Collections.list(keyStore.aliases()).toSet()

    override val size: Int = keys.size

    override fun clear() {
        keys.forEach { key ->
            remove(key)
        }
    }

    override fun remove(key: String) {
        /* only remove keys that correspond to alias's in the keystore */
        if (hasKey(key)) {
            kotlin.runCatching {
                androidSettings.remove(key)
                keyStore.deleteEntry(key)
            }
        }
    }

    override fun hasKey(key: String): Boolean = keys.contains(key)

    override fun putInt(key: String, value: Int): Unit = encryptAndStore(key, value)

    override fun getInt(key: String, defaultValue: Int): Int = runCatching {

        val encryptedString = androidSettings.getString(key)
        val encryptedByeArray = encryptedString.toByteArray()
        val decryptedString = decryptor.decryptData(key, encryptedByeArray, null)
        decryptedString.toInt()
    }.onFailure {
        return defaultValue
    }.onSuccess {
        return it
    }.getOrElse {
        defaultValue
    }

    override fun getIntOrNull(key: String): Int? = runCatching {

        val encryptedString = androidSettings.getString(key)
        val encryptedByeArray = encryptedString.toByteArray()
        val decryptedString = decryptor.decryptData(key, encryptedByeArray, null)
        decryptedString.toInt()
    }.onSuccess {
        return it
    }.getOrNull()

    override fun putLong(key: String, value: Long): Unit = encryptAndStore(key, value)

    override fun getLong(key: String, defaultValue: Long): Long = runCatching {

        val encryptedString = androidSettings.getString(key)
        val encryptedByeArray = encryptedString.toByteArray()
        val decryptedString = decryptor.decryptData(key, encryptedByeArray, null)
        decryptedString.toLong()
    }.onFailure {
        return defaultValue
    }.onSuccess {
        return it
    }.getOrElse {
        defaultValue
    }

    override fun getLongOrNull(key: String): Long? = runCatching {

        val encryptedString = androidSettings.getString(key)
        val encryptedByeArray = encryptedString.toByteArray()
        val decryptedString = decryptor.decryptData(key, encryptedByeArray, null)
        decryptedString.toLong()
    }.onSuccess {
        return it
    }.getOrNull()

    override fun putString(key: String, value: String): Unit = encryptAndStore(key, value)

    override fun getString(key: String, defaultValue: String): String = runCatching {

        val encryptedString = androidSettings.getString(key)
        val encryptedByeArray = encryptedString.toByteArray()
        decryptor.decryptData(key, encryptedByeArray, null)
    }.onFailure {
        return defaultValue
    }.onSuccess {
        return it
    }.getOrElse {
        defaultValue
    }

    override fun getStringOrNull(key: String): String? = runCatching {

        val encryptedString = androidSettings.getString(key)
        val encryptedByeArray = encryptedString.toByteArray()
        decryptor.decryptData(key, encryptedByeArray, null)
    }.onSuccess {
        return it
    }.getOrNull()

    override fun putFloat(key: String, value: Float): Unit = encryptAndStore(key, value)

    override fun getFloat(key: String, defaultValue: Float): Float = runCatching {

        val encryptedString = androidSettings.getString(key)
        val encryptedByeArray = encryptedString.toByteArray()
        val decryptedString = decryptor.decryptData(key, encryptedByeArray, null)
        decryptedString.toFloat()
    }.onFailure {
        return defaultValue
    }.onSuccess {
        return it
    }.getOrElse {
        defaultValue
    }

    override fun getFloatOrNull(key: String): Float? = runCatching {

        val encryptedString = androidSettings.getString(key)
        val encryptedByeArray = encryptedString.toByteArray()
        val decryptedString = decryptor.decryptData(key, encryptedByeArray, null)
        decryptedString.toFloat()
    }.onSuccess {
        return it
    }.getOrNull()

    override fun putDouble(key: String, value: Double): Unit = encryptAndStore(key, value)

    override fun getDouble(key: String, defaultValue: Double): Double = runCatching {

        val encryptedString = androidSettings.getString(key)
        val encryptedByeArray = encryptedString.toByteArray()
        val decryptedString = decryptor.decryptData(key, encryptedByeArray, null)
        decryptedString.toDouble()
    }.onFailure {
        return defaultValue
    }.onSuccess {
        return it
    }.getOrElse {
        defaultValue
    }

    override fun getDoubleOrNull(key: String): Double? = runCatching {

        val encryptedString = androidSettings.getString(key)
        val encryptedByeArray = encryptedString.toByteArray()
        val decryptedString = decryptor.decryptData(key, encryptedByeArray, null)
        decryptedString.toDouble()
    }.onSuccess {
        return it
    }.getOrNull()

    override fun putBoolean(key: String, value: Boolean): Unit = encryptAndStore(key, value)

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = runCatching {

        val encryptedString = androidSettings.getString(key)
        val encryptedByeArray = encryptedString.toByteArray()
        val decryptedString = decryptor.decryptData(key, encryptedByeArray, null)
        decryptedString.toBoolean()
    }.onFailure {
        return defaultValue
    }.onSuccess {
        return it
    }.getOrElse {
        defaultValue
    }

    override fun getBooleanOrNull(key: String): Boolean? = runCatching {

        val encryptedString = androidSettings.getString(key)
        val encryptedByeArray = encryptedString.toByteArray()
        val decryptedString = decryptor.decryptData(key, encryptedByeArray, null)
        decryptedString.toBoolean()
    }.onSuccess {
        return it
    }.getOrNull()

    private fun encryptAndStore(alias: String, value: Any) {

        val encrypted = encryptor.encryptText(alias, value.toString())
        val encryptedString = encrypted.toString()
        androidSettings.putString(alias, encryptedString)
    }
}