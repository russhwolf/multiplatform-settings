@file:OptIn(ExperimentalForeignApi::class)

package com.russhwolf.settings

import kotlinx.cinterop.CArrayPointer
import kotlinx.cinterop.CPointer
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.MemScope
import kotlinx.cinterop.NativePlacement
import kotlinx.cinterop.alloc
import kotlinx.cinterop.allocArray
import kotlinx.cinterop.cstr
import kotlinx.cinterop.invoke
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.pointed
import kotlinx.cinterop.toKString
import platform.android.JNIEnvVar
import platform.android.JNI_FALSE
import platform.android.JNI_TRUE
import platform.android.jboolean
import platform.android.jclass
import platform.android.jfloat
import platform.android.jint
import platform.android.jlong
import platform.android.jmethodID
import platform.android.jobject
import platform.android.jstring
import platform.android.jvalue
import kotlin.experimental.ExperimentalNativeApi
import kotlin.native.ref.createCleaner

public class JniSharedPreferencesSettings(
    private val env: CPointer<JNIEnvVar>,
    sharedPreferences: jobject
) : Settings {

    private val jniNativeInterface get() = env.pointed.pointed ?: error("Failed to get jniNativeInterface")

    private val sharedPreferences = jniNativeInterface.NewGlobalRef?.invoke(env, sharedPreferences)
        ?: error("Failed to create global ref for sharedPreferences")

    @Suppress("unused")
    @OptIn(ExperimentalNativeApi::class)
    private val cleaner = createCleaner(this.sharedPreferences to env) { (sharedPreferences, env) ->
        // Can't use jniNativeInterface here because it would capture `this`
        env.pointed.pointed?.DeleteGlobalRef?.invoke(env, sharedPreferences)
    }

    override val keys: Set<String>
        get() {
            val jall = sharedPreferences.callJavaObjectMethod(
                "android/content/SharedPreferences",
                "getAll",
                "()Ljava/util/Map;"
            )
            val jkeys = jall.callJavaObjectMethod(
                "java/util/Map",
                "keySet",
                "()Ljava/util/Set;"
            )
            val jiterator = jkeys.callJavaObjectMethod(
                "java/util/Set",
                "iterator",
                "()Ljava/util/Iterator;"
            )
            return buildSet {
                while (jiterator.callJavaBooleanMethod(
                        "java/util/Iterator",
                        "hasNext",
                        "()Z"
                    ) != JNI_FALSE.toUByte()
                ) {
                    val jkey = jiterator.callJavaObjectMethod(
                        "java/util/Iterator",
                        "next",
                        "()Ljava/lang/Object;"
                    )

                    add(jkey.toKString(env))
                }
            }
        }

    override val size: Int
        get() {
            val jall = sharedPreferences.callJavaObjectMethod(
                "android/content/SharedPreferences",
                "getAll",
                "()Ljava/util/Map;"
            )
            val jsize = jall.callJavaIntMethod(
                "java/util/Map",
                "size",
                "()I"
            )
            return jsize
        }

    override fun clear() {
        callEditorMethod(
            "clear",
            "()Landroid/content/SharedPreferences\$Editor;"
        )
    }

    override fun remove(key: String): Unit = memScoped {
        callEditorMethod(
            "remove",
            "(Ljava/lang/String;)Landroid/content/SharedPreferences\$Editor;",
            jvalueOf(key.toJString(env))
        )
    }

    override fun hasKey(key: String): Boolean = memScoped {
        return sharedPreferences.callJavaBooleanMethod(
            "android/content/SharedPreferences",
            "contains",
            "(Ljava/lang/String;)Z",
            jvalueOf(key.toJString(env))
        ) != JNI_FALSE.toUByte()
    }

    override fun putInt(key: String, value: Int): Unit = memScoped {
        callEditorMethod(
            "putInt",
            "(Ljava/lang/String;I)Landroid/content/SharedPreferences\$Editor;",
            jvalueOf(key.toJString(env)),
            jvalueOf(value)
        )
    }

    override fun getInt(key: String, defaultValue: Int): Int = memScoped {
        return sharedPreferences.callJavaIntMethod(
            "android/content/SharedPreferences",
            "getInt",
            "(Ljava/lang/String;I)I",
            jvalueOf(key.toJString(env)),
            jvalueOf(defaultValue)
        )
    }

    override fun getIntOrNull(key: String): Int? = if (hasKey(key)) getInt(key, 0) else null

    override fun putLong(key: String, value: Long): Unit = memScoped {
        callEditorMethod(
            "putLong",
            "(Ljava/lang/String;J)Landroid/content/SharedPreferences\$Editor;",
            jvalueOf(key.toJString(env)),
            jvalueOf(value)
        )
    }

    override fun getLong(key: String, defaultValue: Long): Long = memScoped {
        return sharedPreferences.callJavaLongMethod(
            "android/content/SharedPreferences",
            "getLong",
            "(Ljava/lang/String;J)J",
            jvalueOf(key.toJString(env)),
            jvalueOf(defaultValue)
        )
    }

    override fun getLongOrNull(key: String): Long? = if (hasKey(key)) getLong(key, 0L) else null

    override fun putString(key: String, value: String): Unit = memScoped {
        callEditorMethod(
            "putString",
            "(Ljava/lang/String;Ljava/lang/String;)Landroid/content/SharedPreferences\$Editor;",
            jvalueOf(key.toJString(env)),
            jvalueOf(value.toJString(env))
        )
    }

    override fun getString(key: String, defaultValue: String): String = memScoped {
        val jstring = sharedPreferences.callJavaObjectMethod(
            "android/content/SharedPreferences",
            "getString",
            "(Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;",
            jvalueOf(key.toJString(env)),
            jvalueOf(defaultValue.toJString(env))
        )

        return jstring.toKString(env)
    }


    override fun getStringOrNull(key: String): String? = if (hasKey(key)) getString(key, "") else null

    override fun putFloat(key: String, value: Float): Unit = memScoped {
        callEditorMethod(
            "putFloat",
            "(Ljava/lang/String;F)Landroid/content/SharedPreferences\$Editor;",
            jvalueOf(key.toJString(env)),
            jvalueOf(value)
        )
    }

    override fun getFloat(key: String, defaultValue: Float): Float = memScoped {
        return sharedPreferences.callJavaFloatMethod(
            "android/content/SharedPreferences",
            "getFloat",
            "(Ljava/lang/String;F)F",
            jvalueOf(key.toJString(env)),
            jvalueOf(defaultValue)
        )
    }

    override fun getFloatOrNull(key: String): Float? = if (hasKey(key)) getFloat(key, 0f) else null

    override fun putDouble(key: String, value: Double): Unit = memScoped {
        val longValue = callStaticJavaLongMethod(
            "java/lang/Double",
            "doubleToRawLongBits",
            "(D)J",
            jvalueOf(value)
        )

        callEditorMethod(
            "putLong",
            "(Ljava/lang/String;J)Landroid/content/SharedPreferences\$Editor;",
            jvalueOf(key.toJString(env)),
            jvalueOf(longValue)
        )
    }

    override fun getDouble(key: String, defaultValue: Double): Double = memScoped {
        val longDefault = callStaticJavaLongMethod(
            "java/lang/Double",
            "doubleToRawLongBits",
            "(D)J",
            jvalueOf(defaultValue)
        )

        val longValue = sharedPreferences.callJavaLongMethod(
            "android/content/SharedPreferences",
            "getLong",
            "(Ljava/lang/String;J)J",
            jvalueOf(key.toJString(env)),
            jvalueOf(longDefault)
        )

        return callStaticJavaDoubleMethod(
            "java/lang/Double",
            "longBitsToDouble",
            "(J)D",
            jvalueOf(longValue)
        )
    }

    override fun getDoubleOrNull(key: String): Double? = if (hasKey(key)) getDouble(key, 0.0) else null

    override fun putBoolean(key: String, value: Boolean): Unit = memScoped {
        callEditorMethod(
            "putBoolean",
            "(Ljava/lang/String;Z)Landroid/content/SharedPreferences\$Editor;",
            jvalueOf(key.toJString(env)),
            jvalueOf(value)
        )
    }

    override fun getBoolean(key: String, defaultValue: Boolean): Boolean = memScoped {
        return sharedPreferences.callJavaBooleanMethod(
            "android/content/SharedPreferences",
            "getBoolean",
            "(Ljava/lang/String;Z)Z",
            jvalueOf(key.toJString(env)),
            jvalueOf(defaultValue)
        ) != JNI_FALSE.toUByte()
    }

    override fun getBooleanOrNull(key: String): Boolean? = if (hasKey(key)) getBoolean(key, false) else null

    private fun jclass(className: String): jclass = memScoped {
        jniNativeInterface.FindClass?.invoke(env, className.cstr.ptr)
            ?: error("Failed to get class '$className'")
    }

    private fun callEditorMethod(
        methodName: String,
        signature: String,
        vararg args: jvalue
    ) {
        val editor = sharedPreferences.callJavaObjectMethod(
            "android/content/SharedPreferences",
            "edit",
            "()Landroid/content/SharedPreferences\$Editor;"
        )

        val edited = editor.callJavaObjectMethod(
            "android/content/SharedPreferences\$Editor",
            methodName,
            signature,
            *args
        )

        // TODO commit vs apply toggle?
        edited.callJavaVoidMethod(
            "android/content/SharedPreferences\$Editor",
            "apply",
            "()V"
        )
    }

    private fun jobject.callJavaObjectMethod(
        className: String,
        methodName: String,
        signature: String,
        vararg args: jvalue
    ): jobject = memScoped {
        val jniNativeInterfaceCall = jniNativeInterface.CallObjectMethodA
            ?: error("Failed to get CallObjectMethodA function instance")

        return jniNativeInterfaceCall(
            env,
            this@callJavaObjectMethod,
            getMethodID(jclass(className), methodName, signature),
            cArrayOf(*args)
        ) ?: error("Failed to call '$methodName' on '$className'")
    }

    private fun jobject.callJavaIntMethod(
        className: String,
        methodName: String,
        signature: String,
        vararg args: jvalue
    ): jint = memScoped {
        val jniNativeInterfaceCall = jniNativeInterface.CallIntMethodA
            ?: error("Failed to get CallIntMethodA function instance")

        return jniNativeInterfaceCall(
            env,
            this@callJavaIntMethod,
            getMethodID(jclass(className), methodName, signature),
            cArrayOf(*args)
        )
    }

    private fun jobject.callJavaLongMethod(
        className: String,
        methodName: String,
        signature: String,
        vararg args: jvalue
    ): jlong = memScoped {
        val jniNativeInterfaceCall = jniNativeInterface.CallLongMethodA
            ?: error("Failed to get CallLongMethodA function instance")

        return jniNativeInterfaceCall(
            env,
            this@callJavaLongMethod,
            getMethodID(jclass(className), methodName, signature),
            cArrayOf(*args)
        )
    }

    private fun jobject.callJavaFloatMethod(
        className: String,
        methodName: String,
        signature: String,
        vararg args: jvalue
    ): jfloat = memScoped {
        val jniNativeInterfaceCall = jniNativeInterface.CallFloatMethodA
            ?: error("Failed to get CallFloatMethodA function instance")

        return jniNativeInterfaceCall(
            env,
            this@callJavaFloatMethod,
            getMethodID(jclass(className), methodName, signature),
            cArrayOf(*args)
        )
    }

    private fun jobject.callJavaBooleanMethod(
        className: String,
        methodName: String,
        signature: String,
        vararg args: jvalue
    ): jboolean = memScoped {
        val jniNativeInterfaceCall = jniNativeInterface.CallBooleanMethodA
            ?: error("Failed to get CallBooleanMethodA function instance")

        return jniNativeInterfaceCall(
            env,
            this@callJavaBooleanMethod,
            getMethodID(jclass(className), methodName, signature),
            cArrayOf(*args)
        )
    }

    private fun jobject.callJavaVoidMethod(
        className: String,
        methodName: String,
        signature: String,
        vararg args: jvalue
    ): Unit = memScoped {
        val jniNativeInterfaceCall = jniNativeInterface.CallVoidMethodA
            ?: error("Failed to get CallVoidMethodA function instance")

        return jniNativeInterfaceCall(
            env,
            this@callJavaVoidMethod,
            getMethodID(jclass(className), methodName, signature),
            cArrayOf(*args)
        )
    }

    private fun callStaticJavaLongMethod(
        className: String,
        methodName: String,
        signature: String,
        vararg args: jvalue
    ): Long = memScoped {
        val jniNativeInterfaceCall = jniNativeInterface.CallStaticLongMethodA
            ?: error("Failed to get CallVoidMethodA function instance")

        val jclass = jclass(className)
        return jniNativeInterfaceCall(
            env,
            jclass,
            getStaticMethodID(jclass, methodName, signature),
            cArrayOf(*args)
        )
    }

    private fun callStaticJavaDoubleMethod(
        className: String,
        methodName: String,
        signature: String,
        vararg args: jvalue
    ): Double = memScoped {
        val jniNativeInterfaceCall = jniNativeInterface.CallStaticDoubleMethodA
            ?: error("Failed to get CallVoidMethodA function instance")

        val jclass = jclass(className)
        return jniNativeInterfaceCall(
            env,
            jclass,
            getStaticMethodID(jclass, methodName, signature),
            cArrayOf(*args)
        )
    }

    private fun MemScope.getMethodID(
        jclass: jclass,
        methodName: String,
        signature: String
    ): jmethodID {
        val jniNativeInterfaceCall = jniNativeInterface.GetMethodID
            ?: error("Failed to get CallVoidMethodA function instance")

        return jniNativeInterfaceCall(
            env,
            jclass,
            methodName.cstr.ptr,
            signature.cstr.ptr
        ) ?: error("Failed to get '$methodName' method")
    }

    private fun MemScope.getStaticMethodID(
        jclass: jclass,
        methodName: String,
        signature: String
    ): jmethodID {
        val jniNativeInterfaceCall = jniNativeInterface.GetStaticMethodID
            ?: error("Failed to get CallVoidMethodA function instance")

        return jniNativeInterfaceCall(
            env,
            jclass,
            methodName.cstr.ptr,
            signature.cstr.ptr
        ) ?: error("Failed to get '$methodName' method")
    }
}

internal fun NativePlacement.jvalueOf(value: jobject): jvalue = alloc<jvalue> {
    l = value
}

internal fun NativePlacement.jvalueOf(value: Int): jvalue = alloc<jvalue> {
    i = value
}

internal fun NativePlacement.jvalueOf(value: Long): jvalue = alloc<jvalue> {
    j = value
}

internal fun NativePlacement.jvalueOf(value: Float): jvalue = alloc<jvalue> {
    f = value
}

internal fun NativePlacement.jvalueOf(value: Double): jvalue = alloc<jvalue> {
    d = value
}

internal fun NativePlacement.jvalueOf(value: Boolean): jvalue = alloc<jvalue> {
    z = (if (value) JNI_TRUE else JNI_FALSE).toUByte()
}

internal fun NativePlacement.cArrayOf(vararg jvalues: jvalue): CArrayPointer<jvalue> =
    allocArray(jvalues.size) { index ->
        d = jvalues[index].d
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
