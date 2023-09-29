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

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.TextButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.hadilq.mastan.timeline.data.Account
import com.hadilq.mastan.timeline.ui.model.UI
import kotlinx.coroutines.launch
import com.hadilq.mastan.legacy.R
import com.hadilq.mastan.theme.LocalThemeOutput

@Composable
fun ButtonBar(
    status: UI?,
    account: Account?,
    replyCount: Int? = null,
    boostCount: Int? = null,
    favoriteCount: Int? = null,
    favorited: Boolean? = false,
    boosted: Boolean? = false,
    hasParent: Boolean? = false,
    goToBottomSheet: suspend (SheetContentState) -> Unit,
    onBoost: () -> Unit,
    onFavorite: () -> Unit,
    onReply: () -> Unit,
    onShowReplies: () -> Unit,
    bookmarked: Boolean,
    onBookmark: () -> Unit
) {
    val dim = LocalThemeOutput.current.dim
    Column {
        Row(horizontalArrangement = Arrangement.SpaceEvenly, modifier = Modifier.fillMaxWidth()) {
            TextButton(
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
                contentPadding = PaddingValues(dim.paddingSize1, 0.dp),
                border = BorderStroke(dim.thickSm, Color.Transparent),
                onClick = onReply
            ) {
                Image(
                    modifier = Modifier.size(20.dp),
                    painter = painterResource(R.drawable.reply),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
                )
            }

            SpringyButton(
                onBoost,
                boosted == true,
                R.drawable.rocketfilled,
                R.drawable.rocket3,
                boostCount,
                iconSize = 20.dp
            )
            SpringyButton(
                onFavorite,
                favorited == true,
                R.drawable.starfilled,
                R.drawable.star,
                favoriteCount,
                iconSize = 28.dp
            )
            TextButton(
                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                border = BorderStroke(dim.thickSm, Color.Transparent),
                onClick = onBookmark
            ) {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(if (!bookmarked) R.drawable.bookmark else R.drawable.bookmarkfilled),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
                )
            }

            if ((replyCount != null && replyCount > 0) || hasParent == true) {
                TextButton(
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                    contentPadding = PaddingValues(dim.paddingSize1, 0.dp),
                    onClick = {
                        onShowReplies()
                    }
                ) {
                    Image(
                        modifier = Modifier.size(dim.paddingSize3),
                        painter = painterResource(R.drawable.chat),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
                    )
                    replyCount?.let {
                        Text(
                            color = MaterialTheme.colorScheme.primary,
                            text = " $it"
                        )
                    }
                }
            } else {
                //placeholder I am bad at code
                TextButton(
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
                    contentPadding = PaddingValues(dim.paddingSize1, 0.dp),
                    onClick = {
                        onShowReplies()
                    }
                ) {
                    Image(
                        modifier = Modifier.size(dim.paddingSize3),
                        painter = painterResource(R.drawable.chat),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(Color.Gray)
                    )
                }
            }

            if (status != null) {
                MoreMenu(status, account, goToBottomSheet)
            }
        }
    }

}

@Composable
private fun MoreMenu(
    status: UI,
    account: Account?,
    goToBottomSheet: suspend (SheetContentState) -> Unit,
) {
    val dim = LocalThemeOutput.current.dim
    val coroutineScope = rememberCoroutineScope()
    TextButton(
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
        contentPadding = PaddingValues(dim.paddingSize1, 0.dp),
        onClick = {
            coroutineScope.launch {
                if (status.accountId == account?.id) {
                    goToBottomSheet(
                        SheetContentState.OwnedStatusMenu(status)
                    )
                } else {
                    goToBottomSheet(
                        SheetContentState.StatusMenu(status)
                    )
                }
            }
        }
    ) {
        val dim = LocalThemeOutput.current.dim
        Image(
            modifier = Modifier.size(dim.paddingSize3),
            painter = painterResource(R.drawable.more_vert),
            contentDescription = "",
            colorFilter = ColorFilter.tint(Color.Gray)
        )
    }
}

@Composable
private fun SpringyButton(
    onClick: () -> Unit,
    on: Boolean,
    @DrawableRes onIcon: Int,
    @DrawableRes offIcon: Int,
    count: Int?,
    iconSize:Dp = LocalThemeOutput.current.dim.paddingSize3
) {

    val dim = LocalThemeOutput.current.dim
    var clicked by remember { mutableStateOf(on) }
    var localCount by remember { mutableStateOf(count) }

    val scope = rememberCoroutineScope()

    TextButton(
        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.secondary),
        contentPadding = PaddingValues(dim.paddingSize1, 0.dp),
        onClick = {
            localCount?.let {
                localCount = it + if (clicked) { if (it > 0) -1 else 0 } else 1
            }
            clicked = !clicked
            scope.launch {
                onClick()
            }
        }
    ) {
        val dim = LocalThemeOutput.current.dim
        Image(
            modifier = Modifier
                .padding(end = dim.paddingSize0_5)
                .size(iconSize),
            painter = painterResource(if (clicked) onIcon else offIcon),
            contentDescription = "",
            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary),
        )
        localCount?.let {
            Text(
                color = MaterialTheme.colorScheme.secondary,
                text = " $it"
            )
        }
    }
}
