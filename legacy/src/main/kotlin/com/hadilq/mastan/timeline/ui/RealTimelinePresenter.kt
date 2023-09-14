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

import androidx.compose.material3.ColorScheme
import androidx.paging.*
import com.hadilq.mastan.AuthRequiredScope
import com.hadilq.mastan.SingleIn
import com.hadilq.mastan.auth.data.OauthRepository
import com.hadilq.mastan.shared.UserApi
import com.hadilq.mastan.shared.headerLinks
import com.hadilq.mastan.timeline.data.*
import com.hadilq.mastan.timeline.ui.model.UI
import com.hadilq.mastan.ui.util.Presenter
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

@ExperimentalPagingApi
@ContributesBinding(AuthRequiredScope::class, boundType = TimelinePresenter::class)
@SingleIn(AuthRequiredScope::class)
class RealTimelinePresenter @Inject constructor(
    val timelineRemoteMediators: @JvmSuppressWildcards Set<TimelineRemoteMediator>,
    val hashtagRemoteMediatorFactory: HashtagRemoteMediatorFactory,
    val statusDao: StatusDao,
    val api: UserApi,
    val oauthRepository: OauthRepository,
    val accountRepository: AccountRepository,
) : TimelinePresenter() {
    val scope = CoroutineScope(Dispatchers.Main)
    private val pagingConfig = PagingConfig(
        pageSize = 20,
        initialLoadSize = 30,
        prefetchDistance = 10
    )


    val homeFlow: Flow<PagingData<StatusDB>> = Pager(
        config = pagingConfig,
        remoteMediator = timelineRemoteMediators
            .filterIsInstance<HomeTimelineRemoteMediator>()
            .single()
    ) {
        statusDao.getTimeline(FeedType.Home.type)
    }
        .flow.cachedIn(scope)
    val localFlow = Pager(
        config = pagingConfig,
        remoteMediator = timelineRemoteMediators
            .filterIsInstance<LocalTimelineRemoteMediator>()
            .single()
    ) {
        statusDao.getTimeline(FeedType.Local.type)
    }
        .flow.cachedIn(scope)
    val federatedFlow = Pager(
        config = pagingConfig,
        remoteMediator = timelineRemoteMediators
            .filterIsInstance<FederatedTimelineRemoteMediator>()
            .single()
    ) {
        statusDao.getTimeline(FeedType.Federated.type)
    }
        .flow.cachedIn(scope)
    val trendingFlow = Pager(
        config = pagingConfig,
        remoteMediator = timelineRemoteMediators
            .filterIsInstance<TrendingRemoteMediator>()
            .single()
    ) {
        statusDao.getTimeline(FeedType.Trending.type)
    }
        .flow.cachedIn(scope)

    val bookmarksFlow: Flow<PagingData<Status>> = Pager(
        config = pagingConfig,
    ) {
        BookmarksPagingSource(userApi = api, oauthRepository = oauthRepository)
    }
        .flow.cachedIn(scope)

    val favoritesFlow: Flow<PagingData<Status>> = Pager(
        config = pagingConfig,
    ) {
        FavoritesPagingSource(userApi = api, oauthRepository = oauthRepository)
    }
        .flow.cachedIn(scope)


    override suspend fun eventHandler(event: HomeEvent, scope: CoroutineScope) {
        when (event) {
            is Load -> {
                model = model.copy(currentAccount = accountRepository.getCurrent())
                when (event.feedType) {
                    FeedType.Home -> {
                        model = model.copy(homeStatuses = homeFlow.map {
                            it.map {
                                it.mapStatus(event.colorScheme)
                            }
                        })
                    }

                    FeedType.Local -> {
                        model = model.copy(localStatuses = localFlow.map {
                            it.map {
                                it.mapStatus(event.colorScheme)
                            }
                        })
                    }

                    FeedType.Federated -> {
                        model = model.copy(federatedStatuses = federatedFlow.map {
                            it.map {
                                it.mapStatus(event.colorScheme)
                            }
                        })
                    }

                    FeedType.Trending -> {
                        model = model.copy(trendingStatuses = trendingFlow.map {
                            it.map {
                                it.mapStatus(event.colorScheme)
                            }
                        })

                    }

                    FeedType.UserWithReplies -> {
                        val userWithRepliesRemoteMediator =
                            timelineRemoteMediators.filterIsInstance<UserWithRepliesRemoteMediator>()
                                .single()
                        userWithRepliesRemoteMediator.accountId = event.accountId!!

                        val flow = Pager(
                            config = PagingConfig(
                                pageSize = 10,
                                initialLoadSize = 10,
                                prefetchDistance = 10
                            ),
                            remoteMediator = userWithRepliesRemoteMediator
                        ) {
                            statusDao.getUserTimeline(
                                FeedType.UserWithReplies,
                                event.accountId
                            )
                        }.flow

                        val remoteMediatorWithMedia =
                            timelineRemoteMediators.filterIsInstance<UserWithMediaRemoteMediator>()
                                .single()
                        remoteMediatorWithMedia.accountId = event.accountId

                        val flowWithMedia = Pager(
                            config = pagingConfig,
                            remoteMediator = remoteMediatorWithMedia
                        ) {
                            statusDao.getUserTimeline(FeedType.UserWithMedia, event.accountId)
                        }.flow

                        val remoteMediator =
                            timelineRemoteMediators.filterIsInstance<UserRemoteMediator>()
                                .single()
                        remoteMediator.accountId = event.accountId

                        val flow2 = Pager(
                            config = pagingConfig,
                            remoteMediator = remoteMediator
                        ) {
                            statusDao.getUserTimeline(FeedType.User, event.accountId)
                        }.flow


                        model = model.copy(
                            userWithRepliesStatuses = flow.map {
                                it.map { it.mapStatus(event.colorScheme) }
                            }.cachedIn(scope),
                            userWithMediaStatuses = flowWithMedia.map {
                                it.map { it.mapStatus(event.colorScheme) }
                            }.cachedIn(scope),
                            userStatuses = flow2.map {
                                it.map { it.mapStatus(event.colorScheme) }
                            }.cachedIn(scope)
                        )

                    }

                    FeedType.Bookmarks -> {
                        model = model.copy(bookmarkedStatuses = bookmarksFlow.map {
                            it.map {
                                it.toStatusDb(FeedType.Bookmarks)
                                    .mapStatus(colorScheme = event.colorScheme)
                            }
                        })
                    }

                    FeedType.Favorites -> {
                        model = model.copy(favoriteStatuses = favoritesFlow.map {
                            it.map {
                                it.toStatusDb(FeedType.Favorites)
                                    .mapStatus(colorScheme = event.colorScheme)
                            }
                        })
                    }

                    FeedType.Hashtag -> {
                        model = model.copy(hashtagStatuses = Pager(
                            config = pagingConfig,
                            remoteMediator = hashtagRemoteMediatorFactory.from(event.feedType.tagName)
                        )
                        {
                            statusDao.getTimeline(FeedType.Hashtag.type + event.feedType.tagName)
                        }
                            .flow.cachedIn(scope).map {
                                it.map {
                                    it.mapStatus(colorScheme = event.colorScheme)
                                }
                            })
                    }

                    else -> {}
                }
            }
        }
    }
}


