import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl

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
}

kotlin {
    applyDefaultHierarchyTemplate()
    jvmToolchain(17)

    androidTarget()
    jvm()
    js {
        browser()
    }
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }

    listOf(
        iosArm64(),
        iosSimulatorArm64(),
        iosX64()
    ).onEach {
        it.binaries {
            framework("Shared") {
                // Make AppleSettings visible from Swift
                export("com.russhwolf:multiplatform-settings:${rootProject.ext["library_version"]}")
                transitiveExport = true
            }
        }
    }

    sourceSets {
        commonMain {
            dependencies {
                api("com.russhwolf:multiplatform-settings:${rootProject.ext["library_version"]}")
            }
        }
        commonTest {
            dependencies {
                implementation("com.russhwolf:multiplatform-settings-test:${rootProject.ext["library_version"]}")

                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
            }
        }
        val androidUnitTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        val jvmMain by getting {
            dependencies {
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        val jsMain by getting {
            dependencies {
            }
        }
        val jsTest by getting {
            dependencies {
            }
        }

        val iosMain by getting {
            dependencies {
            }
        }
        val iosTest by getting {
            dependencies {
            }
        }
    }
}

android {
    namespace = "com.russhwolf.settings.example"

    compileSdk = 35

    defaultConfig {
        minSdk = 21
    }
}
