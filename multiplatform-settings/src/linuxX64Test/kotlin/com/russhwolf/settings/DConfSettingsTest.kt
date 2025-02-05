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

@file:OptIn(ExperimentalForeignApi::class)

package com.russhwolf.settings

import com.russhwolf.settings.cinterop.dconf.GError
import com.russhwolf.settings.cinterop.dconf.TRUE
import com.russhwolf.settings.cinterop.dconf.dconf_client_list
import com.russhwolf.settings.cinterop.dconf.dconf_client_new
import com.russhwolf.settings.cinterop.dconf.dconf_client_read
import com.russhwolf.settings.cinterop.dconf.dconf_client_sync
import com.russhwolf.settings.cinterop.dconf.dconf_client_write_sync
import com.russhwolf.settings.cinterop.dconf.g_object_ref
import com.russhwolf.settings.cinterop.dconf.g_object_unref
import com.russhwolf.settings.cinterop.dconf.g_variant_get_int32
import com.russhwolf.settings.cinterop.dconf.g_variant_new_int32
import com.russhwolf.settings.cinterop.dconf.gintVar
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocPointerTo
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import platform.posix.NULL
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@OptIn(ExperimentalSettingsImplementation::class)
public class DConfSettingsTest : BaseSettingsTest(
    platformFactory = DConfSettings.Factory("com/russhwolf/settings/test"),
    hasListeners = false
) {
    @Test
    fun foo(): Unit = memScoped {
        val dConfClient = dconf_client_new()
        g_object_ref(dConfClient)

        try {
            val error = allocPointerTo<GError>()
            val writeStatus = dconf_client_write_sync(
                client = dConfClient,
                key = "/com/russhwolf/settings/test/foo",
                value = g_variant_new_int32(42),
                tag = NULL?.reinterpret(),
                cancellable = NULL?.reinterpret(),
                error = error.ptr
            )

            assertEquals(TRUE, writeStatus)

            dconf_client_sync(dConfClient)

            val keysSizeVar = alloc<gintVar>()
            val keys = dconf_client_list(dConfClient, "/com/russhwolf/settings/test/", keysSizeVar.ptr)
            val keysSize = keysSizeVar.value
            assertTrue(keysSize > 0)
            val read = dconf_client_read(dConfClient, "/com/russhwolf/settings/test/foo")
            assertNotNull(read)
            assertEquals(42, g_variant_get_int32(read))
        } finally {
            dconf_client_sync(dConfClient)
            g_object_unref(dConfClient)
        }

    }
    // TODO add test cases to verify that we write to the files we think we do

    // TODO add cleanup methods so we don't leave test DBs lying around
}
