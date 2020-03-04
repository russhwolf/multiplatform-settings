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

@file:Suppress("KDocMissingDocumentation")

package com.russhwolf.settings.build

import com.android.build.gradle.BaseExtension
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Project
import org.gradle.api.tasks.testing.AbstractTestTask
import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.gradle.kotlin.dsl.invoke
import org.gradle.kotlin.dsl.withType
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTargetPreset
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family

private val Project.kotlin: KotlinMultiplatformExtension
    get() = extensions.getByType()

private val Project.android: BaseExtension
    get() = extensions.getByType()

fun Project.standardConfiguration(
    vararg presetNames: String = kotlin.presets.map { it.name }.toTypedArray(),
    isTestModule: Boolean = false
) {
    val targetPresets = kotlin.presets.matching { it.name in presetNames }
    kotlin.buildAllTargets(targetPresets)
    android.configureAndroidApiLevel()

    if (!isTestModule) {
        configureTests()
        configureDokka()
    }
}

private fun KotlinMultiplatformExtension.buildAllTargets(targetPresets: NamedDomainObjectCollection<KotlinTargetPreset<*>>) {
    android {
        publishAllLibraryVariants()
    }
    js {
        browser()
    }

    // Create empty targets for presets with no specific configuration
    targetPresets.forEach {
        if (it.name == "jvmWithJava") return@forEach // Probably don't need this, and it chokes on Android plugin
        if (targets.findByName(it.name) == null) {
            targetFromPreset(it)
        }
    }

    sourceSets.all {
        languageSettings.apply {
            useExperimentalAnnotation("kotlin.Experimental")
        }
    }

    linkAppleSourceSets()
}

private fun KotlinMultiplatformExtension.linkAppleSourceSets() {
    sourceSets {
        val appleMain by creating
        val appleTest by creating
        val apple64Main by creating {
            dependsOn(appleMain)
        }
        val apple64Test by creating {
            dependsOn(appleTest)
        }
        val apple32Main by creating {
            dependsOn(appleMain)
        }
        val apple32Test by creating {
            dependsOn(appleTest)
        }

        // TODO this is just here to make the IDE happy (ish) while we wait for HMPP to improve
        val iosX64Main by getting {
            kotlin.srcDirs(*appleMain.kotlin.srcDirs.toTypedArray())
            kotlin.srcDirs(*apple64Main.kotlin.srcDirs.toTypedArray())
        }

        targets
            .withType<KotlinNativeTarget>()
            .matching { it.konanTarget.family.isAppleFamily }
            .configureEach {
                if (konanTarget.architecture.bitness == 32 || konanTarget.family == Family.WATCHOS) {
                    compilations["main"].defaultSourceSet.dependsOn(apple32Main)
                    compilations["test"].defaultSourceSet.dependsOn(apple32Test)
                } else {
                    compilations["main"].defaultSourceSet.dependsOn(apple64Main)
                    compilations["test"].defaultSourceSet.dependsOn(apple64Test)
                }
            }

    }
}

private fun BaseExtension.configureAndroidApiLevel() {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(15)
    }
}

private fun Project.configureTests() {
    createIosTestTask("iosX64", "iPhone 11")
    createIosTestTask("watchosX86", "Apple Watch Series 5 - 40mm")
    createIosTestTask("tvosX64", "Apple TV")

    tasks.withType<AbstractTestTask> {
        testLogging {
            showStandardStreams = true
            events("passed", "failed")
        }
    }
}

private fun Project.createIosTestTask(targetName: String, simulatorName: String) {
    val testTaskName = "${targetName}Test"
    tasks.create(testTaskName) {
        dependsOn("linkDebugTest${targetName.capitalize()}")
        group = "verification"
        doLast {
            val target = kotlin.targets.withType<KotlinNativeTarget>()[targetName]
            val testBinaryPath = target.binaries.getTest("DEBUG").outputFile.absolutePath

            exec { commandLine("xcrun", "simctl", "spawn", "--standalone", simulatorName, testBinaryPath) }
        }
    }
    if (System.getProperty("os.name").contains("mac", ignoreCase = true)) {
        tasks["allTests"].dependsOn(testTaskName)
    }
}

private fun Project.configureDokka() {
    tasks.withType<DokkaTask> {
        multiplatform {
            kotlin.targets.forEach { create(it.name) }
        }
    }
}
