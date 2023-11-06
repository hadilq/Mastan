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

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Serializable
sealed interface AuthState

@Stable
@Serializable
object NoAuthState : AuthState

@Stable
@Serializable
data class LoggedInAccountsState(
    val servers: Map<String, Server> = emptyMap(),
    val currentUser: User,
) : AuthState

@Stable
@Serializable
data class Server(
    val domain: String,
    val users: Map<String, User> = emptyMap(),
)

@Stable
@Serializable
data class User(
    val accessTokenRequest: AccessTokenRequest,
    val domain: String,
    val accessToken: String,
)

@Stable
@Serializable
data class AccessTokenRequest(
    val domain: String,
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
    val code: String,
    val grantType: String = "authorization_code",
    val scope: String = "read write follow push",
)
