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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import com.hadilq.mastan.logictreearchitecture.LogicTreeArchitecture

class RealMastanThemeUiIo(
    logicTreeArchitecture: LogicTreeArchitecture,
) : MastanThemeUiIo {

    private val mastanThemeEvent: MutableState<MastanThemeEvent> =
        mutableStateOf(NoMastanThemeEvent)
    private var lastMastanThemeEvent: MastanThemeEvent = NoMastanThemeEvent

    override val lightColors: ColorScheme by logicTreeArchitecture.singleWithNoRace {
        LightColors
    }

    override val darkColors: ColorScheme by logicTreeArchitecture.singleWithNoRace {
        DarkColors
    }

    override val dim: Dimension by logicTreeArchitecture.singleWithNoRace {
        RealDimension()
    }

    override val type: Typography by logicTreeArchitecture.singleWithNoRace {
        Typography
    }

    override var mastanThemeState: MastanThemeState = MastanThemeState()

    override val mastanThemeLogic: MastanThemeLogic
        get() = {
                state: MastanThemeState,
                event: MastanThemeEvent,
                onState: (MastanThemeState) -> Unit,
            ->
            if (lastMastanThemeEvent != mastanThemeEvent.value) {
                lastMastanThemeEvent = mastanThemeEvent.value
                RealMastanThemeLogic(state = state, event = mastanThemeEvent.value) {
                    mastanThemeState = it
                    onState(it)
                }
            } else {
                lastMastanThemeEvent = event
                mastanThemeEvent.value = event
                RealMastanThemeLogic(state = state, event = event) {
                    mastanThemeState = it
                    onState(it)
                }

            }
        }

    override val eventSink: (MastanThemeEvent) -> Unit = {
        mastanThemeEvent.value = it
    }

    override val mastanThemeUi: MastanThemeUi
        get() = { content: @Composable () -> Unit ->
            RealMastanThemeUi(this, mastanThemeState, content)
        }
}