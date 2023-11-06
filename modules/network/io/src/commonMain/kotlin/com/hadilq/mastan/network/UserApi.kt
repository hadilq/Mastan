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

import com.hadilq.mastan.network.dto.Account
import com.hadilq.mastan.network.dto.NewStatus
import com.hadilq.mastan.network.dto.Notification
import com.hadilq.mastan.network.dto.Poll
import com.hadilq.mastan.network.dto.Relationship
import com.hadilq.mastan.network.dto.SearchResult
import com.hadilq.mastan.network.dto.Status
import com.hadilq.mastan.network.dto.StatusNode
import com.hadilq.mastan.network.dto.Tag
import com.hadilq.mastan.network.dto.UploadIds
import com.hadilq.mastan.network.dto.WithHeaders


interface UserApi {
    suspend fun getHomeTimeline(
        limit: String = "40",
        since: String?,
    ): List<Status>

    suspend fun getTagTimeline(
        tag: String,
        limit: String = "40",
        since: String?,
    ): List<Status>

    suspend fun getLocalTimeline(
        localOnly: Boolean = true,
        limit: String = "40",
        since: String? = null,
    ): List<Status>

    suspend fun getTrending(
        limit: String = "40",
        offset: String?,
    ): List<Status>

    suspend fun conversations(
        limit: String = "40",
        types: Set<String>? = setOf("mention"),
    ): List<Notification>

    suspend fun search(
        searchTerm: String,
        limit: String = "40",
        resolve: Boolean = false,
        following: Boolean = false,
    ): SearchResult

    suspend fun notifications(
        offset: String?,
        limit: String = "40",
    ): List<Notification>

    suspend fun newStatus(
        status: NewStatus,
    ): Status

    suspend fun getStatus(
        id: String,
    ): Status

    suspend fun accountStatuses(
        accountId: String,
        since: String?,
        excludeReplies: Boolean? = null,
        onlyMedia: Boolean? = null,
        pinned: Boolean? = null,
        limit: Int? = 40,
    ): List<Status>

    suspend fun bookmarkedStatuses(
        limit: Int? = 40,
    ): WithHeaders<List<Status>>

    suspend fun bookmarkedStatuses(
        url: String,
    ): WithHeaders<List<Status>>

    suspend fun favorites(
        limit: Int? = 40,
    ): WithHeaders<List<Status>>

    suspend fun favorites(
        url: String,
    ): WithHeaders<List<Status>>

    suspend fun followers(
        accountId: String,
        since: String?,
        limit: Int? = 40,
    ): WithHeaders<List<Account>>

    suspend fun followers(
        url: String,
    ): WithHeaders<List<Account>>

    suspend fun following(
        accountId: String,
        since: String?,
        limit: Int? = 40,
    ): WithHeaders<List<Account>>

    suspend fun following(
        url: String,
    ): WithHeaders<List<Account>>

    suspend fun relationships(
        accountIds: List<String>,
    ): List<Relationship>

    suspend fun account(
        accountId: String,
    ): Account

    suspend fun conversation(
        statusId: String,
    ): StatusNode

    suspend fun boostStatus(
        id: String,
    ): Status

    suspend fun unBoostStatus(
        id: String,
    ): Status

    suspend fun followAccount(
        accountId: String,
    ): Relationship

    suspend fun unfollowAccount(
        accountId: String,
    ): Relationship

    suspend fun followTag(
        name: String,
    ): Tag

    suspend fun upload(
        fileMultipartBodyPart: Any,
        descriptionMultipartBodyPart: Any? = null,
        focusMultipartBodyPart: Any? = null,
    ): UploadIds

    suspend fun unfollowTag(
        name: String,
    ): Tag

    suspend fun favouriteStatus(
        id: String,
    ): Status

    suspend fun unfavouriteStatus(
        id: String,
    ): Status

    suspend fun bookmarkStatus(
        id: String,
    ): Status

    suspend fun accountVerifyCredentials(): Account

    suspend fun followedTags(): List<Tag>

    suspend fun viewPoll(
        id: String,
    ): Poll

    suspend fun votePoll(
        id: String,
        choices: List<Int>,
    ): Poll

    suspend fun deleteStatus(
        id: String,
    )

    suspend fun muteAccount(
        id: String,
    )

    suspend fun unMuteAccount(
        id: String,
    )

    suspend fun blockAccount(
        id: String,
    )

    suspend fun unblockAccount(
        id: String,
    )
}
