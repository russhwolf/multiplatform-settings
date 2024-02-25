import com.russhwolf.settings.BaseSettingsTest
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.toRuntimeObservable
import kotlin.test.Test
import kotlin.test.assertIs
import kotlin.test.assertNotSame
import kotlin.test.assertSame

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

class SettingsExtensionTest() : BaseSettingsTest(
    platformFactory = RuntimeObservableSettingFactory(),
    allowsDuplicateInstances = false,
    hasNamedInstances = false
) {
    @Test
    fun checkObservableFlagTest() {
        val delegate = MapSettings()

        assertSame(delegate, delegate.toRuntimeObservable(true))
        assertNotSame(delegate, delegate.toRuntimeObservable(false))
    }
}