package com.russhwolf.settings.coroutines

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.SharedPreferencesSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.runner.RunWith
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

@RunWith(AndroidJUnit4::class)
class SharedPreferencesSettingsCoroutineExtensionsTest : BaseCoroutineExtensionsTest() {

    @BeforeTest
    fun setup() {
        @OptIn(ExperimentalCoroutinesApi::class)
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @AfterTest
    fun tearDown() {
        @OptIn(ExperimentalCoroutinesApi::class)
        Dispatchers.resetMain()
    }

    override val settings: ObservableSettings = SharedPreferencesSettings(
        ApplicationProvider.getApplicationContext<Context>()
            .getSharedPreferences("test", Context.MODE_PRIVATE)
    )
}
