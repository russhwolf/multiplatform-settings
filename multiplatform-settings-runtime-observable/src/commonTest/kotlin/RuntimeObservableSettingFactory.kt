import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.Settings
import com.russhwolf.settings.serialization.toRuntimeObservable

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

internal class RuntimeObservableSettingFactory : Settings.Factory {

    override fun create(name: String?): Settings {
        // delegating to MapSettings rather than using it directly because MapSettings is already
        // observable, and delegating to it make the delegate non observable
        val delegate = object : Settings by MapSettings() {}
        return delegate.toRuntimeObservable()
    }
}