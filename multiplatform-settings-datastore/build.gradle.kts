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

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("standard-configuration")
    id("module-publication")
}

kotlin {
    androidTarget {
        publishAllLibraryVariants()
    }
    iosArm64()
    iosSimulatorArm64()
    iosX64()
    jvm()
    linuxX64()
    macosArm64()
    macosX64()

    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":multiplatform-settings"))
                implementation(project(":multiplatform-settings-coroutines"))

                implementation(libs.kotlinx.coroutines.core)

                implementation(libs.androidx.datastore.preferences.core)
            }
        }
        commonTest {
            dependencies {
                implementation(libs.kotlin.test)

                implementation(project(":tests"))
                implementation(project(":multiplatform-settings-test"))

                implementation(libs.kotlinx.coroutines.test)

                implementation(libs.turbine)
                implementation(libs.okio.fakefilesystem)
            }
        }
    }
}

android {
    namespace = "com.russhwolf.settings.datastore"
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        jvmTarget = JvmTarget.JVM_1_8
        freeCompilerArgs.add("-Xjvm-default=all")
    }
}
