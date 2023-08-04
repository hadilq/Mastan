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

import androidx.paging.*
import androidx.room.withTransaction
import com.androiddev.social.SingleIn
import com.androiddev.social.UserScope
import com.androiddev.social.auth.data.OauthRepository
import com.androiddev.social.shared.UserApi
import com.androiddev.social.timeline.ui.TimelineReplyRearrangerMediator
import com.squareup.anvil.annotations.ContributesMultibinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


@OptIn(ExperimentalPagingApi::class)
abstract class TimelineRemoteMediator : RemoteMediator<Int, StatusDB>() {
    abstract override suspend fun load(
        loadType: LoadType, state: PagingState<Int, StatusDB>
    ): MediatorResult

}

@ExperimentalPagingApi
@ContributesMultibinding(UserScope::class, boundType = TimelineRemoteMediator::class)
@SingleIn(UserScope::class)
class LocalTimelineRemoteMediator @Inject constructor(
    private val dao: StatusDao,
    private val database: AppDatabase,
    private val userApi: UserApi,
    private val oauthRepository: OauthRepository,
    private val timelineReplyRearrangerMediator: TimelineReplyRearrangerMediator,
) : TimelineRemoteMediator() {

    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, StatusDB>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> state.lastItemOrNull()?.originalId
            }

            val response = userApi.getLocalTimeline(
                authHeader = oauthRepository.getAuthHeader(),
                since = loadKey
            )

            val statuses = timelineReplyRearrangerMediator.rearrangeTimeline(
                response.map { it.toStatusDb(FeedType.Local) },
            )

            val collection = withContext(Dispatchers.Default) {
                statuses.toCollection(mutableListOf())
            }
            database.withTransaction {
                dao.insertAll(collection)
            }

            MediatorResult.Success(
                endOfPaginationReached = false
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}

@ExperimentalPagingApi
@ContributesMultibinding(UserScope::class, boundType = TimelineRemoteMediator::class)
@SingleIn(UserScope::class)
class HomeTimelineRemoteMediator @Inject constructor(
    private val dao: StatusDao,
    private val database: AppDatabase,
    private val userApi: UserApi,
    private val oauthRepository: OauthRepository,
    private val timelineReplyRearrangerMediator: TimelineReplyRearrangerMediator,
) : TimelineRemoteMediator() {
    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, StatusDB>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> state.lastItemOrNull()?.originalId
            }

            val response = userApi.getHomeTimeline(
                authHeader = oauthRepository.getAuthHeader(),
                since = loadKey
            )

            val statuses = timelineReplyRearrangerMediator.rearrangeTimeline(
                response.map { it.toStatusDb(FeedType.Home) },
            )
            val collection = withContext(Dispatchers.Default) {
                statuses.toCollection(mutableListOf())
            }
            database.withTransaction {
                dao.insertAll(collection)
            }

            MediatorResult.Success(
                endOfPaginationReached = false
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}


@ExperimentalPagingApi
@ContributesMultibinding(UserScope::class, boundType = TimelineRemoteMediator::class)
@SingleIn(UserScope::class)
class FederatedTimelineRemoteMediator @Inject constructor(
    private val dao: StatusDao,
    private val database: AppDatabase,
    private val userApi: UserApi,
    private val oauthRepository: OauthRepository,
    private val timelineReplyRearrangerMediator: TimelineReplyRearrangerMediator,
) : TimelineRemoteMediator() {

    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, StatusDB>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> state.lastItemOrNull()?.originalId
            }

            val response = userApi.getLocalTimeline(
                authHeader = oauthRepository.getAuthHeader(),
                since = loadKey,
                localOnly = false
            )

            val statuses = timelineReplyRearrangerMediator.rearrangeTimeline(
                response.map { it.toStatusDb(FeedType.Federated) },
            )
            val collection = withContext(Dispatchers.Default) {
                statuses.toCollection(mutableListOf())
            }
            database.withTransaction {
                dao.insertAll(collection)
            }

            MediatorResult.Success(
                endOfPaginationReached = false
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}

@ExperimentalPagingApi
@ContributesMultibinding(UserScope::class, boundType = TimelineRemoteMediator::class)
@SingleIn(UserScope::class)
class TrendingRemoteMediator @Inject constructor(
    private val dao: StatusDao,
    private val database: AppDatabase,
    private val userApi: UserApi,
    private val oauthRepository: OauthRepository,
    private val timelineReplyRearrangerMediator: TimelineReplyRearrangerMediator,
) : TimelineRemoteMediator() {
    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, StatusDB>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> state.anchorPosition
            }

            val response =
                userApi.getTrending(
                    authHeader = oauthRepository.getAuthHeader(),
                    offset = loadKey.toString()
                )

            val statuses = timelineReplyRearrangerMediator.rearrangeTimeline(
                response.map { it.toStatusDb(FeedType.Trending) },
            )
            val collection = withContext(Dispatchers.Default) {
                statuses.toCollection(mutableListOf())
            }
            database.withTransaction {
                dao.insertAll(collection)
            }

            MediatorResult.Success(
                endOfPaginationReached = false
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}


