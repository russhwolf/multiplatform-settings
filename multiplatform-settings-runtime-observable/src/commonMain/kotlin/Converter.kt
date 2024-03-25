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

package com.russhwolf.settings.serialization

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.Settings

/**
 *  Creates a [RuntimeObservableSettingsWrapper] around the [Settings] instance.
 *
 *  If the [this] is already a [ObservableSettings] it doesn't create a
 *  [RuntimeObservableSettingsWrapper] for it and the same instance is returned.
 *
 */
public fun Settings.toRuntimeObservable(): ObservableSettings =
    if (this is ObservableSettings) this
    else RuntimeObservableSettingsWrapper(this)