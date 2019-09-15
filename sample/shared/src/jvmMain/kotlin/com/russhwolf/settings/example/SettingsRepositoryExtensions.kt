/*
 * Copyright 2019 Russell Wolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.russhwolf.settings.example

import com.russhwolf.settings.ExperimentalJvm
import com.russhwolf.settings.JvmSettings
import com.russhwolf.settings.Settings
import java.io.File
import java.util.Properties

fun settingsRepository() = SettingsRepository(JvmSettingsFactory)

@UseExperimental(ExperimentalJvm::class)
object JvmSettingsFactory : Settings.Factory {
    override fun create(name: String?): Settings {
        val properties = Properties()
        val file = File("$name.properties")
        if (file.exists()) {
            properties.load(file.reader())
        }
        return JvmSettings(properties)
    }
}
