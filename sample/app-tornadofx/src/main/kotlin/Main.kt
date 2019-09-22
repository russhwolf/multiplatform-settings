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
import com.russhwolf.settings.ExperimentalListener
import com.russhwolf.settings.JvmPreferencesSettings
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import javafx.scene.Parent
import tornadofx.App
import tornadofx.View
import tornadofx.action
import tornadofx.button
import tornadofx.checkbox
import tornadofx.combobox
import tornadofx.label
import tornadofx.launch
import tornadofx.onChange
import tornadofx.selectedItem
import tornadofx.textfield
import tornadofx.vbox
import java.util.prefs.Preferences

fun main() = launch<SettingsDemoApp>()

class SettingsDemoApp : App(SettingsDemoView::class)

@UseExperimental(ExperimentalListener::class)
class SettingsDemoView : View() {
    override val root: Parent = vbox {
        val selectedItem = SimpleObjectProperty<SettingConfig<*>>()
        val combobox = combobox(
            values = FXCollections.observableArrayList(settingsRepository.mySettings),
            property = selectedItem
        )
        val input = textfield()
        val setButton = button("Set Value")
        val getButton = button("Get Value")
        val removeButton = button("Remove Value")
        val clearButton = button("Clear All Values")
        val output = label()
        val checkbox = checkbox("Enable Listener")

        selectedItem.onChange {
            checkbox.isSelected = it?.isLoggingEnabled == true
        }
        setButton.action {
            if (combobox.selectedItem?.set(input.text) == true) {
                output.text = ""
            } else {
                output.text = "INVALID VALUE!"
            }
        }
        getButton.action {
            output.text = combobox.selectedItem?.get()
        }
        removeButton.action {
            combobox.selectedItem?.remove()
            output.text = "Setting removed!"
        }
        clearButton.action {
            settingsRepository.clear()
            output.text = "Settings cleared!"
        }
        checkbox.action {
            combobox.selectedItem?.isLoggingEnabled = checkbox.isSelected
        }

        selectedItem.value = settingsRepository.mySettings.first()
    }
}

@UseExperimental(ExperimentalJvm::class)
val settingsRepository: SettingsRepository by lazy {
    val preferences = Preferences.userRoot()
    val settings = JvmPreferencesSettings(preferences)
    SettingsRepository(settings)
}
