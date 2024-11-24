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

@file:OptIn(ExperimentalNativeApi::class, ExperimentalForeignApi::class)
@file:Suppress("KDocMissingDocumentation", "unused", "UNUSED_PARAMETER")

package com.russhwolf.settings

import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.cstr
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import platform.android.JNIEnvVar
import platform.android.JNI_FALSE
import platform.android.JNI_TRUE
import platform.android.jarray
import platform.android.jboolean
import platform.android.jdouble
import platform.android.jfloat
import platform.android.jint
import platform.android.jlong
import platform.android.jobject
import platform.android.jstring
import platform.android.jvalue
import kotlin.experimental.ExperimentalNativeApi
import kotlin.reflect.KMutableProperty1


@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativeGetKeys")
fun nativeGetKeys(env: CPointer<JNIEnvVar>, self: jobject, sharedPreferences: jobject): jarray = memScoped {
    val settings = JniSharedPreferencesSettings(env, sharedPreferences)
    val kkeys = settings.keys.toList()
    val jkeys = env.pointed.pointed!!.NewObjectArray!!.invoke(
        env,
        kkeys.size,
        env.pointed.pointed?.FindClass?.invoke(env, "java/lang/String".cstr.ptr)!!,
        null
    )!!
    for (i in kkeys.indices) {
        env.pointed.pointed!!.SetObjectArrayElement!!.invoke(env, jkeys, i, (kkeys[i].toJString(env)))
    }

    return jkeys
}

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativeGetSize")
fun nativeGetSize(env: CPointer<JNIEnvVar>, self: jobject, sharedPreferences: jobject): jint {
    val settings = JniSharedPreferencesSettings(env, sharedPreferences)
    return settings.size
}

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativeClear")
fun nativeClear(env: CPointer<JNIEnvVar>, self: jobject, sharedPreferences: jobject) {
    val settings = JniSharedPreferencesSettings(env, sharedPreferences)
    settings.clear()
}

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativeRemove")
fun nativeRemove(env: CPointer<JNIEnvVar>, self: jobject, sharedPreferences: jobject, key: jstring) {
    val settings = JniSharedPreferencesSettings(env, sharedPreferences)
    settings.remove(key.toKString(env))
}

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativeHasKey")
fun nativeHasKey(env: CPointer<JNIEnvVar>, self: jobject, sharedPreferences: jobject, key: jstring): jboolean {
    val settings = JniSharedPreferencesSettings(env, sharedPreferences)
    return if (settings.hasKey(key.toKString(env))) JNI_TRUE.toUByte() else JNI_FALSE.toUByte()
}

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativePutInt")
fun nativePutInt(env: CPointer<JNIEnvVar>, self: jobject, sharedPreferences: jobject, key: jstring, value: jint) {
    val settings = JniSharedPreferencesSettings(env, sharedPreferences)
    settings.putInt(key.toKString(env), value)
}

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativeGetInt")
fun nativeGetInt(
    env: CPointer<JNIEnvVar>,
    self: jobject,
    sharedPreferences: jobject,
    key: jstring,
    defaultValue: jint
): jint {
    val settings = JniSharedPreferencesSettings(env, sharedPreferences)
    return settings.getInt(key.toKString(env), defaultValue)
}

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativeGetIntOrNull")
fun nativeGetIntOrNull(env: CPointer<JNIEnvVar>, self: jobject, sharedPreferences: jobject, key: jstring): jobject? =
    getBoxedPrimitiveOrNull(
        env,
        self,
        sharedPreferences,
        key,
        "java/lang/Integer",
        "I",
        Settings::getIntOrNull,
        jvalue::i
    )

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativePutLong")
fun nativePutLong(env: CPointer<JNIEnvVar>, self: jobject, sharedPreferences: jobject, key: jstring, value: jlong) {
    val settings = JniSharedPreferencesSettings(env, sharedPreferences)
    settings.putLong(key.toKString(env), value)
}

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativeGetLong")
fun nativeGetLong(
    env: CPointer<JNIEnvVar>,
    self: jobject,
    sharedPreferences: jobject,
    key: jstring,
    defaultValue: jlong
): jlong {
    val settings = JniSharedPreferencesSettings(env, sharedPreferences)
    return settings.getLong(key.toKString(env), defaultValue)
}

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativeGetLongOrNull")
fun nativeGetLongOrNull(env: CPointer<JNIEnvVar>, self: jobject, sharedPreferences: jobject, key: jstring): jobject? =
    getBoxedPrimitiveOrNull(
        env,
        self,
        sharedPreferences,
        key,
        "java/lang/Long",
        "J",
        Settings::getLongOrNull,
        jvalue::j
    )

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativePutString")
fun nativePutString(env: CPointer<JNIEnvVar>, self: jobject, sharedPreferences: jobject, key: jstring, value: jstring) {
    val settings = JniSharedPreferencesSettings(env, sharedPreferences)
    settings.putString(key.toKString(env), value.toKString(env))
}

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativeGetString")
fun nativeGetString(
    env: CPointer<JNIEnvVar>,
    self: jobject,
    sharedPreferences: jobject,
    key: jstring,
    defaultValue: jstring
): jstring {
    val settings = JniSharedPreferencesSettings(env, sharedPreferences)
    return settings.getString(key.toKString(env), defaultValue.toKString(env)).toJString(env)
}

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativeGetStringOrNull")
fun nativeGetStringOrNull(env: CPointer<JNIEnvVar>, self: jobject, sharedPreferences: jobject, key: jstring): jstring? {
    val settings = JniSharedPreferencesSettings(env, sharedPreferences)
    return settings.getStringOrNull(key.toKString(env))?.toJString(env)
}

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativePutFloat")
fun nativePutFloat(env: CPointer<JNIEnvVar>, self: jobject, sharedPreferences: jobject, key: jstring, value: jfloat) {
    val settings = JniSharedPreferencesSettings(env, sharedPreferences)
    settings.putFloat(key.toKString(env), value)
}

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativeGetFloat")
fun nativeGetFloat(
    env: CPointer<JNIEnvVar>,
    self: jobject,
    sharedPreferences: jobject,
    key: jstring,
    defaultValue: jfloat
): jfloat {
    val settings = JniSharedPreferencesSettings(env, sharedPreferences)
    return settings.getFloat(key.toKString(env), defaultValue)
}

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativeGetFloatOrNull")
fun nativeGetFloatOrNull(env: CPointer<JNIEnvVar>, self: jobject, sharedPreferences: jobject, key: jstring): jobject? =
    getBoxedPrimitiveOrNull(
        env,
        self,
        sharedPreferences,
        key,
        "java/lang/Float",
        "F",
        Settings::getFloatOrNull,
        jvalue::f
    )

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativePutDouble")
fun nativePutDouble(env: CPointer<JNIEnvVar>, self: jobject, sharedPreferences: jobject, key: jstring, value: jdouble) {
    val settings = JniSharedPreferencesSettings(env, sharedPreferences)
    settings.putDouble(key.toKString(env), value)
}

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativeGetDouble")
fun nativeGetDouble(
    env: CPointer<JNIEnvVar>,
    self: jobject,
    sharedPreferences: jobject,
    key: jstring,
    defaultValue: jdouble
): jdouble {
    val settings = JniSharedPreferencesSettings(env, sharedPreferences)
    return settings.getDouble(key.toKString(env), defaultValue)
}

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativeGetDoubleOrNull")
fun nativeGetDoubleOrNull(env: CPointer<JNIEnvVar>, self: jobject, sharedPreferences: jobject, key: jstring): jobject? =
    getBoxedPrimitiveOrNull(
        env,
        self,
        sharedPreferences,
        key,
        "java/lang/Double",
        "D",
        Settings::getDoubleOrNull,
        jvalue::d
    )

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativePutBoolean")
fun nativePutBoolean(
    env: CPointer<JNIEnvVar>,
    self: jobject,
    sharedPreferences: jobject,
    key: jstring,
    value: jboolean
) {
    val settings = JniSharedPreferencesSettings(env, sharedPreferences)
    settings.putBoolean(key.toKString(env), value != JNI_FALSE.toUByte())
}

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativeGetBoolean")
fun nativeGetBoolean(
    env: CPointer<JNIEnvVar>,
    self: jobject,
    sharedPreferences: jobject,
    key: jstring,
    defaultValue: jboolean
): jboolean {
    val settings = JniSharedPreferencesSettings(env, sharedPreferences)
    return if (settings.getBoolean(
            key.toKString(env),
            defaultValue != JNI_FALSE.toUByte()
        )
    ) JNI_TRUE.toUByte() else JNI_FALSE.toUByte()
}

