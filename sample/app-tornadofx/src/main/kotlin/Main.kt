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

import javafx.collections.FXCollections
import javafx.scene.Parent
import tornadofx.App
import tornadofx.View
import tornadofx.action
import tornadofx.button
import tornadofx.combobox
import tornadofx.label
import tornadofx.launch
import tornadofx.select
import tornadofx.selectedItem
import tornadofx.textfield
import tornadofx.vbox

fun main() = launch<SettingsDemoApp>()

class SettingsDemoApp : App(SettingsDemoView::class)

class SettingsDemoView : View() {
    private val settingsRepository = settingsRepository()

    override val root: Parent = vbox {
        val combobox = combobox(
            values = FXCollections.observableArrayList(settingsRepository.mySettings)
        )
        val input = textfield()
        val setButton = button("Set Value")
        val getButton = button("Get Value")
        val removeButton = button("Remove Value")
        val clearButton = button("Clear All Values")
        val output = label()

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
    }
}
