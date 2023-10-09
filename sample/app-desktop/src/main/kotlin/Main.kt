/*
 * Copyright 2023 Russell Wolf
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

package com.russhwolf.settings.example.jvm

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.Checkbox
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.material.TextField
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.lightColors
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.example.SettingConfig
import com.russhwolf.settings.example.SettingsRepository
import com.russhwolf.settings.example.StringSettingConfig
import java.util.prefs.Preferences

fun main() = application {
    val settingsRepository: SettingsRepository by lazy {
        val preferences = Preferences.userRoot()
        val settings = PreferencesSettings(preferences)
        SettingsRepository(settings)
    }

    Window(
        onCloseRequest = ::exitApplication,
        title = "Settings Demo"
    ) {
        MaterialTheme(
            colors = if (isSystemInDarkTheme()) darkColors() else lightColors()
        ) {
            App(
                settings = settingsRepository.mySettings,
                onClearSettings = { settingsRepository.clear() }
            )
        }
    }
}

@Composable
private fun App(
    settings: List<SettingConfig<*>>,
    onClearSettings: () -> Unit,
) {
    var settingConfig by remember { mutableStateOf(settings.first()) }
    var enableLoggingChecked by remember { mutableStateOf(settingConfig.isLoggingEnabled) }
    var valueText by remember { mutableStateOf("") }
    var outputText by remember { mutableStateOf("") }

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Spinner(
                text = settingConfig.key,
                items = settings,
                itemText = { it.key },
                onClick = { setting ->
                    settingConfig = setting
                    enableLoggingChecked = setting.isLoggingEnabled
                },
            )
            TextField(
                value = valueText,
                onValueChange = { valueText = it },
                modifier = Modifier.padding(vertical = 8.dp),
                placeholder = { Text(text = "Value") },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            )
            Button(
                onClick = { outputText = (if (settingConfig.set(valueText)) "" else "INVALID VALUE!") },
            ) {
                Text(text = "Set Value")
            }
            Button(onClick = { outputText = settingConfig.get() }) {
                Text(text = "Get Value")
            }
            Button(
                onClick = {
                    settingConfig.remove()
                    outputText = "Setting removed!"
                },
            ) {
                Text(text = "Remove Value")
            }
            Button(
                onClick = {
                    onClearSettings()
                    outputText = "Settings cleared!"
                },
            ) {
                Text(text = "Clear All Values")
            }
            LabeledCheckbox(
                checked = enableLoggingChecked,
                onClick = { value ->
                    settingConfig.isLoggingEnabled = value
                    enableLoggingChecked = value
                },
            )
            Text(text = outputText, modifier = Modifier.padding(top = 8.dp))
        }
    }
}

@Composable
private fun <T> Spinner(
    text: String,
    items: List<T>,
    itemText: (T) -> String,
    onClick: (T) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        var expanded by remember { mutableStateOf(false) }
        TextButton(onClick = { expanded = true }) {
            Text(text = text)
            Icon(imageVector = Icons.Rounded.ArrowDropDown, contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        onClick(item)
                        expanded = false
                    },
                ) { Text(text = itemText(item)) }
            }
        }
    }
}

@Composable
private fun LabeledCheckbox(
    checked: Boolean,
    onClick: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .toggleable(
                value = checked,
                indication = rememberRipple(),
                interactionSource = remember { MutableInteractionSource() },
                role = Role.Checkbox,
                onValueChange = onClick,
            )
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = checked, onCheckedChange = null)
        Text(text = "Enable Logging")
    }
}

@Preview
@Composable
private fun Preview() {
    val settings = PreferencesSettings(Preferences.userRoot())
    App(
        settings = listOf(StringSettingConfig(settings, "MY_STRING", "default")),
        onClearSettings = {},
    )
}
