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

import com.russhwolf.settings.cinterop.qdbm.villa.VILLA
import com.russhwolf.settings.cinterop.qdbm.villa.VL_CMPLEX
import com.russhwolf.settings.cinterop.qdbm.villa.VL_DOVER
import com.russhwolf.settings.cinterop.qdbm.villa.VL_OCREAT
import com.russhwolf.settings.cinterop.qdbm.villa.VL_OREADER
import com.russhwolf.settings.cinterop.qdbm.villa.VL_OWRITER
import com.russhwolf.settings.cinterop.qdbm.villa.dpecode
import com.russhwolf.settings.cinterop.qdbm.villa.dperrmsg
import com.russhwolf.settings.cinterop.qdbm.villa.vlclose
import com.russhwolf.settings.cinterop.qdbm.villa.vlcurfirst
import com.russhwolf.settings.cinterop.qdbm.villa.vlcurkey
import com.russhwolf.settings.cinterop.qdbm.villa.vlcurnext
import com.russhwolf.settings.cinterop.qdbm.villa.vlget
import com.russhwolf.settings.cinterop.qdbm.villa.vlopen
import com.russhwolf.settings.cinterop.qdbm.villa.vlout
import com.russhwolf.settings.cinterop.qdbm.villa.vlput
import com.russhwolf.settings.cinterop.qdbm.villa.vlrnum
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toKString

// TODO clean up error checking?
// TODO allow specifying directory
@OptIn(ExperimentalForeignApi::class)
@ExperimentalSettingsImplementation
public class QdbmVillaSettings(private val path: String) : Settings {

    override val keys: Set<String>
        get() = villaOperation { villa ->
            villa.foldKeys(mutableListOf<String>()) { list, key -> list.apply { add(key) } }.toSet()
        }

    override val size: Int get() = villaOperation { villa -> vlrnum(villa) }

    public override fun clear(): Unit = keys.forEach { remove(it) }

    //villaOperation { villa -> villa.forEachKey { vlout(villa, it, -1) } }
    public override fun remove(key: String): Unit = villaOperation { villa -> vlout(villa, key, -1) }
    public override fun hasKey(key: String): Boolean = villaOperation { villa ->
        villa.forEachKey { if (key == it) return true }
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

    private inline fun saveString(key: String, value: String): Unit = villaOperation { villa ->
        vlput(villa, key, -1, value, -1, VL_DOVER.toInt())
    }

    private inline fun loadString(key: String): String? = villaOperation { villa ->
        val output = vlget(villa, key, -1, null)
        output?.toKString()
    }

    private inline fun CPointer<VILLA>.forEachKey(block: (key: String) -> Unit) {
        val villa = this
        if (vlcurfirst(villa) != 0) {
            while (true) {
                val key = vlcurkey(villa, null) ?: error("error iterating through Villa keys!")
                block(key.toKString())
                if (vlcurnext(villa) == 0) {
                    break
                }
            }
        }
    }

    private inline fun <A> CPointer<VILLA>.foldKeys(initial: A, block: (accumulator: A, key: String) -> A): A {
        var accumulator = initial
        forEachKey { accumulator = block(accumulator, it) }
        return accumulator
    }

    private inline fun <T> villaOperation(action: MemScope.(villa: CPointer<VILLA>) -> T): T = memScoped {
        val villa = vlopen(path, (VL_OWRITER or VL_OREADER or VL_OCREAT).toInt(), VL_CMPLEX)
        if (villa == null) {
            val message = dperrmsg(dpecode)?.toKString()
            error("error on villa open: $message")
        }
        val out = action(villa)
        vlclose(villa)
        out
    }
}
