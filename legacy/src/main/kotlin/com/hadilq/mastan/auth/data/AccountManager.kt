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
package com.hadilq.mastan.auth.data

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class LoggedInAccounts(val servers: Map<String, Server> = emptyMap())

@Serializable
data class Server(val users: Map<String, User> = emptyMap(), val domain: String)

@Serializable
data class User(val accessTokenRequest: AccessTokenRequest, val accessToken: String?)

object LoggedInAccountsSerializer : Serializer<LoggedInAccounts> {

    override val defaultValue = LoggedInAccounts()

    override suspend fun readFrom(input: InputStream): LoggedInAccounts =
        withContext(Dispatchers.IO) {
            try {
                Json.decodeFromString(
                    LoggedInAccounts.serializer(), input.readBytes().decodeToString()
                )
            } catch (serialization: SerializationException) {
                throw CorruptionException("Unable to read LoggedInAccounts", serialization)
            }
        }

    override suspend fun writeTo(t: LoggedInAccounts, output: OutputStream) =
        withContext(Dispatchers.IO) {
            output.write(
                Json.encodeToString(LoggedInAccounts.serializer(), t)
                    .encodeToByteArray()
            )
        }
}