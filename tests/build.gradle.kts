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
import org.jetbrains.kotlin.konan.target.Family

plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    android()
    jvm()
    iosArm64()
    iosArm32()
    iosX64()
    watchosArm32()
    watchosArm64()
    watchosX86()
    tvosArm64()
    tvosX64()
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

        val appleMain by creating

        targets
            .withType(KotlinNativeTarget::class)
            .matching { it.konanTarget.family in listOf(Family.IOS, Family.OSX, Family.WATCHOS, Family.TVOS) }
            .configureEach {
                compilations["main"].defaultSourceSet.dependsOn(appleMain)
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
