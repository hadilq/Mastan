package com.androiddev.social.timeline.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import com.androiddev.social.timeline.data.Account
import com.androiddev.social.timeline.ui.model.UI
import kotlinx.coroutines.flow.MutableSharedFlow

@ExperimentalMaterialApi
@Composable
fun After(
    status: UI,
    goToConversation: (UI) -> Unit,
    account: Account?,
    goToBottomSheet: suspend (SheetContentState) -> Unit,
    goToProfile: (String) -> Unit,
    goToTag: (String) -> Unit,
) {
    val provider = LocalAuthComponent.current.conversationPresenter().get()
    var presenter by remember { mutableStateOf(provider) }

    LaunchedEffect(key1 = status) {
        presenter.start()
    }
    val colorScheme = MaterialTheme.colorScheme
    LaunchedEffect(key1 = status) {
        presenter.handle(ConversationPresenter.Load(status.remoteId, status.type, colorScheme))
    }
    val after = presenter.model.conversations.get(status.remoteId)?.after

    InnerLazyColumn(after, account, goToBottomSheet, goToConversation, goToProfile, goToTag)
}

@Composable
fun InnerLazyColumn(
    items: List<UI>?,
    account: Account?,
    goToBottomSheet: suspend (SheetContentState) -> Unit,
    goToConversation: (UI) -> Unit,
    goToProfile: (String) -> Unit,
    goToTag: (String) -> Unit,
) {
    val submitPresenter = LocalAuthComponent.current.submitPresenter()
    LaunchedEffect(key1 = items) {
        submitPresenter.start()
    }
    val configuration = LocalConfiguration.current

    val screenHeight = configuration.screenHeightDp
    LazyColumn(modifier = Modifier.heightIn(1.dp, max = (screenHeight * .8).dp)) {
        if (!items.isNullOrEmpty()) {
            items.take(10).forEach { inner ->
                item {
                    card(
                        modifier = Modifier.background(MaterialTheme.colorScheme.background),
                        status = inner,
                        account = account,
                        events = submitPresenter.events,
                        goToBottomSheet = goToBottomSheet,
                        goToConversation = goToConversation,
                        goToProfile = goToProfile,
                        goToTag = goToTag,
                    )
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun card(
    modifier: Modifier,
    status: UI,
    account: Account?,
    events: MutableSharedFlow<SubmitPresenter.SubmitEvent>,
    goToBottomSheet: suspend (SheetContentState) -> Unit,
    goToConversation: (UI) -> Unit,
    goToProfile: (String) -> Unit,
    goToTag: (String) -> Unit,
) {

    var eagerStatus by remember { mutableStateOf(status) }

    AnimatedVisibility(true) {
        Column {
            TimelineCard(
                goToBottomSheet = goToBottomSheet,
                goToProfile = goToProfile,
                goToTag = goToTag,
                ui = eagerStatus,
                account = account,
                replyToStatus = { content, visiblity, replyToId, replyCount, uris ->
                    events.tryEmit(
                        SubmitPresenter.PostMessage(
                            content = content,
                            visibility = visiblity,
                            replyStatusId = replyToId,
                            replyCount = replyCount,
                            uris = uris
                        )
                    )
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
            )
        }
    }
}
