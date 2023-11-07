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
package com.hadilq.mastan.search

import com.hadilq.mastan.SingleIn
import com.hadilq.mastan.UserScope
import com.hadilq.mastan.network.UserApi
import com.hadilq.mastan.network.dto.SearchResult
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.flow.Flow
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.StoreBuilder
import org.mobilenativefoundation.store.store5.StoreRequest
import org.mobilenativefoundation.store.store5.StoreResponse
import javax.inject.Inject
@ContributesBinding(UserScope::class)
@SingleIn(UserScope::class)
class RealSearchRepository @Inject constructor(
    private val userApi: UserApi,
) : SearchRepository {

    val store = StoreBuilder.from(
        Fetcher.of { searchTerm: String ->
            userApi.search(
                searchTerm = searchTerm
            )
        }
    ).build()


    override fun data(searchTerm: String): Flow<StoreResponse<SearchResult>> {
        // return data from memory or disk cache AND refresh from network
        // think of this as the classic "double tap"
        // NOTE: if network is not available, return cached data only
        return store.stream(StoreRequest.cached(searchTerm, refresh = true))
    }
}

interface SearchRepository {

    fun data(searchTerm: String): Flow<StoreResponse<SearchResult>>
}
