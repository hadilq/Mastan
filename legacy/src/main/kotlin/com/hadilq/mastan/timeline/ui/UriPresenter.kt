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
import com.hadilq.mastan.network.UserApi
import com.hadilq.mastan.timeline.data.FeedType
import com.hadilq.mastan.ui.util.Presenter
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URI
import javax.inject.Inject

abstract class UriPresenter :
    Presenter<UriPresenter.UrlEvent, UriPresenter.UriModel, UriPresenter.UriEffect>(
        UriModel()
    ) {
    sealed interface UrlEvent

    data class Open(val uri: URI, val type: FeedType) : UrlEvent
    object Reset : UrlEvent

    data class UriModel(
        val handledUri: HandledUri = NotHandled,
    )

    sealed interface HandledUri

    object NotHandled : HandledUri

    data class UnknownHandledUri(
        val uri: URI,
    ) : HandledUri

    data class ConversationHandledUri(
        val statusId: String,
        val type: FeedType,
    ) : HandledUri

    data class ProfileHandledUri(
        val accountId: String,
    ) : HandledUri

    sealed interface UriEffect
}

@ContributesBinding(AuthRequiredScope::class, boundType = UriPresenter::class)
@SingleIn(AuthRequiredScope::class)
class RealUriPresenter @Inject constructor(
    private val userApi: UserApi,
) : UriPresenter() {

    override suspend fun eventHandler(event: UrlEvent, scope: CoroutineScope) =
        withContext(Dispatchers.IO) {
            when (event) {
                is Open -> {
                    val searchResult = kotlin.runCatching {
                        userApi.search(
                            searchTerm = event.uri.toASCIIString(),
                            limit = 1.toString(),
                            resolve = true,
                            following = false,
                        )
                    }

                    when {
                        searchResult.isSuccess -> {
                            val result = searchResult.getOrThrow()
                            when {
                                result.statuses.isNotEmpty() -> {
                                    model = model.copy(
                                        handledUri = ConversationHandledUri(
                                            statusId = result.statuses.first().id,
                                            type = event.type
                                        )
                                    )
                                    return@withContext
                                }
                                result.accounts.isNotEmpty() -> {
                                    model = model.copy(
                                        handledUri = ProfileHandledUri(
                                            accountId = result.accounts.first().id,
                                        )
                                    )
                                    return@withContext
                                }
                            }
                        }
                    }
                    model = model.copy(handledUri = UnknownHandledUri(uri = event.uri))
                }

                Reset -> {
                    model = model.copy(handledUri = NotHandled)
                }
            }
        }

}
