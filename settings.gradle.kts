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
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        google()
        mavenLocal()
    }
}

rootProject.name = "Mastan"
include(":app")
include(":legacy")
include(":splash:io")
include(":splash:impl")
include(":datastore:io")
include(":datastore:impl")
include(":log:io")
include(":log:impl")
include(":log:fixture")
include(":logic-tree-architecture:io")
include(":logic-tree-architecture:impl")
include(":logic-tree-architecture:fixture")
include(":network:io")
include(":network:impl")
include(":network:fixture")
include(":theme:io")
include(":theme:impl")
include(":root:io")
include(":root:impl")
include(":navigation:io")
include(":navigation:impl")
include(":navigation:fixture")
include(":auth:io")
include(":auth:impl")
include(":login-flow:io")
include(":login-flow:impl")
