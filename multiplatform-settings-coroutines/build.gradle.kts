import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

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
    "linuxX64",
    "macosArm64",
    "macosX64",
    "mingwX64",
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
        val commonMain by getting {
            dependencies {
                implementation(project(":multiplatform-settings"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(project(":tests"))
                implementation(project(":multiplatform-settings-test"))

                implementation(kotlin("test"))

                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutines}")
                implementation("app.cash.turbine:turbine:${Versions.turbine}")
            }
        }

        val androidMain by getting {
            dependencies {
            }
        }
        val androidTest by getting {
            dependencies {
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

// Link to internal sources which are shared between coroutines and coroutines-native-mt modules
kotlin.sourceSets.all {
    kotlin.srcDirs.forEach {
        kotlin.srcDir(
            it.path.replace(
                "multiplatform-settings-coroutines",
                "multiplatform-settings-coroutines-internal"
            )
        )
    }
}

android {
    testOptions.unitTests.isIncludeAndroidResources = true
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
    kotlinOptions.freeCompilerArgs += "-Xjvm-default=all"
}
