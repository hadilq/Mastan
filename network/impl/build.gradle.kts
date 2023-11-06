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
                implementation(projects.logicTreeArchitecture.io)
                implementation(projects.log.io)
                implementation(projects.network.io)
                implementation(projects.auth.io)

                implementation(libs.androidx.compose.runtime)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.coroutines)
                implementation(libs.store.cache)
            }
        }
        val commonTest by getting
        val androidMain by getting {
            dependencies {
                implementation(libs.com.squareup.retrofit2.retrofit)
                implementation(libs.com.squareup.okhttp3.logging.interceptor)
                implementation(libs.com.squareup.okhttp3.okhttp)
                implementation(libs.com.jakewharton.retrofit)
                implementation(libs.store.jvm)
            }
        }
    }
}

android {
    namespace = "com.hadilq.mastan.network.impl"
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
