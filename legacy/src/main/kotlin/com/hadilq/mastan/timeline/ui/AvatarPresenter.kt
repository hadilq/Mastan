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

import android.app.Application
import com.hadilq.mastan.AuthRequiredScope
import com.hadilq.mastan.SingleIn
import com.hadilq.mastan.auth.AccessTokenRequest
import com.hadilq.mastan.auth.LoggedInAccountsState
import com.hadilq.mastan.auth.data.UserManager
import com.hadilq.mastan.di.legacyDependencies
import com.hadilq.mastan.network.UserApi
import com.hadilq.mastan.network.dto.Account
import com.hadilq.mastan.ui.util.Presenter
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

abstract class AvatarPresenter :
    Presenter<AvatarPresenter.AvatarEvent, AvatarPresenter.AvatarModel, AvatarPresenter.AvatarEffect>(
        AvatarModel(true)
    ) {
    sealed interface AvatarEvent

    object Load : AvatarEvent

    data class AvatarModel(
        val loading: Boolean,
        val accounts: List<Account>? = null,
    )

    sealed interface AvatarEffect
}

@ContributesBinding(AuthRequiredScope::class, boundType = AvatarPresenter::class)
@SingleIn(AuthRequiredScope::class)
class RealAvatarPresenter @Inject constructor(
    val api: UserApi,
    val application: Application,
    val userManager: UserManager,
) :
    AvatarPresenter() {

    override suspend fun eventHandler(event: AvatarEvent, scope: CoroutineScope) =
        withContext(Dispatchers.IO) {
            when (event) {
                is Load -> {
                    val loggedInAccountsState = legacyDependencies.authLogicIo.state as LoggedInAccountsState
                    val accountTokens: List<AccessTokenRequest> = loggedInAccountsState
                            .servers.values.flatMap { server -> server.users.values.map { it.accessTokenRequest } }
                    val accounts = accountTokens.map { accountTokenRequest ->
                        val account: Result<Account?> =
                            kotlin.runCatching {
                                val userComponent =
                                    userManager.userComponentFor(accessTokenRequest = accountTokenRequest)
                                val credentials = userComponent.api()
                                    .accountVerifyCredentials()
                                credentials
                            }

                        account.getOrNull()
                            ?.copy(
                                domain = accountTokenRequest.domain
                            )
                    }.filterNotNull()

                    model =
                        model.copy(accounts = accounts.sortedBy { if (it.domain == loggedInAccountsState.currentUser.domain) 0 else 1 })

                }
            }
        }
}
