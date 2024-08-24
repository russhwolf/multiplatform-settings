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

@file:OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)

package com.russhwolf.settings

import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArrayOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.ptr
import kotlinx.cinterop.reinterpret
import kotlinx.cinterop.value
import platform.CoreFoundation.CFDictionaryCreate
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFStringRef
import platform.CoreFoundation.CFTypeRef
import platform.CoreFoundation.CFTypeRefVar
import platform.CoreFoundation.kCFAllocatorDefault
import platform.CoreFoundation.kCFBooleanTrue
import platform.Foundation.CFBridgingRelease
import platform.Foundation.CFBridgingRetain
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.create
import platform.Security.SecItemCopyMatching
import platform.Security.kSecAttrAccessGroup
import platform.Security.kSecAttrAccount
import platform.Security.kSecAttrService
import platform.Security.kSecClass
import platform.Security.kSecClassGenericPassword
import platform.Security.kSecMatchLimit
import platform.Security.kSecMatchLimitOne
import platform.Security.kSecReturnData
import kotlin.test.Test
import kotlin.test.assertEquals

@ExperimentalSettingsImplementation
@OptIn(ExperimentalForeignApi::class)
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

    @Test
    fun keys_no_name() {
        val settings = KeychainSettings()

        // Ensure this doesn't throw
        settings.keys
    }

    @OptIn(ExperimentalSettingsApi::class)
    @Test
    fun custom_attributes_group() {
        val settings = KeychainSettings(
            kSecAttrService to CFBridgingRetain("SettingsGroupTest"),
            kSecAttrAccessGroup to CFBridgingRetain("group.com.russhwolf.settings.test")
        )

        settings["key"] = "value"

        assertEquals("value", settings["key"])
    }
}

// Copy/paste utilities since we can't access internals now that this is a separate module
private fun MemScope.cfDictionaryOf(vararg items: Pair<CFStringRef?, CFTypeRef?>): CFDictionaryRef? =
    cfDictionaryOf(mapOf(*items))

private fun MemScope.cfDictionaryOf(map: Map<CFStringRef?, CFTypeRef?>): CFDictionaryRef? {
    val size = map.size
    val keys = allocArrayOf(*map.keys.toTypedArray())
    val values = allocArrayOf(*map.values.toTypedArray())
    return CFDictionaryCreate(
        kCFAllocatorDefault,
        keys.reinterpret(),
        values.reinterpret(),
        size.convert(),
        null,
        null
    )
}

// Turn casts into dot calls for better readability
@Suppress("CAST_NEVER_SUCCEEDS")
private fun String.toNSString() = this as NSString

@Suppress("CAST_NEVER_SUCCEEDS")
private fun NSString.toKString() = this as String

private inline fun <T> cfRetain(value: Any?, block: MemScope.(CFTypeRef?) -> T): T = memScoped {
    val cfValue = CFBridgingRetain(value)
    return try {
        block(cfValue)
    } finally {
        CFBridgingRelease(cfValue)
    }
}

private inline fun <T> cfRetain(value1: Any?, value2: Any?, block: MemScope.(CFTypeRef?, CFTypeRef?) -> T): T =
    memScoped {
        val cfValue1 = CFBridgingRetain(value1)
        val cfValue2 = CFBridgingRetain(value2)
        return try {
            block(cfValue1, cfValue2)
        } finally {
            CFBridgingRelease(cfValue1)
            CFBridgingRelease(cfValue2)
        }
    }
