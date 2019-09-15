package com.russhwolf.settings.coroutines

import kotlinx.coroutines.runBlocking

actual fun suspendTest(block: suspend () -> Unit) = runBlocking { block() }
