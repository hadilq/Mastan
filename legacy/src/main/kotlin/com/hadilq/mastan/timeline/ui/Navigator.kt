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

import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Column
import androidx.compose.material.IconButton
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.navigation
import com.hadilq.mastan.AuthRequiredComponent
import com.hadilq.mastan.UserComponent
import com.hadilq.mastan.UserParentComponentProvider
import com.hadilq.mastan.accounts.AccountTab
import com.hadilq.mastan.auth.AccessTokenRequest
import com.hadilq.mastan.auth.LocalAuthLogicIo
import com.hadilq.mastan.auth.LoggedInAccountsState
import com.hadilq.mastan.auth.data.UserManagerProvider
import com.hadilq.mastan.navigation.LocalNavigationLogicIo
import com.hadilq.mastan.navigation.NavigationLogicIo
import com.hadilq.mastan.navigation.SelectServerNavigationEvent
import com.hadilq.mastan.network.dto.Account
import com.hadilq.mastan.search.SearchPresenter
import com.hadilq.mastan.search.SearchScreen
import com.hadilq.mastan.theme.LocalMastanThemeUiIo
import com.hadilq.mastan.timeline.ui.model.UI
import dev.marcellogalhardo.retained.compose.retain
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


@Composable
fun getUserComponent(accessTokenRequest: AccessTokenRequest): UserComponent {
    val userManager =
        ((LocalContext.current.applicationContext as UserParentComponentProvider).userParentComponent as UserManagerProvider).getUserManager()
    return userManager.userComponentFor(accessTokenRequest = accessTokenRequest)
}

@Composable
fun AuthScoped(
    content: @Composable (
        userComponent: UserComponent,
        component: AuthRequiredInjector,
        accessTokenRequest: AccessTokenRequest,
    ) -> Unit,
) {
    val authOutput = LocalAuthLogicIo.current

    val state = authOutput.state
    val accessTokenRequest = if (state is LoggedInAccountsState) {
        state.currentUser.accessTokenRequest
    } else {
        error("User must be logged in in this place!")
    }
    val userComponent = getUserComponent(accessTokenRequest)
    CompositionLocalProvider(LocalUserComponent provides userComponent) {
        val component = retain(
            key = accessTokenRequest.code
        ) { (userComponent as AuthRequiredComponent.ParentComponent).createAuthRequiredComponent() } as AuthRequiredInjector
        CompositionLocalProvider(LocalAuthComponent provides component) {
            content(userComponent, component, accessTokenRequest)
        }
    }
}

