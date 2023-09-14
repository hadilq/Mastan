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

import android.app.Application
import androidx.datastore.dataStore
import androidx.room.Room
import com.hadilq.mastan.SingleIn
import com.hadilq.mastan.UserScope
import com.hadilq.mastan.auth.data.AccessTokenRequest
import com.hadilq.mastan.auth.data.LoggedInAccountsSerializer
import com.hadilq.mastan.shared.UserApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit

@ContributesTo(UserScope::class)
@Module
class UserModule {

    @Provides
    @SingleIn(UserScope::class)
    fun providesRetrofit(
        httpClient: OkHttpClient,
        accessTokenRequest: AccessTokenRequest
    ): UserApi {
        val json = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()
        return Retrofit
            .Builder()
            .baseUrl("https://${accessTokenRequest.domain}")
            .client(httpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build().create(UserApi::class.java)
    }

    @Provides
    @SingleIn(UserScope::class)
    fun provideDB(
        applicationContext: Application,
        accessTokenRequest: AccessTokenRequest
    ): AppDatabase =
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, accessTokenRequest.domain
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @SingleIn(UserScope::class)
    fun provideAccountStore() = dataStore("account", LoggedInAccountsSerializer)

    @Provides
    @SingleIn(UserScope::class)
    fun provideStatusDao(appDatabase: AppDatabase): StatusDao = appDatabase.statusDao()
}
