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

class FakeAuthRepository(
    val getAppTokenResponse: OauthApplicationResponse? = null,
    val createAccessTokenResponse: Boolean = false,
    val needToLoginResponse: Boolean = true,
) : AuthRepository {

    override suspend fun getAppToken(appTokenRequest: AppTokenRequest): OauthApplicationResponse {
        getAppTokenCalls.add(appTokenRequest)
        return getAppTokenResponse ?: throw IllegalStateException("getAppTokenResponse is error!")
    }

    override suspend fun createAccessToken(accessTokenRequest: AccessTokenRequest) {
        createAccessTokenCalls.add(accessTokenRequest)
        if (!createAccessTokenResponse) {
            throw IllegalStateException("createAccessTokenResponse is error!")
        }
    }

    override val needToLogin: Boolean
        get() = needToLoginResponse

    val getAppTokenCalls = mutableListOf<AppTokenRequest>()
    val createAccessTokenCalls = mutableListOf<AccessTokenRequest>()
}
