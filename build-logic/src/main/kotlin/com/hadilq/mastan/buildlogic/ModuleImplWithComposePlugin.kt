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
package com.hadilq.mastan.buildlogic

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.LibraryPlugin
import com.hadilq.mastan.buildlogic.util.the
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.compose.ComposePlugin
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinMultiplatformPluginWrapper

class ModuleImplWithComposePlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.pluginManager.apply(LibraryPlugin::class.java)
        target.pluginManager.apply(KotlinMultiplatformPluginWrapper::class.java)
        target.pluginManager.apply(ComposePlugin::class.java)

        val libs = target.the<LibrariesForLibs>()

        target.extensions.configure(
            KotlinMultiplatformExtension::class.java,
            configureMultiplatformModule(libs)
        )
        target.extensions.configure(LibraryExtension::class.java, configureAndroidModule(libs))
    }

    private fun configureMultiplatformModule(libs: LibrariesForLibs): (KotlinMultiplatformExtension).() -> Unit =
        {
            android {
                compilations.all {
                    kotlinOptions {
                        jvmTarget = libs.versions.jvmTarget.get()
                    }
                }
            }
        }

    private fun configureAndroidModule(libs: LibrariesForLibs): (LibraryExtension).() -> Unit =
        {
            compileSdkVersion(libs.versions.compileSdk.get().toInt())

            defaultConfig {
                minSdk = libs.versions.minSdk.get().toInt()
            }

            val targetSdk = when (libs.versions.jvmTarget.get()) {
                "17" -> JavaVersion.VERSION_17
                "11" -> JavaVersion.VERSION_11
                else -> JavaVersion.VERSION_18
            }
            compileOptions {
                sourceCompatibility = targetSdk
                targetCompatibility = targetSdk
            }

            buildFeatures.apply {
                compose = true
            }

            composeOptions.kotlinCompilerExtensionVersion =
                libs.versions.androidxComposeCompiler.get()

            testOptions {
                unitTests {
                    isReturnDefaultValues = true
                    isIncludeAndroidResources = true
                }
            }

            packaging {
                resources {
                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
            }
        }
}
