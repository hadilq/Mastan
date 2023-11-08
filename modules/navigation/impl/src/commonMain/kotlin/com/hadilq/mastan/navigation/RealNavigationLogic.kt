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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.hadilq.mastan.auth.LocalAuthLogicIo
import com.hadilq.mastan.auth.NoAuthState
import com.hadilq.mastan.log.LocalLogLogicIo
import com.hadilq.mastan.loginflow.InitialLoginFlowState
import com.hadilq.mastan.loginflow.LocalLoginFlowLogicIo
import com.hadilq.mastan.loginflow.NoLoginFlowEvent

@Composable
fun RealNavigationLogic(
    state: NavigationState,
    event: NavigationEvent,
    onState: (NavigationState) -> Unit,
) {
    val logger = LocalLogLogicIo.current
    val authLogicIo = LocalAuthLogicIo.current
    LaunchedEffect(state, event) {
        logger.logDebug { "navigation logic $state, $event" }
        when (event) {
            SelectServerNavigationEvent -> {
                onState(NavigationState(listOf(LoginFlow(InitialLoginFlowState))))
            }

            TimeLineNavigationEvent -> {
                logger.logDebug { "navigation logic TimeLineNavigationEvent, $event" }
                if (authLogicIo.state is NoAuthState) {
                    logger.logDebug("navigation logic TimeLineNavigationEvent NoAuth")
                    onState(NavigationState(listOf(LoginFlow(InitialLoginFlowState))))
                } else {
                    logger.logDebug("navigation logic TimeLineNavigationEvent MainContent")
                    onState(NavigationState(listOf(LegacyMainContent)))
                }
            }

            is LoginFlowNavigationEvent -> {}
            NoNavigationEvent -> {
                if (authLogicIo.state is NoAuthState && state.stack.isEmpty()) {
                    onState(NavigationState(listOf(LoginFlow(InitialLoginFlowState))))
                }
            }
        }
    }

    val currentPage = state.stack.lastOrNull() ?: return
    when (currentPage) {
        is LoginFlow -> {
            val loginFlowLogicIo = LocalLoginFlowLogicIo.current
            loginFlowLogicIo.loginFlowLogic(
                currentPage.loginState,
                when (event) {
                    is LoginFlowNavigationEvent -> event.loginFlowEvent
                    else -> NoLoginFlowEvent
                }
            ) {
                onState(
                    state.copy(stack = state.stack.toMutableList().apply {
                        removeLast()
                        add(LoginFlow(it))
                    }.toList())
                )
            }
        }

        LegacyMainContent -> {}
    }
}