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

import com.hadilq.mastan.auth.AccessTokenRequest
import com.hadilq.mastan.auth.AuthLogicIo
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
import okhttp3.MultipartBody
import retrofit2.Response

class UserApiDelegate(
    private val realUserApi: RealUserApi,
    accessTokenRequest: AccessTokenRequest,
    authLogicIo: AuthLogicIo,
) : UserApi {

    private val authorizationHeader: String = authLogicIo.authorizationHeader(accessTokenRequest)

    override suspend fun getHomeTimeline(limit: String, since: String?): List<Status> {
        return realUserApi.getHomeTimeline(authorizationHeader, limit, since)
    }

    override suspend fun getTagTimeline(tag: String, limit: String, since: String?): List<Status> {
        return realUserApi.getTagTimeline(authorizationHeader, tag, limit, since)
    }

    override suspend fun getLocalTimeline(
        localOnly: Boolean,
        limit: String,
        since: String?,
    ): List<Status> {
        return realUserApi.getLocalTimeline(authorizationHeader, localOnly, limit, since)
    }

    override suspend fun getTrending(limit: String, offset: String?): List<Status> {
        return realUserApi.getTrending(authorizationHeader, limit, offset)
    }

    override suspend fun conversations(limit: String, types: Set<String>?): List<Notification> {
        return realUserApi.conversations(authorizationHeader, limit, types)
    }

    override suspend fun search(
        searchTerm: String,
        limit: String,
        resolve: Boolean,
        following: Boolean,
    ): SearchResult {
        return realUserApi.search(authorizationHeader, searchTerm, limit, resolve, following)
    }

    override suspend fun notifications(offset: String?, limit: String): List<Notification> {
        return realUserApi.notifications(authorizationHeader, offset, limit)
    }

    override suspend fun newStatus(status: NewStatus): Status {
        return realUserApi.newStatus(authorizationHeader, status)
    }

    override suspend fun getStatus(id: String): Status {
        return realUserApi.getStatus(authorizationHeader, id)
    }

    override suspend fun accountStatuses(
        accountId: String,
        since: String?,
        excludeReplies: Boolean?,
        onlyMedia: Boolean?,
        pinned: Boolean?,
        limit: Int?,
    ): List<Status> {
        return realUserApi.accountStatuses(
            authorizationHeader,
            accountId,
            since,
            excludeReplies,
            onlyMedia,
            pinned,
            limit
        )
    }

    override suspend fun bookmarkedStatuses(limit: Int?): WithHeaders<List<Status>> {
        return realUserApi.bookmarkedStatuses(authorizationHeader, limit).withHeader(emptyList())
    }

    override suspend fun bookmarkedStatuses(url: String): WithHeaders<List<Status>> {
        return realUserApi.bookmarkedStatuses(authorizationHeader, url).withHeader(emptyList())
    }

    override suspend fun favorites(limit: Int?): WithHeaders<List<Status>> {
        return realUserApi.favorites(authorizationHeader, limit).withHeader(emptyList())
    }

    override suspend fun favorites(url: String): WithHeaders<List<Status>> {
        return realUserApi.favorites(authorizationHeader, url).withHeader(emptyList())
    }

    override suspend fun followers(
        accountId: String,
        since: String?,
        limit: Int?,
    ): WithHeaders<List<Account>> {
        return realUserApi.followers(authorizationHeader, accountId, since, limit)
            .withHeader(emptyList())
    }

    override suspend fun followers(url: String): WithHeaders<List<Account>> {
        return realUserApi.followers(authorizationHeader, url).withHeader(emptyList())
    }

    override suspend fun following(
        accountId: String,
        since: String?,
        limit: Int?,
    ): WithHeaders<List<Account>> {
        return realUserApi.following(authorizationHeader, accountId, since, limit)
            .withHeader(emptyList())
    }

    override suspend fun following(url: String): WithHeaders<List<Account>> {
        return realUserApi.following(authorizationHeader, url).withHeader(emptyList())
    }

    override suspend fun relationships(accountIds: List<String>): List<Relationship> {
        return realUserApi.relationships(authorizationHeader, accountIds)
    }

    override suspend fun account(accountId: String): Account {
        return realUserApi.account(authorizationHeader, accountId)
    }

    override suspend fun conversation(statusId: String): StatusNode {
        return realUserApi.conversation(authorizationHeader, statusId)
    }

    override suspend fun boostStatus(id: String): Status {
        return realUserApi.boostStatus(authorizationHeader, id)
    }

    override suspend fun unBoostStatus(id: String): Status {
        return realUserApi.unBoostStatus(authorizationHeader, id)
    }

    override suspend fun followAccount(accountId: String): Relationship {
        return realUserApi.followAccount(authorizationHeader, accountId)
    }

    override suspend fun unfollowAccount(accountId: String): Relationship {
        return realUserApi.unfollowAccount(authorizationHeader, accountId)
    }

    override suspend fun followTag(name: String): Tag {
        return realUserApi.followTag(authorizationHeader, name)
    }

    override suspend fun upload(
        fileMultipartBodyPart: Any,
        descriptionMultipartBodyPart: Any?,
        focusMultipartBodyPart: Any?
    ): UploadIds {
        return realUserApi.upload(
            authorizationHeader,
            fileMultipartBodyPart as MultipartBody.Part,
            descriptionMultipartBodyPart as MultipartBody.Part,
            focusMultipartBodyPart as MultipartBody.Part,
        )
    }

    override suspend fun unfollowTag(name: String): Tag {
        return realUserApi.unfollowTag(authorizationHeader, name)
    }

    override suspend fun favouriteStatus(id: String): Status {
        return realUserApi.favouriteStatus(authorizationHeader, id)
    }

    override suspend fun unfavouriteStatus(id: String): Status {
        return realUserApi.unfavouriteStatus(authorizationHeader, id)
    }

    override suspend fun bookmarkStatus(id: String): Status {
        return realUserApi.bookmarkStatus(authorizationHeader, id)
    }

    override suspend fun accountVerifyCredentials(): Account {
        return realUserApi.accountVerifyCredentials(authorizationHeader)
    }

    override suspend fun followedTags(): List<Tag> {
        return realUserApi.followedTags(authorizationHeader)
    }

    override suspend fun viewPoll(id: String): Poll {
        return realUserApi.viewPoll(authorizationHeader, id)
    }

    override suspend fun votePoll(id: String, choices: List<Int>): Poll {
        return realUserApi.votePoll(authorizationHeader, id, choices)
    }

    override suspend fun deleteStatus(id: String) {
        return realUserApi.deleteStatus(authorizationHeader, id)
    }

    override suspend fun muteAccount(id: String) {
        return realUserApi.muteAccount(authorizationHeader, id)
    }

    override suspend fun unMuteAccount(id: String) {
        return realUserApi.unMuteAccount(authorizationHeader, id)
    }

    override suspend fun blockAccount(id: String) {
        return realUserApi.blockAccount(authorizationHeader, id)
    }

    override suspend fun unblockAccount(id: String) {
        return realUserApi.unblockAccount(authorizationHeader, id)
    }

    private fun <T> Response<T>.withHeader(emptyType: T): WithHeaders<T> = let { response ->
        response.body()?.let { body ->
            WithHeaders(
                body = body,
                headers = response.headers().associate { it.first to it.second },
            )
        } ?: WithHeaders(emptyType, emptyMap())
    }

}