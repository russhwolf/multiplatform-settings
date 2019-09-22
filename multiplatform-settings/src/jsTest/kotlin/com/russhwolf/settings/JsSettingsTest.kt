package com.russhwolf.settings

import kotlin.browser.localStorage

class JsSettingsTest : BaseSettingsTest(
    platformFactory = object : Settings.Factory {
        @UseExperimental(ExperimentalJs::class)
        override fun create(name: String?): JsSettings {
            return JsSettings(localStorage)
        }
    },
    hasNamedInstances = false,
    hasListeners = false
)
