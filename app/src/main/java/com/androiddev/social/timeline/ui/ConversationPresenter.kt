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
package com.androiddev.social.timeline.ui

import androidx.compose.material3.ColorScheme
import com.androiddev.social.AuthRequiredScope
import com.androiddev.social.SingleIn
import com.androiddev.social.auth.data.OauthRepository
import com.androiddev.social.shared.UserApi
import com.androiddev.social.timeline.data.Account
import com.androiddev.social.timeline.data.AccountRepository
import com.androiddev.social.timeline.data.FeedStoreRequest
import com.androiddev.social.timeline.data.FeedType
import com.androiddev.social.timeline.data.StatusRepository
import com.androiddev.social.timeline.data.mapStatus
import com.androiddev.social.timeline.data.toStatusDb
import com.androiddev.social.timeline.ui.model.ReplyType
import com.androiddev.social.timeline.ui.model.UI
import com.androiddev.social.ui.util.Presenter
import com.squareup.anvil.annotations.ContributesBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

abstract class ConversationPresenter :
    Presenter<ConversationPresenter.ConversationEvent, ConversationPresenter.ConversationModel, ConversationPresenter.ConversationEffect>(
        ConversationModel(emptyMap())
    ) {
    sealed interface ConversationEvent

    data class Load(
        val statusId: String, val type: FeedType, val colorScheme: ColorScheme,
    ) : ConversationEvent

    data class ConversationModel(
        val conversations: Map<String, ConvoUI> = emptyMap(),
        val account: Account? = null,
    )

    sealed interface ConversationEffect
}

@ContributesBinding(AuthRequiredScope::class, boundType = ConversationPresenter::class)
@SingleIn(AuthRequiredScope::class)
class RealConversationPresenter @Inject constructor(
    val api: UserApi,
    val repository: OauthRepository,
    val statusRepository: StatusRepository,
    val accountRepository: AccountRepository,
    val conversationReplyRearrangerMediator: ConversationReplyRearrangerMediator,
) :
    ConversationPresenter() {


    override suspend fun eventHandler(event: ConversationEvent, coroutineScope: CoroutineScope) =
        withContext(Dispatchers.IO) {
            when (event) {
                is Load -> {
                    model = model.copy(account = accountRepository.getCurrent())
                    val token = repository.getAuthHeader()
                    var currentConvo = model.conversations.getOrDefault(event.statusId, ConvoUI())

                    val status = kotlin.runCatching {
                        statusRepository.get(
                            FeedStoreRequest(
                                event.statusId,
                                event.type,
                            )
                        )
                    }
                    if (status.isSuccess) {
                        currentConvo =
                            currentConvo.copy(
                                status = status.getOrThrow().mapStatus(event.colorScheme)
                                    .copy(replyIndention = 0)
                            )
                    }
                    val conversations = model.conversations.toMutableMap()
                    conversations.put(event.statusId, currentConvo)
                    model = model.copy(conversations = conversations)

                    val conversation = kotlin.runCatching {
                        api.conversation(
                            authHeader = token,
                            statusId = event.statusId
                        )
                    }
                    if (conversation.isSuccess) {
                        val statuses = conversation.getOrThrow()
                        val after = statuses.descendants.map {
                            it.toStatusDb(FeedType.Home).copy(replyIndention = 0)
                        }
                        currentConvo = currentConvo.copy(
                            before = statuses.ancestors
                                .map { it.toStatusDb(FeedType.Home).mapStatus(event.colorScheme) }
                                .map { it.copy(replyType = ReplyType.CHILD, replyIndention = 0) },
                            after = conversationReplyRearrangerMediator
                                .rearrangeConversations(after, event.statusId)
                                .map { it.mapStatus(event.colorScheme) }
                                .map { it.copy(replyType = ReplyType.CHILD) },
                        )

                        val conversations = model.conversations.toMutableMap()
                        conversations.put(event.statusId, currentConvo)
                        model = model.copy(conversations = conversations)
                    }
                }
            }
        }

}

data class ConvoUI(
    val before: List<UI> = emptyList(),
    val after: List<UI> = emptyList(),
    val status: UI? = null
)
