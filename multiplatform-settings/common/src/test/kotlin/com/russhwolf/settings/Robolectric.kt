/*
 * Copyright 2018 Russell Wolf
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

package com.russhwolf.settings

import kotlin.reflect.KClass

/*
These expect declarations allow our unit tests to run using Robolectric on the jvm. The native implementations do nothing.
 */

expect annotation class RunWith(val value: KClass<out Runner>)
expect abstract class Runner
expect class RobolectricTestRunner : SandboxTestRunner
expect open class SandboxTestRunner : BlockJUnit4ClassRunner
expect open class BlockJUnit4ClassRunner : ParentRunner<FrameworkMethod>
expect class FrameworkMethod
expect abstract class ParentRunner<T> : Runner
