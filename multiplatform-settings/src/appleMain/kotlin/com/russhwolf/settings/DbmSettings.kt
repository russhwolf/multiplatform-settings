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

import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValue
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.cValue
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.plus
import kotlinx.cinterop.pointed
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toCValues
import kotlinx.cinterop.useContents
import kotlinx.cinterop.value
import platform.darwin.DBM
import platform.darwin.DBM_REPLACE
import platform.darwin.datum
import platform.darwin.dbm_clearerr
import platform.darwin.dbm_close
import platform.darwin.dbm_delete
import platform.darwin.dbm_error
import platform.darwin.dbm_fetch
import platform.darwin.dbm_firstkey
import platform.darwin.dbm_nextkey
import platform.darwin.dbm_open
import platform.darwin.dbm_store
import platform.posix.O_CREAT
import platform.posix.O_RDWR
import platform.posix.S_IRGRP
import platform.posix.S_IROTH
import platform.posix.S_IRUSR
import platform.posix.S_IWUSR
import platform.posix.size_t

// TODO clean up error checking?
@UseExperimental(ExperimentalUnsignedTypes::class)
class DbmSettings(private val filename: String) : Settings {

    public override fun clear(): Unit = dbmTransaction { dbm -> dbm.keyIterator().forEach { dbm_delete(dbm, it) } }

    public override fun remove(key: String): Unit = dbmTransaction { dbm -> dbm_delete(dbm, datumOf(key)) }

    public override fun hasKey(key: String): Boolean = dbmTransaction { dbm ->
        val keyBytes = key.encodeToByteArray()
        dbm.keyIterator().asSequence().any { datum ->
            datum.toByteArray()?.contentEquals(keyBytes) == true
        }
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
    public override fun getBooleanOrNull(key: String): Boolean? = loadBytes(key)?.get(0)?.equals(0)?.not()

    private inline fun <T> dbmTransaction(action: MemScope.(dbm: CPointer<DBM>?) -> T): T = memScoped {
        val dbm = dbm_open(filename, O_RDWR or O_CREAT, (S_IRUSR or S_IWUSR or S_IRGRP or S_IROTH).toUShort())
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

    private inline fun saveBytes(key: String, bytes: ByteArray): Unit = dbmTransaction { dbm ->
        dbm_store(dbm, datumOf(key), datumOf(bytes), DBM_REPLACE)
    }

    private inline fun loadBytes(key: String): ByteArray? = dbmTransaction { dbm ->
        val datum = dbm_fetch(dbm, datumOf(key))
        datum.toByteArray()
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

    private inline fun CPointer<DBM>?.keyIterator(): Iterator<CValue<datum>> = object : Iterator<CValue<datum>> {
        private val dbm = this@keyIterator
        private var nextKey = dbm_firstkey(dbm)

        override fun hasNext(): Boolean = nextKey.useContents { dptr != null }

        override fun next(): CValue<datum> {
            val current = nextKey
            nextKey = dbm_nextkey(dbm)
            return current
        }
    }

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
        dsize = cValues.size.toSize_t()
    }
}

internal expect inline fun Int.toSize_t(): size_t
