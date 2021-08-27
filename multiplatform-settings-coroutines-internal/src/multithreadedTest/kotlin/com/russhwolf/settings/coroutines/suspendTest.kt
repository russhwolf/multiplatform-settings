package com.russhwolf.settings.coroutines

public actual fun suspendTest(block: suspend () -> Unit): Unit = runBlocking { block() }
