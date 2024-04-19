import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.dsl.ExperimentalWasmDsl

// TODO are there better ways to inject this function into build scripts?
open class StandardConfigExtension {
    private val Project.kotlin get() = extensions.getByType<KotlinMultiplatformExtension>()
    private fun Project.kotlin(block: KotlinMultiplatformExtension.() -> Unit) = kotlin.block()

    fun Project.defaultTargets() {
        kotlin {
            androidTarget {
                publishAllLibraryVariants()
            }

            androidNativeX64()
            androidNativeX86()
            androidNativeArm32()
            androidNativeArm64()

            iosArm64()
            iosSimulatorArm64()
            iosX64()

            js {
                browser()
                // TODO need separate targets nodejs() because impl is browser-dependent
                //  https://youtrack.jetbrains.com/issue/KT-47038/
//                nodejs()
            }

            jvm()

            linuxArm64()
            linuxX64()

            macosArm64()
            macosX64()

            mingwX64()

            tvosArm64()
            tvosSimulatorArm64()
            tvosX64()

            @OptIn(ExperimentalWasmDsl::class)
            wasmJs {
                browser()
            }
//            // TODO Add WASI after Kotlin 2.0
//            @OptIn(ExperimentalWasmDsl::class)
//            wasmWasi()

            watchosArm32()
            watchosArm64()
            watchosDeviceArm64()
            watchosSimulatorArm64()
            watchosX64()
        }
    }
}
