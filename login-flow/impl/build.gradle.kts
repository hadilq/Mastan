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
                implementation(projects.loginFlow.io)
                implementation(projects.navigation.io)
                implementation(projects.theme.io)
                implementation(projects.auth.io)
                implementation(projects.network.io)

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
                implementation(projects.logicTreeArchitecture.fixture)
                implementation(projects.log.fixture)
                implementation(projects.network.fixture)
                implementation(projects.navigation.fixture)

                implementation(libs.junit)
                implementation(libs.coroutines.test)
                implementation(libs.assertk)
            }
        }
        val androidMain by getting {
            dependencies {
                implementation(projects.auth.io)

                implementation(libs.androidx.browser)
            }
        }
    }
}

android {
    namespace = "com.hadilq.mastan.loginflow.imp"
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
    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }
    dependencies {
        testImplementation(libs.robolectric)
        testImplementation(libs.androidx.compose.ui.test.junit4)
        testReleaseImplementation(libs.androidx.compose.ui.test.manifest)
        debugImplementation(libs.androidx.compose.ui.test.manifest)
    }
}
