package com.russhwolf.settings

import org.w3c.dom.set
import kotlin.browser.localStorage
import kotlin.test.Test
import kotlin.test.assertEquals

@UseExperimental(ExperimentalJs::class)
class JsSettingsTest : BaseSettingsTest(
    platformFactory = object : Settings.Factory {
        override fun create(name: String?): JsSettings {
            return JsSettings(localStorage)
        }
    },
    hasNamedInstances = false,
    hasListeners = false
) {
    @Test
    fun constructor_localStorage() {
        val settings = JsSettings()
        localStorage["a"] = "value"
        assertEquals("value", settings["a", ""])
    }
}
