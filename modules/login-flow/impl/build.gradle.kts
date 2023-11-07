plugins {
    id(libs.plugins.module.impl.with.compose.get().pluginId)
}

kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(projects.modules.logicTreeArchitecture.io)
                implementation(projects.modules.log.io)
                implementation(projects.modules.loginFlow.io)
                implementation(projects.modules.navigation.io)
                implementation(projects.modules.theme.io)
                implementation(projects.modules.auth.io)
                implementation(projects.modules.network.io)

                implementation(libs.androidx.annotation)
                implementation(libs.androidx.compose.runtime)
                implementation(libs.androidx.compose.ui)
                implementation(libs.androidx.compose.foundation)
                implementation(libs.androidx.compose.material)
                implementation(libs.androidx.compose.material3)
                implementation(libs.androidx.compose.animation)
                implementation(libs.accompanist.navigation.animation)
                implementation(libs.accompanist.navigation.material)
            }
        }
        val commonTest by getting {
            dependencies  {
                implementation(projects.modules.logicTreeArchitecture.fixture)
                implementation(projects.modules.log.fixture)
                implementation(projects.modules.network.fixture)
                implementation(projects.modules.navigation.fixture)

                implementation(libs.junit)
                implementation(libs.coroutines.test)
                implementation(libs.assertk)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(projects.modules.auth.io)

                implementation(libs.androidx.browser)
            }
        }
    }
}

android {
    namespace = "com.hadilq.mastan.loginflow.imp"
    dependencies {
        testImplementation(libs.robolectric)
        testImplementation(libs.androidx.compose.ui.test.junit4)
        testReleaseImplementation(libs.androidx.compose.ui.test.manifest)
        debugImplementation(libs.androidx.compose.ui.test.manifest)
    }
}
