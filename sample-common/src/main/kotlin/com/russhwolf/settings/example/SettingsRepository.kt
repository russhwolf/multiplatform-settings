package com.russhwolf.settings.example

import com.russhwolf.settings.Settings
import com.russhwolf.settings.boolean
import com.russhwolf.settings.double
import com.russhwolf.settings.float
import com.russhwolf.settings.int
import com.russhwolf.settings.long
import com.russhwolf.settings.string

class SettingsRepository(private var settings: Settings) {
    var myStringSetting: String by settings.string("MY_String")
    var myIntSetting: Int by settings.int("MY_INT")
    var myLongSetting: Long by settings.long("MY_LONG")
    var myFloatSetting: Float by settings.float("MY_FLOAT")
    var myDoubleSetting: Double by settings.double("MY_DOUBLE")
    var myBooleanSetting: Boolean by settings.boolean("MY_BOOLEAN")

    fun clear() = settings.clear()
}
