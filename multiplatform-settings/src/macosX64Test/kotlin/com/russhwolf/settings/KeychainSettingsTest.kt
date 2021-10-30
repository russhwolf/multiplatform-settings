/*
 * Copyright 2021 Russell Wolf
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

import kotlinx.cinterop.alloc
import kotlinx.cinterop.ptr
import kotlinx.cinterop.value
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Security.SecItemCopyMatching
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import kotlin.test.Test
import kotlin.test.assertEquals

// TODO figure out how to get this running on ios, watchos, and tvos simulators
@ExperimentalSettingsImplementation
class KeychainSettingsTest : BaseSettingsTest(
    platformFactory = object : Settings.Factory {
        override fun create(name: String?): Settings = KeychainSettings(name ?: "com.russhwolf.settings.test")
    },
    hasListeners = false
) {

    @Test
    fun constructor_application() {
        val settings = KeychainSettings("com.russhwolf.settings.test")
        settings -= "key"
        settings["key"] = "value"
        val value = try {
            cfRetain("key", "com.russhwolf.settings.test") { cfKey, cfService ->
                val cfValue = alloc<CFTypeRefVar>()
                val query = cfDictionaryOf(
                    kSecClass to kSecClassGenericPassword,
                    kSecAttrAccount to cfKey,
                    kSecReturnData to kCFBooleanTrue,
                    kSecMatchLimit to kSecMatchLimitOne,
                    kSecAttrService to cfService
                )
                val status = SecItemCopyMatching(query, cfValue.ptr)
                assertEquals(0, status)
                val nsData = CFBridgingRelease(cfValue.value) as NSData
                NSString.create(nsData, NSUTF8StringEncoding)?.toKString()
            }
        } finally {
            settings -= "key"
        }
        assertEquals("value", value)
    }
}
