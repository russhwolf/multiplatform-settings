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

@file:Suppress("KDocMissingDocumentation")

package com.russhwolf.settings.build

import com.android.build.gradle.BaseExtension
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Project
import org.gradle.api.tasks.testing.AbstractTestTask
import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTargetPreset
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.konan.target.Family

private val Project.kotlin: KotlinMultiplatformExtension
    get() = extensions.getByType(KotlinMultiplatformExtension::class.java)

private val Project.android: BaseExtension
    get() = extensions.getByType(BaseExtension::class.java)

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

private val ideaActive by lazy { System.getProperty("idea.active") == "true" }

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
        it.languageSettings.apply {
            useExperimentalAnnotation("kotlin.RequiresOptIn")
        }
    }

    linkNativeSourceSets()
}

private fun KotlinMultiplatformExtension.linkNativeSourceSets() {
    sourceSets.apply {
        val commonMain = getByName("commonMain")
        val commonTest = getByName("commonTest")
        val nativeMain = create("nativeMain").apply {
            dependsOn(commonMain)
        }
        val nativeTest = create("nativeTest").apply {
            dependsOn(commonTest)
        }
        val appleMain = create("appleMain").apply {
            dependsOn(nativeMain)
        }
        val appleTest = create("appleTest").apply {
            dependsOn(nativeTest)
        }
        val apple64Main = create("apple64Main").apply {
            dependsOn(appleMain)
        }
        val apple64Test = create("apple64Test").apply {
            dependsOn(appleTest)
        }
        val apple32Main = create("apple32Main").apply {
            dependsOn(appleMain)
        }
        val apple32Test = create("apple32Test").apply {
            dependsOn(appleTest)
        }

        // TODO this is just here to make the IDE happy (ish) while we wait for HMPP to improve
        if (ideaActive) {
            val macosX64Main = getByName("macosX64Main").apply {
                kotlin.srcDirs(*nativeMain.kotlin.srcDirs.toTypedArray())
                kotlin.srcDirs(*appleMain.kotlin.srcDirs.toTypedArray())
                kotlin.srcDirs(*apple64Main.kotlin.srcDirs.toTypedArray())
            }
            val macosX64Test = getByName("macosX64Test").apply {
                kotlin.srcDirs(*nativeTest.kotlin.srcDirs.toTypedArray())
                kotlin.srcDirs(*appleTest.kotlin.srcDirs.toTypedArray())
                kotlin.srcDirs(*apple64Test.kotlin.srcDirs.toTypedArray())
            }
        }

        targets
            .withType(KotlinNativeTarget::class.java)
            .matching { it.konanTarget.family.isAppleFamily }
            .configureEach {
                it.apply {
                    if (konanTarget.architecture.bitness == 32 || konanTarget.family == Family.WATCHOS) {
                        compilations.getByName("main").defaultSourceSet.dependsOn(apple32Main)
                        compilations.getByName("test").defaultSourceSet.dependsOn(apple32Test)
                    } else {
                        compilations.getByName("main").defaultSourceSet.dependsOn(apple64Main)
                        compilations.getByName("test").defaultSourceSet.dependsOn(apple64Test)
                    }
                }
            }

        targets
            .withType(KotlinNativeTarget::class.java)
            .matching { !it.konanTarget.family.isAppleFamily }
            .configureEach {
                it.apply {
                    compilations.getByName("main").defaultSourceSet.dependsOn(nativeMain)
                    compilations.getByName("test").defaultSourceSet.dependsOn(nativeTest)
                }
            }
    }

}

private fun BaseExtension.configureAndroidApiLevel() {
    compileSdkVersion(29)
    defaultConfig.apply {
        minSdkVersion(15)
    }
}

private fun Project.configureTests() {
    tasks.withType(AbstractTestTask::class.java) { task ->
        task.testLogging.apply {
            showStandardStreams = true
            events("passed", "failed")
        }
    }
}

private fun Project.configureDokka() {
    tasks.withType(DokkaTask::class.java) { task ->
        task.multiplatform.apply {
            kotlin.targets.forEach { create(it.name) }
        }
    }
}
