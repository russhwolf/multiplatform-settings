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
    kotlin("multiplatform") version "1.4.32" apply false
    kotlin("android") version "1.4.32" apply false
    id("com.android.library") version "4.1.2" apply false
    id("com.android.application") version "4.1.2" apply false
}

allprojects {
    ext["library_version"] = "0.7.7"

    repositories {
        mavenLocal()
        google()
        mavenCentral()
    }
}
