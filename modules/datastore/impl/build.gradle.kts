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
    id(libs.plugins.module.impl.asProvider().get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.modules.datastore.io)
                implementation(projects.modules.logicTreeArchitecture.io)
                implementation(projects.modules.log.io)
                implementation(projects.modules.root.io)
                implementation(projects.modules.auth.io)
                implementation(projects.modules.navigation.io)
                implementation(projects.modules.theme.io)
            }
        }
        val commonTest by getting
        val androidMain by getting
    }
}

android {
    namespace = "com.hadilq.mastan.datastore.impl"
}

dependencies {
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.material)
    implementation(libs.androidx.datastore)
    implementation(libs.kotlinx.serialization.json)

    testImplementation(libs.coroutines.test)
    testImplementation(libs.junit)
    testImplementation(libs.assertj)

    androidTestImplementation(libs.coroutines.test)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.assertj)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.espresso)
    androidTestImplementation(libs.junit.ext)
}
