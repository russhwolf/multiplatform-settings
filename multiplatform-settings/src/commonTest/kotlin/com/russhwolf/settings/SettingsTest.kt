package com.russhwolf.settings

@RunWith(AndroidJUnit4::class)
class SettingsTest : BaseSettingsTest(
    platformFactory = platformFactory,
    hasNamedInstances = hasNamedInstances,
    hasListeners = hasListeners
)
