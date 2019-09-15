package com.russhwolf.settings.coroutines

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise


actual fun suspendTest(block: suspend () -> Unit): dynamic = GlobalScope.promise { block() }
