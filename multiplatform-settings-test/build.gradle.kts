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
    id("com.android.library")
    kotlin("multiplatform")
    id("org.jetbrains.dokka")
    `maven-publish`
    signing
}

standardConfiguration()

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":multiplatform-settings"))
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
