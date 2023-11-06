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
include(":modules:splash:io")
include(":modules:splash:impl")
include(":modules:datastore:io")
include(":modules:datastore:impl")
include(":modules:log:io")
include(":modules:log:impl")
include(":modules:log:fixture")
include(":modules:logic-tree-architecture:io")
include(":modules:logic-tree-architecture:impl")
include(":modules:logic-tree-architecture:fixture")
include(":modules:network:io")
include(":modules:network:impl")
include(":modules:network:fixture")
include(":modules:theme:io")
include(":modules:theme:impl")
include(":modules:root:io")
include(":modules:root:impl")
include(":modules:navigation:io")
include(":modules:navigation:impl")
include(":modules:navigation:fixture")
include(":modules:auth:io")
include(":modules:auth:impl")
include(":modules:login-flow:io")
include(":modules:login-flow:impl")
