import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

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
}

kotlin {
    android()
    jvm()
    iosArm64("ios")
    iosArm32("ios32")
    iosX64("iosSim")
    macosX64("macos")
    js {
        browser()
        compilations.all {
            tasks.withType<Kotlin2JsCompile> {
                kotlinOptions {
                    metaInfo = true
                    sourceMap = true
                    moduleKind = "umd"
                }
            }
        }
    }
    sourceSets {
        all {
            languageSettings.apply {
                useExperimentalAnnotation("kotlin.Experimental")
            }
        }
        commonMain {
            dependencies {
                implementation(kotlin("stdlib-common"))

                implementation(project(":multiplatform-settings"))

                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))

                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.12")
            }
        }

        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))

                implementation(kotlin("test"))
                implementation(kotlin("test-junit"))
                implementation("junit:junit:4.12")
            }
        }

        val iosMain by getting
        val ios32Main by getting {
            dependsOn(iosMain)
        }
        val iosSimMain by getting {
            dependsOn(iosMain)
        }
        val macosMain by getting {
            dependsOn(iosMain)
        }

        val jsMain by getting {
            dependencies {
                implementation(kotlin("stdlib-js"))

                implementation(kotlin("test-js"))
            }
        }
    }
}

android {
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(15)
    }
}
