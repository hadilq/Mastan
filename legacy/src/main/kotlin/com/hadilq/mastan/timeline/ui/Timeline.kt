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
@file:OptIn(ExperimentalMaterial3Api::class)

package com.hadilq.mastan.timeline.ui

import androidx.compose.animation.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelStoreOwner
import androidx.navigation.NavController
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.ImageLoader
import com.hadilq.mastan.AuthRequiredComponent
import com.hadilq.mastan.UserComponent
import com.hadilq.mastan.auth.AccessTokenRequest
import com.hadilq.mastan.tabselector.Tab
import com.hadilq.mastan.network.dto.Account
import com.hadilq.mastan.timeline.data.FeedType
import com.hadilq.mastan.timeline.ui.model.UI
import dev.marcellogalhardo.retained.compose.retainInActivity
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import com.hadilq.mastan.legacy.R
import com.hadilq.mastan.theme.LocalMastanThemeUiIo
import com.hadilq.mastan.ui.util.lazyItems
import java.net.URI

val LocalAuthComponent = compositionLocalOf<AuthRequiredInjector> { error("No component found!") }
val LocalUserComponent = compositionLocalOf<UserComponent> { error("No component found!") }
val LocalImageLoader = compositionLocalOf<ImageLoader> { error("No component found!") }

