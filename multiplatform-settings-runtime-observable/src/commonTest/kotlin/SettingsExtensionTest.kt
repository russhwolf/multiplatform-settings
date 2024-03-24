import com.russhwolf.settings.BaseSettingsTest
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.get
import com.russhwolf.settings.minusAssign
import com.russhwolf.settings.serialization.toRuntimeObservable
import com.russhwolf.settings.set
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertNull
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
    fun extensionObservableInstanceTest() {

        val observableSetting = MapSettings()
        val nonObservableSetting = object : Settings by observableSetting {}

        assertSame(observableSetting, observableSetting.toRuntimeObservable())
        assertNotSame(observableSetting, nonObservableSetting.toRuntimeObservable())
    }

    @Test
    fun delegateTest() {

        val delegate = object : Settings by MapSettings() {}

        delegate["key"] = "test_value"

        val runtimeObservable = delegate.toRuntimeObservable()

        assertEquals("test_value", runtimeObservable["key"])

        delegate -= "key"

        assertNull(runtimeObservable.getStringOrNull("key"))
    }
}