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

import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    `maven-publish`
    signing
}

standardConfiguration()

kotlin {
    targets.getByName<KotlinNativeTarget>("linuxX64") {
        compilations["main"].cinterops.create("qdbm-depot")
        compilations["main"].cinterops.create("qdbm-relic")
        compilations["main"].cinterops.create("qdbm-villa")
    }
    sourceSets {
        commonMain {
            dependencies {
            }
        }
        commonTest {
            dependencies {
                implementation(project(":tests"))

                implementation(kotlin("test"))
            }
        }

        val androidMain by getting {
            dependencies {
            }
        }
        val androidUnitTest by getting {
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

android {
    namespace = "com.russhwolf.settings"
    testOptions.unitTests.isIncludeAndroidResources = true
}
