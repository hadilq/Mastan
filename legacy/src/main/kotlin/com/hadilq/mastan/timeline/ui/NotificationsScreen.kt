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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.hadilq.mastan.timeline.data.FeedType
import com.hadilq.mastan.network.dto.Notification
import com.hadilq.mastan.network.dto.Type
import com.hadilq.mastan.timeline.data.mapStatus
import com.hadilq.mastan.timeline.data.toStatusDb
import com.hadilq.mastan.timeline.ui.model.UI
import com.hadilq.mastan.legacy.R
import com.hadilq.mastan.theme.LocalMastanThemeUiIo

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationsScreen(
    navController: NavHostController,
    code: String,
    goToConversation: (UI) -> Unit,
    goToProfile: (String) -> Unit,
    goToTag: (String) -> Unit,
) {
    val dim = LocalMastanThemeUiIo.current.dim
    val component = LocalAuthComponent.current
    val userComponent = LocalUserComponent.current

    val notificationPresenter = component.notificationPresenter()
    val submitPresenter = component.submitPresenter()
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = userComponent.accessTokenRequest()) {
        notificationPresenter.start(scope)
    }
    LaunchedEffect(key1 = userComponent.accessTokenRequest()) {
        notificationPresenter.handle(NotificationPresenter.Load)
    }
    val statuses = notificationPresenter.model.statuses
    LaunchedEffect(key1 = userComponent.accessTokenRequest()) {
        submitPresenter.start()
    }
    val uriPresenter = remember { component.urlPresenter().get() }
    LaunchedEffect(key1 = userComponent.accessTokenRequest()) {
        uriPresenter.start()
    }
    OpenHandledUri(uriPresenter, navController)

    val pullRefreshState = rememberPullRefreshState(false, {
        notificationPresenter.handle(NotificationPresenter.Load)
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
            colorScheme = colorScheme,
            pullRefreshState = pullRefreshState,
            statuses = statuses,
            goToProfile = goToProfile,
            notificationPresenter = notificationPresenter,
            submitPresenter = submitPresenter,
            uriPresenter = uriPresenter,
            goToBottomSheet = bottomSheetContentProvider::showContent,
            goToConversation = goToConversation,
            goToTag = goToTag,
            navController = navController
        )
    }
}

@Composable
@OptIn(ExperimentalMaterialApi::class)
private fun ScaffoldParent(
    colorScheme: ColorScheme,
    pullRefreshState: PullRefreshState,
    statuses: List<Notification>,
    goToProfile: (String) -> Unit,
    notificationPresenter: NotificationPresenter,
    submitPresenter: SubmitPresenter,
    uriPresenter: UriPresenter,
    goToBottomSheet: suspend (SheetContentState) -> Unit,
    goToConversation: (UI) -> Unit,
    goToTag: (String) -> Unit,
    navController: NavHostController,
) {
    val dim = LocalMastanThemeUiIo.current.dim
    Box(
        Modifier
            .background(colorScheme.surface)
            .pullRefresh(pullRefreshState)
            .padding(top = 0.dp)
            .fillMaxSize()
    ) {
        LazyColumn(
            Modifier
                .wrapContentHeight()
                .padding(top = 60.dp)
        ) {
            itemsIndexed(items = statuses, key = { a, status -> status.id }) { index, it ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    if (it.realType == Type.favourite) {
                        Boosted(
                            modifier = Modifier.height(30.dp),
                            boostedBy = (if (it.account.displayName.isNullOrEmpty()) it.account.username else it.account.displayName) + " favorited",
                            boostedAvatar = it.account.avatar,
                            boostedEmojis = it.account.emojis,
                            drawable = R.drawable.star,
                            containerColor = colorScheme.surface,
                            onClick = {
                                goToProfile(it.account.id)
                            }
                        )
                    }
                    if (it.realType == Type.reblog) {
                        Boosted(
                            modifier = Modifier.height(30.dp),
                            boostedBy = (if (it.account.displayName.isNullOrEmpty()) it.account.username else it.account.displayName) + " boosted",
                            boostedAvatar = it.account.avatar,
                            boostedEmojis = it.account.emojis,
                            containerColor = colorScheme.surface,
                            onClick = {
                                goToProfile(it.account.id)
                            },
                            drawable = R.drawable.rocket3
                        )
                    }
                    ConversationCard(
                        modifier = Modifier.background(Color.Transparent),
                        status = it.status!!.toStatusDb(FeedType.Home).mapStatus(colorScheme, dim),
                        account = notificationPresenter.model.account,
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
    }
    CustomViewPullRefreshView(
        pullRefreshState, refreshTriggerDistance = 4.dp, isRefreshing = false
    )
    BackBar(navController, "Notifications")
}

@Composable
fun BackBar(navController: NavHostController, title: String) {
    Column {
        TopAppBar(
            backgroundColor = MaterialTheme.colorScheme.surface,
            title = { Text(text = title, color = MaterialTheme.colorScheme.onSurface) },
            navigationIcon = if (navController.previousBackStackEntry != null) {
                {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            tint = MaterialTheme.colorScheme.onSurface,
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "search"
                        )
                    }
                }
            } else {
                null
            }
        )
    }
}

