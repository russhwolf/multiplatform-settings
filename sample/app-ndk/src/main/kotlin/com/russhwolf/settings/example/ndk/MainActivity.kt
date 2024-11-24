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

package com.russhwolf.settings.example.ndk

import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
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
import androidx.preference.PreferenceManager

class MainActivity : ComponentActivity() {
    companion object {
        init {
            System.loadLibrary("kotlinNdk")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initializeRepository(PreferenceManager.getDefaultSharedPreferences(applicationContext))

        setContent {
            MaterialTheme(
                colorScheme = if (isSystemInDarkTheme()) darkColorScheme() else lightColorScheme()
            ) {
                App(
                    settings = listRepositoryKeys().toList(),
                    onClearSettings = { clearRepository() },
                )
            }
        }
    }
}

@Composable
private fun App(
    settings: List<String>,
    onClearSettings: () -> Unit,
) {
    var selectedSetting by remember { mutableStateOf(getSelectedSetting()) }
    var enableLoggingChecked by remember { mutableStateOf(isLoggingEnabled()) }
    var valueText by remember { mutableStateOf("") }
    var outputText by remember { mutableStateOf("") }

    Surface {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
        ) {
            Spinner(
                text = selectedSetting,
                items = settings,
                itemText = { it },
                onClick = { setting ->
                    setSelectedSetting(setting)
                    selectedSetting = setting
                    enableLoggingChecked = isLoggingEnabled()
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
                onClick = { outputText = (if (setSelectedValue(valueText)) "" else "INVALID VALUE!") },
            ) {
                Text(text = "Set Value")
            }
            Button(onClick = { outputText = getSelectedValue() }) {
                Text(text = "Get Value")
            }
            Button(
                onClick = {
                    removeSelectedValue()
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
                    setLoggingEnabled(value)
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
                    text = { Text(text = itemText(item)) },
                    onClick = {
                        onClick(item)
                        expanded = false
                    },
                )
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

external fun initializeRepository(sharedPreferences: SharedPreferences)
external fun clearRepository()
external fun listRepositoryKeys(): Array<String>
external fun isLoggingEnabled(): Boolean
external fun setLoggingEnabled(enabled: Boolean)
external fun getSelectedSetting(): String
external fun setSelectedSetting(key: String)
external fun getSelectedValue(): String
external fun setSelectedValue(value: String): Boolean
external fun removeSelectedValue()
