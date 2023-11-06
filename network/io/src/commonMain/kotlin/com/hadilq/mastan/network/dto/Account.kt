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
package com.hadilq.mastan.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * https://docs.joinmastodon.org/entities/account/
 */
@Serializable
data class Account(
    // base attributes
    @SerialName("id") val id: String,
    @SerialName("username") val username: String,
    @SerialName("acct") val acct: String,
    @SerialName("url") val url: String,

    // display attributes
    @SerialName("display_name") val displayName: String,
    @SerialName("note") val note: String,
    @SerialName("avatar") val avatar: String,
    @SerialName("avatar_static") val avatarStatic: String,
    @SerialName("header") val header: String,
    @SerialName("header_static") val headerStatic: String,
    @SerialName("locked") val locked: Boolean,
    @SerialName("emojis") val emojis: List<Emoji>,
    @SerialName("discoverable") val discoverable: Boolean? = false,

    // statistical attributes
    @SerialName("created_at") val createdAt: String,
    @SerialName("last_status_at") val last_statusAt: String? = "",
    @SerialName("statuses_count") val statusesCount: Int,
    @SerialName("followers_count") val followersCount: Int,
    @SerialName("following_count") val followingCount: Int,

    // optional attributes
    @SerialName("moved") val moved: Account? = null,
    @SerialName("fields") val fields: List<Field>? = null,
    @SerialName("bot") val bot: Boolean? = null,
    @SerialName("source") val source: Source? = null,
    @SerialName("suspended") val suspended: Boolean? = null,
    @SerialName("mute_expires_at") val muteExpiresAt: String? = null,

    val domain: String? = null,
    val isFollowed: Boolean = false,
    val muting: Boolean? = null,
    val blocking: Boolean? = null,
)

@Serializable
data class Source(
    // base attributes
    @SerialName("note") val note: String,
    @SerialName("fields") val fields: List<Field>,

    // optional attributes
    @SerialName("privacy") val privacy: Privacy,
    @SerialName("sensitive") val sensitive: Boolean,
    @SerialName("language") val language: String?,
    @SerialName("follow_requests_count") val followRequestsCount: Int,
)

@Serializable
data class Field(
    @SerialName("name") val name: String,
    @SerialName("value") val value: String,
    @SerialName("verified_at") val verifiedAt: String? = null
)

