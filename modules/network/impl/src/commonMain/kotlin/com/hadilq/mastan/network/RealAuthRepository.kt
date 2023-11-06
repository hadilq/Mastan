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
package com.hadilq.mastan.network

import com.hadilq.mastan.auth.AccessTokenRequest
import com.hadilq.mastan.auth.AddUserAuthEvent
import com.hadilq.mastan.auth.AuthLogicIo
import com.hadilq.mastan.auth.NoAuthState
import com.hadilq.mastan.auth.User
import com.hadilq.mastan.log.LogLogicIo

class RealAuthRepository(
    private val logger: LogLogicIo,
    private val api: Api,
    private val authLogicIo: AuthLogicIo,
) : AuthRepository {

    override suspend fun getAppToken(appTokenRequest: AppTokenRequest): OauthApplicationResponse {
        val newOauthApplication = api.createApplication(
            "https://${appTokenRequest.domain}/api/v1/apps",
            appTokenRequest.scopes,
            appTokenRequest.client_name,
            appTokenRequest.redirect_uris
        )
        return OauthApplicationResponse(
            newOauthApplication.clientId,
            newOauthApplication.clientSecret,
            newOauthApplication.redirectUri
        )
    }

    override suspend fun createAccessToken(accessTokenRequest: AccessTokenRequest) {
        val token = api.createAccessToken(
            domain = "https://${accessTokenRequest.domain}/oauth/token",
            clientId = accessTokenRequest.clientId,
            clientSecret = accessTokenRequest.clientSecret,
            redirectUri = accessTokenRequest.redirectUri,
            grantType = "authorization_code",
            code = accessTokenRequest.code,
            scope = "read write follow push"
        )
        val addUserAuthEvent = AddUserAuthEvent(
            User(
                accessTokenRequest,
                accessTokenRequest.domain,
                token.accessToken,
            )
        )
        logger.logDebug { "calling eventSink with $addUserAuthEvent" }
        authLogicIo.eventSink(addUserAuthEvent)
    }

    override val needToLogin: Boolean
        get() = authLogicIo.state is NoAuthState
}
