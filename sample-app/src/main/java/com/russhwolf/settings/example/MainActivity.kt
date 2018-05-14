package com.russhwolf.settings.example

import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import com.russhwolf.settings.Settings

class MainActivity : AppCompatActivity() {

    val settingsRepository by lazy {
        SettingsRepository(Settings(PreferenceManager.getDefaultSharedPreferences(this)))
    }
    val typesSpinner by lazy { findViewById<Spinner>(R.id.types_spinner) }
    val valueInput by lazy { findViewById<EditText>(R.id.value_input) }
    val setButton by lazy { findViewById<Button>(R.id.set_button) }
    val getButton by lazy { findViewById<Button>(R.id.get_button) }
    val clearButton by lazy { findViewById<Button>(R.id.clear_button) }
    val output by lazy { findViewById<TextView>(R.id.output) }

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

        clearButton.setOnClickListener {
            settingsRepository.clear()
            output.text = "Settings cleared!"
        }

    }
}