@ExperimentalPagingApi
@ContributesMultibinding(UserScope::class, boundType = TimelineRemoteMediator::class)
@SingleIn(UserScope::class)
class UserRemoteMediator @Inject constructor(
    private val dao: StatusDao,
    private val database: AppDatabase,
    private val userApi: UserApi,
    private val oauthRepository: OauthRepository,
    private val accountRepository: AccountRepository,
    private val timelineReplyRearrangerMediator: TimelineReplyRearrangerMediator,
) : TimelineRemoteMediator() {
    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH
    lateinit var accountId: String
    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, StatusDB>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> state.lastItemOrNull()?.originalId
            }

            val response =
                userApi.accountStatuses(
                    authHeader = oauthRepository.getAuthHeader(),
                    accountId = accountId,
                    since = loadKey,
                    excludeReplies = true
                )

            if (response.isEmpty()) {
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            val statuses = timelineReplyRearrangerMediator.rearrangeTimeline(
                response.map { it.toStatusDb(FeedType.User) },
            )
            val collection = withContext(Dispatchers.Default) {
                statuses.toCollection(mutableListOf())
            }
            database.withTransaction {
                dao.insertAll(collection)
            }

            MediatorResult.Success(
                endOfPaginationReached = false
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}

@ExperimentalPagingApi
@ContributesMultibinding(UserScope::class, boundType = TimelineRemoteMediator::class)
@SingleIn(UserScope::class)
class UserWithMediaRemoteMediator @Inject constructor(
    private val dao: StatusDao,
    private val database: AppDatabase,
    private val userApi: UserApi,
    private val oauthRepository: OauthRepository,
    private val accountRepository: AccountRepository,
    private val timelineReplyRearrangerMediator: TimelineReplyRearrangerMediator,
) : TimelineRemoteMediator() {
    lateinit var accountId: String

    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH
    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, StatusDB>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> state.lastItemOrNull()?.originalId
            }

            val response =
                userApi.accountStatuses(
                    authHeader = oauthRepository.getAuthHeader(),
                    accountId = accountRepository.get(accountId).id,
                    onlyMedia = true,
                    since = loadKey
                )

            if (response.isEmpty()) {
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            val statuses = timelineReplyRearrangerMediator.rearrangeTimeline(
                response.map { it.toStatusDb(FeedType.UserWithMedia) },
            )
            val collection = withContext(Dispatchers.Default) {
                statuses.toCollection(mutableListOf())
            }
            database.withTransaction {
                dao.insertAll(collection)
            }

            MediatorResult.Success(
                endOfPaginationReached = false
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}

@ExperimentalPagingApi
@ContributesMultibinding(UserScope::class, boundType = TimelineRemoteMediator::class)
@SingleIn(UserScope::class)
class UserWithRepliesRemoteMediator @Inject constructor(
    private val dao: StatusDao,
    private val database: AppDatabase,
    private val userApi: UserApi,
    private val oauthRepository: OauthRepository,
    private val accountRepository: AccountRepository,
    private val timelineReplyRearrangerMediator: TimelineReplyRearrangerMediator,
) : TimelineRemoteMediator() {
    lateinit var accountId: String

    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH
    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, StatusDB>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> state.lastItemOrNull()?.originalId
            }

            val response =
                userApi.accountStatuses(
                    authHeader = oauthRepository.getAuthHeader(),
                    accountId = accountRepository.get(accountId).id,
                    since = loadKey,
                    excludeReplies = false
                )

            if (response.isEmpty()) {
                return MediatorResult.Success(endOfPaginationReached = true)
            }

            val statuses = timelineReplyRearrangerMediator.rearrangeTimeline(
                response.map { it.toStatusDb(FeedType.UserWithReplies) },
            )
            val collection = withContext(Dispatchers.Default) {
                statuses.toCollection(mutableListOf())
            }
            database.withTransaction {
                dao.insertAll(collection)
            }

            MediatorResult.Success(
                endOfPaginationReached = response.isEmpty()
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}

@SingleIn(UserScope::class)
class HashtagRemoteMediatorFactory @Inject constructor(
    private val dao: StatusDao,
    private val database: AppDatabase,
    private val userApi: UserApi,
    private val oauthRepository: OauthRepository,
    private val timelineReplyRearrangerMediator: TimelineReplyRearrangerMediator,
) {

    @OptIn(ExperimentalPagingApi::class)
    fun from(hashtag: String) =
        HashtagRemoteMediator(
            dao,
            database,
            userApi,
            oauthRepository,
            timelineReplyRearrangerMediator,
            hashtag
        )
}

@ExperimentalPagingApi
@SingleIn(UserScope::class)
class HashtagRemoteMediator(
    private val dao: StatusDao,
    private val database: AppDatabase,
    private val userApi: UserApi,
    private val oauthRepository: OauthRepository,
    private val timelineReplyRearrangerMediator: TimelineReplyRearrangerMediator,
    private val hashtag: String,
) : TimelineRemoteMediator() {

    override suspend fun initialize(): InitializeAction = InitializeAction.SKIP_INITIAL_REFRESH

    override suspend fun load(
        loadType: LoadType, state: PagingState<Int, StatusDB>
    ): MediatorResult {
        return try {
            val loadKey = when (loadType) {
                LoadType.REFRESH -> null
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> state.lastItemOrNull()?.originalId
            }

            val response = userApi.getTagTimeline(
                authHeader = oauthRepository.getAuthHeader(),
                since = loadKey,
                tag = hashtag
            )

            val statuses = timelineReplyRearrangerMediator.rearrangeTimeline(
                response.map { it.toStatusDb(FeedType.Hashtag) }
                    .map { it.copy(type = it.type + hashtag) },
            )

            val collection = withContext(Dispatchers.Default) {
                statuses.toCollection(mutableListOf())
            }
            database.withTransaction {
                dao.insertAll(collection)
            }

            MediatorResult.Success(
                endOfPaginationReached = false
            )
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }
}
