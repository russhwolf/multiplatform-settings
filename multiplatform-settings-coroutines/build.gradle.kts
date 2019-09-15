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

import com.russhwolf.settings.build.standardConfiguration

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.dokka")
    `maven-publish`
    signing
}

standardConfiguration(
    "android",
//    "androidNativeArm32",
//    "androidNativeArm64",
//    "androidNativeX64",
//    "androidNativeX86",
    "iosArm32",
    "iosArm64",
    "iosX64",
    "js",
    "jvm",
//    "linuxArm32Hfp",
//    "linuxArm64",
//    "linuxMips32",
//    "linuxMipsel32",
    "linuxX64",
    "macosX64",
    "mingwX64",
//    "mingwX86",
    "tvosArm64",
    "tvosX64",
//    "wasm32",
    "watchosArm32",
    "watchosArm64",
    "watchosX86"
)
kotlin {
    val coroutineVersion = "1.3.9"

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":multiplatform-settings"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutineVersion")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(project(":multiplatform-settings-test"))

                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                implementation("app.cash.turbine:turbine:0.2.1")
            }
        }

        val androidMain by getting {
            dependencies {
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13")
                implementation("androidx.test:core:1.2.0")
                implementation("androidx.test.ext:junit:1.1.1")
                implementation("org.robolectric:robolectric:4.3.1")
            }
        }

        val jvmMain by getting {
            dependencies {
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13")
            }
        }

        val jsMain by getting {
            dependencies {
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}

android {
    testOptions.unitTests.isIncludeAndroidResources = true
}

