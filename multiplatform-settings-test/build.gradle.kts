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
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.4-jetbrains-3"
}
apply(from = "../gradle/publish.gradle")

kotlin {
    android {
        publishAllLibraryVariants()
    }
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
            }
        }
        commonTest {
            dependencies {
                implementation(project(":tests"))

                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
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
                implementation("junit:junit:4.12")
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
                implementation("junit:junit:4.12")
            }
        }

        val iosMain by getting
        val iosTest by getting
        val ios32Main by getting {
            dependsOn(iosMain)
        }
        val ios32Test by getting {
            dependsOn(iosTest)
        }
        val iosSimMain by getting {
            dependsOn(iosMain)
        }
        val iosSimTest by getting {
            dependsOn(iosTest)
        }
        val macosMain by getting {
            dependsOn(iosMain)
        }
        val macosTest by getting {
            dependsOn(iosTest)
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

android {
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(15)
    }
}

task("iosTest") {
    dependsOn("linkDebugTestIosSim")
    doLast {
        val testBinaryPath =
            (kotlin.targets["iosSim"] as KotlinNativeTarget).binaries.getTest("DEBUG").outputFile.absolutePath
        exec {
            commandLine("xcrun", "simctl", "spawn", "--standalone", "iPhone 11", testBinaryPath)
        }
    }
}
tasks["allTests"].dependsOn("iosTest")
