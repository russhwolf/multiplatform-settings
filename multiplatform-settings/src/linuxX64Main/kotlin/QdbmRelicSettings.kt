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

import com.russhwolf.settings.cinterop.qdbm.relic.DBM
import com.russhwolf.settings.cinterop.qdbm.relic.DBM_REPLACE
import com.russhwolf.settings.cinterop.qdbm.relic.datum
import com.russhwolf.settings.cinterop.qdbm.relic.dbm_clearerr
import com.russhwolf.settings.cinterop.qdbm.relic.dbm_close
import com.russhwolf.settings.cinterop.qdbm.relic.dbm_delete
import com.russhwolf.settings.cinterop.qdbm.relic.dbm_error
import com.russhwolf.settings.cinterop.qdbm.relic.dbm_fetch
import com.russhwolf.settings.cinterop.qdbm.relic.dbm_firstkey
import com.russhwolf.settings.cinterop.qdbm.relic.dbm_nextkey
import com.russhwolf.settings.cinterop.qdbm.relic.dbm_open
import com.russhwolf.settings.cinterop.qdbm.relic.dbm_store
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValue
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.cValue
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.plus
import kotlinx.cinterop.pointed
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toCValues
import kotlinx.cinterop.useContents
import kotlinx.cinterop.value
import platform.posix.O_CREAT
import platform.posix.O_RDWR
import platform.posix.S_IRGRP
import platform.posix.S_IROTH
import platform.posix.S_IRUSR
import platform.posix.S_IWUSR
import platform.posix.errno

// TODO clean up error checking?
// TODO allow specifying directory
@OptIn(ExperimentalForeignApi::class)
@ExperimentalSettingsImplementation
public class QdbmRelicSettings(private val path: String) : Settings {

    override val keys: Set<String>
        get() = dbmOperation { dbm ->
            dbm.foldKeys(mutableListOf<String>()) { list, key -> list.apply { add(key.toKString()!!) } }.toSet()
        }

    override val size: Int get() = dbmOperation { dbm -> dbm.foldKeys(0) { size, _ -> size + 1 } }

    public override fun clear(): Unit = dbmOperation { dbm -> dbm.forEachKey { dbm_delete(dbm, it) } }
    public override fun remove(key: String): Unit = dbmOperation { dbm -> dbm_delete(dbm, datumOf(key)) }
    public override fun hasKey(key: String): Boolean = dbmOperation { dbm ->
        dbm.forEachKey { if (key == it.toKString()) return true }
        return false
    }

    public override fun putInt(key: String, value: Int): Unit = saveBytes(key, value.toByteArray())
    public override fun getInt(key: String, defaultValue: Int): Int = getIntOrNull(key) ?: defaultValue
    public override fun getIntOrNull(key: String): Int? = loadBytes(key)?.toInt()

    public override fun putLong(key: String, value: Long): Unit = saveBytes(key, value.toByteArray())
    public override fun getLong(key: String, defaultValue: Long): Long = getLongOrNull(key) ?: defaultValue
    public override fun getLongOrNull(key: String): Long? = loadBytes(key)?.toLong()

    public override fun putString(key: String, value: String): Unit = saveBytes(key, value.encodeToByteArray())
    public override fun getString(key: String, defaultValue: String): String = getStringOrNull(key) ?: defaultValue
    public override fun getStringOrNull(key: String): String? = loadBytes(key)?.decodeToString()

    public override fun putFloat(key: String, value: Float): Unit = saveBytes(key, value.toRawBits().toByteArray())
    public override fun getFloat(key: String, defaultValue: Float): Float = getFloatOrNull(key) ?: defaultValue
    public override fun getFloatOrNull(key: String): Float? = loadBytes(key)?.toInt()?.let { Float.fromBits(it) }

    public override fun putDouble(key: String, value: Double): Unit = saveBytes(key, value.toRawBits().toByteArray())
    public override fun getDouble(key: String, defaultValue: Double): Double = getDoubleOrNull(key) ?: defaultValue
    public override fun getDoubleOrNull(key: String): Double? = loadBytes(key)?.toLong()?.let { Double.fromBits(it) }

    public override fun putBoolean(key: String, value: Boolean): Unit = saveBytes(key, byteArrayOf(if (value) 1 else 0))
    public override fun getBoolean(key: String, defaultValue: Boolean): Boolean = getBooleanOrNull(key) ?: defaultValue
    public override fun getBooleanOrNull(key: String): Boolean? = loadBytes(key)?.get(0)?.equals(0.toByte())?.not()

    private inline fun saveBytes(key: String, bytes: ByteArray): Unit = dbmOperation { dbm ->
        dbm_store(dbm, datumOf(key), datumOf(bytes), DBM_REPLACE.toInt())
    }

    private inline fun loadBytes(key: String): ByteArray? = dbmOperation { dbm ->
        val datum = dbm_fetch(dbm, datumOf(key))
        datum.toByteArray()
    }

    private inline fun CPointer<DBM>.forEachKey(block: (key: CValue<datum>) -> Unit) {
        val dbm = this
        var key = dbm_firstkey(dbm)
        while (key.useContents { dptr != null }) {
            block(key)
            key = dbm_nextkey(dbm)
        }
    }

    private inline fun <A> CPointer<DBM>.foldKeys(initial: A, block: (accumulator: A, key: CValue<datum>) -> A): A {
        var accumulator = initial
        forEachKey { accumulator = block(accumulator, it) }
        return accumulator
    }

    private inline fun <T> dbmOperation(action: MemScope.(dbm: CPointer<DBM>) -> T): T = memScoped {
        val dbm = dbm_open(path.cstr, O_RDWR or O_CREAT, S_IRUSR or S_IWUSR or S_IRGRP or S_IROTH)
            ?: error("Error on dbm_open: $errno")
        val out = action(dbm)
        val error = dbm_error(dbm)
        if (error != 0) {
            try {
                error("error: $error")
            } finally {
                dbm_clearerr(dbm)
            }
        }
        dbm_close(dbm)
        out
    }

    private inline fun ByteArray.toLong(): Long = foldIndexed(0) { index, total: Long, byte: Byte ->
        ((0xff.toLong() and byte.toLong()) shl index * Byte.SIZE_BITS) or total
    }

    private inline fun ByteArray.toInt(): Int = foldIndexed(0) { index, total: Int, byte: Byte ->
        ((0xff and byte.toInt()) shl index * Byte.SIZE_BITS) or total
    }

    private inline fun Long.toByteArray(): ByteArray = ByteArray(Long.SIZE_BYTES) { index ->
        ((this shr (Byte.SIZE_BITS * index)) and 0xff).toByte()
    }

    private inline fun Int.toByteArray(): ByteArray = ByteArray(Int.SIZE_BYTES) { index ->
        ((this shr (Byte.SIZE_BITS * index)) and 0xff).toByte()
    }

    private inline fun CValue<datum>.toKString(): String? = toByteArray()?.decodeToString()
    private inline fun CValue<datum>.toByteArray(): ByteArray? = useContents {
        val size = dsize.toInt()
        val firstPtr: CPointer<ByteVar> = dptr?.reinterpret()
            ?: return null
        return ByteArray(size) {
            val pointedValue = firstPtr.plus(it)?.pointed?.value
            pointedValue ?: 0
        }
    }

    private inline fun MemScope.datumOf(string: String): CValue<datum> = datumOf(string.encodeToByteArray())
    private inline fun MemScope.datumOf(bytes: ByteArray): CValue<datum> = cValue {
        val cValues = bytes.toCValues()
        dptr = cValues.ptr
        dsize = cValues.size.toULong()
    }
}
