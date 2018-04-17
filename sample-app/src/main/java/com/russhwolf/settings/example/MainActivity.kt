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
        SettingsRepository(
            Settings(
                PreferenceManager.getDefaultSharedPreferences(
                    this
                )
            )
        )
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
                ArrayAdapter<SettingConfig>(
                    this,
                    android.R.layout.simple_list_item_1,
                    SETTING_CONFIGS
                )

        setButton.setOnClickListener {
            val settingConfig = typesSpinner.selectedItem as SettingConfig
            val value = valueInput.text.toString()
            try {
                settingConfig.set(settingsRepository, value)
                output.text = ""
            } catch (exception: Exception) {
                exception.printStackTrace()
                output.text = "${exception::class.java.simpleName}\n${exception.message}"
            }
        }

        getButton.setOnClickListener {
            val settingConfig = typesSpinner.selectedItem as SettingConfig
            try {
                output.text = settingConfig.get(settingsRepository)
            } catch (exception: Exception) {
                exception.printStackTrace()
                output.text = "${exception::class.java.simpleName}\n${exception.message}"
            }
        }

        clearButton.setOnClickListener {
            settingsRepository.clear()
            output.text = "Settings cleared!"
        }

    }
}

val SETTING_CONFIGS = arrayOf(
    SettingConfig("String", { myStringSetting = it }, { myStringSetting }),
    SettingConfig("Int", { myIntSetting = it.toInt() }, { myIntSetting.toString() }),
    SettingConfig("Long", { myLongSetting = it.toLong() }, { myLongSetting.toString() }),
    SettingConfig("Float", { myFloatSetting = it.toFloat() }, { myFloatSetting.toString() }),
    SettingConfig("Double", { myDoubleSetting = it.toDouble() }, { myDoubleSetting.toString() }),
    SettingConfig("Boolean", { myBooleanSetting = it.toBoolean() }, { myBooleanSetting.toString() })
)

class SettingConfig(
    private val label: String,
    val set: SettingsRepository.(String) -> Unit,
    val get: SettingsRepository.() -> String
) {
    override fun toString() = label
}
