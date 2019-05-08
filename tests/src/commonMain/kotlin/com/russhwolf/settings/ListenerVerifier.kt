package com.russhwolf.settings

import kotlin.test.assertEquals

@Suppress("KDocMissingDocumentation")
class ListenerVerifier {
    val listener: () -> Unit = { invokeCount++ }

    private var invokeCount = 0

    fun assertInvoked(times: Int = 1, message: String? = null) {
        assertEquals(times, invokeCount, message)
        invokeCount = 0
    }

    fun assertNotInvoked(message: String? = null) {
        assertInvoked(0, message)
    }
}
