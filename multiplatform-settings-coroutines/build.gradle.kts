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
    "iosX64",
    "js",
    "jvm",
    "linuxX64",
    "macosX64",
    "mingwX64",
    "tvosArm64",
    "tvosX64",
    "watchosArm32",
    "watchosArm64",
    "watchosX86"
)
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(project(":multiplatform-settings"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(project(":multiplatform-settings-test"))

                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))

                implementation("app.cash.turbine:turbine:${Versions.turbine}")
            }
        }

        val androidMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("junit:junit:${Versions.junit}")
                implementation("androidx.test:core:${Versions.androidxTest}")
                implementation("androidx.test.ext:junit:${Versions.androidxTestExt}")
                implementation("org.robolectric:robolectric:${Versions.robolectric}")
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
                implementation("junit:junit:${Versions.junit}")
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

