plugins {
    id(libs.plugins.module.impl.asProvider().get().pluginId)
    alias(libs.plugins.kotlin.serialization)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.modules.logicTreeArchitecture.io)
                implementation(projects.modules.log.io)
                implementation(projects.modules.network.io)
                implementation(projects.modules.auth.io)

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
}
