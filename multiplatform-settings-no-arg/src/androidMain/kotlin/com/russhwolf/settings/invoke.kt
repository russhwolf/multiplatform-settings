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

import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.net.Uri

private var appContext: Context? = null

/**
 * Returns a default [Settings] instance.
 *
 * On Android, this delegates to the equivalent of
 * [PreferenceManager.getDefaultSharedPreferences][android.preference.PreferenceManager.getDefaultSharedPreferences].
 * It handles context via [ContextProvider], which is a no-op [ContentProvider].
 */
actual operator fun Settings.Companion.invoke(): Settings {
    val appContext = appContext!!

    // Match the behavior of PreferenceManager.getDefaultSharedPreferences()
    val preferencesName = "${appContext.packageName}_preferences"
    val delegate = appContext.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
    return AndroidSettings(delegate)
}

/** Use to provide a context reference to [invoke] */
@Suppress("DEPRECATION", "KDocMissingDocumentation")
@Hidden
class ContextProvider : ContentProvider() {
    override fun insert(uri: Uri, values: ContentValues?): Uri? = null

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? = null

    override fun onCreate(): Boolean {
        appContext = context!!.applicationContext
        return true
    }

    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int = 0

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
    override fun getType(uri: Uri): String? = null
}

@Deprecated(message = "Hidden annotation signifies non-public API", level = DeprecationLevel.WARNING)
@RequiresOptIn(message = "Hidden annotation signifies non-public API", level = RequiresOptIn.Level.WARNING)
private annotation class Hidden
