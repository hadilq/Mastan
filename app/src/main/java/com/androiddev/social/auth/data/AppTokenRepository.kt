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
package com.androiddev.social.auth.data

import com.androiddev.social.AppScope
import com.androiddev.social.SingleIn
import com.androiddev.social.shared.Api
import com.androiddev.social.timeline.data.NewOauthApplication
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.StoreBuilder
import org.mobilenativefoundation.store.store5.get
import javax.inject.Inject

interface AppTokenRepository {
    suspend fun getAppToken(appTokenRequest: AppTokenRequest): NewOauthApplication
}

@ContributesBinding(AppScope::class)
@SingleIn(AppScope::class)
class RealAppTokenRepository @Inject constructor(
    val api: Api,
) : AppTokenRepository {


    private val appTokenStore = StoreBuilder.from(fetcher = Fetcher.of { key: AppTokenRequest ->
        fetchAppToken(key)
    }).build()

    override suspend fun getAppToken(appTokenRequest: AppTokenRequest): NewOauthApplication {
        return appTokenStore.get(appTokenRequest)
    }

    suspend fun fetchAppToken(appTokenRequest: AppTokenRequest): NewOauthApplication = withContext(Dispatchers.IO) {
         api.createApplication(
            appTokenRequest.url,
            appTokenRequest.scopes,
            appTokenRequest.client_name,
            appTokenRequest.redirect_uris
        )
    }
}

data class AppTokenRequest(
    val url: String, val scopes: String, val client_name: String, val redirect_uris: String
)

@Serializable
data class AccessTokenRequest(
    val domain: String? = null,
    val clientId: String,
    val clientSecret: String,
    val redirectUri: String,
    val code: String,
    val grantType: String = "authorization_code",
    val scope: String = "read write follow push"
)
