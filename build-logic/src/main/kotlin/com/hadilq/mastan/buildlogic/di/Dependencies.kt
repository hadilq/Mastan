package com.hadilq.mastan.buildlogic.di

import com.android.build.gradle.AppExtension
import com.android.build.gradle.LibraryExtension
import org.gradle.accessors.dm.LibrariesForLibs
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinTopLevelExtension

val dependencies: Dependencies by lazy { RealDependencies() }

interface Dependencies {
    fun configureAndroidApp(libs: LibrariesForLibs): (AppExtension).() -> Unit

    fun configureAndroidModule(libs: LibrariesForLibs): (LibraryExtension).() -> Unit

    fun configureAndroidModuleWithCompose(libs: LibrariesForLibs): (LibraryExtension).() -> Unit

    fun configureMultiplatformModule(libs: LibrariesForLibs): (KotlinMultiplatformExtension).() -> Unit

    fun configureKotlinProject(libs: LibrariesForLibs): (KotlinTopLevelExtension).() -> Unit
}
