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

import org.jetbrains.dokka.gradle.DokkaTask
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

plugins {
    id("binary-compatibility-validator") version "0.2.3"
}

allprojects {
    group = "com.russhwolf"
    version = "0.6.3"

    repositories {
        google()
        mavenCentral()
        jcenter()
    }

    val emptyJavadocJar by tasks.registering(Jar::class) {
        archiveClassifier.set("javadoc")
    }

    afterEvaluate {
        extensions.findByType<PublishingExtension>()?.apply {
            repositories {
                maven {
                    url = uri(
                        if (isReleaseBuild) {
                            "https://oss.sonatype.org/service/local/staging/deploy/maven2"
                        } else {
                            "https://oss.sonatype.org/content/repositories/snapshots"
                        }
                    )
                    credentials {
                        username = properties["sonatypeUsername"].toString()
                        password = properties["sonatypePassword"].toString()
                    }
                }
            }

            publications.withType<MavenPublication>().configureEach {
                artifact(emptyJavadocJar.get())

                pom {
                    name.set("Multiplatform Settings")
                    description.set("A Kotlin Multiplatform library for saving simple key-value data")
                    url.set("https://github.com/russhwolf/multiplatform-settings")

                    licenses {
                        license {
                            name.set("The Apache Software License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                            distribution.set("repo")
                        }
                    }
                    developers {
                        developer {
                            id.set("russhwolf")
                            name.set("Russell Wolf")
                        }
                    }
                    scm {
                        url.set("https://github.com/russhwolf/multiplatform-settings")
                    }
                }
            }
        }

        extensions.findByType<SigningExtension>()?.apply {
            val publishing = extensions.findByType<PublishingExtension>() ?: return@apply
            val key = properties["signingKey"]?.toString()?.replace("\\n", "\n")
            val password = properties["signingPassword"]?.toString()

            useInMemoryPgpKeys(key, password)
            sign(publishing.publications)
        }

        tasks.withType<Sign>().configureEach {
            onlyIf { isReleaseBuild }
        }

        tasks.withType(DokkaTask::class.java) {
            multiplatform {
                extensions.findByType<KotlinMultiplatformExtension>()?.targets?.forEach { create(it.name) }
            }
        }
    }
}

val isReleaseBuild: Boolean
    get() = properties.containsKey("signingKey")



apiValidation {
    ignoredProjects.add("tests")
}
