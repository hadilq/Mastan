package com.hadilq.mastan.buildlogic.di

import com.android.build.gradle.AppExtension
import com.android.build.gradle.BaseExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.gradle.api.JavaVersion
import org.gradle.api.plugins.ExtensionAware
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinTopLevelExtension

class RealDependencies: Dependencies {
    override fun configureAndroidApp(libs: LibrariesForLibs): AppExtension.() -> Unit = {
        compileSdkVersion(libs.versions.compileSdk.get().toInt())

        defaultConfig {
            minSdk = libs.versions.minSdk.get().toInt()
            targetSdk = libs.versions.targetSdk.get().toInt()

            testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            vectorDrawables {
                useSupportLibrary = true
            }
        }

        configureJvmTarget(libs)

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

    override fun configureAndroidModule(libs: LibrariesForLibs): (LibraryExtension).() -> Unit = {
        compileSdk = libs.versions.compileSdk.get().toInt()

        defaultConfig {
            minSdk = libs.versions.minSdk.get().toInt()
        }

        configureJvmTarget(libs)

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

    override fun configureAndroidModuleWithCompose(libs: LibrariesForLibs): LibraryExtension.() -> Unit = {
        configureAndroidModule(libs)()

        buildFeatures.apply {
            compose = true
        }

        composeOptions.kotlinCompilerExtensionVersion =
            libs.versions.androidxComposeCompiler.get()

    }

    private fun BaseExtension.configureJvmTarget(libs: LibrariesForLibs) {
        val targetSdk = javaVersion(libs)
        compileOptions {
            sourceCompatibility = targetSdk
            targetCompatibility = targetSdk
        }
    }

    override fun configureMultiplatformModule(libs: LibrariesForLibs): (KotlinMultiplatformExtension).() -> Unit = {
        android {
            compilations.all {
                kotlinOptions {
                    jvmTarget = libs.versions.jvmTarget.get()
                }
            }
        }
    }

    override fun configureKotlinProject(libs: LibrariesForLibs): KotlinTopLevelExtension.() -> Unit = {
        jvmToolchain(libs.versions.jvmTarget.get().toInt())
    }

    private fun javaVersion(libs: LibrariesForLibs): JavaVersion {
        val targetSdk = when (libs.versions.jvmTarget.get()) {
            "19" -> JavaVersion.VERSION_19
            "17" -> JavaVersion.VERSION_17
            "11" -> JavaVersion.VERSION_11
            else -> JavaVersion.VERSION_18
        }
        return targetSdk
    }
}
