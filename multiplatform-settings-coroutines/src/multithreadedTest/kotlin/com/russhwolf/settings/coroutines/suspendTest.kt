package com.russhwolf.settings.coroutines

import kotlinx.coroutines.runBlocking

public actual fun suspendTest(block: suspend () -> Unit): Unit = runBlocking { block() }
