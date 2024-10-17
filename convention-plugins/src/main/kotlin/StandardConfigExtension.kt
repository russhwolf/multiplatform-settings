import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

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

            @OptIn(ExperimentalWasmDsl::class)
            wasmWasi {
                nodejs()
            }

            watchosArm32()
            watchosArm64()
            watchosDeviceArm64()
            watchosSimulatorArm64()
            watchosX64()
        }
    }
}
