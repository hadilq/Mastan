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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

@Composable
fun RealAuthLogic(
    state: AuthState,
    event: AuthEvent,
    onState: (AuthState) -> Unit,
) {
    LaunchedEffect(state, event) {
        when (event) {
            is AddUserAuthEvent -> {
                addUser(state, onState, event)
            }

            is RemoveUserAuthEvent -> {
                removeUser(state, event.user.domain, event.user.accessTokenRequest.code, onState)
            }

            is RemoveUserByDetailsAuthEvent -> {
                removeUser(state, event.domain, event.code, onState)
            }

            NoAuthEvent -> Unit
        }
    }
}

private fun addUser(
    state: AuthState,
    onState: (AuthState) -> Unit,
    event: AddUserAuthEvent,
) {
    when (state) {
        is LoggedInAccountsState -> {
            onState(
                state.copy(
                    servers = state.servers.toMutableMap().apply {
                        val server = state.servers[event.user.domain]
                        if (server != null) {
                            set(
                                event.user.domain, server.copy(
                                    users = server.users.toMutableMap().apply {
                                        set(event.user.accessTokenRequest.code, event.user)
                                    }.toMap()
                                )
                            )
                        } else {
                            set(
                                event.user.domain, Server(
                                    domain = event.user.domain,
                                    users = mapOf(event.user.accessTokenRequest.code to event.user)
                                )
                            )
                        }
                    }.toMap()
                )
            )
        }

        NoAuthState -> {
            onState(
                LoggedInAccountsState(
                    servers = mapOf(
                        event.user.domain to Server(
                            event.user.domain,
                            users = mapOf(event.user.accessTokenRequest.code to event.user)
                        )
                    ),
                    currentUser = event.user,
                )
            )
        }
    }
}

private fun removeUser(
    state: AuthState,
    domain: String,
    code: String,
    onState: (AuthState) -> Unit,
) {
    when (state) {
        is LoggedInAccountsState -> {
            val server = state.servers[domain]
            server?.users?.toMutableMap()?.apply {
                remove(code)
            }?.toMap()?.let { users ->
                val servers = state.servers.toMutableMap().apply {
                    if (users.isEmpty()) {
                        remove(domain)
                    } else {
                        set(domain, server.copy(users = users))
                    }
                }.toMap()
                if (servers.isEmpty()) {
                    onState(NoAuthState)
                } else {
                    onState(state.copy(servers = servers))
                }
            }
        }

        NoAuthState -> Unit
    }
}