@OptIn(
    ExperimentalAnimationApi::class
)
@Composable
fun Navigator(
    navController: NavHostController,
    navigation: NavigationLogicIo,
    onChangeTheme: () -> Unit,
) {

    val dim = LocalMastanThemeUiIo.current.dim
    val scope = rememberCoroutineScope()
    AnimatedNavHost(
        navController = navController,
        startDestination = "home",
        enterTransition = { slideIntoContainer(AnimatedContentScope.SlideDirection.Start) },
        exitTransition = { slideOutOfContainer(AnimatedContentScope.SlideDirection.End) },
        modifier = Modifier,
    ) {
        navigation(
            startDestination = "timeline",
            route = "home"
        ) {
            composable("timeline", enterTransition = { fadeIn() }, exitTransition = { fadeOut() }) {
                AuthScoped { userComponent, _, accessTokenRequest ->
                    CompositionLocalProvider(LocalUserComponent provides userComponent) {
                        TimelineScreen(
                            navController = navController,
                            accessTokenRequest = accessTokenRequest,
                            userComponent,
                            onChangeTheme,
                            onNewAccount = {
                                scope.launch { navigation.eventSink(SelectServerNavigationEvent) }
                            },
                            onProfileClick = { accountId, isCurrent ->
                                if (isCurrent)
                                    navController.navigate(
                                        "profile/${accountId}"
                                    )
                                else
                                    navController.navigate("login/${it.arguments?.getString("server")}")
                            },
                            goToMentions = {
                                navController.navigate("mentions")
                            },
                            goToNotifications = {
                                navController.navigate("notifications")
                            },
                            goToSearch = {
                                navController.navigate("search")
                            },
                            goToConversation = { status: UI ->
                                navController.navigate("conversation/${status.remoteId}/${status.type.type}")
                            },
                            goToProfile =
                            { accountId: String ->
                                navController.navigate("profile/${accountId}")
                            },
                            goToTag = { tag: String ->
                                navController.navigate("tag/${tag}")
                            },
                        )
                    }
                }
            }
            composable(
                route = "mentions",
            ) {
                AuthScoped { _, _, accessTokenRequest ->
                    val code = accessTokenRequest.code
                    MentionsScreen(
                        navController = navController,
                        code = code,
                        goToConversation = { status ->
                            navController.navigate("conversation/${status.remoteId}/${status.type.type}")
                        },
                        showBackBar = true,
                        goToProfile = { accountId: String ->
                            navController.navigate("profile/${accountId}")
                        },
                        goToTag = { tag: String ->
                            navController.navigate("tag/${tag}")
                        },
                    )
                }
            }
            composable(
                route = "tag/{tag}",
            ) {
                AuthScoped { _, _, accessTokenRequest ->
                    val code = accessTokenRequest.code
                    TagScreen(
                        navController,
                        code = code,
                        tag = it.arguments?.getString("tag")!!,
                        goToConversation = { status ->
                            navController.navigate("conversation/${status.remoteId}/${status.type.type}")
                        },
                        showBackBar = true,
                        goToProfile = { accountId: String ->
                            navController.navigate("profile/${accountId}")
                        },
                        goToTag = { tag: String ->
                            navController.navigate("tag/${tag}")
                        },
                    )
                }
            }
        }


        composable(
            route = "profile/{accountId}",
        ) {
            val accountId = it.arguments?.getString("accountId")!!
            AuthScoped { _, component, accessTokenRequest ->
                val code = accessTokenRequest.code
                ProfileScreen(
                    component = component,
                    navController = navController,
                    code = code,
                    accountId = accountId,
                    goToFollowers = {
                        navController.navigate("followers/$accountId")
                    },
                    goToFollowing = {
                        navController.navigate("following/$accountId")
                    },
                )
            }

        }

        composable(
            route = "following/{accountId}",
        ) {
            val accountId = it.arguments?.getString("accountId")!!
            AuthScoped { _, component, accessTokenRequest ->
                val code = accessTokenRequest.code
                val followerPresenter = component.followerPresenter()

                LaunchedEffect(key1 = accountId) {
                    followerPresenter.start()
                }
                LaunchedEffect(key1 = accountId) {
                    followerPresenter.handle(FollowerPresenter.Load(accountId, true))
                }

                followerPresenter.model.accounts?.let { account ->
                    FollowerScreen(account, navController = navController, code)
                }

            }

        }

        composable(
            route = "followers/{accountId}",
        ) {
            val accountId = it.arguments?.getString("accountId")!!
            AuthScoped { _, component, accessTokenRequest ->
                val code = accessTokenRequest.code
                val followerPresenter = component.followerPresenter()

                LaunchedEffect(key1 = accountId) {
                    followerPresenter.start()
                }
                LaunchedEffect(key1 = accountId) {
                    followerPresenter.handle(
                        FollowerPresenter.Load(
                            accountId,
                            following = false
                        )
                    )
                }

                followerPresenter.model.accounts?.let { account ->
                    FollowerScreen(account, navController = navController, code)
                }
            }

        }

        composable(
            route = "search",
        ) {
            AuthScoped { _, component: AuthRequiredInjector, accessTokenRequest ->
                val code = accessTokenRequest.code
                val searchPresenter = component.searchPresenter()
                val searchScope = rememberCoroutineScope()
                LaunchedEffect(key1 = code) {
                    searchPresenter.start(searchScope)
                }
                val colorScheme = MaterialTheme.colorScheme
                LaunchedEffect(key1 = code) {
                    searchPresenter.handle(SearchPresenter.Init(colorScheme, dim))
                }
                val uriPresenter = remember { component.urlPresenter().get() }
                LaunchedEffect(key1 = code) {
                    uriPresenter.start()
                }
                OpenHandledUri(uriPresenter, navController)

                SearchScreen(
                    searchPresenter.model,
                    navController = navController,
                    uriPresenter = uriPresenter,
                    onQueryChange = { searchTerm: String ->
                        searchPresenter.onQueryTextChange(searchTerm)
                    },
                    goToProfile = { accountId: String ->
                        navController.navigate("profile/${accountId}")
                    },
                    goToTag = { tag: String ->
                        navController.navigate("tag/${tag}")
                    },
                    goToConversation = { status ->
                        navController.navigate("conversation/${status.remoteId}/${status.type.type}")
                    },
                )
            }

        }

        composable(
            route = "conversation/{statusId}/{type}",
        ) {
            val statusId = it.arguments?.getString("statusId")!!
            val type = it.arguments?.getString("type")!!
            AuthScoped { _, _, _ ->
                ConversationScreen(
                    navController = navController,
                    statusId = statusId,
                    type = type,
                    goToConversation = { status ->
                        if (statusId != status.remoteId) {
                            navController.navigate("conversation/${status.remoteId}/${status.type.type}")
                        }
                    },
                    goToProfile = { accountId ->
                        navController.navigate("profile/${accountId}")
                    },
                    goToTag = { tag ->
                        navController.navigate("tag/${tag}")
                    },
                )
            }
        }
        composable(
            route = "notifications",
        ) {
            AuthScoped { _, _, accessTokenRequest ->
                val code = accessTokenRequest.code
                NotificationsScreen(
                    navController = navController,
                    code = code,
                    goToConversation = { status: UI ->
                        navController.navigate("conversation/${status.remoteId}/${status.type}") {
                        }
                    },
                    goToProfile = { accountId: String ->
                        navController.navigate("profile/${accountId}")
                    },
                    goToTag = { tag ->
                        navController.navigate("tag/${tag}")
                    },
                )
            }
        }
    }
}

@Composable
fun FollowerScreen(
    accounts: Flow<PagingData<Account>>,
    navController: NavHostController,
    code: String,
) {
    Column {
        TopAppBar(
            backgroundColor = MaterialTheme.colorScheme.surface,
            title = {
                Text(
                    text = "Followers",
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            navigationIcon = {
                if (navController.previousBackStackEntry != null) {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(
                            tint = MaterialTheme.colorScheme.onSurface,
                            imageVector = Icons.Filled.ArrowBack,
                            contentDescription = "search"
                        )
                    }
                }
            }
        )
        val pagingItems = accounts.collectAsLazyPagingItems()
        AccountTab(
            results = null,
            resultsPaging = pagingItems,
            goToProfile = { accountId: String ->
                navController.navigate("profile/$accountId")
            })

    }


}
