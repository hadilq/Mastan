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

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.dataStore
import com.androiddev.social.AppScope
import com.androiddev.social.SingleIn
import com.androiddev.social.auth.data.LoggedInAccounts
import com.androiddev.social.auth.data.LoggedInAccountsSerializer
import com.squareup.anvil.annotations.ContributesTo
import dagger.Module
import dagger.Provides

 val Context.dataStore by dataStore("account_preferences", LoggedInAccountsSerializer)

@ContributesTo(AppScope::class)
@Module
class AccountModule {
    @Provides
    @SingleIn(AppScope::class)
    fun provide(
        context: Application,
    ): DataStore<LoggedInAccounts> {
        return context.dataStore
    }
}