@OptIn(
    ExperimentalMaterialApi::class
)
@Composable
fun TimelineScreen(
    navController: NavController,
    accessTokenRequest: AccessTokenRequest,
    userComponent: UserComponent,
    onChangeTheme: () -> Unit,
    onNewAccount: () -> Unit,
    onProfileClick: (accountId: String, isCurrent: Boolean) -> Unit = { a, b -> },
    goToMentions: () -> Unit,
    goToNotifications: () -> Unit,
    goToSearch: () -> Unit,
    goToConversation: (UI) -> Unit,
    goToProfile: (String) -> Unit,
    goToTag: (String) -> Unit,
) {
    val dim = LocalMastanThemeUiIo.current.dim
    val component =
        retainInActivity(
            owner = LocalContext.current as ViewModelStoreOwner,
            key = accessTokenRequest.domain ?: ""
        ) { (userComponent as AuthRequiredComponent.ParentComponent).createAuthRequiredComponent() } as AuthRequiredInjector
    CompositionLocalProvider(LocalAuthComponent provides component) {
        val homePresenter = component.homePresenter()
        val submitPresenter = component.submitPresenter()
        val avatarPresenter = component.avatarPresenter()
        val uriPresenter = remember { component.urlPresenter().get() }
        val scope = rememberCoroutineScope()
        LaunchedEffect(key1 = accessTokenRequest) {
            homePresenter.start(scope)
        }
        LaunchedEffect(key1 = accessTokenRequest) {
            avatarPresenter.start()
        }
        LaunchedEffect(key1 = accessTokenRequest) {
            submitPresenter.start()
        }
        LaunchedEffect(key1 = accessTokenRequest) {
            uriPresenter.start()
        }
        OpenHandledUri(uriPresenter, navController)

        val bottomState: ModalBottomSheetState =
            rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
        val bottomSheetContentProvider = remember { BottomSheetContentProvider(bottomState) }
        val context = LocalContext.current

        ModalBottomSheetLayout(
            sheetState = bottomState,
            sheetShape = RoundedCornerShape(topStart = dim.paddingSize1, topEnd = dim.paddingSize1),
            sheetElevation = dim.paddingSize2,
            sheetBackgroundColor = MaterialTheme.colorScheme.surfaceVariant,
            sheetContent = {
                BottomSheetContent(
                    bottomSheetContentProvider = bottomSheetContentProvider,
                    onShareStatus = { context.shareStatus(it) },
                    onDelete = { statusId->
                        submitPresenter.handle(SubmitPresenter.DeleteStatus(statusId))
                    },
                    onMessageSent = { newMessage ->
                        submitPresenter.handle(newMessage.toSubmitPostMessage())
                        scope.launch {
                            bottomSheetContentProvider.hide()
                        }
                    },
                    goToConversation = goToConversation,
                    goToProfile = goToProfile,
                    goToTag = goToTag,
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
                accessTokenRequest = accessTokenRequest,
                avatarPresenter = avatarPresenter,
                onChangeTheme = onChangeTheme,
                onNewAccount = onNewAccount,
                onProfileClick = onProfileClick,
                goToSearch = goToSearch,
                goToMentions = goToMentions,
                goToNotifications = goToNotifications,
                homePresenter = homePresenter,
                bottomSheetContentProvider = bottomSheetContentProvider,
                submitPresenter = submitPresenter,
                uriPresenter = uriPresenter,
                goToConversation = goToConversation,
                goToProfile = goToProfile,
                goToTag = goToTag,
            )
        }
    }
}

@Composable
@OptIn(
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class
)
private fun ScaffoldParent(
    accessTokenRequest: AccessTokenRequest,
    avatarPresenter: AvatarPresenter,
    onChangeTheme: () -> Unit,
    onNewAccount: () -> Unit,
    onProfileClick: (accountId: String, isCurrent: Boolean) -> Unit,
    goToSearch: () -> Unit,
    goToMentions: () -> Unit,
    goToNotifications: () -> Unit,
    homePresenter: TimelinePresenter,
    bottomSheetContentProvider: BottomSheetContentProvider,
    submitPresenter: SubmitPresenter,
    uriPresenter: UriPresenter,
    goToConversation: (UI) -> Unit,
    goToProfile: (String) -> Unit,
    goToTag: (String) -> Unit,
) {
    val dim = LocalMastanThemeUiIo.current.dim
    var tabToLoad: FeedType by rememberSaveable { mutableStateOf(FeedType.Home) }
    var refresh: Boolean by remember { mutableStateOf(false) }
    var expanded: Boolean by remember { mutableStateOf(false) }
    var isReplying: Boolean by remember(bottomSheetContentProvider.bottomState.currentValue) {
        mutableStateOf(bottomSheetContentProvider.bottomState.currentValue == ModalBottomSheetValue.Expanded)
    }
    var selectedIndex by rememberSaveable { mutableStateOf(0) }
    val items = mutableListOf(
        Tab(
            "Home",
            R.drawable.house,
            onClick = {
                tabToLoad = FeedType.valueOf("Home"); selectedIndex = 0; expanded = false
            }),
        Tab(
            "Local",
            R.drawable.local,
            onClick = {
                tabToLoad = FeedType.valueOf("Local"); selectedIndex = 1; expanded = false
            }),
        Tab(
            "Federated",
            R.drawable.world,
            onClick = {
                tabToLoad = FeedType.valueOf("Federated"); selectedIndex = 2; expanded = false
            }),
        Tab(
            "Trending",
            R.drawable.trend,
            onClick = {
                tabToLoad = FeedType.valueOf("Trending"); selectedIndex = 3; expanded = false
            }),
        Tab(
            "Bookmarks",
            R.drawable.bookmark,
            onClick = {
                tabToLoad = FeedType.valueOf("Bookmarks"); selectedIndex = 4; expanded = false
            }),
        Tab(
            "Favorites",
            R.drawable.star,
            onClick = {
                tabToLoad = FeedType.valueOf("Favorites"); selectedIndex = 5; expanded = false
            }),

        )
    Scaffold(
        topBar = {
            TopAppBar(modifier = Modifier.clickable {
                refresh = true
            },
                backgroundColor = MaterialTheme.colorScheme.background,

                title = {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        LaunchedEffect(key1 = accessTokenRequest.domain) {
                            avatarPresenter.start()
                        }



                        LaunchedEffect(key1 = accessTokenRequest.domain) {
                            avatarPresenter.events.tryEmit(AvatarPresenter.Load)
                        }
                        Box {
                            AccountChooser(
                                model = avatarPresenter.model,
                                onChangeTheme = onChangeTheme,
                                onNewAccount = onNewAccount,
                                onProfileClick = onProfileClick
                            )
                        }

                        Box(Modifier.align(Alignment.CenterVertically)) {
                            TabSelector(items, selectedIndex, expanded) { expanded = !expanded }
                        }
                        Search {
                            goToSearch()
                        }
                    }
                })
        },

        backgroundColor = Color.Transparent,
        bottomBar = {
            AnimatedVisibility(!isReplying, enter = fadeIn(), exit = fadeOut()) {
                BottomAppBar(
                    modifier = Modifier.height(dim.paddingSize8),
                    contentPadding = PaddingValues(dim.paddingSizeNone, dim.paddingSizeNone),
                    elevation = dim.bottomBarElevation,
                    backgroundColor = MaterialTheme.colorScheme.surface,
                ) {
                    BottomBar(
                        goToMentions = goToMentions,
                        goToNotifications = goToNotifications,
                    )
                }
            }

        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        floatingActionButton = {
            val scope = rememberCoroutineScope()
            FAB(visible = !isReplying, MaterialTheme.colorScheme) {
                scope.launch {
                    homePresenter.model.currentAccount?.let {
                        bottomSheetContentProvider.showContent(SheetContentState.UserInput(it))
                    }
                }
                isReplying = true
            }
        }
    ) { padding ->
        Box {
            val model = homePresenter.model

            when (tabToLoad) {
                FeedType.Home -> {
                    TimelineTab(
                        goToBottomSheet = bottomSheetContentProvider::showContent,
                        goToProfile = goToProfile,
                        goToTag = goToTag,
                        domain = accessTokenRequest.domain,
                        events = homePresenter.events,
                        submitEvents = submitPresenter.events,
                        tabToLoad = FeedType.Home,
                        items = model.homeStatuses?.collectAsLazyPagingItems(),
                        currentAccount = model.currentAccount,
                        goToConversation = goToConversation,
                        onReplying = { isReplying = it },
                        onProfileClick = onProfileClick,
                        refresh = refresh,
                        doneRefreshing = { refresh = false },
                        onOpenURI = { uri, type ->
                            uriPresenter.handle(UriPresenter.Open(uri, type))
                        },
                    )
                }

                FeedType.Local -> {
                    TimelineTab(
                        goToBottomSheet = bottomSheetContentProvider::showContent,
                        goToProfile = goToProfile,
                        goToTag = goToTag,
                        domain = accessTokenRequest.domain,
                        events = homePresenter.events,
                        submitEvents = submitPresenter.events,
                        tabToLoad = FeedType.Local,
                        items = model.localStatuses?.collectAsLazyPagingItems(),
                        currentAccount = model.currentAccount,
                        goToConversation = goToConversation,
                        onReplying = { isReplying = it },
                        onProfileClick = onProfileClick,
                        refresh = refresh,
                        doneRefreshing = { refresh = false },
                        onOpenURI = { uri, type ->
                            uriPresenter.handle(UriPresenter.Open(uri, type))
                        },
                    )
                }

                FeedType.Federated -> {
                    TimelineTab(
                        goToBottomSheet = bottomSheetContentProvider::showContent,
                        goToProfile = goToProfile,
                        goToTag = goToTag,
                        domain = accessTokenRequest.domain,
                        events = homePresenter.events,
                        submitEvents = submitPresenter.events,
                        tabToLoad = FeedType.Federated,
                        items = model.federatedStatuses?.collectAsLazyPagingItems(),
                        currentAccount = model.currentAccount,
                        goToConversation = goToConversation,
                        onReplying = { isReplying = it },
                        onProfileClick = onProfileClick,
                        refresh = refresh,
                        doneRefreshing = { refresh = false },
                        onOpenURI = { uri, type ->
                            uriPresenter.handle(UriPresenter.Open(uri, type))
                        },
                    )
                }

                FeedType.Trending -> {
                    TimelineTab(
                        goToBottomSheet = bottomSheetContentProvider::showContent,
                        goToProfile = goToProfile,
                        goToTag = goToTag,
                        domain = accessTokenRequest.domain,
                        events = homePresenter.events,
                        submitEvents = submitPresenter.events,
                        tabToLoad = FeedType.Trending,
                        items = model.trendingStatuses?.collectAsLazyPagingItems(),
                        currentAccount = model.currentAccount,
                        goToConversation = goToConversation,
                        onReplying = { isReplying = it },
                        onProfileClick = onProfileClick,
                        refresh = refresh,
                        doneRefreshing = { refresh = false },
                        onOpenURI = { uri, type ->
                            uriPresenter.handle(UriPresenter.Open(uri, type))
                        },
                    )
                }
                FeedType.User -> {}
                FeedType.UserWithMedia -> {}
                FeedType.UserWithReplies -> {}
                FeedType.Bookmarks -> {
                    TimelineTab(
                        goToBottomSheet = bottomSheetContentProvider::showContent,
                        goToProfile = goToProfile,
                        goToTag = goToTag,
                        domain = accessTokenRequest.domain,
                        events = homePresenter.events,
                        submitEvents = submitPresenter.events,
                        tabToLoad = FeedType.Bookmarks,
                        items = model.bookmarkedStatuses?.collectAsLazyPagingItems(),
                        currentAccount = model.currentAccount,
                        goToConversation = goToConversation,
                        onReplying = { isReplying = it },
                        onProfileClick = onProfileClick,
                        refresh = refresh,
                        doneRefreshing = { refresh = false },
                        onOpenURI = { uri, type ->
                            uriPresenter.handle(UriPresenter.Open(uri, type))
                        },
                    )
                }

                FeedType.Favorites -> {
                    TimelineTab(
                        goToBottomSheet = bottomSheetContentProvider::showContent,
                        goToProfile = goToProfile,
                        goToTag = goToTag,
                        domain = accessTokenRequest.domain,
                        events = homePresenter.events,
                        submitEvents = submitPresenter.events,
                        tabToLoad = FeedType.Favorites,
                        items = model.favoriteStatuses?.collectAsLazyPagingItems(),
                        currentAccount = model.currentAccount,
                        goToConversation = goToConversation,
                        onReplying = { isReplying = it },
                        onProfileClick = onProfileClick,
                        refresh = refresh,
                        doneRefreshing = { refresh = false },
                        onOpenURI = { uri, type ->
                            uriPresenter.handle(UriPresenter.Open(uri, type))
                        },
                    )
                }

                FeedType.Hashtag -> {
                    TimelineTab(
                        goToBottomSheet = bottomSheetContentProvider::showContent,
                        goToProfile = goToProfile,
                        goToTag = goToTag,
                        domain = accessTokenRequest.domain,
                        events = homePresenter.events,
                        submitEvents = submitPresenter.events,
                        tabToLoad = FeedType.Hashtag,
                        items = model.hashtagStatuses?.collectAsLazyPagingItems(),
                        currentAccount = model.currentAccount,
                        goToConversation = goToConversation,
                        onReplying = { isReplying = it },
                        onProfileClick = onProfileClick,
                        refresh = refresh,
                        doneRefreshing = { refresh = false },
                        onOpenURI = { uri, type ->
                            uriPresenter.handle(UriPresenter.Open(uri, type))
                        },
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun TimelineTab(
    goToBottomSheet: suspend (SheetContentState) -> Unit,
    goToProfile: (String) -> Unit,
    goToTag: (String) -> Unit,
    domain: String?,
    events: MutableSharedFlow<TimelinePresenter.HomeEvent>,
    submitEvents: MutableSharedFlow<SubmitPresenter.SubmitEvent>,
    tabToLoad: FeedType,
    items: LazyPagingItems<UI>?,
    currentAccount: Account?,
    goToConversation: (UI) -> Unit,
    onReplying: (Boolean) -> Unit,
    onProfileClick: (accountId: String, isCurrent: Boolean) -> Unit,
    refresh: Boolean,
    doneRefreshing: () -> Unit,
    onOpenURI: (URI, FeedType) -> Unit,
) {
    val dim = LocalMastanThemeUiIo.current.dim
    val colorScheme = MaterialTheme.colorScheme
    LaunchedEffect(key1 = tabToLoad, key2 = domain, key3 = tabToLoad.tagName) {
        events.tryEmit(TimelinePresenter.Load(tabToLoad, colorScheme = colorScheme, dim = dim))
    }

    val refreshing = items?.loadState?.refresh is LoadState.Loading
    val pullRefreshState = rememberPullRefreshState(refreshing, {
        items?.refresh()
    })

    Box(
        Modifier
            .pullRefresh(pullRefreshState)
            .fillMaxSize()
    ) {

        items?.let {
            val lazyListState = rememberLazyListState()
            if (refresh) {
                LaunchedEffect(key1 = Unit) {
                    lazyListState.scrollToItem(0)
                    items.refresh()
                    doneRefreshing()
                }
            }
            LaunchedEffect(key1 = tabToLoad, key2 = domain) {
                //very unexact way to run after the first append/prepend ran
                //otherwise infinite scroll never calls append on first launch
                // and I have no idea why
                delay(200)
                if (items.itemCount == 0) items.refresh()
            }
            TimelineRows(
                goToBottomSheet = goToBottomSheet,
                goToProfile,
                goToTag,
                items,
                currentAccount = currentAccount,
                replyToStatus = {
                    submitEvents.tryEmit(it.toSubmitPostMessage())
                },
                boostStatus = { statusId, boosted ->
                    submitEvents.tryEmit(
                        SubmitPresenter
                            .BoostMessage(statusId, tabToLoad, boosted)
                    )
                },
                favoriteStatus = { statusId, favourited ->
                    submitEvents.tryEmit(
                        SubmitPresenter
                            .FavoriteMessage(statusId, tabToLoad, favourited)
                    )
                },
                onReplying = onReplying,
                goToConversation = goToConversation,
                onProfileClick = onProfileClick,
                lazyListState,
                onVote = { statusId, pollId, choices ->
                    submitEvents.tryEmit(SubmitPresenter.VotePoll(statusId, pollId, choices))
                },
                onOpenURI = onOpenURI,
            )
        }
        CustomViewPullRefreshView(
            pullRefreshState, refreshTriggerDistance = 4.dp, isRefreshing = refreshing
        )
    }
}

private val SaveMap = mutableMapOf<String, ScrollKeyParams>()

private data class ScrollKeyParams(
    val value: Int
)


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TimelineRows(
    goToBottomSheet: suspend (SheetContentState) -> Unit,
    goToProfile: (String) -> Unit,
    goToTag: (String) -> Unit,
    ui: LazyPagingItems<UI>,
    currentAccount: Account?,
    replyToStatus: (PostNewMessageUI) -> Unit,
    boostStatus: (remoteId: String, boosted: Boolean) -> Unit,
    favoriteStatus: (remoteId: String, favourited: Boolean) -> Unit,
    onReplying: (Boolean) -> Unit,
    goToConversation: (UI) -> Unit,
    onProfileClick: (accountId: String, isCurrent: Boolean) -> Unit,
    lazyListState: LazyListState,
    onVote: (statusId: String, pollId: String, choices: List<Int>) -> Unit,
    onOpenURI: (URI, FeedType) -> Unit,
) {
    val dim = LocalMastanThemeUiIo.current.dim
    Crossfade(targetState = ui, label = "") { item ->

        if (item.itemCount == 0) {
            if (ui.loadState.append.endOfPaginationReached) {
                Text(
                    modifier = Modifier.padding(dim.paddingSize8),
                    text = "This page is empty!",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            } else {
                LazyColumn {
                    items(3) {
                        TimelineCard(
                            goToBottomSheet = goToBottomSheet,
                            goToProfile = goToProfile,
                            goToTag = goToTag,
                            ui = null,
                            account = null,
                            replyToStatus = replyToStatus,
                            boostStatus = boostStatus,
                            favoriteStatus = favoriteStatus,
                            goToConversation = goToConversation,
                            onReplying = onReplying,
                            onProfileClick = onProfileClick,
                            onVote = onVote,
                            onOpenURI = onOpenURI,
                        )
                    }
                }
            }
        } else {
            LazyColumn(state = lazyListState) {
                lazyItems(
                    items = item,
                    key = { "${it.originalId}  ${it.boostCount} ${it.replyCount}" }) {
                    TimelineCard(
                        goToBottomSheet = goToBottomSheet,
                        goToProfile = goToProfile,
                        goToTag = goToTag,
                        ui = it,
                        account = currentAccount,
                        replyToStatus = replyToStatus,
                        boostStatus = boostStatus,
                        favoriteStatus = favoriteStatus,
                        goToConversation = goToConversation,
                        onReplying = onReplying,
                        onProfileClick = onProfileClick,
                        onVote = onVote,
                        onOpenURI = onOpenURI,
                    )
                }
            }
        }
    }
}

fun PostNewMessageUI.toSubmitPostMessage(): SubmitPresenter.PostMessage = SubmitPresenter.PostMessage(
    content = content,
    visibility = visibility,
    replyStatusId = replyStatusId,
    replyCount = replyCount,
    uris = uris,
    pollOptions = pollOptions,
    pollExpiresIn = pollExpiresIn,
    pollMultipleChoices = pollMultipleChoices,
    pollHideTotals = pollHideTotals
)
