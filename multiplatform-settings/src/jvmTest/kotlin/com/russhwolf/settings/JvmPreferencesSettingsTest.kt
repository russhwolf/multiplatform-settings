package com.russhwolf.settings

import java.util.concurrent.CountDownLatch
import java.util.prefs.PreferenceChangeEvent
import java.util.prefs.PreferenceChangeListener
import java.util.prefs.Preferences

private val preferences = Preferences.userRoot()

@UseExperimental(ExperimentalJvm::class)
class JvmPreferencesSettingsTest : BaseSettingsTest(
    platformFactory = JvmPreferencesSettings.Factory(preferences),
    syncListeners = preferences::syncListeners
)

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
