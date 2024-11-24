/*
 * Copyright 2024 Russell Wolf
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:OptIn(ExperimentalForeignApi::class, ExperimentalNativeApi::class)
@file:Suppress("unused")

package com.russhwolf.settings.ndk.androidNative

import com.russhwolf.settings.JniSharedPreferencesSettings
import com.russhwolf.settings.example.SettingConfig
import com.russhwolf.settings.example.SettingsRepository
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cstr
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import platform.android.JNIEnvVar
import platform.android.JNI_FALSE
import platform.android.JNI_TRUE
import platform.android.jboolean
import platform.android.jobject
import platform.android.jstring
import kotlin.experimental.ExperimentalNativeApi

lateinit var settingsRepository: SettingsRepository
lateinit var selectedSettingConfig: SettingConfig<*>


@CName("Java_com_russhwolf_settings_example_ndk_MainActivityKt_initializeRepository")
fun initializeRepository(env: CPointer<JNIEnvVar>, self: jobject, sharedPreferences: jobject) {
    val settings = JniSharedPreferencesSettings(env, sharedPreferences)
    settingsRepository = SettingsRepository(settings)
    selectedSettingConfig = settingsRepository.mySettings.first()
}

@CName("Java_com_russhwolf_settings_example_ndk_MainActivityKt_clearRepository")
fun clearRepository(env: CPointer<JNIEnvVar>, self: jobject) {
    settingsRepository.clear()
}

@CName("Java_com_russhwolf_settings_example_ndk_MainActivityKt_listRepositoryKeys")
fun listRepositoryKeys(env: CPointer<JNIEnvVar>, self: jobject): jobject = memScoped {
    val keys = settingsRepository.mySettings
    val array = env.pointed.pointed!!.NewObjectArray!!.invoke(
        env,
        keys.size,
        env.pointed.pointed?.FindClass?.invoke(env, "java/lang/String".cstr.ptr)!!,
        null
    )!!
    for (i in keys.indices) {
        env.pointed.pointed!!.SetObjectArrayElement!!.invoke(env, array, i, (keys[i].key.toJString(env)))
    }

    return array
}

@CName("Java_com_russhwolf_settings_example_ndk_MainActivityKt_isLoggingEnabled")
fun isLoggingEnabled(env: CPointer<JNIEnvVar>, self: jobject): jboolean {
    return (if (selectedSettingConfig.isLoggingEnabled) JNI_TRUE else JNI_FALSE).toUByte()
}

@CName("Java_com_russhwolf_settings_example_ndk_MainActivityKt_setLoggingEnabled")
fun setLoggingEnabled(env: CPointer<JNIEnvVar>, self: jobject, enabled: jboolean) {
    selectedSettingConfig.isLoggingEnabled = (enabled != JNI_FALSE.toUByte())
}

@CName("Java_com_russhwolf_settings_example_ndk_MainActivityKt_getSelectedSetting")
fun getSelectedSetting(env: CPointer<JNIEnvVar>, self: jobject): jstring {
    return selectedSettingConfig.key.toJString(env)
}

@CName("Java_com_russhwolf_settings_example_ndk_MainActivityKt_setSelectedSetting")
fun setSelectedSetting(env: CPointer<JNIEnvVar>, self: jobject, key: jstring) {
    val keyString = key.toKString(env)
    for (settingConfig in settingsRepository.mySettings) {
        if (settingConfig.key == keyString) {
            selectedSettingConfig = settingConfig
            return
        }
    }
}

@CName("Java_com_russhwolf_settings_example_ndk_MainActivityKt_getSelectedValue")
fun getSelectedValue(env: CPointer<JNIEnvVar>, self: jobject): jstring {
    val output = selectedSettingConfig.get()
    return output.toJString(env)
}

@CName("Java_com_russhwolf_settings_example_ndk_MainActivityKt_setSelectedValue")
fun setSelectedValue(env: CPointer<JNIEnvVar>, self: jobject, key: jstring): jboolean {
    return (if (selectedSettingConfig.set(key.toKString(env))) JNI_TRUE else JNI_FALSE).toUByte()
}

@CName("Java_com_russhwolf_settings_example_ndk_MainActivityKt_removeSelectedValue")
fun removeSelectedValue(env: CPointer<JNIEnvVar>, self: jobject) {
    selectedSettingConfig.remove()
}

internal fun String.toJString(env: CPointer<JNIEnvVar>): jstring = memScoped {
    env.pointed.pointed?.NewStringUTF?.invoke(env, cstr.ptr)
        ?: error("failed to create jstring for String '${this@toJString}'")
}

internal fun jstring.toKString(env: CPointer<JNIEnvVar>): String {
    val chars = env.pointed.pointed?.GetStringUTFChars?.invoke(env, this, null)
        ?: error("Failed to get chars from jstring")
    val out = chars.toKString()
    env.pointed.pointed?.ReleaseStringUTFChars?.invoke(env, this, chars)
    return out
}