abstract class TimelinePresenter :
    Presenter<TimelinePresenter.HomeEvent, TimelinePresenter.HomeModel, TimelinePresenter.HomeEffect>(
        HomeModel(true)
    ) {
    sealed interface HomeEvent
    data class Load(
        val feedType: FeedType,
        val accountId: String? = null,
        val colorScheme: ColorScheme
    ) : HomeEvent


    data class HomeModel(
        val loading: Boolean,
        val homeStatuses: Flow<PagingData<UI>>? = null,
        val currentAccount: Account? = null,
        val federatedStatuses: Flow<PagingData<UI>>? = null,
        val trendingStatuses: Flow<PagingData<UI>>? = null,
        val bookmarkedStatuses: Flow<PagingData<UI>>? = null,
        val favoriteStatuses: Flow<PagingData<UI>>? = null,
        val hashtagStatuses: Flow<PagingData<UI>>? = null,
        val localStatuses: Flow<PagingData<UI>>? = null,
        val userStatuses: Flow<PagingData<UI>>? = null,
        val userWithMediaStatuses: Flow<PagingData<UI>>? = null,
        val userWithRepliesStatuses: Flow<PagingData<UI>>? = null,
    )

    sealed interface HomeEffect
}


class BookmarksPagingSource(
    val userApi: UserApi,
    val oauthRepository: OauthRepository,
) : PagingSource<String, Status>() {
    override suspend fun load(
        params: LoadParams<String>
    ): LoadResult<String, Status> {
        try {

            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key
            //we loaded all values
//            if (nextPageNumber == "end") return LoadResult.Error(NoSuchElementException())
            val response = if (nextPageNumber == null) {
                userApi.bookmarkedStatuses(
                    authHeader = oauthRepository.getAuthHeader(),
                )
            } else {
                userApi.bookmarkedStatuses(
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

    override fun getRefreshKey(state: PagingState<String, Status>): String? {
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


class FavoritesPagingSource(
    val userApi: UserApi,
    val oauthRepository: OauthRepository
) : PagingSource<String, Status>() {
    override suspend fun load(
        params: LoadParams<String>
    ): LoadResult<String, Status> {
        try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key
            val response = if (nextPageNumber == null) {
                userApi.favorites(
                    authHeader = oauthRepository.getAuthHeader(),
                )
            } else {
                userApi.favorites(
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

    override fun getRefreshKey(state: PagingState<String, Status>): String? {
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