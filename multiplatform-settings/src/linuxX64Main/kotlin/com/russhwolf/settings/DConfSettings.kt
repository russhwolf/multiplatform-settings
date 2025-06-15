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

import com.russhwolf.settings.cinterop.dconf.DConfClient
import com.russhwolf.settings.cinterop.dconf.FALSE
import com.russhwolf.settings.cinterop.dconf.GError
import com.russhwolf.settings.cinterop.dconf.GVariant
import com.russhwolf.settings.cinterop.dconf.dconf_client_list
import com.russhwolf.settings.cinterop.dconf.dconf_client_new
import com.russhwolf.settings.cinterop.dconf.dconf_client_read
import com.russhwolf.settings.cinterop.dconf.dconf_client_sync
import com.russhwolf.settings.cinterop.dconf.dconf_client_write_sync
import com.russhwolf.settings.cinterop.dconf.dconf_is_rel_key
import com.russhwolf.settings.cinterop.dconf.g_object_ref
import com.russhwolf.settings.cinterop.dconf.g_object_unref
import com.russhwolf.settings.cinterop.dconf.g_variant_get_boolean
import com.russhwolf.settings.cinterop.dconf.g_variant_get_double
import com.russhwolf.settings.cinterop.dconf.g_variant_get_int32
import com.russhwolf.settings.cinterop.dconf.g_variant_get_int64
import com.russhwolf.settings.cinterop.dconf.g_variant_get_string
import com.russhwolf.settings.cinterop.dconf.g_variant_new_boolean
import com.russhwolf.settings.cinterop.dconf.g_variant_new_double
import com.russhwolf.settings.cinterop.dconf.g_variant_new_int32
import com.russhwolf.settings.cinterop.dconf.g_variant_new_int64
import com.russhwolf.settings.cinterop.dconf.g_variant_new_string
import com.russhwolf.settings.cinterop.dconf.gintVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.get
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import platform.posix.NULL


@OptIn(ExperimentalForeignApi::class)
@ExperimentalSettingsImplementation
public class DConfSettings(private val dir: String) : Settings {
    // TODO sanitize slashes on `dir`
    public class Factory(private val rootDir: String) : Settings.Factory {
        // TODO sanitize slashes on `rootDir` and `name`
        override fun create(name: String?): Settings {
            val dir = if (name != null) "$rootDir/$name/" else "$rootDir/"
            return DConfSettings("/${dir.removePrefix("/")}")
        }
    }

    override val keys: Set<String>
        get() = dConfOperation { dConfClient ->
            buildSet { forEachKey(dConfClient) { add(it) } }
        }

    override val size: Int
        get() = dConfOperation { dConfClient ->
            foldKeys(dConfClient, 0) { size, _ -> size + 1 } ?: 0
        }

    override fun clear(): Unit = dConfOperation { dConfClient ->
        // TODO can we do this without repeating remove internals? (nested dConfOperation causes problems)
        forEachKey(dConfClient) { key ->
            val error = allocPointerTo<GError>()
            val out = dconf_client_write_sync(dConfClient, "$dir$key", NULL?.reinterpret(), null, null, error.ptr)
            if (out == FALSE) {
                checkError(error.pointed)
            }
        }
    }

    override fun remove(key: String) {
        removeGVariant(key)
    }

    override fun hasKey(key: String): Boolean = dConfOperation { dConfClient ->
        forEachKey(dConfClient) { if (it == key) return@dConfOperation true }
        false
    }

    override fun putInt(key: String, value: Int): Unit = writeGVariant(key, value)
    override fun getInt(key: String, defaultValue: Int): Int = getIntOrNull(key) ?: defaultValue
    override fun getIntOrNull(key: String): Int? = readGVariant(key)?.let { g_variant_get_int32(it) }


