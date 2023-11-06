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
import androidx.room.Room
import com.hadilq.mastan.SingleIn
import com.hadilq.mastan.UserScope
import com.hadilq.mastan.auth.AccessTokenRequest
import com.hadilq.mastan.di.legacyDependencies
import com.hadilq.mastan.network.UserApi
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides

@ContributesTo(UserScope::class)
@Module
class UserModule {

    @Provides
    @SingleIn(UserScope::class)
    fun providesRetrofit(
        accessTokenRequest: AccessTokenRequest,
    ): UserApi {
        return legacyDependencies.networkLogicIo.userApi(accessTokenRequest)
    }

    @Provides
    @SingleIn(UserScope::class)
    fun provideDB(
        applicationContext: Application,
        accessTokenRequest: AccessTokenRequest,
    ): AppDatabase =
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, accessTokenRequest.domain
        )
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    @SingleIn(UserScope::class)
    fun provideStatusDao(appDatabase: AppDatabase): StatusDao = appDatabase.statusDao()
}
