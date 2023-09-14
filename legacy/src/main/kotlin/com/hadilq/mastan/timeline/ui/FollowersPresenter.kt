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
package com.hadilq.mastan.timeline.ui

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.cachedIn
import com.hadilq.mastan.AuthRequiredScope
import com.hadilq.mastan.SingleIn
import com.hadilq.mastan.auth.data.OauthRepository
import com.hadilq.mastan.shared.UserApi
import com.hadilq.mastan.shared.headerLinks
import com.hadilq.mastan.timeline.data.Account
import com.hadilq.mastan.ui.util.Presenter
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

abstract class FollowerPresenter :
    Presenter<FollowerPresenter.FollowerEvent, FollowerPresenter.FollowerModel, FollowerPresenter.FollowerEffect>(
        FollowerModel(null)
    ) {
    sealed interface FollowerEvent

    data class Load(val accountId: String, val following: Boolean = true) : FollowerEvent

    data class FollowerModel(
        val accounts: Flow<PagingData<Account>>?
    )

    sealed interface FollowerEffect
}

@ContributesBinding(AuthRequiredScope::class, boundType = FollowerPresenter::class)
@SingleIn(AuthRequiredScope::class)
class RealFollowerPresenter @Inject constructor(
    val userApi: UserApi,
    val oauthRepository: OauthRepository
) : FollowerPresenter() {


    override suspend fun eventHandler(event: FollowerEvent, coroutineScope: CoroutineScope) {
        when (event) {
            is Load -> {
                val flow = Pager(
                    // Configure how data is loaded by passing additional properties to
                    // PagingConfig, such as prefetchDistance.
                    PagingConfig(pageSize = 40)
                ) {
                    val followersPagingSource = if (event.following) FollowingPagingSource(
                        userApi = userApi,
                        accountId = event.accountId,
                        oauthRepository = oauthRepository
                    ) else FollowersPagingSource(
                        userApi = userApi,
                        accountId = event.accountId,
                        oauthRepository = oauthRepository
                    )
                    followersPagingSource
                }
                    .flow
                    .cachedIn(coroutineScope)

                model = model.copy(accounts = flow)
            }
        }
    }
}


class FollowersPagingSource(
    val userApi: UserApi,
    val accountId: String,
    val oauthRepository: OauthRepository
) : PagingSource<String, Account>() {
    override suspend fun load(
        params: LoadParams<String>
    ): LoadResult<String, Account> {
        try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key
            val response = if (nextPageNumber == null) {
                userApi.followers(
                    authHeader = oauthRepository.getAuthHeader(),
                    accountId = accountId, since = nextPageNumber
                )
            } else {
                userApi.followers(
                    authHeader = oauthRepository.getAuthHeader(),
                    url = nextPageNumber
                )

            }

            val data = response.body()!!
            val links = headerLinks(response)
            return LoadResult.Page(
                data = data,
                prevKey = null,
                nextKey = links.second.toString()
            )
        } catch (e: Exception) {
            val cause = e.cause
            return LoadResult.Error(e)

        }
    }

    override fun getRefreshKey(state: PagingState<String, Account>): String? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.plus(-1)
        }
    }
}


class FollowingPagingSource(
    val userApi: UserApi,
    val accountId: String,
    val oauthRepository: OauthRepository
) : PagingSource<String, Account>() {
    override suspend fun load(
        params: LoadParams<String>
    ): LoadResult<String, Account> {
        try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key
            val response = if (nextPageNumber == null) {
                userApi.following(
                    authHeader = oauthRepository.getAuthHeader(),
                    accountId = accountId, since = nextPageNumber
                )
            } else {
                userApi.following(
                    authHeader = oauthRepository.getAuthHeader(),
                    url = nextPageNumber
                )

            }

            val data = response.body()!!
            val links = headerLinks(response)
            return LoadResult.Page(
                data = data,
                prevKey = null,
                nextKey = links.second.toString()
            )
        } catch (e: Exception) {
            val cause = e.cause
            return LoadResult.Error(e)

        }
    }

    override fun getRefreshKey(state: PagingState<String, Account>): String? {
        // Try to find the page key of the closest page to anchorPosition, from
        // either the prevKey or the nextKey, but you need to handle nullability
        // here:
        //  * prevKey == null -> anchorPage is the first page.
        //  * nextKey == null -> anchorPage is the last page.
        //  * both prevKey and nextKey null -> anchorPage is the initial page, so
        //    just return null.
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.plus(-1)
        }
    }
}