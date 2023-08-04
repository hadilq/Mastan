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
import com.androiddev.social.auth.data.OauthRepository
import com.androiddev.social.shared.UserApi
import com.androiddev.social.timeline.ui.ConversationReplyRearrangerMediator
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.SourceOfTruth
import org.mobilenativefoundation.store.store5.StoreBuilder
import org.mobilenativefoundation.store.store5.get
import java.net.URLDecoder
import javax.inject.Inject

interface StatusRepository {
    suspend fun get(feedStoreRequest: FeedStoreRequest): StatusDB
}

@ContributesBinding(UserScope::class)
@SingleIn(UserScope::class)
class RealStatusRepository @Inject constructor(
    val statusDao: StatusDao,
    val api: UserApi,
    val oauthRepository: OauthRepository,
) : StatusRepository {

    private val fetcher = Fetcher.of { status: FeedStoreRequest ->
        api.getStatus(oauthRepository.getAuthHeader(), status.remoteId)
    }

    private val sourceOfTruth = SourceOfTruth.of<FeedStoreRequest, Status, StatusDB>(
        reader = {
            val statusBy = statusDao.getStatusBy(it.remoteId)
            statusBy
        },
        writer = { key, status ->
            statusDao.insertAll(listOf(status.toStatusDb(key.feedType)))
        }
    )

    private val store = StoreBuilder.from(
        fetcher = fetcher,
        sourceOfTruth = sourceOfTruth
    ).build()

    override suspend fun get(feedStoreRequest: FeedStoreRequest): StatusDB = withContext(Dispatchers.IO) {
        store.get(feedStoreRequest)
    }
}

data class FeedStoreRequest(
    val remoteId: String,
    val feedType: FeedType = FeedType.Home,
)