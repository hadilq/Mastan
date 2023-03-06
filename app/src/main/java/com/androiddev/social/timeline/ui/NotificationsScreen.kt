package com.androiddev.social.timeline.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.androiddev.social.timeline.data.FeedType
import com.androiddev.social.timeline.data.Type
import com.androiddev.social.timeline.data.mapStatus
import com.androiddev.social.timeline.data.toStatusDb
import com.androiddev.social.timeline.ui.model.UI
import social.androiddev.R

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun NotificationsScreen(navController: NavHostController, goToConversation: (UI) -> Unit) {
    val component = LocalAuthComponent.current
    val userComponent = LocalUserComponent.current

    val notificationPresenter = component.notificationPresenter()
    LaunchedEffect(key1 = userComponent.request()) {
        notificationPresenter.start()
    }
    LaunchedEffect(key1 = userComponent.request()) {
        notificationPresenter.handle(NotificationPresenter.Load)
    }
    val statuses = notificationPresenter.model.statuses
    LaunchedEffect(key1 = userComponent.request()) {
        component.submitPresenter().start()
    }
    val pullRefreshState = rememberPullRefreshState(false, {
        notificationPresenter.handle(NotificationPresenter.Load)
    })


    Box(
        Modifier
            .background(MaterialTheme.colorScheme.surface)
            .pullRefresh(pullRefreshState)
            .padding(top = 0.dp)
            .fillMaxSize()
    ) {
        LazyColumn(
            Modifier
                .wrapContentHeight()
                .padding(top = 60.dp)
        ) {

            items(statuses, key = { it.id }) {
                Column {
                    if (it.realType == Type.favourite) {
                        Boosted(
                            boostedBy = (if (it.account.displayName.isNullOrEmpty()) it.account.username else it.account.displayName) + " favorited",
                            boostedAvatar = it.account.avatar,
                            boostedEmojis = it.account.emojis,
                            drawable = R.drawable.star
                        )
                    }
                    if (it.realType == Type.reblog) {
                        Boosted(
                            boostedBy = (if (it.account.displayName.isNullOrEmpty()) it.account.username else it.account.displayName) + " boosted",
                            boostedAvatar = it.account.avatar,
                            boostedEmojis = it.account.emojis,
                            drawable = R.drawable.rocket3
                        )
                    }
                    card(
                        modifier = Modifier.background(Color.Transparent),
                        status = it.status!!.toStatusDb(FeedType.Home).mapStatus(),
                        events = component.submitPresenter().events,
                        goToConversation = goToConversation,
                        showInlineReplies = false
                    )
                }

            }
        }
        CustomViewPullRefreshView(
            pullRefreshState, refreshTriggerDistance = 4.dp, isRefreshing = false
        )
        BackBar(navController, "Notifs")
    }

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

