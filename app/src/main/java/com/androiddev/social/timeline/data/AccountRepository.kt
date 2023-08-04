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
package com.androiddev.social.timeline.data

import com.androiddev.social.SingleIn
import com.androiddev.social.UserScope
import com.androiddev.social.auth.data.AccessTokenRequest
import com.androiddev.social.auth.data.OauthRepository
import com.androiddev.social.shared.UserApi
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.StoreBuilder
import org.mobilenativefoundation.store.store5.StoreRequest
import org.mobilenativefoundation.store.store5.fresh
import org.mobilenativefoundation.store.store5.get
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject

interface AccountRepository {
    suspend fun get(accountId: String): Account
    suspend fun clear(accountId: String)
    suspend fun fresh(accountId: String)
    suspend fun subscribe(accountId: String): Flow<Account>
    suspend fun getCurrent(): Account?
}

@ContributesBinding(UserScope::class)
@SingleIn(UserScope::class)
class RealAccountRepository @Inject constructor(
    val api: UserApi,
    val oauthRepository: OauthRepository,
) : AccountRepository {
    private val fetcher = Fetcher.of { accountId: String ->
        val token = oauthRepository.getAuthHeader()
        val account = api.account(authHeader = token, accountId = accountId)
        val relationships = api.relationships(token, listOf(account.id))
        val relationship = relationships.firstOrNull()
        account.copy(
            isFollowed = relationship?.following == true,
            muting = relationship?.muting == true,
            blocking = relationship?.blocking == true,
        )
    }

    private val store = StoreBuilder.from(
        fetcher = fetcher,
    ).build()

    private var currentAccount: AtomicReference<Account?> = AtomicReference(null)

    override suspend fun get(accountId: String): Account = store.get(accountId)

    override suspend fun clear(accountId: String) {
        store.clear(accountId)
    }

    override suspend fun fresh(accountId: String) {
        store.fresh(accountId)
    }

    override suspend fun subscribe(accountId: String): Flow<Account> {
        return store.stream(StoreRequest.cached(key = accountId, refresh = false))
            .mapNotNull { it.dataOrNull() }
    }

    override suspend fun getCurrent(): Account? {
        return currentAccount.get() ?: run {
            val result = kotlin.runCatching {
                api.accountVerifyCredentials(authHeader = oauthRepository.getAuthHeader())
            }
            result.getOrNull()?.let {
                currentAccount.set(get(it.id))
            }
            currentAccount.get()
        }
    }
}