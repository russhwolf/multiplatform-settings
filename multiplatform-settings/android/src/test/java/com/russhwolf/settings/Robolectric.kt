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

actual typealias RunWith = org.junit.runner.RunWith
actual typealias Runner = org.junit.runner.Runner
actual typealias RobolectricTestRunner = org.robolectric.RobolectricTestRunner
actual typealias SandboxTestRunner = org.robolectric.internal.SandboxTestRunner
actual typealias BlockJUnit4ClassRunner = org.junit.runners.BlockJUnit4ClassRunner
actual typealias ParentRunner<T> = org.junit.runners.ParentRunner<T>
actual typealias FrameworkMethod = org.junit.runners.model.FrameworkMethod
