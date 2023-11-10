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
import com.google.devtools.ksp.gradle.KspGradleSubplugin
import com.hadilq.mastan.buildlogic.di.dependencies
import com.hadilq.mastan.buildlogic.util.the
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.internal.Kapt3GradleSubplugin
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper
import org.jetbrains.kotlinx.serialization.gradle.SerializationGradleSubplugin

class AndroidAppPlugin : Plugin<Project> {

    override fun apply(target: Project) = with(dependencies) {
        target.pluginManager.apply(AppPlugin::class.java)
        target.pluginManager.apply(KotlinAndroidPluginWrapper::class.java)
        target.pluginManager.apply(Kapt3GradleSubplugin::class.java)
        target.pluginManager.apply(KspGradleSubplugin::class.java)
        target.pluginManager.apply(SerializationGradleSubplugin::class.java)

        val libs = target.the<LibrariesForLibs>()

        target.extensions.configure(AppExtension::class.java, configureAndroidApp(libs))
        target.extensions.configure(
            KotlinProjectExtension::class.java,
            configureKotlinProject(libs)
        )
    }
}
