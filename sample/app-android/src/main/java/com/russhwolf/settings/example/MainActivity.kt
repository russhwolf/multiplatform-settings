/*
 * Copyright 2018 Russell Wolf
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

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    private val settingsRepository by lazy {
        SettingsRepository(SettingsFactory(applicationContext))
    }
    private val typesSpinner by lazy { findViewById<Spinner>(R.id.types_spinner) }
    private val valueInput by lazy { findViewById<EditText>(R.id.value_input) }
    private val setButton by lazy { findViewById<Button>(R.id.set_button) }
    private val getButton by lazy { findViewById<Button>(R.id.get_button) }
    private val removeButton by lazy { findViewById<Button>(R.id.remove_button) }
    private val clearButton by lazy { findViewById<Button>(R.id.clear_button) }
    private val output by lazy { findViewById<TextView>(R.id.output) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        typesSpinner.adapter =
                ArrayAdapter<SettingConfig<*>>(
                    this,
                    android.R.layout.simple_list_item_1,
                    settingsRepository.mySettings
                )

        setButton.setOnClickListener {
            val settingConfig = typesSpinner.selectedItem as SettingConfig<*>
            val value = valueInput.text.toString()
            if (settingConfig.set(value)) {
                output.text = ""
            } else {
                output.text = "INVALID VALUE!"
            }
        }

        getButton.setOnClickListener {
            val settingConfig = typesSpinner.selectedItem as SettingConfig<*>
            output.text = settingConfig.get()
        }

        removeButton.setOnClickListener {
            val settingConfig = typesSpinner.selectedItem as SettingConfig<*>
            settingConfig.remove()
            output.text = "Setting removed!"
        }

        clearButton.setOnClickListener {
            settingsRepository.clear()
            output.text = "Settings cleared!"
        }

    }
}
