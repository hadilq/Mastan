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
package com.androiddev.social.shared

import com.androiddev.social.timeline.data.NewOauthApplication
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import retrofit2.http.*


interface Api {
    @POST
    @FormUrlEncoded
    suspend fun createApplication(
        @Url url: String,
        @Field("scopes") scopes: String,
        @Field("client_name") client_name: String,
        @Field("redirect_uris") redirect_uris: String
    ): NewOauthApplication

    @POST
    @FormUrlEncoded
    suspend fun createAccessToken(
        @Url domain: String,
        @Field("client_id") clientId: String,
        @Field("client_secret")  clientSecret: String,
        @Field("redirect_uri")  redirectUri: String,
        @Field("grant_type")  grantType: String,
        @Field("code") code: String,
        @Field("scope") scope: String
    ): Token

}


@Serializable
data class Token(
    val scope: String,
    @SerialName("access_token") val accessToken: String,
    @SerialName("token_type") val tokenType: String,
)

