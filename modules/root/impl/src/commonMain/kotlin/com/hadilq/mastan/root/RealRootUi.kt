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
package com.hadilq.mastan.root

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.hadilq.mastan.auth.LocalAuthLogicIo
import com.hadilq.mastan.log.LocalLogUiIo
import com.hadilq.mastan.loginflow.LocalLoginFlowUiIo
import com.hadilq.mastan.theme.LocalMastanThemeUiIo
import com.hadilq.mastan.theme.UpdateDarkThemeThemeEvent

@Composable
fun RealRootUi(
    rootUiIo: RootUiIo,
    mainContent: @Composable () -> Unit,
) {
    val rootState by remember { rootDependencies.state }
    var rootEvent by remember { rootDependencies.event }
    CompositionLocalProvider(
        LocalRootUiIo provides rootUiIo,
        LocalAuthLogicIo provides rootDependencies.authLogicIo,
        LocalMastanThemeUiIo provides rootDependencies.mastanThemeUiIo,
        LocalLoginFlowUiIo provides rootDependencies.loginFlowUiIo,
        LocalLogUiIo provides rootDependencies.logUiIo,
    ) {
        val isDarkTheme = isSystemInDarkTheme()
        LaunchedEffect(Unit) {
            rootDependencies.mastanThemeUiIo.eventSink(UpdateDarkThemeThemeEvent(isDarkTheme))
        }
        rootDependencies.navigationUiIo.navigationUi(
            rootState.navigationState,
            { rootEvent = NavigationRootEvent(it) },
            mainContent,
        )
    }
}
