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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hadilq.mastan.theme.LocalMastanThemeUiIo
import com.hadilq.mastan.timeline.data.FeedType
import com.hadilq.mastan.timeline.data.mapStatus
import com.hadilq.mastan.timeline.data.toStatusDb
import com.hadilq.mastan.timeline.ui.model.UI

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MentionsScreen(
    navController: NavHostController,
    code: String,
    goToConversation: (UI) -> Unit,
    showBackBar: Boolean,
    goToProfile: (String) -> Unit,
    goToTag: (String) -> Unit,
) {
    val component = LocalAuthComponent.current
    val userComponent = LocalUserComponent.current

    val mentionsPresenter = component.mentionsPresenter()
    val submitPresenter = component.submitPresenter()
    val uriPresenter = remember { component.urlPresenter().get() }
    LaunchedEffect(key1 = userComponent.accessTokenRequest()) {
        mentionsPresenter.start()
    }
    LaunchedEffect(key1 = userComponent.accessTokenRequest()) {
        mentionsPresenter.handle(MentionsPresenter.Load)
    }
    val dim = LocalMastanThemeUiIo.current.dim
    val statuses = mentionsPresenter.model.statuses.map {
        it.toStatusDb(FeedType.Home).mapStatus(MaterialTheme.colorScheme, dim)
    }
    LaunchedEffect(key1 = userComponent.accessTokenRequest()) {
        submitPresenter.start()
    }
    LaunchedEffect(key1 = userComponent.accessTokenRequest()) {
        uriPresenter.start()
    }
    OpenHandledUri(uriPresenter, navController)

    val pullRefreshState = rememberPullRefreshState(false, {
        component.mentionsPresenter().handle(MentionsPresenter.Load)
    })
    val bottomState: ModalBottomSheetState =
        rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val bottomSheetContentProvider = remember { BottomSheetContentProvider(bottomState) }

    ModalBottomSheetLayout(
        sheetState = bottomState,
        sheetShape = RoundedCornerShape(topStart = dim.paddingSize1, topEnd = dim.paddingSize1),
        sheetContent = {
            BottomSheetContent(
                bottomSheetContentProvider = bottomSheetContentProvider,
                onShareStatus = {},
                onDelete = { statusId->
                    submitPresenter.handle(SubmitPresenter.DeleteStatus(statusId))
                },
                onMessageSent = { newMessage ->
                    submitPresenter.handle(newMessage.toSubmitPostMessage())
                },
                goToProfile = goToProfile,
                goToTag = goToTag,
                goToConversation = {},
                onMuteAccount = {
                    submitPresenter.handle(SubmitPresenter.MuteAccount(it, true))
                },
                onBlockAccount = {
                    submitPresenter.handle(SubmitPresenter.BlockAccount(it, true))
                },
            )
        },
    ) {
        ScaffoldParent(
            pullRefreshState = pullRefreshState,
            statuses = statuses,
            mentionsPresenter = mentionsPresenter,
            submitPresenter = submitPresenter,
            uriPresenter = uriPresenter,
            goToBottomSheet = bottomSheetContentProvider::showContent,
            goToConversation = goToConversation,
            goToProfile = goToProfile,
            goToTag = goToTag,
            showBackBar = showBackBar,
            navController = navController,

        )
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun ScaffoldParent(
    pullRefreshState: PullRefreshState,
    statuses: List<UI>,
    mentionsPresenter: MentionsPresenter,
    submitPresenter: SubmitPresenter,
    uriPresenter: UriPresenter,
    goToBottomSheet: suspend (SheetContentState) -> Unit,
    goToConversation: (UI) -> Unit,
    goToProfile: (String) -> Unit,
    goToTag: (String) -> Unit,
    showBackBar: Boolean,
    navController: NavHostController,
) {
    Box(
        Modifier
            .background(MaterialTheme.colorScheme.surface)
            .pullRefresh(pullRefreshState)
            .padding(top = 56.dp)
            .fillMaxSize()
    ) {

        LazyColumn(
            Modifier
                .wrapContentHeight()
                .padding(top = 0.dp)
        ) {
            items(statuses, key = { it.remoteId }) {
                ConversationCard(
                    modifier = Modifier.background(Color.Transparent),
                    status = it,
                    account = mentionsPresenter.model.account,
                    events = submitPresenter.events,
                    goToBottomSheet = goToBottomSheet,
                    goToConversation = goToConversation,
                    goToProfile = goToProfile,
                    goToTag = goToTag,
                    onOpenURI = { uri, type ->
                        uriPresenter.handle(UriPresenter.Open(uri, type))
                    },
                )
            }
        }
    }
    CustomViewPullRefreshView(
        pullRefreshState, refreshTriggerDistance = 4.dp, isRefreshing = false
    )
    if (showBackBar) {
        BackBar(navController, "Mentions")
    }
}


