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

import com.hadilq.mastan.AppScope
import com.hadilq.mastan.SingleIn
import com.hadilq.mastan.di.BuildConfigDetails
import com.hadilq.mastan.network.RealApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

@ContributesTo(AppScope::class)
@Module
class DataModule {
    @Provides
    @SingleIn(AppScope::class)
    fun providesHttpClient(
        buildConfigDetails: BuildConfigDetails,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
//            .cache(Cache(context.cacheDir, 1000))

        return builder
            .apply {
                if (buildConfigDetails.debug) {
                    addInterceptor(HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                    })
                }
            }
            .build()
    }

    @Provides
    @SingleIn(AppScope::class)
    fun providesRetrofit(
        httpClient: OkHttpClient,
    ): RealApi {
        val json = Json { ignoreUnknownKeys = true }
        val contentType = "application/json".toMediaType()
        return Retrofit
            .Builder()
            .baseUrl("https://androiddev.social")
            .client(httpClient)
            .addConverterFactory(json.asConverterFactory(contentType))
            .build().create(com.hadilq.mastan.network.RealApi::class.java)
    }
}