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

package com.russhwolf.settings.coroutines

import com.russhwolf.settings.BaseSettingsTest
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

// TODO we should have test-cases specific to SuspendSettings and FlowSettings, but until we do we test things by
//  passing them through toBlockingSettings()

private val mockSettingsFactory = MapSettings.Factory()

@OptIn(ExperimentalSettingsApi::class)
class ToSuspendSettingsTest : BaseSettingsTest(
    platformFactory = object : Settings.Factory {
        override fun create(name: String?): Settings {
            return mockSettingsFactory.create(name)
                .toSuspendSettings(Dispatchers.Unconfined)
                .toBlockingSettings()
        }
    },
    hasListeners = false
)

@OptIn(ExperimentalSettingsApi::class)
class ToFlowSettingsTest : BaseSettingsTest(
    platformFactory = object : Settings.Factory {
        override fun create(name: String?): Settings {
            return (mockSettingsFactory.create(name) as ObservableSettings)
                .toFlowSettings(Dispatchers.Unconfined)
                .toBlockingObservableSettings(CoroutineScope(Dispatchers.Unconfined))
        }
    },
    hasListeners = true
)
