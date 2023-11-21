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

import cnames.structs.MDB_cursor
import cnames.structs.MDB_env
import cnames.structs.MDB_txn
import com.russhwolf.settings.cinterop.lmdb.MDB_CREATE
import com.russhwolf.settings.cinterop.lmdb.MDB_NOTFOUND
import com.russhwolf.settings.cinterop.lmdb.MDB_SUCCESS
import com.russhwolf.settings.cinterop.lmdb.MDB_cursor_op
import com.russhwolf.settings.cinterop.lmdb.MDB_dbi
import com.russhwolf.settings.cinterop.lmdb.MDB_dbiVar
import com.russhwolf.settings.cinterop.lmdb.MDB_stat
import com.russhwolf.settings.cinterop.lmdb.MDB_val
import com.russhwolf.settings.cinterop.lmdb.mdb_cursor_close
import com.russhwolf.settings.cinterop.lmdb.mdb_cursor_del
import com.russhwolf.settings.cinterop.lmdb.mdb_cursor_get
import com.russhwolf.settings.cinterop.lmdb.mdb_cursor_open
import com.russhwolf.settings.cinterop.lmdb.mdb_dbi_close
import com.russhwolf.settings.cinterop.lmdb.mdb_dbi_open
import com.russhwolf.settings.cinterop.lmdb.mdb_del
import com.russhwolf.settings.cinterop.lmdb.mdb_env_close
import com.russhwolf.settings.cinterop.lmdb.mdb_env_create
import com.russhwolf.settings.cinterop.lmdb.mdb_env_open
import com.russhwolf.settings.cinterop.lmdb.mdb_get
import com.russhwolf.settings.cinterop.lmdb.mdb_put
import com.russhwolf.settings.cinterop.lmdb.mdb_stat
import com.russhwolf.settings.cinterop.lmdb.mdb_strerror
import com.russhwolf.settings.cinterop.lmdb.mdb_txn_begin
import com.russhwolf.settings.cinterop.lmdb.mdb_txn_commit
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toKString
import kotlinx.cinterop.value
import platform.posix.S_IRWXG
import platform.posix.S_IRWXO
import platform.posix.S_IRWXU
import platform.posix.mkdir

@OptIn(ExperimentalForeignApi::class)
@ExperimentalSettingsImplementation
public class LmdbSettings(private val path: String) : Settings {
    override val keys: Set<String>
        get() = buildList {
            lmdbCursorTransaction { _, key ->
                add(key.mv_data!!.reinterpret<ByteVar>().toKString())
            }
        }.toSet()

    override val size: Int
        get() = lmdbTransaction { txn, dbi ->
            val stat = alloc<MDB_stat>()
            mdb_stat(txn, dbi, stat.ptr)
            stat.ms_entries.toInt()
        }

    public override fun clear(): Unit = lmdbCursorTransaction { cursor, _ ->
        mdb_cursor_del(cursor, 0.toUInt())
    }

    public override fun remove(key: String): Unit =
        lmdbTransaction { txn, dbi -> mdb_del(txn, dbi, createMdbVal(key).ptr, null).checkError(MDB_NOTFOUND) }

    public override fun hasKey(key: String): Boolean = loadString(key) != null

    public override fun putInt(key: String, value: Int): Unit = saveString(key, value.toString())
    public override fun getInt(key: String, defaultValue: Int): Int = getIntOrNull(key) ?: defaultValue
    public override fun getIntOrNull(key: String): Int? = loadString(key)?.toInt()

    public override fun putLong(key: String, value: Long): Unit = saveString(key, value.toString())
    public override fun getLong(key: String, defaultValue: Long): Long = getLongOrNull(key) ?: defaultValue
    public override fun getLongOrNull(key: String): Long? = loadString(key)?.toLong()

    public override fun putString(key: String, value: String): Unit = saveString(key, value)
    public override fun getString(key: String, defaultValue: String): String = getStringOrNull(key) ?: defaultValue
    public override fun getStringOrNull(key: String): String? = loadString(key)

