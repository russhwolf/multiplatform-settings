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

import com.android.build.gradle.BaseExtension
import org.gradle.api.NamedDomainObjectCollection
import org.gradle.api.Project
import org.gradle.api.tasks.testing.AbstractTestTask
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.KotlinTargetPreset
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJsTargetPreset
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinJvmWithJavaTargetPreset
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTargetPreset
import org.jetbrains.kotlin.gradle.targets.js.KotlinJsTarget
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTarget
import org.jetbrains.kotlin.gradle.targets.js.ir.KotlinJsIrTargetPreset
import org.jetbrains.kotlin.konan.target.Architecture
import org.jetbrains.kotlin.konan.target.KonanTarget

private val Project.kotlin: KotlinMultiplatformExtension
    get() = extensions.getByType(KotlinMultiplatformExtension::class.java)

private val Project.android: BaseExtension
    get() = extensions.getByType(BaseExtension::class.java)

fun Project.standardConfiguration(
    // This is ugly but string-matching lets us share configuration more easily between modules
    vararg presetNames: String = kotlin.presets.map { it.name }.toTypedArray(),
    isTestModule: Boolean = false
) {
    val targetPresets = kotlin.presets.matching { it.name in presetNames }
    kotlin.buildAllTargets(targetPresets)
    android.configureAndroidApiLevel()

    if (!isTestModule) {
        kotlin.apply {
            explicitApi()
        }
        configureTests()
    }
}

private val ideaActive by lazy { System.getProperty("idea.active") == "true" }
private val KotlinTargetPreset<*>.isJsTargetPreset: Boolean
    get() = this is KotlinJsTargetPreset || this is KotlinJsIrTargetPreset
private val KotlinTargetPreset<*>.isNativeTargetPreset: Boolean
    get() = this is KotlinNativeTargetPreset
private val KotlinTarget.isJsTarget: Boolean
    get() = this is KotlinJsTarget || this is KotlinJsIrTarget

private fun KotlinMultiplatformExtension.buildAllTargets(targetPresets: NamedDomainObjectCollection<KotlinTargetPreset<*>>) {
    if (targetPresets.findByName("android") != null) {
        android {
            publishAllLibraryVariants()
        }
    }
    if (targetPresets.findByName("js") != null) {
        // TODO include nodejs somehow?
        js {
            browser()
        }
    }

    // Create empty targets for presets with no specific configuration
    targetPresets.forEach {
        if (it is KotlinJvmWithJavaTargetPreset) return@forEach // Probably don't need this, and it chokes on Android plugin
        if (it.isJsTargetPreset && targets.any { it.isJsTarget }) return@forEach // Ignore repeat js targets
        if (it.name.contains("wasm")) return@forEach // No more Kotlin/Native WASM. TODO re-add new WASM
        if (targets.findByName(it.name) == null) {
            targetFromPreset(it)
        }
    }

    sourceSets.all {
        it.languageSettings.apply {
            optIn("kotlin.RequiresOptIn")
        }
    }
    targets.configureEach {
        it.compilations.configureEach {
            // TODO reenable this once duplicate library warnings are cleared
//            it.kotlinOptions.allWarningsAsErrors = true
        }
    }

    linkSourceSets(targetPresets)
}

private fun KotlinMultiplatformExtension.linkSourceSets(targetPresets: NamedDomainObjectCollection<KotlinTargetPreset<*>>) {
    sourceSets.apply {
        val commonMain = getByName("commonMain")
        val commonTest = getByName("commonTest")

        val multithreadedMain = create("multithreadedMain").apply {
            dependsOn(commonMain)
        }
        val multithreadedTest = create("multithreadedTest").apply {
            dependsOn(commonTest)
        }
        val jvmCommonMain = create("jvmCommonMain").apply {
            dependsOn(multithreadedMain)
        }
        val jvmCommonTest = create("jvmCommonTest").apply {
            dependsOn(multithreadedTest)
        }

        findByName("androidMain")?.dependsOn(jvmCommonMain)
        findByName("androidTest")?.dependsOn(jvmCommonTest)
        findByName("jvmMain")?.dependsOn(jvmCommonMain)
        findByName("jvmTest")?.dependsOn(jvmCommonTest)

        if (targetPresets.any { it.isNativeTargetPreset }) {

            val nativeMain = create("nativeMain").apply {
                dependsOn(multithreadedMain)
            }
            val nativeTest = create("nativeTest").apply {
                dependsOn(multithreadedTest)
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

            targets
                .withType(KotlinNativeTarget::class.java)
                .matching { it.konanTarget.family.isAppleFamily }
                .configureEach {
                    it.apply {
                        if (
                            konanTarget.architecture !in listOf(Architecture.X64, Architecture.ARM64) ||
                            konanTarget in listOf<KonanTarget>(KonanTarget.WATCHOS_ARM64)
                        ) {
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

}

private fun BaseExtension.configureAndroidApiLevel() {
    compileSdkVersion(Versions.compileSdk)
    defaultConfig.apply {
        minSdkVersion(Versions.minSdk)
    }
    lintOptions.apply {
        isAbortOnError = true
        isWarningsAsErrors = true
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
