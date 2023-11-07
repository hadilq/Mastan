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

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.hadilq.mastan.buildlogic.util.the
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.internal.Kapt3GradleSubplugin
import org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

class AndroidAppPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        target.pluginManager.apply(AppPlugin::class.java)
        target.pluginManager.apply(KotlinAndroidPluginWrapper::class.java)
        target.pluginManager.apply(Kapt3GradleSubplugin::class.java)

        val libs = target.the<LibrariesForLibs>()

        target.extensions.configure(AppExtension::class.java, configureAndroidApp(libs))

        target.tasks.withType(KaptGenerateStubsTask::class.java).configureEach {
            kotlinOptions {
                jvmTarget = libs.versions.jvmTarget.get()
            }
        }
    }

    private fun configureAndroidApp(libs: LibrariesForLibs): (AppExtension).() -> Unit =
        {
            compileSdkVersion(libs.versions.compileSdk.get().toInt())

            defaultConfig {
                minSdk = libs.versions.minSdk.get().toInt()
                targetSdk = libs.versions.targetSdk.get().toInt()

                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                vectorDrawables {
                    useSupportLibrary = true
                }
            }

            val targetSdk = when(libs.versions.jvmTarget.get()) {
                "17" -> JavaVersion.VERSION_17
                "11" -> JavaVersion.VERSION_11
                else -> JavaVersion.VERSION_18
            }
            compileOptions {
                sourceCompatibility = targetSdk
                targetCompatibility = targetSdk
            }

            (this as ExtensionAware).configure<KotlinJvmOptions> {
                jvmTarget = libs.versions.jvmTarget.get()
            }

            buildFeatures.apply {
                compose = true
                buildConfig = true
            }

            composeOptions.kotlinCompilerExtensionVersion =
                libs.versions.androidxComposeCompiler.get()

            packagingOptions {
                resources {
                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
            }
        }
}
