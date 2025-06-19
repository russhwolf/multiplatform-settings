/*
 * Copyright 2019 Russell Wolf
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

import platform.Foundation.NSUbiquitousKeyValueStore

internal actual fun NSUbiquitousKeyValueStore.intForKey(defaultName: String): Int = longLongForKey(defaultName).toInt()
internal actual fun NSUbiquitousKeyValueStore.setInt(value: Int, forKey: String) = setLongLong(value.toLong(), forKey)
internal actual fun NSUbiquitousKeyValueStore.longForKey(defaultName: String): Long = longLongForKey(defaultName)
internal actual fun NSUbiquitousKeyValueStore.setLong(value: Long, forKey: String) = setLongLong(value, forKey)
internal actual fun NSUbiquitousKeyValueStore.setFloat(value: Float, forKey: String) = setDouble(value.toDouble(), forKey)
internal actual fun NSUbiquitousKeyValueStore.floatForKey(defaultName: String): Float = doubleForKey(defaultName).toFloat()