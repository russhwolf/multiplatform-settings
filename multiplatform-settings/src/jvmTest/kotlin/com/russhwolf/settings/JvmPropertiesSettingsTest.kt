package com.russhwolf.settings

import org.junit.Test
import java.util.Properties
import kotlin.test.assertEquals

@UseExperimental(ExperimentalJvm::class)
class JvmPropertiesSettingsTest : BaseSettingsTest(
    platformFactory = object : Settings.Factory {
        val properties = Properties()

        override fun create(name: String?): JvmPropertiesSettings {
            return JvmPropertiesSettings(properties)
        }
    },
    hasNamedInstances = false,
    hasListeners = false
) {
    @Test
    fun constructor_properties() {
        val properties = Properties()
        val settings = JvmPropertiesSettings(properties)
        properties["a"] = "value"
        assertEquals("value", settings["a", ""])
    }
}
