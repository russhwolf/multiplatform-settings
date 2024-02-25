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
 *  Creates a [RuntimeObservableSettingsWrapper] wrapper around the [Settings] instance.
 *
 *  @param checkObservable flag that indicates whether to skip the observable check for current
 *  instance or not. If it is false and the [Settings] instance is already a [ObservableSettings]
 *  it doesn't create a [RuntimeObservableSettingsWrapper] for it.
 *
 */
public fun Settings.toRuntimeObservable(checkObservable: Boolean = true): ObservableSettings =
    if (checkObservable && this is ObservableSettings) this
    else RuntimeObservableSettingsWrapper(this)