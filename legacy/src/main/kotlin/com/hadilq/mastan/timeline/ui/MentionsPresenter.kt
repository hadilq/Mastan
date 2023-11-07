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
package com.hadilq.mastan.timeline.ui

import com.hadilq.mastan.AuthRequiredScope
import com.hadilq.mastan.SingleIn
import com.hadilq.mastan.UserScope
import com.hadilq.mastan.network.dto.Account
import com.hadilq.mastan.timeline.data.AccountRepository
import com.hadilq.mastan.network.dto.Notification
import com.hadilq.mastan.network.dto.Status
import com.hadilq.mastan.ui.util.Presenter
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.mobilenativefoundation.store.store5.Fetcher
import org.mobilenativefoundation.store.store5.StoreBuilder
import org.mobilenativefoundation.store.store5.StoreRequest
import javax.inject.Inject

abstract class MentionsPresenter :
    Presenter<MentionsPresenter.MentionsEvent, MentionsPresenter.MentionsModel, MentionsPresenter.MentionsEffect>(
        MentionsModel(emptyList())
    ) {
    sealed interface MentionsEvent

    object Load : MentionsEvent

    data class MentionsModel(
        val statuses: List<Status>,
        val account: Account? = null,
    )

    sealed interface MentionsEffect
}

@ContributesBinding(AuthRequiredScope::class, boundType = MentionsPresenter::class)
@SingleIn(AuthRequiredScope::class)
class RealMentionsPresenter @Inject constructor(
    val mentionRepository: MentionRepository,
    val accountRepository: AccountRepository,
) : MentionsPresenter() {
    override suspend fun eventHandler(event: MentionsEvent, coroutineScope: CoroutineScope) {
        when (event) {
            is Load -> {
                model = model.copy(account = accountRepository.getCurrent())
                coroutineScope.launch(Dispatchers.IO) {
                    mentionRepository.get().collectLatest {
                        val statuses = it.mapNotNull { it.status }
                        model = model.copy(statuses = statuses)
                    }
                }
            }
        }
    }
}

interface MentionRepository {
    suspend fun get(): Flow<List<Notification>>
}

@ContributesBinding(UserScope::class)
@SingleIn(UserScope::class)
class RealMentionRepository @Inject constructor(
    userApi: com.hadilq.mastan.network.UserApi,
) :
    MentionRepository {

    val store = StoreBuilder.from(
        Fetcher.of { key: Unit ->
            userApi.conversations()
        }
    ).build()

    override suspend fun get(): Flow<List<Notification>> = withContext(Dispatchers.IO) {
        store.stream(StoreRequest.cached(Unit, refresh = true)).map { it.dataOrNull() }
            .filterNotNull()
    }
}