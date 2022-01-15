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

import com.russhwolf.settings.cinterop.qdbm.depot.DEPOT
import com.russhwolf.settings.cinterop.qdbm.depot.DP_DOVER
import com.russhwolf.settings.cinterop.qdbm.depot.DP_OCREAT
import com.russhwolf.settings.cinterop.qdbm.depot.DP_OREADER
import com.russhwolf.settings.cinterop.qdbm.depot.DP_OWRITER
import com.russhwolf.settings.cinterop.qdbm.depot.dpclose
import com.russhwolf.settings.cinterop.qdbm.depot.dpecode
import com.russhwolf.settings.cinterop.qdbm.depot.dperrmsg
import com.russhwolf.settings.cinterop.qdbm.depot.dpget
import com.russhwolf.settings.cinterop.qdbm.depot.dpiterinit
import com.russhwolf.settings.cinterop.qdbm.depot.dpiternext
import com.russhwolf.settings.cinterop.qdbm.depot.dpopen
import com.russhwolf.settings.cinterop.qdbm.depot.dpout
import com.russhwolf.settings.cinterop.qdbm.depot.dpput
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString

// TODO clean up error checking?
// TODO allow specifying directory
@OptIn(ExperimentalForeignApi::class)
@ExperimentalSettingsImplementation
public class QdbmDepotSettings(private val path: String) : Settings {

    override val keys: Set<String>
        get() = depotOperation { depot ->
            depot.foldKeys(mutableListOf<String>()) { list, key -> list.apply { add(key) } }.toSet()
        }

    override val size: Int get() = depotOperation { depot -> depot.foldKeys(0) { size, _ -> size + 1 } }

    public override fun clear(): Unit = depotOperation { depot -> depot.forEachKey { dpout(depot, it, -1) } }
    public override fun remove(key: String): Unit = depotOperation { depot -> dpout(depot, key, -1) }
    public override fun hasKey(key: String): Boolean = depotOperation { depot ->
        depot.forEachKey { if (key == it) return true }
        return false
    }

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

    private inline fun saveString(key: String, value: String): Unit = depotOperation { depot ->
        dpput(depot, key, -1, value, -1, DP_DOVER.toInt())
    }

    private inline fun loadString(key: String): String? = depotOperation { depot ->
        val output = dpget(depot, key, -1, 0, -1, null)
        output?.toKString()
    }

    private inline fun CPointer<DEPOT>.forEachKey(block: (key: String) -> Unit) {
        val depot = this
        if (dpiterinit(depot) != 0) {
            while (true) {
                val key = dpiternext(depot, null)?.toKString()
                if (key != null) {
                    block(key)
                } else {
                    break
                }
            }
        }
    }

    private inline fun <A> CPointer<DEPOT>.foldKeys(initial: A, block: (accumulator: A, key: String) -> A): A {
        var accumulator = initial
        forEachKey { accumulator = block(accumulator, it) }
        return accumulator
    }

    private inline fun <T> depotOperation(action: MemScope.(depot: CPointer<DEPOT>) -> T): T = memScoped {
        val depot = dpopen(path, (DP_OWRITER or DP_OREADER or DP_OCREAT).toInt(), 0)
        if (depot == null) {
            val message = dperrmsg(dpecode)?.toKString()
            error("error on depot open: $message")
        }
        val out = action(depot)
        dpclose(depot)
        out
    }
}
