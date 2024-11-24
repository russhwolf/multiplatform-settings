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

import org.gradle.internal.extensions.stdlib.capitalized
import org.jetbrains.kotlin.gradle.dsl.ExplicitApiMode

plugins {
    id("standard-configuration-without-android")
}

kotlin {
    explicitApi = ExplicitApiMode.Disabled

    listOf(
        androidNativeArm32(),
        androidNativeArm64(),
        androidNativeX86(),
        androidNativeX64()
    ).forEach { target ->
        target.binaries.sharedLib("androidNativeTestHelpers")
    }

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":multiplatform-settings"))
            }
        }
    }
}

// Adapted from https://github.com/LandryNorris/JniUtils/blob/d7bd76996fa0c6fc34d408a2df8d5c1e27b56801/sample/build.gradle.kts#L66
enum class Platform(val platformName: String, val archName: String) {
    AndroidArm64("androidNativeArm64", "arm64-v8a"),
    AndroidArm32("androidNativeArm32", "armeabi-v7a"),
    AndroidX64("androidNativeX64", "x86_64"),
    AndroidX86("androidNativeX86", "x86")
}

/**
 * Create a task to prepare binary for a given platform.
 */
fun createSoPrepareTask(platform: Platform, buildtype: String): Task {
    return tasks.create("prepare${buildtype.capitalized()}${platform.platformName.capitalized()}So") {
        dependsOn("linkAndroidNativeTestHelpers${buildtype.capitalized()}Shared${platform.platformName.capitalized()}")
        val srcFolder =
            layout.buildDirectory.file("bin/${platform.platformName}/androidNativeTestHelpers${buildtype.capitalized()}Shared")
        val destFolder =
            rootProject.projectDir.resolve("multiplatform-settings/src/androidInstrumentedTest/jniLibs/${platform.archName}")

        doLast {
            copy {
                from(srcFolder)
                into(destFolder)
                include("*.so")
            }
        }
    }
}

val debugBinaryTasks = listOf(
    createSoPrepareTask(Platform.AndroidArm64, "debug"),
    createSoPrepareTask(Platform.AndroidArm32, "debug"),
    createSoPrepareTask(Platform.AndroidX64, "debug"),
    createSoPrepareTask(Platform.AndroidX86, "debug")
)
val releaseBinaryTasks = listOf(
    createSoPrepareTask(Platform.AndroidArm64, "release"),
    createSoPrepareTask(Platform.AndroidArm32, "release"),
    createSoPrepareTask(Platform.AndroidX64, "release"),
    createSoPrepareTask(Platform.AndroidX86, "release")
)

// TODO wire these tasks into androidUnitTest
tasks.create("prepareDebugAndroidNdkSo") {
    debugBinaryTasks.forEach {
        this.dependsOn(it)
    }
}
tasks.create("prepareReleaseAndroidNdkSo") {
    releaseBinaryTasks.forEach {
        this.dependsOn(it)
    }
}
