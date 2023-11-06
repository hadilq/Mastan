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
package com.hadilq.mastan.navigation

import com.hadilq.mastan.logictreearchitecture.LogicTreeArchitecture

class RealNavigationLogicIo(
    private val logicTreeArchitecture: LogicTreeArchitecture,
) : NavigationLogicIo {

    private val navigationWithSink by logicTreeArchitecture.single {
        logicTreeArchitecture.logicWithEventSink<NavigationState, NavigationEvent>(
            NavigationState(), NoNavigationEvent
        )
    }

    @Suppress("NAME_SHADOWING")
    override val navigationLogic: NavigationLogic
        get() = { state: NavigationState, event: NavigationEvent, onState: (NavigationState) -> Unit ->
            navigationWithSink.Update(
                beforeUpdateState = state,
                event = event,
                onState = onState
            ) { state: NavigationState, event: NavigationEvent, onState: (NavigationState) -> Unit ->
                RealNavigationLogic(state, event, onState)
            }
        }

    override val eventSink: suspend (NavigationEvent) -> Unit =
        { event -> navigationWithSink.eventSink(event) }
}
