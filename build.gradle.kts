import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

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

plugins {
    id("binary-compatibility-validator") version "0.2.3"
    id("com.jfrog.bintray") version "1.8.5" apply false
}

allprojects {
    group = "com.russhwolf"
    version = "0.6-1.4-M2"

    repositories {
        maven(url = "https://dl.bintray.com/kotlin/kotlin-eap")
        google()
        mavenCentral()
        jcenter()
    }

    // Workaround for potential name collisions with default module name (https://youtrack.jetbrains.com/issue/KT-36721)
    pluginManager.withPlugin("kotlin-multiplatform") {
        val kotlinExtension = project.extensions.getByName("kotlin") as KotlinMultiplatformExtension
        val uniqueName = "${project.group}.${project.name}"
        kotlinExtension.targets.withType(KotlinNativeTarget::class.java) {
            compilations["main"].kotlinOptions.freeCompilerArgs += listOf("-module-name", uniqueName)
        }
    }
}

apiValidation {
    ignoredProjects.add("tests")
}
