@file:UseExperimental(ExperimentalJvm::class)

package com.russhwolf.settings

import java.util.concurrent.CountDownLatch
import java.util.prefs.PreferenceChangeEvent
import java.util.prefs.PreferenceChangeListener
import java.util.prefs.Preferences
import kotlin.test.Test
import kotlin.test.assertEquals

private val preferences = Preferences.userRoot()
private val factory = JvmPreferencesSettings.Factory(preferences)

class JvmPreferencesSettingsTest : BaseSettingsTest(
    platformFactory = factory,
    syncListeners = preferences::syncListeners
) {
    @Test
    fun constructor_preferences() {
        val preferences = Preferences.userRoot().node("Settings")
        val settings = JvmPreferencesSettings(preferences)

        preferences.putInt("a", 3)
        assertEquals(3, settings["a", 0])
    }


    @Test
    fun factory_name() {
        val preferences = Preferences.userRoot().node("Settings")
        val settings = factory.create("Settings")

        preferences.putInt("a", 3)
        assertEquals(3, settings["a", 0])
    }

    @Test
    fun factory_noName() {
        val settings = factory.create()

        preferences.putInt("a", 3)
        assertEquals(3, settings["a", 0])
    }
}

/**
 * This is a mildly flaky workaround for the fact that Preferences listeners are forcibly executed on a background
 * thread. We call this function before verifying listener calls in our test in order to give that background thread a
 * chance to run first. This should clear out the listener queue because the internal listener here is being added last,
 * but it occasionally seems to fail.
 */
private fun Preferences.syncListeners() {
    val latch = CountDownLatch(1)
    val preferenceChangeListener = object : PreferenceChangeListener {
        override fun preferenceChange(it: PreferenceChangeEvent) {
            removePreferenceChangeListener(this)
            latch.countDown()
        }
    }
    addPreferenceChangeListener(preferenceChangeListener)
    putInt("sync", 1 + getInt("sync", 0))
    latch.await()
}
