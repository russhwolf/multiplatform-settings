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

import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.NativeBuildType
import org.jetbrains.kotlin.konan.target.Family.MINGW

plugins {
    id("standard-configuration")
    id("module-publication")
}

kotlin {
    androidTarget {
        publishAllLibraryVariants()
    }
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    js {
        browser()
    }
    jvm()
    macosArm64()
    macosX64()
    mingwX64()
    tvosArm64()
    tvosSimulatorArm64()
    tvosX64()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    watchosArm32()
    watchosArm64()
    watchosDeviceArm64()
    watchosSimulatorArm64()
    watchosX64()

    targets.withType<KotlinNativeTarget>().matching { it.konanTarget.family == MINGW }.configureEach {
        // WindowsNoArgTest will check that we use this as our parent registry key
        binaries.getTest(NativeBuildType.DEBUG).baseName = "com.russhwolf.settings.noarg.test"
    }

    sourceSets {
        commonMain {
            dependencies {
                api(project(":multiplatform-settings"))
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)

                implementation(project(":tests"))
            }
        }

        androidMain {
            dependencies {
                implementation(libs.androidx.startup.runtime)
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(libs.androidx.test.core)
                implementation(libs.androidx.test.junit)
                implementation(libs.robolectric)

                implementation(libs.androidx.preference)
            }
        }

        wasmJsMain {
            dependencies {
                implementation(libs.kotlinx.browser)
            }
        }
    }
}

android {
    namespace = "com.russhwolf.settings.no_arg"
    testOptions.unitTests.isIncludeAndroidResources = true

    // Oops, this was on in 1.0, so now it's technically a breaking change to turn it off
    buildFeatures.buildConfig = true
}
