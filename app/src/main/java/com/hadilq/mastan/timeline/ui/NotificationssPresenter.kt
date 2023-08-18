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
import com.hadilq.mastan.auth.data.OauthRepository
import com.hadilq.mastan.shared.UserApi
import com.hadilq.mastan.timeline.data.Account
import com.hadilq.mastan.timeline.data.AccountRepository
import com.hadilq.mastan.timeline.data.Notification
import com.hadilq.mastan.timeline.data.StatusDao
import com.hadilq.mastan.timeline.data.toStatusDb
import com.hadilq.mastan.timeline.data.updateOldStatus
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

abstract class NotificationPresenter :
    Presenter<NotificationPresenter.NotificationEvent, NotificationPresenter.NotificationModel, NotificationPresenter.NotificationEffect>(
        NotificationModel(emptyList())
    ) {
    sealed interface NotificationEvent

    object Load : NotificationEvent

    data class NotificationModel(
        val statuses: List<Notification>,
        val account: Account? = null,
    )

    sealed interface NotificationEffect
}

@ContributesBinding(AuthRequiredScope::class, boundType = NotificationPresenter::class)
@SingleIn(AuthRequiredScope::class)
class RealNotificationPresenter @Inject constructor(
    val repository: NotificationsRepository,
    val accountRepository: AccountRepository,
) : NotificationPresenter() {


    override suspend fun eventHandler(event: NotificationEvent, coroutineScope: CoroutineScope) {
        when (event) {
            is Load -> {
                model = model.copy(account = accountRepository.getCurrent())
                coroutineScope.launch(Dispatchers.IO) {
                    repository.get().collectLatest {
                        val statuses = it.filter {
                            it.status != null
                        }
                        model = model.copy(statuses = statuses)
                    }
                }
            }
        }
    }
}

interface NotificationsRepository {
    suspend fun get(): Flow<List<Notification>>
}

@ContributesBinding(UserScope::class)
@SingleIn(UserScope::class)
class RealNotificationsRepository @Inject constructor(
    userApi: UserApi,
    statusDao: StatusDao,
    oauthRepository: OauthRepository
) :
    NotificationsRepository {
    val store = StoreBuilder.from(
        Fetcher.of { key: Unit ->
            val notification = userApi.notifications(
                authHeader = oauthRepository.getAuthHeader(),
                offset = null
            )
            notification.forEach { notif ->
                notif.status?.let {status ->
                    statusDao.updateOldStatus(status.toStatusDb())
                }
            }
            notification
        }
    ).build()

    override suspend fun get(): Flow<List<Notification>> = withContext(Dispatchers.IO) {
        store.stream(StoreRequest.cached(Unit, refresh = true)).map { it.dataOrNull() }
            .filterNotNull()
    }
}