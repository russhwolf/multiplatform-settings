/*
 * Copyright 2020 Russell Wolf
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
pluginManagement {
    includeBuild("convention-plugins")
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "MultiplatformSettings"

include(
    ":multiplatform-settings",
    ":multiplatform-settings:keychain-tests",
    ":multiplatform-settings:node-tests",
    ":multiplatform-settings-test",
    ":multiplatform-settings-no-arg",
    ":multiplatform-settings-coroutines",
    ":multiplatform-settings-datastore",
    ":multiplatform-settings-serialization",
    ":multiplatform-settings-make-observable",
    ":tests"
)
