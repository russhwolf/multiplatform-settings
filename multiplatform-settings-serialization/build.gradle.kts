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
    id("org.jetbrains.kotlin.plugin.serialization") version "1.4.0"
    id("org.jetbrains.dokka")
    id("maven-publish")
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
    "linuxArm32Hfp",
    "linuxArm64",
//    "linuxMips32",
//    "linuxMipsel32",
    "linuxX64",
    "macosX64",
    "mingwX64",
    "mingwX86",
    "tvosArm64",
    "tvosX64",
//    "wasm32",
    "watchosArm32",
    "watchosArm64",
    "watchosX86"
)

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation(project(":multiplatform-settings"))

                implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.0.0-RC")
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                implementation(project(":multiplatform-settings-test"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.13")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
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
                implementation(kotlin("stdlib-js"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
            }
        }
    }
}