@CName("Java_com_russhwolf_settings_JniSharedPreferencesSettingsTestKt_nativeGetBooleanOrNull")
fun nativeGetBooleanOrNull(
    env: CPointer<JNIEnvVar>,
    self: jobject,
    sharedPreferences: jobject,
    key: jstring
): jobject? =
    getBoxedPrimitiveOrNull(
        env,
        self,
        sharedPreferences,
        key,
        "java/lang/Boolean",
        "Z",
        Settings::getBooleanOrNull,
        jvalue::z
    ) {
        when (it) {
            true -> JNI_TRUE.toUByte()
            false -> JNI_FALSE.toUByte()
        }
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

private fun <Type> getBoxedPrimitiveOrNull(
    env: CPointer<JNIEnvVar>,
    self: jobject,
    sharedPreferences: jobject,
    key: jstring,
    boxClassName: String,
    primitiveType: String,
    getter: Settings.(String) -> Type?,
    jvalueProp: KMutableProperty1<jvalue, Type>,
) = getBoxedPrimitiveOrNull<Type, Type>(
    env,
    self,
    sharedPreferences,
    key,
    boxClassName,
    primitiveType,
    getter,
    jvalueProp,
    { it }
)

private inline fun <KType, JType> getBoxedPrimitiveOrNull(
    env: CPointer<JNIEnvVar>,
    self: jobject,
    sharedPreferences: jobject,
    key: jstring,
    boxClassName: String,
    primitiveType: String,
    getter: Settings.(String) -> KType?,
    jvalueProp: KMutableProperty1<jvalue, JType>,
    transform: (KType) -> JType
): jobject? = memScoped {
    val settings = JniSharedPreferencesSettings(env, sharedPreferences)
    val out = settings.getter(key.toKString(env)) ?: return null

    val boxedClass = env.pointed.pointed?.FindClass?.invoke(env, boxClassName.cstr.ptr)
        ?: error("Failed to find $boxClassName class")
    val valueOfMethodID = env.pointed.pointed?.GetStaticMethodID?.invoke(
        env,
        boxedClass,
        "valueOf".cstr.ptr,
        "($primitiveType)L$boxClassName;".cstr.ptr
    )
        ?: error("Failed to find valueOf methodID")
    val args = allocArray<jvalue>(1) { _: Int ->
        jvalueProp.set(this, transform(out))
    }
    val jobject = env.pointed.pointed?.CallStaticObjectMethodA?.invoke(
        env,
        boxedClass,
        valueOfMethodID,
        args
    ) ?: error("Failed to call valueOf")
    return jobject
}
