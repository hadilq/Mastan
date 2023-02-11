package com.androiddev.social.timeline.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.androiddev.social.R
import com.androiddev.social.timeline.ui.model.UI
import me.saket.swipe.SwipeAction
import me.saket.swipe.SwipeableActionsBox

@Composable
fun Timeline(ui: List<UI>) {
    LazyColumn {
        ui.forEach {
            item { TimelineCard(it) }
//            item { TimelineCard(it.copy(directMessage = true)) }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterial3Api::class)
@Composable
fun TimelineCard(ui: UI) {
    val rocket = SwipeAction(
        icon = painterResource(id = R.drawable.rocket3),
        background = colorScheme.tertiary.copy(alpha = .5f),
        onSwipe = { }
    )

    val reply = SwipeAction(
        icon = painterResource(id = R.drawable.reply_o),
        background = colorScheme.tertiary.copy(alpha = .5f),
        onSwipe = { }
    )

    val replyAll = SwipeAction(
        icon = painterResource(id = R.drawable.reply_all),
        background = colorScheme.tertiary.copy(alpha = .5f),
        isUndo = true,
        onSwipe = { },
    )

    SwipeableActionsBox(
        startActions = listOf(rocket),
        endActions = listOf(reply,replyAll)
    ) {
        Column(
            Modifier
                .background(colorScheme.primary.copy(alpha = .7f))
        ) {
            DirectMessage(ui.directMessage)
            Boosted(ui.boostedBy)
            UserInfo(ui)
            ContentRow(ui)
        }
    }
}

@Composable
private fun UserInfo(ui: UI) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        ui.avatar?.let { Avatar(52.dp, it) }
        Column(Modifier.padding(start = 8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text( color= colorScheme.secondary,
                    modifier = Modifier.padding(bottom = 4.dp),
                    text = ui.displayName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Text(color= colorScheme.secondary, text = ui.timePosted, fontSize = 18.sp)
            }
            Text(color= colorScheme.secondary, text = ui.userName, fontSize = 14.sp)
        }
    }
}
