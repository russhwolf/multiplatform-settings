import org.jetbrains.compose.desktop.application.dsl.TargetFormat

/*
 * Copyright 2023 Russell Wolf
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
    kotlin("jvm")
    kotlin("plugin.compose")
    id("org.jetbrains.compose") version "1.6.11"
}

dependencies {
    implementation(project(":shared"))
    implementation("com.russhwolf:multiplatform-settings:${rootProject.ext["library_version"]}")

    implementation(compose.desktop.currentOs)
    testImplementation(compose.desktop.uiTestJUnit4)
}

compose.desktop {
    application {
        mainClass = "com.russhwolf.settings.example.jvm.MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "SettingsDemo"
            packageVersion = "1.0.0"
        }
    }
}
