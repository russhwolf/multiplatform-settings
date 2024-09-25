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

package com.russhwolf.settings.example.js

import com.russhwolf.settings.StorageSettings
import com.russhwolf.settings.example.SettingConfig
import com.russhwolf.settings.example.SettingsRepository
import kotlinx.browser.document
import kotlinx.html.InputType
import kotlinx.html.dom.append
import kotlinx.html.js.button
import kotlinx.html.js.div
import kotlinx.html.js.input
import kotlinx.html.js.onClickFunction
import kotlinx.html.js.option
import kotlinx.html.js.output
import kotlinx.html.js.select
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.HTMLOutputElement
import org.w3c.dom.HTMLSelectElement

fun main() {
    document.body?.append {
        div {
            select = select {
                settingsRepository.mySettings.forEach { setting ->
                    option {
                        text(setting.key)
                    }
                }
            }
        }
        div {
            input = input(type = InputType.text)
        }
        div {
            button {
                text("Set Value")
                onClickFunction = {
                    if (selectedItem.set(input.value)) {
                        showOutput("")
                    } else {
                        showOutput("INVALID VALUE")
                    }
                }
            }
        }
        div {
            button {
                text("Get Value")
                onClickFunction = {
                    showOutput(selectedItem.get())
                }
            }
        }
        div {
            button {
                text("Remove Value")
                onClickFunction = {
                    selectedItem.remove()
                    showOutput("Setting removed!")
                }
            }
        }
        div {
            button {
                text("Clear All Values")
                onClickFunction = {
                    settingsRepository.clear()
                    showOutput("Settings cleared!")
                }
            }
        }
        div {
            output = output()
        }
    }
}

private lateinit var input: HTMLInputElement
private lateinit var select: HTMLSelectElement
private lateinit var output: HTMLOutputElement

private val selectedItem: SettingConfig<*> get() = settingsRepository.mySettings[select.selectedIndex]
fun showOutput(value: String) {
    output.value = value
}

private val settingsRepository: SettingsRepository by lazy { SettingsRepository(StorageSettings()) }
