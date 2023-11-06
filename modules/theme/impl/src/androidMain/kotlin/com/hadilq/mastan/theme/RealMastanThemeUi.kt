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

import android.os.Build
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun RealMastanThemeUi(
    mastanThemeUiIo: MastanThemeUiIo,
    mastanState: MastanThemeState,
    content: @Composable () -> Unit,
) {
    val dynamicColor = mastanState.isDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val myColorScheme = when {
        dynamicColor && mastanState.isDarkTheme -> {
            dynamicDarkColorScheme(LocalContext.current)
        }

        dynamicColor && !mastanState.isDarkTheme -> {
            dynamicLightColorScheme(LocalContext.current)
        }

        mastanState.isDarkTheme -> mastanThemeUiIo.darkColors
        else -> mastanThemeUiIo.lightColors
    }

    MaterialTheme(
        colorScheme = myColorScheme,
        content = content
    )
}
