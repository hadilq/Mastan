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

class FakeUserApi: UserApi {
    override suspend fun getHomeTimeline(limit: String, since: String?): List<Status> {
        TODO("Not yet implemented")
    }

    override suspend fun getTagTimeline(tag: String, limit: String, since: String?): List<Status> {
        TODO("Not yet implemented")
    }

    override suspend fun getLocalTimeline(
        localOnly: Boolean,
        limit: String,
        since: String?,
    ): List<Status> {
        TODO("Not yet implemented")
    }

    override suspend fun getTrending(limit: String, offset: String?): List<Status> {
        TODO("Not yet implemented")
    }

    override suspend fun conversations(limit: String, types: Set<String>?): List<Notification> {
        TODO("Not yet implemented")
    }

    override suspend fun search(
        searchTerm: String,
        limit: String,
        resolve: Boolean,
        following: Boolean,
    ): SearchResult {
        TODO("Not yet implemented")
    }

    override suspend fun notifications(offset: String?, limit: String): List<Notification> {
        TODO("Not yet implemented")
    }

    override suspend fun newStatus(status: NewStatus): Status {
        TODO("Not yet implemented")
    }

    override suspend fun getStatus(id: String): Status {
        TODO("Not yet implemented")
    }

    override suspend fun accountStatuses(
        accountId: String,
        since: String?,
        excludeReplies: Boolean?,
        onlyMedia: Boolean?,
        pinned: Boolean?,
        limit: Int?,
    ): List<Status> {
        TODO("Not yet implemented")
    }

    override suspend fun bookmarkedStatuses(limit: Int?): WithHeaders<List<Status>> {
        TODO("Not yet implemented")
    }

    override suspend fun bookmarkedStatuses(url: String): WithHeaders<List<Status>> {
        TODO("Not yet implemented")
    }

    override suspend fun favorites(limit: Int?): WithHeaders<List<Status>> {
        TODO("Not yet implemented")
    }

    override suspend fun favorites(url: String): WithHeaders<List<Status>> {
        TODO("Not yet implemented")
    }

    override suspend fun followers(
        accountId: String,
        since: String?,
        limit: Int?,
    ): WithHeaders<List<Account>> {
        TODO("Not yet implemented")
    }

    override suspend fun followers(url: String): WithHeaders<List<Account>> {
        TODO("Not yet implemented")
    }

    override suspend fun following(
        accountId: String,
        since: String?,
        limit: Int?,
    ): WithHeaders<List<Account>> {
        TODO("Not yet implemented")
    }

    override suspend fun following(url: String): WithHeaders<List<Account>> {
        TODO("Not yet implemented")
    }

    override suspend fun relationships(accountIds: List<String>): List<Relationship> {
        TODO("Not yet implemented")
    }

    override suspend fun account(accountId: String): Account {
        TODO("Not yet implemented")
    }

    override suspend fun conversation(statusId: String): StatusNode {
        TODO("Not yet implemented")
    }

    override suspend fun boostStatus(id: String): Status {
        TODO("Not yet implemented")
    }

    override suspend fun unBoostStatus(id: String): Status {
        TODO("Not yet implemented")
    }

    override suspend fun followAccount(accountId: String): Relationship {
        TODO("Not yet implemented")
    }

    override suspend fun unfollowAccount(accountId: String): Relationship {
        TODO("Not yet implemented")
    }

    override suspend fun followTag(name: String): Tag {
        TODO("Not yet implemented")
    }

    override suspend fun upload(
        fileMultipartBodyPart: Any,
        descriptionMultipartBodyPart: Any?,
        focusMultipartBodyPart: Any?,
    ): UploadIds {
        TODO("Not yet implemented")
    }

    override suspend fun unfollowTag(name: String): Tag {
        TODO("Not yet implemented")
    }

    override suspend fun favouriteStatus(id: String): Status {
        TODO("Not yet implemented")
    }

    override suspend fun unfavouriteStatus(id: String): Status {
        TODO("Not yet implemented")
    }

    override suspend fun bookmarkStatus(id: String): Status {
        TODO("Not yet implemented")
    }

    override suspend fun accountVerifyCredentials(): Account {
        TODO("Not yet implemented")
    }

    override suspend fun followedTags(): List<Tag> {
        TODO("Not yet implemented")
    }

    override suspend fun viewPoll(id: String): Poll {
        TODO("Not yet implemented")
    }

    override suspend fun votePoll(id: String, choices: List<Int>): Poll {
        TODO("Not yet implemented")
    }

    override suspend fun deleteStatus(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun muteAccount(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun unMuteAccount(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun blockAccount(id: String) {
        TODO("Not yet implemented")
    }

    override suspend fun unblockAccount(id: String) {
        TODO("Not yet implemented")
    }
}