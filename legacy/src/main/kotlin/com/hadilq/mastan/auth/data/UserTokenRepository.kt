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
package com.hadilq.mastan.auth.data

import androidx.datastore.core.DataStore
import com.hadilq.mastan.SingleIn
import com.hadilq.mastan.UserScope
import com.hadilq.mastan.shared.Api
import com.hadilq.mastan.shared.Token
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.*
import javax.inject.Inject

interface OauthRepository {
    suspend fun getCurrent(): String?
    suspend fun getAuthHeader(): String
}

val USER_KEY_PREFIX = "USER_TOKEN_FOR_"

@ContributesBinding(UserScope::class)
@SingleIn(UserScope::class)
class RealOauthRepository @Inject constructor(
    val accessTokenRequest: AccessTokenRequest,
    val api: Api,
    private val dataStore: DataStore<LoggedInAccounts>
) : OauthRepository {

    private val sourceOfTruth = SourceOfTruth.of<String, Token, String>(
        reader = {
            dataStore.data.map {
                val server = it.servers[accessTokenRequest.domain]
                val currentUser =
                    server?.users?.get(accessTokenRequest.code)
                currentUser?.accessToken
            }
        },
        writer = { _, token ->
            dataStore.updateData {
                val server = it.servers[accessTokenRequest.domain]!!
                val users = server.users.toMutableMap()
                val user = users.getOrDefault(
                    accessTokenRequest.code,
                    User(accessToken = token.accessToken, accessTokenRequest = accessTokenRequest)
                )

                users[accessTokenRequest.code] = user.copy(accessToken = token.accessToken)

                val serverResult = server.copy(users = users)
                val servers = it.servers.toMutableMap()
                servers[accessTokenRequest.domain!!] = serverResult

                it.copy(servers = servers)
            }
        }
    )

    private val userTokenStore: Store<String, String> =
        StoreBuilder.from(
            fetcher = Fetcher.of { key: String -> fetcher() },
            sourceOfTruth = sourceOfTruth
        ).build()

    override suspend fun getCurrent(): String = withContext(Dispatchers.IO) {
         userTokenStore.get(accessTokenRequest.domain!!)
    }

    override suspend fun getAuthHeader(): String {
        return " Bearer ${getCurrent()}"
    }

    suspend fun fetcher(): Token {
        return api.createAccessToken(
            domain = "https://${accessTokenRequest.domain}/oauth/token",
            clientId = accessTokenRequest.clientId,
            clientSecret = accessTokenRequest.clientSecret,
            redirectUri = accessTokenRequest.redirectUri,
            grantType = "authorization_code",
            code = accessTokenRequest.code,
            scope = "read write follow push"
        )
    }
}

