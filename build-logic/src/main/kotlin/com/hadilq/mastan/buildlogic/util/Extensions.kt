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
package com.hadilq.mastan.buildlogic.util

import org.gradle.api.Project
import org.gradle.api.reflect.TypeOf

/**
 * Returns the plugin convention or extension of the specified type.
 *
 * Note, that the concept of conventions is deprecated and scheduled for
 * removal in Gradle 8.
 */
inline fun <reified T : Any> Project.the(): T =
    @Suppress("deprecation")
    typeOf<T>().let { type ->
        convention.findByType(type)
            ?: convention.findPlugin(T::class.java)
            ?: convention.getByType(type)
    }


/**
 * Creates an instance of [TypeOf] for the given parameterized type.
 *
 * @param T the type
 * @return the [TypeOf] that captures the generic type of the given parameterized type
 */
inline fun <reified T> typeOf(): TypeOf<T> =
    object : TypeOf<T>() {}


