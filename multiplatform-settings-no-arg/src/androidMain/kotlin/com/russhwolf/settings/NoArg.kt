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

import android.content.Context
import androidx.startup.Initializer

private var appContext: Context? = null

/**
 * Returns a default [Settings] instance.
 *
 * On Android, this delegates to the equivalent of
 * [PreferenceManager.getDefaultSharedPreferences][android.preference.PreferenceManager.getDefaultSharedPreferences].
 * It handles context via androidx.startup
 */
public actual fun Settings(): Settings {
    val appContext = appContext!!

    // Match the behavior of PreferenceManager.getDefaultSharedPreferences(), without AndroidX lib or deprecated API
    val preferencesName = "${appContext.packageName}_preferences"
    val delegate = appContext.getSharedPreferences(preferencesName, Context.MODE_PRIVATE)
    return SharedPreferencesSettings(delegate)
}

/**
 * Initializer component to pass a context to the no-arg `Settings()` call on Android. Pass a `Context` to  `create()`
 * if you're doing custom initialization logic or need to call `Settings()` from tests.
 */
public class SettingsInitializer : Initializer<Context> {
    /**
     * Stores the `applicationContext` from the provided `context` so that it can be used by the no-arg `Settings()`
     * function.
     */
    override fun create(context: Context): Context = context.applicationContext.also { appContext = it }

    /**
     * Returns an empty list. This initializer depends only on the provided context.
     */
    override fun dependencies(): List<Class<out Initializer<*>>> = emptyList()
}
