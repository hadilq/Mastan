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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.hadilq.mastan.timeline.data.Account
import com.hadilq.mastan.timeline.data.FeedType
import com.hadilq.mastan.timeline.ui.model.UI
import kotlinx.coroutines.flow.MutableSharedFlow
import java.net.URI

@Composable
fun card(
    modifier: Modifier,
    status: UI,
    mainConversationStatusId: String? = null,
    account: Account?,
    events: MutableSharedFlow<SubmitPresenter.SubmitEvent>,
    goToBottomSheet: suspend (SheetContentState) -> Unit,
    goToConversation: (UI) -> Unit,
    goToProfile: (String) -> Unit,
    goToTag: (String) -> Unit,
    onOpenURI: (URI, FeedType) -> Unit,
) {

    var eagerStatus by remember { mutableStateOf(status) }

    AnimatedVisibility(true) {
        Column {
            TimelineCard(
                goToBottomSheet = goToBottomSheet,
                goToProfile = goToProfile,
                goToTag = goToTag,
                ui = eagerStatus,
                mainConversationStatusId = mainConversationStatusId,
                account = account,
                replyToStatus = {
                    events.tryEmit(it.toSubmitPostMessage())
                    eagerStatus = eagerStatus.copy(replyCount = eagerStatus.replyCount + 1)
                },
                boostStatus = { statusId, boosted ->
                    events.tryEmit(
                        SubmitPresenter
                            .BoostMessage(statusId, status.type, boosted)
                    )
                    eagerStatus =
                        eagerStatus.copy(boostCount = eagerStatus.boostCount + 1, boosted = true)

                },
                favoriteStatus = { statusId, favourited ->
                    events.tryEmit(
                        SubmitPresenter
                            .FavoriteMessage(statusId, status.type, favourited)
                    )
                    eagerStatus = eagerStatus.copy(
                        favoriteCount = eagerStatus.favoriteCount + 1,
                        favorited = true
                    )

                },
                goToConversation = goToConversation,
                onReplying = { },
                modifier = modifier,
                onVote = { statusId, pollId, choices ->
                    events.tryEmit(SubmitPresenter.VotePoll(statusId, pollId, choices))
                },
                onOpenURI = onOpenURI,
            )
        }
    }
}
