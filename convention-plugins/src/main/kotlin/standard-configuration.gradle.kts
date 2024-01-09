/*
 * Copyright 2024 Russell Wolf
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

import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

// h4x so we can access version catalog from convention script
// https://github.com/gradle/gradle/issues/15383#issuecomment-779893192
val libs = the<LibrariesForLibs>()

extensions.create<StandardConfigExtension>("standardConfig")

kotlin {
    explicitApi()

    targets.configureEach {
        compilations.configureEach {
            // TODO reenable this once duplicate library warnings are cleared
//            kotlinOptions.allWarningsAsErrors = true
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        common {
            group("multithreaded") {
                withAndroidTarget()
                withJvm()
                withNative()
            }

            group("jvmCommon") {
                withAndroidTarget()
                withJvm()
            }

            group("browserCommon") {
                withJs()
                withWasm() // TODO we actually want withWasmJs() here but it doesn't matter because no Wasi yet
            }

            group("apple") {
                group("apple64") {
                    withIos()
                    withMacos()
                    withTvos()

                    withWatchosX64()
                    withWatchosDeviceArm64()
                    withWatchosSimulatorArm64()
                }

                group("apple32") {
                    withWatchosArm32()
                    withWatchosArm64() // NB this is weird target w/ 32-bit numbers
                }
            }
        }
    }
}

android {
    compileSdk = libs.versions.android.compileSdk.get().toInt()
    defaultConfig {
        minSdk = libs.versions.android.minSdk.get().toInt()
    }

    lint {
        warningsAsErrors = true
        abortOnError = true
    }
}

tasks.withType<AbstractTestTask> {
    testLogging {
        showStandardStreams = true
        events("passed", "failed")
    }
}

//region Compatibility Config
// This configuration is here to avoid breaking consumers

tasks.withType<KotlinCompile> {
    // Keeping 1.8 compatibility until it becomes burdensome
    kotlinOptions.jvmTarget = "1.8"
}


android {
    // Oops, this was on in 1.0, so now it's technically a breaking change to turn it off
    buildFeatures.buildConfig = true
}
//endregion
