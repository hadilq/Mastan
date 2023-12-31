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
package com.hadilq.mastan.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.compositionLocalOf
import com.hadilq.mastan.logictreearchitecture.di.UiIoDefinition

val LocalMastanThemeUiIo = compositionLocalOf<MastanThemeUiIo> { error("No component found!") }

/**
 * This is the definition of `theme` module. Its implementation is available in the `:theme:impl` module.
 * This indicates the functionality that `theme` module provides for other modules.
 */
@UiIoDefinition
interface MastanThemeUiIo {
    val lightColors: ColorScheme
    val darkColors: ColorScheme
    val dim: Dimension
    val type: Typography
    val mastanThemeUi: MastanThemeUi
    val mastanThemeState: MastanThemeState
    val mastanThemeLogic: MastanThemeLogic
    val eventSink: (MastanThemeEvent) -> Unit
}
