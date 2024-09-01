/*
 * Copyright 2024 Russell Wolf
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

import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest

plugins {
    id("standard-configuration-without-android")
}

kotlin {
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    macosX64()
    macosArm64()
    // TODO also add tvos and watchos simulators?

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":multiplatform-settings"))
            }
        }
        commonTest {
            dependencies {
                implementation(project(":tests"))

                implementation(libs.kotlin.test)
            }
        }
    }
}

// Hacks to get KeychainSettingsTest running on iOS simulator
// https://youtrack.jetbrains.com/issue/KT-61470
kotlin {
    iosX64 {
        testRuns.configureEach {
            executionSource.binary.linkerOpts(
                "-sectcreate",
                "__TEXT",
                "__entitlements",
                file("$projectDir/src/commonTest/resources/entitlements.plist").absolutePath
            )
        }
    }
}
if ("mac" in System.getProperties()["os.name"].toString().lowercase()) {
    tasks.create<Exec>("launchIosSimulator") {
        commandLine("open", "-a", "Simulator")
    }

    tasks.getByName("iosX64Test").dependsOn("launchIosSimulator")
}
tasks.withType<KotlinNativeSimulatorTest>().getByName("iosX64Test") {
    standalone.set(false)
    device.set("booted")
}
