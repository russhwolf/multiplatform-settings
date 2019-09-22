package com.russhwolf.settings

import java.util.Properties

class JvmPropertiesSettingsTest : BaseSettingsTest(
    platformFactory = object : Settings.Factory {
        val properties = Properties()

        @UseExperimental(ExperimentalJvm::class)
        override fun create(name: String?): JvmPropertiesSettings {
            return JvmPropertiesSettings(properties)
        }
    },
    hasNamedInstances = false,
    hasListeners = false
)