    override fun putLong(key: String, value: Long): Unit = writeGVariant(key, value)
    override fun getLong(key: String, defaultValue: Long): Long = getLongOrNull(key) ?: defaultValue
    override fun getLongOrNull(key: String): Long? = readGVariant(key)?.let { g_variant_get_int64(it) }

    override fun putString(key: String, value: String): Unit = writeGVariant(key, value)
    override fun getString(key: String, defaultValue: String): String = getStringOrNull(key) ?: defaultValue
    override fun getStringOrNull(key: String): String? =
        readGVariant(key)?.let { g_variant_get_string(it, null)?.toKString() }

    override fun putFloat(key: String, value: Float): Unit = writeGVariant(key, value)
    override fun getFloat(key: String, defaultValue: Float): Float = getFloatOrNull(key) ?: defaultValue
    override fun getFloatOrNull(key: String): Float? = readGVariant(key)?.let { g_variant_get_double(it).toFloat() }

    override fun putDouble(key: String, value: Double): Unit = writeGVariant(key, value)
    override fun getDouble(key: String, defaultValue: Double): Double = getDoubleOrNull(key) ?: defaultValue
    override fun getDoubleOrNull(key: String): Double? = readGVariant(key)?.let { g_variant_get_double(it) }

    override fun putBoolean(key: String, value: Boolean): Unit = writeGVariant(key, value)
    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = getBooleanOrNull(key) ?: defaultValue
    override fun getBooleanOrNull(key: String): Boolean? = readGVariant(key)?.let { g_variant_get_boolean(it) == 1 }

    private inline fun MemScope.forEachKey(dConfClient: CPointer<DConfClient>?, block: MemScope.(key: String) -> Unit) {
        val lengthVar = alloc<gintVar>()
        val list = dconf_client_list(dConfClient, dir, lengthVar.ptr) ?: return

        var index = 0
        var item = list[index]
        while (item != null) {
            val key = item.toKString()
            if (dconf_is_rel_key(key, null) != FALSE) {
                block(key)
            }

            item = list[++index]
        }
    }

    private inline fun <A> MemScope.foldKeys(
        dConfClient: CPointer<DConfClient>?,
        initial: A,
        block: MemScope.(accumulator: A, key: String) -> A
    ): A {
        var accumulator = initial
        forEachKey(dConfClient) { accumulator = block(accumulator, it) }
        return accumulator
    }

    internal fun readGVariant(key: String): CPointer<GVariant>? = dConfOperation { dConfClient ->
        dconf_client_read(dConfClient, "$dir$key")
    }

    internal fun <T> writeGVariant(key: String, value: T) = dConfOperation { dConfClient ->
        val gVariant = gVariantOf(value)
        val error = allocPointerTo<GError>()
        val out = dconf_client_write_sync(dConfClient, "$dir$key", gVariant, null, null, error.ptr)
        if (out == FALSE) {
            checkError(error.pointed)
        }
    }

    internal fun removeGVariant(key: String) = writeGVariant(key, null)

    internal fun <T> gVariantOf(value: T): CPointer<GVariant>? {
        return when (value) {
            null -> NULL?.reinterpret()
            is Int -> g_variant_new_int32(value)
            is Long -> g_variant_new_int64(value)
            is String -> g_variant_new_string(value)
            is Float -> g_variant_new_double(value.toDouble())
            is Double -> g_variant_new_double(value)
            is Boolean -> g_variant_new_boolean(if (value) 1 else 0)
            else -> error("Invalid value type for gVariant! value=$value")
        }
    }

    internal inline fun <T> dConfOperation(action: MemScope.(dConfClient: CPointer<DConfClient>?) -> T): T = memScoped {
        val dConfClient = dconf_client_new()
        g_object_ref(dConfClient)
        val out = action(dConfClient)
        dconf_client_sync(dConfClient)
        g_object_unref(dConfClient)
        out
    }

    private fun checkError(error: GError?) {
        if (error != null) {
            error("dconf error: ${error.message?.toKString()}")
        }
    }
}
