/**
 * Copyright 2023 Hadi Lashkari Ghouchani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
plugins {
    kotlin("multiplatform")
    id("com.android.library")
}

kotlin {
    android {
        compilations.all {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_17.toString()
            }
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.modules.logicTreeArchitecture.io)

                implementation(libs.androidx.annotation)
                implementation(libs.androidx.compose.runtime)
                implementation(libs.androidx.compose.ui)
                implementation(libs.androidx.compose.foundation)
                implementation(libs.androidx.compose.material3)
                implementation(libs.accompanist.navigation.animation)
                implementation(libs.accompanist.navigation.material)
            }
        }
        val commonTest by getting
        val androidMain by getting
    }
}

android {
    namespace = "com.hadilq.mastan.logicTreeArchitecture.fixture"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility(JavaVersion.VERSION_17)
        targetCompatibility(JavaVersion.VERSION_17)
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.androidxComposeCompiler.get()
    }
}
