package com.russhwolf.settings

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidSettingsTest : BaseSettingsTest(AndroidSettings.Factory(ApplicationProvider.getApplicationContext()))
