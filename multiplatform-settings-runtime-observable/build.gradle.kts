/*
 * Copyright 2019 Russell Wolf
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

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.dokka")
    `maven-publish`
    signing
}

standardConfiguration(
    "android",
    "iosArm32",
    "iosArm64",
    "iosSimulatorArm64",
    "iosX64",
    "js",
    "jvm",
    "linuxArm32Hfp",
    "linuxArm64",
    "linuxX64",
    "macosArm64",
    "macosX64",
    "mingwX64",
    "mingwX86",
    "tvosArm64",
    "tvosSimulatorArm64",
    "tvosX64",
    "watchosArm32",
    "watchosArm64",
    "watchosSimulatorArm64",
    "watchosX64",
    "watchosX86"
)

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":multiplatform-settings"))
            }
        }
        commonTest {
            dependencies {
                implementation(project(":multiplatform-settings-test"))
                implementation(project(":tests"))

                implementation(kotlin("test"))
            }
        }
    }
}

android {
    namespace = "com.russhwolf.settings.runtime_observable"
}

