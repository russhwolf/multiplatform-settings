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

import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.Kotlin2JsCompile

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.dokka")
    id("maven-publish")
    id("com.jfrog.bintray") version "1.8.4-jetbrains-3"
}
apply(from = "../gradle/publish.gradle")

kotlin {
    android {
        publishAllLibraryVariants()
    }
    jvm()
    iosArm64()
    iosArm32()
    iosX64()
    macosX64()
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

    // Create empty targets for all other presets. These will build interfaces but no platform-specific implementation
    presets.forEach {
        if (it.name == "jvmWithJava") return@forEach // Probably don't need this, and it chokes on Android plugin
        if (targets.findByName(it.name) == null) {
            targetFromPreset(it)
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

        val iosArm64Main by getting
        val iosArm64Test by getting
        val iosArm32Main by getting {
            dependsOn(iosArm64Main)
        }
        val iosArm32Test by getting {
            dependsOn(iosArm64Test)
        }
        val iosX64Main by getting {
            dependsOn(iosArm64Main)
        }
        val iosX64Test by getting {
            dependsOn(iosArm64Test)
        }
        val macosX64Main by getting {
            dependsOn(iosArm64Main)
        }
        val macosX64Test by getting {
            dependsOn(iosArm64Test)
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

val dokka by tasks.getting(DokkaTask::class) {
    multiplatform {
        val common by creating
        val android by creating
        val jvm by creating
        val iosArm64 by creating
        val iosArm32 by creating
        val iosX64 by creating
        val macosX64 by creating
        val js by creating
    }
}

android {
    compileSdkVersion(29)

    defaultConfig {
        minSdkVersion(15)
    }
}

task("iosTest") {
    dependsOn("linkDebugTestIosX64")
    doLast {
        val testBinaryPath =
            (kotlin.targets["iosX64"] as KotlinNativeTarget).binaries.getTest("DEBUG").outputFile.absolutePath
        exec {
            commandLine("xcrun", "simctl", "spawn", "--standalone", "iPhone 11", testBinaryPath)
        }
    }
}
if (System.getProperty("os.name").contains("mac", ignoreCase = true)) {
    tasks["allTests"].dependsOn("iosTest")
}
