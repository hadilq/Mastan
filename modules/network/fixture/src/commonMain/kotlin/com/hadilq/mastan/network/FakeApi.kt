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

import com.hadilq.mastan.network.dto.NewOauthApplication
import com.hadilq.mastan.network.dto.Token

class FakeApi(): Api {

    override suspend fun createApplication(
        url: String,
        scopes: String,
        client_name: String,
        redirect_uris: String,
    ): NewOauthApplication {
        TODO("Not yet implemented")
    }

    override suspend fun createAccessToken(
        domain: String,
        clientId: String,
        clientSecret: String,
        redirectUri: String,
        grantType: String,
        code: String,
        scope: String,
    ): Token {
        TODO("Not yet implemented")
    }
}