    public override fun putFloat(key: String, value: Float): Unit = saveString(key, value.toString())
    public override fun getFloat(key: String, defaultValue: Float): Float = getFloatOrNull(key) ?: defaultValue
    public override fun getFloatOrNull(key: String): Float? = loadString(key)?.toFloat()

    public override fun putDouble(key: String, value: Double): Unit = saveString(key, value.toString())
    public override fun getDouble(key: String, defaultValue: Double): Double = getDoubleOrNull(key) ?: defaultValue
    public override fun getDoubleOrNull(key: String): Double? = loadString(key)?.toDouble()

    public override fun putBoolean(key: String, value: Boolean): Unit = saveString(key, value.toString())
    public override fun getBoolean(key: String, defaultValue: Boolean): Boolean = getBooleanOrNull(key) ?: defaultValue
    public override fun getBooleanOrNull(key: String): Boolean? = loadString(key)?.toBoolean()

    private inline fun saveString(key: String, value: String): Unit = lmdbTransaction { txn, dbi ->
        mdb_put(txn, dbi, createMdbVal(key).ptr, createMdbVal(value).ptr, 0.toUInt()).checkError()
    }

    private inline fun loadString(key: String): String? = lmdbTransaction { txn, dbi ->
        val value = alloc<MDB_val>()
        mdb_get(txn, dbi, createMdbVal(key).ptr, value.ptr).checkError(MDB_NOTFOUND)
        value.mv_data?.reinterpret<ByteVar>()?.toKString()
    }

    private fun lmdbCursorTransaction(action: MemScope.(cursor: CPointer<MDB_cursor>?, key: MDB_val) -> Unit) =
        lmdbTransaction { txn, dbi ->
            val cursor = allocPointerTo<MDB_cursor>()
            mdb_cursor_open(txn, dbi, cursor.ptr).checkError()
            val mdbKey = alloc<MDB_val>()
            val mdbValue = alloc<MDB_val>()
            mdb_cursor_get(cursor.pointed?.ptr, mdbKey.ptr, mdbValue.ptr, MDB_cursor_op.MDB_FIRST).checkError(
                MDB_NOTFOUND
            )
            while (true) {
                if (mdbKey.mv_data == null) {
                    break
                } else {
                    action(cursor.pointed?.ptr, mdbKey)
                }
                mdbKey.mv_data = null
                mdbKey.mv_size = 0u
                mdb_cursor_get(cursor.pointed?.ptr, mdbKey.ptr, mdbValue.ptr, MDB_cursor_op.MDB_NEXT).checkError(
                    MDB_NOTFOUND
                )
            }
            mdb_cursor_close(cursor.pointed?.ptr)

        }

    private fun MemScope.createMdbVal(string: String): MDB_val {
        val mdbVal = alloc<MDB_val>()
        val cstr = string.cstr
        mdbVal.mv_data = cstr.ptr
        mdbVal.mv_size = cstr.size.toULong()
        return mdbVal
    }

    private inline fun <T> lmdbTransaction(action: MemScope.(txn: CPointer<MDB_txn>?, dbi: MDB_dbi) -> T): T =
        memScoped {
            val env = allocPointerTo<MDB_env>()
            mdb_env_create(env.ptr).checkError()
            val mode = (S_IRWXU or S_IRWXG or S_IRWXO).toUInt()
            mkdir(path, mode)
            mdb_env_open(env.pointed?.ptr, path, 0.toUInt(), mode).checkError()

            val txn = allocPointerTo<MDB_txn>()
            mdb_txn_begin(env.pointed?.ptr, null, 0.toUInt(), txn.ptr).checkError()

            val dbi = alloc<MDB_dbiVar>()
            mdb_dbi_open(txn.pointed?.ptr, null, MDB_CREATE.toUInt(), dbi.ptr).checkError()

            val out = action(txn.pointed?.ptr, dbi.value)

            mdb_dbi_close(env.pointed?.ptr, dbi.value)
            mdb_txn_commit(txn.pointed?.ptr).checkError()
            mdb_env_close(env.pointed?.ptr)

            out
        }

    private fun Int.checkError(vararg expectedErrors: Int) {
        assert(this == MDB_SUCCESS || this in expectedErrors) { "Error: ${mdb_strerror(this)?.toKString()}" }
    }
}
