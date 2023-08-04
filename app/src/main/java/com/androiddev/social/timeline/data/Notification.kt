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

import kotlinx.datetime.Instant
import kotlinx.serialization.Serializable

@Serializable
data class Notification(
    val id: String,
    val type: String,
    val status: Status? = null,
    val account: Account,
    val created_at: Instant,
    val realType: Type = if (type.contains('.')) Type.adminreport else Type.valueOf(type)
)

@Serializable
enum class Type(val value: String) {
    mention("mention"),
    status("status"),
    reblog("reblog"),
    follow("follow"),
    follow_request("follow_request"),
    favourite("favourite"),
    poll("poll"),
    update("update"),
    adminsign_up("admin.sign_up"),
    adminreport("admin.report"),
}