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
    id("standard-configuration")
    id("module-publication")
    alias(libs.plugins.kotlin.serialization)
}

standardConfig {
    defaultTargets()
}

kotlin {
    sourceSets {
        commonMain {
            dependencies {
                implementation(project(":multiplatform-settings"))

                implementation(libs.kotlinx.serialization.core)
            }
        }

        commonTest {
            dependencies {
                implementation(libs.kotlin.test)

                implementation(project(":multiplatform-settings-test"))
            }
        }
    }
}

android {
    namespace = "com.russhwolf.settings.serialization"
}
