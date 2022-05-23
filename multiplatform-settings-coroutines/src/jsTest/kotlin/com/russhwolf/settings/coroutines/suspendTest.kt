package com.russhwolf.settings.coroutines

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise


@OptIn(DelicateCoroutinesApi::class)
actual fun suspendTest(block: suspend () -> Unit): dynamic = GlobalScope.promise { block() }
