package com.russhwolf.settings

import android.content.SharedPreferences
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.doAnswer
import com.nhaarman.mockito_kotlin.doReturn
import com.nhaarman.mockito_kotlin.mock
import org.mockito.invocation.InvocationOnMock

actual fun configureTestSettings(): Settings {
    val storage = mutableMapOf<String, Any>()
    lateinit var editor: SharedPreferences.Editor
    lateinit var preferences: SharedPreferences

    fun mockPut(invocation: InvocationOnMock): SharedPreferences.Editor {
        val (key, value) = invocation.arguments
        storage[key as String] = value
        return editor
    }

    fun <T : Any> mockGet(invocation: InvocationOnMock): T {
        val (key, default) = invocation.arguments
        @Suppress("UNCHECKED_CAST")
        return (storage[key as String] ?: default) as T
    }

    fun mockRemove(invocation: InvocationOnMock): SharedPreferences.Editor {
        storage.remove(invocation.getArgument(0))
        return editor
    }

    editor = mock {
        on { putInt(any(), any()) } doAnswer { mockPut(it) }
        on { putLong(any(), any()) } doAnswer { mockPut(it) }
        on { putString(any(), any()) } doAnswer { mockPut(it) }
        on { putFloat(any(), any()) } doAnswer { mockPut(it) }
        on { putBoolean(any(), any()) } doAnswer { mockPut(it) }
        on { clear() } doAnswer { editor.also { storage.clear() } }
        on { remove(any()) } doAnswer { mockRemove(it) }
    }
    preferences = mock {
        on { getInt(any(), any()) } doAnswer { mockGet(it) }
        on { getLong(any(), any()) } doAnswer { mockGet(it) }
        on { getString(any(), any()) } doAnswer { mockGet(it) }
        on { getFloat(any(), any()) } doAnswer { mockGet(it) }
        on { getBoolean(any(), any()) } doAnswer { mockGet(it) }
        on { contains(any()) } doAnswer { storage.containsKey(it.getArgument(0)) }
        on { edit() } doReturn editor
    }

    return Settings(preferences)
}

