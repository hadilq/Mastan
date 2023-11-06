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
package com.hadilq.mastan.loginflow

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import com.hadilq.mastan.auth.AccessTokenRequest
import com.hadilq.mastan.log.LocalLogLogicIo
import com.hadilq.mastan.log.LogLogicIo
import com.hadilq.mastan.logictreearchitecture.LocalLogicTreeArchitecture
import com.hadilq.mastan.logictreearchitecture.LogicTreeArchitecture
import com.hadilq.mastan.logictreearchitecture.singleInThisScope
import com.hadilq.mastan.navigation.LocalNavigationLogicIo
import com.hadilq.mastan.navigation.TimeLineNavigationEvent
import com.hadilq.mastan.network.AppTokenRequest
import com.hadilq.mastan.network.AuthRepository
import com.hadilq.mastan.network.LocalNetworkLogicIo
import com.hadilq.mastan.network.OauthApplicationResponse
import kotlinx.coroutines.launch
import java.net.URLEncoder

@Composable
fun RealLoginFlowLogic(
    state: LoginFlowState,
    event: LoginFlowEvent,
    onState: (LoginFlowState) -> Unit,
) {
    val logger = LocalLogLogicIo.current
    val network = LocalNetworkLogicIo.current
    val authRepository = singleInThisScope { network.authRepository }
    val navigationLogicIo = LocalNavigationLogicIo.current
    val logicTreeArchitectureOutput = LocalLogicTreeArchitecture.current
    val scope = rememberCoroutineScope()
    LaunchedEffect(state, event) {
        logger.logDebug { "login flow logic $state, $event" }
        when (event) {
            is NextPageServerSelectEvent -> {
                if (event.domain.isBlank()) {
                    onState(ServerErrorServerSelectState("Domain cannot be blank!"))
                } else {
                    onState(ServerSignInState(event.domain))
                    scope.launch {
                        createApplicationToken(
                            logger,
                            event.domain,
                            authRepository,
                            logicTreeArchitectureOutput,
                            onState
                        )
                    }
                }
            }

            is ResultUriSignInEvent -> {
                val query = event.uri.query
                when {
                    query.contains("error=") -> {
                        val error = query.replace("error=", "")
                        if (state is CreatedApplicationSignInState) {
                            onState(OAuthFailedToSignInState(state.domain, error))
                        }
                    }

                    query.contains("code=") -> {
                        val code = query.replace("code=", "")
                        if (state is CreatedApplicationSignInState) {
                            val accessTokenRequest = AccessTokenRequest(
                                code = code,
                                clientId = state.clientId,
                                clientSecret = state.clientSecret,
                                redirectUri = state.redirectUri,
                                domain = state.domain,
                            )

                            scope.launch {
                                val result = logicTreeArchitectureOutput.runCatchingOnIO {
                                    authRepository.createAccessToken(accessTokenRequest)
                                }
                                if (result.isSuccess) {
                                    logger.logDebug { "createAccessToken result:$result" }
                                    navigationLogicIo.eventSink(TimeLineNavigationEvent)
                                } else {
                                    onState(
                                        OAuthFailedToSignInState(
                                            state.domain,
                                            result.exceptionOrNull()
                                                ?.let { it.localizedMessage ?: it.message }
                                                ?: "Cannot get the access token!"
                                        )
                                    )
                                }
                            }
                        } else {
                            logger.wrongState { "state: $state, event: $event" }
                        }
                    }
                }
            }

            WebViewCancelSignInEvent -> {
                navigationLogicIo.eventSink(TimeLineNavigationEvent)
            }

            CreatedApplicationErrorRetrySignInEvent,
            OAuthFailedRetrySignInEvent,
            -> {
                val domain = when (state) {
                    is CreatedApplicationErrorSignInState -> {
                        state.serverDomain
                    }

                    is OAuthFailedToSignInState -> {
                        state.serverDomain
                    }

                    else -> {
                        logger.wrongState { "state: $state, event: $event" }
                        null
                    }
                }
                if (domain != null) {
                    scope.launch {
                        createApplicationToken(
                            logger,
                            domain,
                            authRepository,
                            logicTreeArchitectureOutput,
                            onState
                        )
                    }
                }
            }

            BackSignInEvent -> {
                onState(InitialLoginFlowState)
            }

            NoLoginFlowEvent -> {}
        }
    }
}

private suspend fun createApplicationToken(
    logger: LogLogicIo,
    domain: String,
    authRepository: AuthRepository,
    logicTreeArchitecture: LogicTreeArchitecture,
    onState: (LoginFlowState) -> Unit,
) {
    val params = ApplicationBody(baseUrl = domain)
    logger.logDebug("before request")
    val result = logicTreeArchitecture.runCatchingOnIO {
        authRepository.getAppToken(
            AppTokenRequest(
                domain,
                params.scopes,
                params.clientName,
                params.redirectUris()
            )
        )
    }
    logger.logDebug { "CreatedApplicationErrorSignInState result:$result" }
    if (result.isSuccess) {
        val value: OauthApplicationResponse = result.getOrThrow()
        onState(
            CreatedApplicationSignInState(
                domain = domain,
                redirectUri = value.redirectUri,
                clientId = value.clientId,
                clientSecret = value.clientSecret,
                oauthAuthorizeUrl = createOAuthAuthorizeUrl(value, domain)
            )
        )
    } else {
        onState(
            CreatedApplicationErrorSignInState(
                domain,
                result.exceptionOrNull()
                    ?.let { it.localizedMessage ?: it.message }
                    ?: "Cannot create application for OAuth sign in! Retry!"
            )
        )
    }
}

private fun createOAuthAuthorizeUrl(token: OauthApplicationResponse, server: String): String {
    val b = StringBuilder().apply {
        append("https://${server}")
        append("/oauth/authorize?client_id=${token.clientId}")
        append("&scope=${"read write follow push".encode()}")
        append("&redirect_uri=${token.redirectUri.encode()}")
        append("&response_type=code")
    }
    return b.toString()
}

fun String.encode(): String = URLEncoder.encode(this, "UTF-8")

data class ApplicationBody(
    val baseUrl: String = "androiddev.social",
    val scopes: String = "read write follow push",
    val clientName: String = "Mastan",
    val redirectScheme: String = "mastanoauth2redirect://",
) {
    fun redirectUris(): String = redirectScheme + baseUrl
}
