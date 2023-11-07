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
package com.hadilq.mastan.auth

import com.hadilq.mastan.logictreearchitecture.LogicTreeArchitecture

class RealAuthLogicIo(
    private val logicTreeArchitecture: LogicTreeArchitecture,
) : AuthLogicIo {

    private val authWithSink by logicTreeArchitecture.single {
        logicTreeArchitecture.logicWithEventSink<AuthState, AuthEvent>(NoAuthState, NoAuthEvent)
    }

    @Suppress("NAME_SHADOWING")
    override val authLogic: AuthLogic
        get() = { state: AuthState, event: AuthEvent, onState: (AuthState) -> Unit ->
            authWithSink.Update(
                beforeUpdateState = state,
                event = event,
                onState = onState
            ) { state: AuthState, event: AuthEvent, onState: (AuthState) -> Unit ->
                RealAuthLogic(state, event, onState)
            }
        }

    override val state: AuthState
        get() = authWithSink.state

    override val eventSink: suspend (AuthEvent) -> Unit = { event -> authWithSink.eventSink(event) }

    private val accessTokenRequestMap: MutableMap<AccessTokenRequest, User> = mutableMapOf()

    override fun authorizationHeader(accessTokenRequest: AccessTokenRequest): String {
        val user: User = accessTokenRequestMap[accessTokenRequest] ?: run {
            (state as LoggedInAccountsState).servers.values
                .map { server ->
                    server.users.values.first { user ->
                        user.accessTokenRequest == accessTokenRequest
                    }
                }.first()
        }
        return user.accessToken.let { " Bearer $it" }
    }
}
