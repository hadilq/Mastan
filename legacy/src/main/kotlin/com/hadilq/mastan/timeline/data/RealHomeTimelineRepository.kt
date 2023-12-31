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
package com.hadilq.mastan.timeline.data

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


//interface HomeTimelineRepository {
//    suspend fun stream(feedRequest: FeedRequest): Flow<StoreResponse<List<StatusDB>>>
//    suspend fun paging(feedRequest: FeedRequest): PagingSource<Int, StatusDB>
//    suspend fun post(newStatus: NewStatus): Status
//}

enum class FeedType(val type: String, ) {
    Home("Home"),
    Local("Local"),

    Federated("Federated"),
    User("User"),
    UserWithMedia("UserWithMedia"),
    UserWithReplies("UserWithReplies"),
    Trending("Trending"),
    Bookmarks("Bookmarks"),
    Favorites("Favorites"),
    Hashtag("Hashtag");

    var tagName: String = ""
//    Mention("Mention")
//    object Favorites : FeedType("Favorites")
}

data class FeedRequest(val feedType: FeedType, val before: String)
