plugins {
    id(libs.plugins.module.io.get().pluginId)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.modules.logicTreeArchitecture.io)

                implementation(libs.androidx.annotation)
                implementation(libs.androidx.compose.runtime)
                implementation(libs.androidx.compose.ui)
                implementation(libs.androidx.compose.foundation)
                implementation(libs.androidx.compose.material3)
                implementation(libs.kotlinx.serialization.json)
            }
        }
        val commonTest by getting
        val androidMain by getting
    }
}

android {
    namespace = "com.hadilq.mastan.navigation.io"
}
