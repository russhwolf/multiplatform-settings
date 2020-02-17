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

import com.russhwolf.settings.cinterop.DBM
import com.russhwolf.settings.cinterop.DBM_REPLACE
import com.russhwolf.settings.cinterop.datum
import com.russhwolf.settings.cinterop.dbm_close
import com.russhwolf.settings.cinterop.dbm_open
import com.russhwolf.settings.cinterop.dbm_store
import kotlinx.cinterop.ByteVar
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.CValuesRef
import kotlinx.cinterop.cValue
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.plus
import kotlinx.cinterop.pointed
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.toCValues
import kotlinx.cinterop.toKString
import kotlinx.cinterop.useContents
import kotlinx.cinterop.value
import platform.posix.O_CREAT
import platform.posix.O_RDWR
import platform.posix.S_IRGRP
import platform.posix.S_IROTH
import platform.posix.S_IRUSR
import platform.posix.S_IWUSR
import platform.posix.errno
import platform.posix.opendir
import platform.posix.readdir
import platform.posix.remove
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalUnsignedTypes::class)
class DbmSettings2Test : BaseSettingsTest(
    platformFactory = object : Settings.Factory {
        override fun create(name: String?): Settings = DbmSettings2(name ?: "dbm")
    },
    hasListeners = false
) {

    private lateinit var initialDbFiles: List<String>

    @AfterTest
    fun getInitialDbFiles() {
        initialDbFiles = getDbFiles()
    }

    @AfterTest
    fun cleanup() {
        val newDbFiles = getDbFiles() - initialDbFiles
        newDbFiles.forEach {
            remove(it)
        }
    }

    private fun getDbFiles(): List<String> {
        val directory = opendir("./") ?: return emptyList()

        val out = mutableListOf<String>()
        while (true) {
            val entry = readdir(directory) ?: break

            val filename = entry.pointed.d_name.toKString()
            if (filename.endsWith(".db")) {
                out.add(filename)
            }
        }
        return out
    }

    @Test
    fun datum_passthrough() {
        val outBytes = memScoped {
            val bytes = "hello".encodeToByteArray()
            val datum = cValue<datum> {
                val cValues = bytes.toCValues()
                dptr = cValues.ptr
                dsize = cValues.size
            }

            datum.useContents {
                val size = dsize
                val firstPtr: CPointer<ByteVar> = dptr?.reinterpret() ?: throw error("lol")
                ByteArray(size) {
                    val pointedValue = firstPtr.plus(it)?.pointed?.value
                    pointedValue ?: 0
                }
            }
        }
        assertEquals("hello", outBytes.decodeToString())
    }

    @Test
    fun constructor_filename() {
        val filename = "test_dbm"
        val settings = DbmSettings2(filename)

        memScoped {
            val dbm = dbm_open(filename.cstr, O_RDWR or O_CREAT, S_IRUSR or S_IWUSR or S_IRGRP or S_IROTH)
                ?: error("error: $errno")
            dbm.checkError()

            val key = cValue<datum> {
                val cValues = "key".encodeToByteArray().toCValues()
                dptr = cValues.ptr
                dsize = cValues.size
            }
            val value = cValue<datum> {
                val cValues = "value".encodeToByteArray().toCValues()
                dptr = cValues.ptr
                dsize = cValues.size
            }

            if (dbm_store(dbm, key, value, DBM_REPLACE) == -1) {
                dbm.checkError()
            }

            dbm_close(dbm)
        }

        assertEquals("value", settings["key", ""])
    }
}

private fun CValuesRef<DBM>.checkError() {
    assertEquals(0, dbm_error(this))
}
