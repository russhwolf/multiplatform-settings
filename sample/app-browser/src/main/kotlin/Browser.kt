/*
 * Copyright 2019 Russell Wolf
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

package com.russhwolf.settings.example

import com.russhwolf.settings.JsSettings
import kotlinx.html.INPUT
import kotlinx.html.InputType
import kotlinx.html.button
import kotlinx.html.div
import kotlinx.html.dom.append
import kotlinx.html.id
import kotlinx.html.js.input
import kotlinx.html.js.onClickFunction
import kotlinx.html.option
import kotlinx.html.output
import kotlinx.html.select
import kotlin.browser.document

fun main() {
    document.body?.append {
        div {
            select {
                id = "select"
                settingsRepository.mySettings.forEach { setting ->
                    option {
                        text(setting.key)
                        id = setting.key
                    }
                }
            }
        }
        div {
            input(type = InputType.text) {
                id = "input"
            }
        }
        div {
            button {
                text("Set Value")
                onClickFunction = {
                    if (selectedItem.set(currentInput)) {
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
            output {
                id = "output"
            }
        }
    }
}

private val currentInput: String get() = document.getElementById("input").unsafeCast<INPUT>().value
private val selectedIndex: Int get() = document.getElementById("select").asDynamic().selectedIndex as? Int ?: 0
private val selectedItem: SettingConfig<*> get() = settingsRepository.mySettings[selectedIndex]
fun showOutput(value: String) {
    document.getElementById("output").asDynamic().value = value
}

private val settingsRepository: SettingsRepository by lazy { SettingsRepository(JsSettings()) }
