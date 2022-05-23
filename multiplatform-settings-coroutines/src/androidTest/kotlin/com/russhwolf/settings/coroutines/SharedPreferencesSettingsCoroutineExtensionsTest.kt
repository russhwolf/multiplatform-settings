package com.russhwolf.settings.coroutines

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SharedPreferencesSettingsCoroutineExtensionsTest : BaseCoroutineExtensionsTest() {
    override val settings: ObservableSettings = SharedPreferencesSettings(
        ApplicationProvider.getApplicationContext<Context>()
            .getSharedPreferences("test", Context.MODE_PRIVATE)
    )
}
