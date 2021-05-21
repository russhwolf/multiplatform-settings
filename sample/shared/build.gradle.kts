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

import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("com.android.library")
    kotlin("multiplatform")
}

kotlin {
    android()
    jvm()
    js {
        browser()
    }

    val isDevice = System.getenv("SDK_NAME")?.startsWith("iphoneos") == true
    val iosTarget = if (isDevice) {
        presets.getByName("iosArm64")
    } else {
        presets.getByName("iosX64")
    }
    targetFromPreset(iosTarget, "ios") {
        this as KotlinNativeTarget
        binaries {
            framework("Shared") {
                // Make AppleSettings visible from Swift
                export("com.russhwolf:multiplatform-settings:${rootProject.ext["library_version"]}")
                transitiveExport = true
            }
        }
    }

    sourceSets {
        all {
            languageSettings.apply {
                useExperimentalAnnotation("kotlin.RequiresOptIn")
            }
        }

        commonMain {
            dependencies {
                api("com.russhwolf:multiplatform-settings:${rootProject.ext["library_version"]}")
            }
        }
        commonTest {
            dependencies {
                implementation("com.russhwolf:multiplatform-settings-test:${rootProject.ext["library_version"]}")

                implementation(kotlin("test"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        val androidMain by getting {
            dependencies {
            }
        }
        val androidTest by getting {
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
                implementation(kotlin("stdlib-js"))
            }
        }
        val jsTest by getting {
            dependencies {
                implementation(kotlin("test-js"))
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
    compileSdkVersion(30)

    defaultConfig {
        minSdkVersion(15)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<KotlinCompile>().all {
    kotlinOptions.jvmTarget = "1.8"
}


task("copyFramework") {
    val buildType = project.findProperty("kotlin.build.type") as? String ?: "DEBUG"
    val framework = (kotlin.targets["ios"] as KotlinNativeTarget).compilations["main"].target.binaries.findFramework("Shared", buildType)!!
    dependsOn(framework.linkTask)

    doLast {
        val srcFile = framework.outputFile
        val targetDir = project.property("configuration.build.dir") as? String ?: ""
        copy {
            from(srcFile.parent)
            into(targetDir)
            include("Shared.framework/**")
            include("Shared.framework.dSYM")
        }
    }
}
