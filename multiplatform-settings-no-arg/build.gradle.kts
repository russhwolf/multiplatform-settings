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

plugins {
    id("com.android.library")
    kotlin("multiplatform")
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
    "macosArm64",
    "macosX64",
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
                api(project(":multiplatform-settings"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))

                implementation(project(":tests"))
            }
        }


        val androidMain by getting {
            dependencies {
                implementation("androidx.startup:startup-runtime:${Versions.androidxStartup}")
            }
        }
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:${Versions.junit}")
                implementation("androidx.test:core:${Versions.androidxTest}")
                implementation("androidx.test.ext:junit:${Versions.androidxTestExt}")
                implementation("org.robolectric:robolectric:${Versions.robolectric}")

                implementation("androidx.preference:preference:${Versions.androidxPreference}")
            }
        }

        val jvmMain by getting {
            dependencies {
            }
        }
        val jvmTest by getting {
            languageSettings.optIn("com.russhwolf.settings.ExperimentalSettingsImplementation")
            dependencies {
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
