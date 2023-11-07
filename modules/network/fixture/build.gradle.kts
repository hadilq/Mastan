plugins {
    id(libs.plugins.module.impl.asProvider().get().pluginId)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.modules.network.io)
                implementation(projects.modules.auth.io)

                implementation(libs.androidx.compose.runtime)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.coroutines)
                implementation(libs.store.cache)
            }
        }
        val commonTest by getting
        val androidMain by getting
    }
}

android {
    namespace = "com.hadilq.mastan.network.fixture"
}
