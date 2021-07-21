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

import kotlinx.cinterop.COpaquePointer
import kotlinx.cinterop.COpaquePointerVar
import kotlinx.cinterop.CValuesRef
import platform.CoreFoundation.CFAllocatorRef
import platform.CoreFoundation.CFArrayRef
import platform.CoreFoundation.CFDictionaryKeyCallBacks
import platform.CoreFoundation.CFDictionaryRef
import platform.CoreFoundation.CFDictionaryValueCallBacks
import platform.CoreFoundation.CFIndex
import platform.Foundation.NSData
import platform.Foundation.NSString
import platform.Foundation.NSStringEncoding
import platform.Foundation.create as platformCreate
import platform.Foundation.dataUsingEncoding as platformDataUsingEncoding

internal actual val NSUTF8StringEncoding: NSStringEncoding = platform.Foundation.NSUTF8StringEncoding
internal actual fun NSString.Companion.create(data: NSData, encoding: NSStringEncoding): NSString? =
    platformCreate(data, encoding)

internal actual fun NSString.dataUsingEncoding(encoding: NSStringEncoding): NSData? =
    platformDataUsingEncoding(encoding)

internal actual fun CFArrayGetCount(theArray: CFArrayRef?): CFIndex = platform.CoreFoundation.CFArrayGetCount(theArray)
internal actual fun CFDictionaryCreate(
    allocator: CFAllocatorRef?,
    keys: CValuesRef<COpaquePointerVar>,
    values: CValuesRef<COpaquePointerVar>,
    numValues: CFIndex,
    keyCallBacks: CValuesRef<CFDictionaryKeyCallBacks>?,
    valueCallBacks: CValuesRef<CFDictionaryValueCallBacks>?
): CFDictionaryRef? =
    platform.CoreFoundation.CFDictionaryCreate(allocator, keys, values, numValues, keyCallBacks, valueCallBacks)

internal actual fun CFArrayGetValueAtIndex(theArray: CFArrayRef?, index: CFIndex): COpaquePointer? =
    platform.CoreFoundation.CFArrayGetValueAtIndex(theArray, index)

internal actual fun CFIndex.asInt(): Int = toInt()
internal actual fun Int.toCFIndex(): CFIndex = toLong()
