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

import androidx.preference.PreferenceManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.runner.RunWith
import kotlin.test.BeforeTest

@RunWith(AndroidJUnit4::class)
public class AndroidNoArgTest : NoArgTest() {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val sharedPreferences by lazy { PreferenceManager.getDefaultSharedPreferences(context) }

    @BeforeTest
    public fun setupContext() {
        val initializer = SettingsInitializer()
        initializer.create(context)
    }

    public override fun getString(key: String, defaultValue: String): String =
        sharedPreferences.getString(key, defaultValue) ?: defaultValue

    public override fun setString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).commit()
    }
}
