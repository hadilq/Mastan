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
@file:OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialApi::class)

package com.hadilq.mastan.timeline.ui

import android.util.Log
import android.webkit.URLUtil
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material.ContentAlpha
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.hadilq.mastan.network.dto.Account
import com.hadilq.mastan.timeline.data.FeedType
import com.hadilq.mastan.timeline.data.LinkListener
import com.hadilq.mastan.timeline.ui.model.PollHashUI
import com.hadilq.mastan.timeline.ui.model.PollUI
import com.hadilq.mastan.timeline.ui.model.UI
import com.google.accompanist.placeholder.PlaceholderHighlight
import com.google.accompanist.placeholder.material3.placeholder
import com.google.accompanist.placeholder.shimmer
import me.saket.swipe.SwipeAction
import com.hadilq.mastan.legacy.R
import com.hadilq.mastan.theme.LocalMastanThemeUiIo
import java.lang.Integer.min
import java.net.URI

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun TimelineCard(
    modifier: Modifier = Modifier,
    goToBottomSheet: suspend (SheetContentState) -> Unit,
    goToProfile: (String) -> Unit,
    goToTag: (String) -> Unit,
    ui: UI?,
    mainConversationStatusId: String? = null,
    account: Account?,
    replyToStatus: (PostNewMessageUI) -> Unit,
    boostStatus: (remoteId: String, boosted: Boolean) -> Unit,
    favoriteStatus: (remoteId: String, favourited: Boolean) -> Unit,
    goToConversation: (UI) -> Unit,
    onReplying: (Boolean) -> Unit,
    onProfileClick: (accountId: String, isCurrent: Boolean) -> Unit = { a, b -> },
    onVote: (statusId: String, pollId: String, choices: List<Int>) -> Unit,
    onOpenURI: (URI, FeedType) -> Unit,
) {

    val dim = LocalMastanThemeUiIo.current.dim
    val urlHandlerMediator = LocalUserComponent.current.urlHandlerMediator()

    val cardModifier = if (
        mainConversationStatusId != null &&
        mainConversationStatusId == ui?.remoteId
    ) {
        modifier.background(color = colorScheme.tertiaryContainer)
    } else {
        modifier
    }

    Column(
        cardModifier
            .padding(
                bottom = dim.paddingSize1,
                start = dim.paddingSize1,
                end = dim.paddingSize1,
                top = dim.paddingSize1
            ),
    ) {

        var showReply by remember(ui) { mutableStateOf(false) }
        UserInfo(ui, goToProfile, onProfileClick = onProfileClick)
        Column {
            Row(
                Modifier
                    .padding(bottom = dim.paddingSize1),
            ) {
                ui?.let { status ->
                    val magicNumber = 2
                    repeat(min(magicNumber , status.replyIndention)) {
                        Text(
                            text = "+",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = colorScheme.onSurface
                            )
                        )
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .padding(end = dim.paddingSize0_5)
                                .width(dim.thickSm)
                                .background(color = colorScheme.onSurface)
                        )
                    }
                    if (status.replyIndention > magicNumber) {
                        Text(
                            text = if (status.replyIndention - magicNumber == 1) {
                                "+"
                            } else {
                                "${status.replyIndention - magicNumber}+"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = colorScheme.onSurface
                            )
                        )
                        Box(
                            Modifier
                                .fillMaxHeight()
                                .padding(end = dim.paddingSize0_5)
                                .width(dim.thickSm)
                                .background(color = colorScheme.onSurface)
                        )
                    }
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val emojiText = ui?.contentEmojiText
                    val mapping = emojiText?.mapping
                    val text = emojiText?.text
                    var clicked by remember(ui) { mutableStateOf(false) }
                    LaunchedEffect(clicked) {
                        if (clicked) onReplying(false)
                    }

                    Box(
                        modifier = Modifier
                            .placeholder(
                                color = colorScheme.surfaceColorAtElevation(
                                    LocalAbsoluteTonalElevation.current + 8.dp
                                ),
                                visible = ui == null,
                                shape = RoundedCornerShape(8.dp),
                                highlight = PlaceholderHighlight.shimmer(
                                    highlightColor = colorScheme.tertiary.copy(alpha = 0.2f)
                                ),
                            )
                    ) {

                        ClickableText(
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = colorScheme.onSurface,
                                lineHeight = 18.sp
                            ),
                            modifier = Modifier
                                .fillMaxWidth(),
                            text = text ?: buildAnnotatedString { },
                            onClick = {
                                clicked = !clicked
                                showReply = false
                                onReplying(false)
                                val annotation = text!!
                                    .getStringAnnotations(
                                        tag = "URL", start = it,
                                        end = it
                                    )
                                    .firstOrNull()

                                urlHandlerMediator.givenUri(
                                    ui = ui,
                                    uri = annotation?.item,
                                    isValidUrl = URLUtil::isValidUrl,
                                    onOpenURI = onOpenURI,
                                    goToTag = goToTag,
                                    goToProfile = goToProfile,
                                    goToConversation = goToConversation,
                                )
                            },
                            inlineContent = mapping ?: emptyMap()
                        )
                    }

                    if (ui?.poll?.options != null && ui.poll.options.isNotEmpty()) {
                        Log.d("qqqq", "ui id: ${ui.originalId}, ${ui.remoteId}")
                        PollVoter(
                            style = MaterialTheme.typography.bodyLarge.copy(
                                color = colorScheme.onSurface,
                                lineHeight = 18.sp
                            ),
                            modifier = Modifier
                                .fillMaxWidth(),
                            poll = ui.poll,
                            options = ui.poll.options,
                            onClick = { choices ->
                                onVote(ui.remoteId, ui.poll.remoteId, choices)
                            },
                        )
                    }

                    ContentImage(url = ui?.attachments?.mapNotNull { it.url } ?: emptyList())
                    val toolbarHeight = dim.paddingSize6
                    val toolbarHeightPx =
                        with(LocalDensity.current) {
                            toolbarHeight.roundToPx().toFloat()
                        }
                    val toolbarOffsetHeightPx = remember(ui) { mutableStateOf(0f) }
                    val nestedScrollConnection = remember(ui) {
                        object : NestedScrollConnection {
                            override fun onPreScroll(
                                available: Offset,
                                source: NestedScrollSource
                            ): Offset {
                                val delta = available.y
                                val newOffset = toolbarOffsetHeightPx.value + delta
                                toolbarOffsetHeightPx.value =
                                    newOffset.coerceIn(-toolbarHeightPx, 0f)
                                return Offset.Zero
                            }
                        }
                    }

                    ui?.card?.let { card ->
                        ContentCard(
                            card = card,
                            feedType = ui.type,
                            onOpenURI = onOpenURI,
                        )
                    }

                    AnimatedVisibility(visible = showReply) {
                        var mentions =
                            ui?.mentions?.map { mention -> mention.username }
                                ?.toMutableList() ?: mutableListOf()

                        mentions.add(ui?.userName ?: "")
                        mentions = mentions.map { "@${it}" }.toMutableList()
                        Column {
                            IconButton(
                                modifier = Modifier
                                    .padding(horizontal = dim.paddingSize1)
                                    .align(Alignment.End),
                                onClick = {
                                    showReply = false
                                    onReplying(false)
                                }
                            ) {
                                Icon(
                                    tint = colorScheme.onSurface,
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = "Close reply"
                                )
                            }
                            UserInput(
                                status = ui,
                                account = account,
                                connection = nestedScrollConnection,
                                goToBottomSheet = goToBottomSheet,
                                onMessageSent = {
                                    ui?.let { ui ->
                                        replyToStatus(
                                            it.copy(
                                                replyStatusId = ui.remoteId,
                                                replyCount = ui.replyCount,
                                            )
                                        )
                                    }
                                    showReply = false
                                    onReplying(false)
                                },
                                defaultVisiblity = "Public",
                                participants = mentions.joinToString(" "),
                                showReplies = true,
                                isVerticalScrollHandled = true,
                                goToConversation = goToConversation,
                                goToProfile = goToProfile,
                                goToTag = goToTag,
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier.placeholder(
                    color = colorScheme.surfaceColorAtElevation(
                        LocalAbsoluteTonalElevation.current + 8.dp
                    ),
                    visible = ui == null,
                    shape = RoundedCornerShape(8.dp),
                    highlight = PlaceholderHighlight.shimmer(
                        highlightColor = colorScheme.onSurface.copy(alpha = 0.2f)
                    ),
                )
            ) {
                val current = LocalAuthComponent.current
                var justBookmarked by remember { mutableStateOf(false) }

                ButtonBar(
                    status = ui,
                    account = account,
                    replyCount = ui?.replyCount,
                    boostCount = ui?.boostCount,
                    favoriteCount = ui?.favoriteCount,
                    favorited = ui?.favorited,
                    boosted = ui?.boosted,
                    hasParent = ui?.inReplyTo != null,
                    goToBottomSheet = goToBottomSheet,
                    onBoost = {
                        boostStatus(ui!!.remoteId, ui.boosted)
                    },
                    onFavorite = {
                        favoriteStatus(ui!!.remoteId, ui.favorited)
                    },
                    onReply = {
                        showReply = !showReply
                        onReplying(showReply)
                    },
                    onShowReplies = {
                        goToConversation(ui!!)
                    },
                    bookmarked = ui?.bookmarked ?: false || justBookmarked,
                    onBookmark = {
                        justBookmarked = true
                        current.submitPresenter()
                            .handle(SubmitPresenter.BookmarkMessage(ui!!.remoteId, ui.type))
                    }
                )
            }
        }
    }
    Divider()
}


@Composable
fun UserInfo(
    ui: UI?,
    goToProfile: (String) -> Unit,
    onProfileClick: (accountId: String, isCurrent: Boolean) -> Unit = { a, b -> }
) {
    val dim = LocalMastanThemeUiIo.current.dim
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = dim.paddingSize1, start = 60.dp)
//            .placeholder(
//                color = colorScheme.surfaceColorAtElevation(
//                    LocalAbsoluteTonalElevation.current + 8.dp
//                ),
//                visible = ui == null,
//                shape = RoundedCornerShape(8.dp),
//                highlight = PlaceholderHighlight.shimmer(
//                    highlightColor = colorScheme.tertiary.copy(alpha = 0.2f)
//                ),
//            )
    ) {
        if (ui?.directMessage != null) {
            DirectMessage(ui.directMessage)
        }
        if (ui?.boostedBy != null)
            Boosted(
                ui.boostedEmojiText, R.drawable.rocket3,
                containerColor = colorScheme.surface
            ) {
                onProfileClick(ui.boostedById!!, true)
            }

    }
    Row(
        Modifier
            .fillMaxWidth()
            .padding(bottom = dim.paddingSize1)
            .clickable {
                ui?.accountId?.let { goToProfile(it) }
            },
        horizontalArrangement = Arrangement.Start
    ) {
        AvatarImage(dim.paddingSize7, ui?.avatar, onClick = { goToProfile(ui?.accountId!!) })
        Column(Modifier.padding(start = dim.paddingSize1)) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = dim.paddingSize0_5)
                    .placeholder(
                        color = colorScheme.surfaceColorAtElevation(
                            LocalAbsoluteTonalElevation.current + 8.dp
                        ),
                        visible = ui == null,
                        shape = RoundedCornerShape(8.dp),
                        highlight = PlaceholderHighlight.shimmer(
                            highlightColor = colorScheme.onSurface.copy(alpha = 0.2f)
                        ),
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {

                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.secondary,
                    modifier = Modifier
                        .fillMaxWidth(.6f)
                        .align(Alignment.Top),
                    text = ui?.accountEmojiText?.text ?: buildAnnotatedString { },
                    style = MaterialTheme.typography.bodyLarge,
                    inlineContent = ui?.accountEmojiText?.mapping ?: emptyMap(),
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .placeholder(
                        color = colorScheme.surfaceColorAtElevation(
                            LocalAbsoluteTonalElevation.current + 8.dp
                        ),
                        visible = ui == null,
                        shape = RoundedCornerShape(8.dp),
                        highlight = PlaceholderHighlight.shimmer(
                            highlightColor = colorScheme.onSurface.copy(alpha = 0.2f)
                        ),
                    ),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    color = colorScheme.secondary,
                    text = ui?.userName ?: "",
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier
                        .padding(end = dim.paddingSize1)
                        .weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    color = colorScheme.secondary,
                    text = ui?.timePosted ?: "",
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 1,
                )
            }
        }
    }
}

@Composable
fun rocket() = SwipeAction(
    icon = {
        androidx.compose.foundation.Image(
            modifier = Modifier.size(LocalMastanThemeUiIo.current.dim.paddingSize10),
            painter = painterResource(R.drawable.rocket3),
            contentDescription = "",
            colorFilter = ColorFilter.tint(colorScheme.tertiary)
        )
    },
    background = colorScheme.tertiaryContainer,
    onSwipe = { }
)

@Composable
fun reply() = SwipeAction(
    icon = {
        androidx.compose.foundation.Image(
            modifier = Modifier.size(LocalMastanThemeUiIo.current.dim.paddingSize10),
            painter = painterResource(R.drawable.reply),
            contentDescription = "",
            colorFilter = ColorFilter.tint(colorScheme.tertiary)
        )
    },
    background = colorScheme.tertiaryContainer,
    onSwipe = { }
)

@Composable
fun replyAll() = SwipeAction(
    icon = {
        androidx.compose.foundation.Image(
            modifier = Modifier.size(LocalMastanThemeUiIo.current.dim.paddingSize10),
            painter = painterResource(R.drawable.reply_all),
            contentDescription = "",
            colorFilter = ColorFilter.tint(colorScheme.tertiary)
        )
    },
    background = colorScheme.tertiaryContainer,
    isUndo = true,
    onSwipe = { },
)

val empty = object : LinkListener {
    override fun onViewTag(tag: String) {
        TODO("Not yet implemented")
    }

    override fun onViewAccount(id: String) {
        TODO("Not yet implemented")
    }

    override fun onViewUrl(url: String) {
        TODO("Not yet implemented")
    }
}

@Composable
fun ClickableText(
    text: AnnotatedString,
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    softWrap: Boolean = true,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    onClick: (Int) -> Unit,
    inlineContent: Map<String, InlineTextContent> = mapOf(),
) {
    val layoutResult = remember(text) { mutableStateOf<TextLayoutResult?>(null) }
    val pressIndicator = Modifier.pointerInput(onClick) {
        detectTapGestures { pos ->
            layoutResult.value?.let { layoutResult ->
                onClick(layoutResult.getOffsetForPosition(pos))
            }
        }
    }

    BasicText(
        text = text,
        modifier = modifier.then(pressIndicator),
        style = style,
        softWrap = softWrap,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = {
            layoutResult.value = it
            onTextLayout(it)
        },
        inlineContent = inlineContent
    )
}

@Composable
private fun PollVoter(
    modifier: Modifier = Modifier,
    style: TextStyle = TextStyle.Default,
    poll: PollUI,
    options: List<PollHashUI>,
    onClick: (choices: List<Int>) -> Unit,
) {

    var disabled by remember { mutableStateOf(poll.expired || (poll.voted == true && poll.ownVotes != null)) }

    if (poll.multiple) {
        MultipleChoicePollVoter(
            style = style,
            modifier = modifier,
            content = poll.content,
            ownVotes = poll.ownVotes?.toSet() ?: emptySet(),
            options = options,
            disabled = disabled,
            onClick = {
                disabled = true
                onClick(it)
            },
        )
    } else {
        SingleChoicePollVoter(
            style = style,
            modifier = modifier,
            content = poll.content,
            ownVote = poll.ownVotes?.firstOrNull(),
            options = options,
            disabled = disabled,
            onClick = {
                disabled = true
                onClick(listOf(it))
            }
        )
    }
}

@Composable
private fun MultipleChoicePollVoter(
    style: TextStyle,
    modifier: Modifier,
    content: String?,
    ownVotes: Set<Int>,
    options: List<PollHashUI>,
    disabled: Boolean,
    onClick: (List<Int>) -> Unit,
) {
    val dim = LocalMastanThemeUiIo.current.dim
    val selected = remember {
        mutableStateListOf(*List(options.size) { index -> ownVotes.contains(index) }.toTypedArray())
    }
    var clicked by remember { mutableStateOf(disabled) }
    val imageSize: Float by animateFloatAsState(
        targetValue = if (clicked) 1.1f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioHighBouncy,
            stiffness = Spring.StiffnessMedium // with medium speed
        )
    )
    Column(
        modifier = Modifier
            .padding(dim.paddingSize1)
            .fillMaxWidth()
    ) {
        options.forEachIndexed { index, option ->
            MultiChoicePollOptionVoter(
                modifier = modifier,
                style = style,
                selected = selected[index],
                option = option,
                disabled = disabled,
                onClick = {
                    selected[index] = !selected[index]
                }
            )
        }
        content?.let {
            Text(
                color = colorScheme.secondary,
                style = if (disabled) {
                    style.copy(color = style.color.copy(alpha = ContentAlpha.disabled))
                } else style,
                text = it
            )
        }

        Button(
            modifier = Modifier
                .padding(end = 20.dp, top = 8.dp, bottom = 2.dp)
                .wrapContentSize()
                .align(Alignment.End),
            elevation = androidx.compose.material3.ButtonDefaults.buttonElevation(
                defaultElevation = dim.paddingSize3,
                pressedElevation = dim.paddingSize1,
                disabledElevation = dim.paddingSize3
            ),
            enabled = !disabled,
            onClick = {
                onClick(selected.mapIndexedNotNull { index, s -> if (s) index else null })
                clicked = true
            },
            shape = CircleShape,
            contentPadding = PaddingValues(dim.paddingSize1)
        ) {
            Row(Modifier.padding(4.dp)) {
                Image(
                    modifier = Modifier
                        .size(24.dp)
                        .scale(2f * imageSize)
                        .rotate(imageSize * -45f)
                        .offset(y = (0).dp, x = (2).dp)
                        .rotate(50f)
                        .padding(start = 2.dp, end = 2.dp),
                    painter = painterResource(R.drawable.horn),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(colorScheme.background),
                )
            }

        }
    }
}

@Composable
private fun SingleChoicePollVoter(
    style: TextStyle,
    modifier: Modifier,
    content: String?,
    ownVote: Int?,
    options: List<PollHashUI>,
    disabled: Boolean,
    onClick: (Int) -> Unit,
) {
    val dim = LocalMastanThemeUiIo.current.dim
    val selected = remember {
        mutableStateListOf(*List(options.size) { index -> ownVote == index }.toTypedArray())
    }
    Column(
        modifier = Modifier
            .padding(dim.paddingSize1)
            .fillMaxWidth()
    ) {
        options.forEachIndexed { index, option ->
            SingleChoicePollOptionVoter(
                modifier = modifier,
                style = style,
                selected = selected[index],
                option = option,
                disabled = disabled,
                onClick = {
                    selected[index] = !selected[index]
                    onClick(index)
                },
            )
        }
        content?.let {
            Text(
                color = colorScheme.secondary,
                style = if (disabled) {
                    style.copy(color = style.color.copy(alpha = ContentAlpha.disabled))
                } else style,
                text = it
            )
        }
    }
}

@Composable
private fun MultiChoicePollOptionVoter(
    modifier: Modifier,
    style: TextStyle,
    option: PollHashUI,
    disabled: Boolean,
    selected: Boolean,
    onClick: (Boolean) -> Unit,
) {
    val dim = LocalMastanThemeUiIo.current.dim
    Row(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        Checkbox(
            checked = selected,
            modifier = Modifier
                .padding(horizontal = dim.paddingSize1)
                .align(Alignment.CenterVertically),
            enabled = !disabled,
            onCheckedChange = {
                onClick(selected)
            }
        )

        ClickableText(
            text = option.voteContent,
            modifier = Modifier
                .padding(dim.paddingSize1)
                .weight(1f)
                .alignByBaseline(),
            style = if (disabled) style.copy(color = style.color.copy(alpha = ContentAlpha.disabled)) else style,
            onClick = {
                if (disabled) return@ClickableText
                onClick(selected)
            },
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )

        if (disabled) {
            Text(
                text = option.percentage,
                modifier = Modifier
                    .padding(dim.paddingSize1)
                    .alignByBaseline(),
                style = style,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun SingleChoicePollOptionVoter(
    modifier: Modifier,
    style: TextStyle,
    option: PollHashUI,
    disabled: Boolean,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth(),
    ) {
        val dim = LocalMastanThemeUiIo.current.dim
        RadioButton(
            selected = selected,
            modifier = Modifier
                .padding(horizontal = dim.paddingSize1)
                .align(Alignment.CenterVertically),
            enabled = !disabled,
            onClick = {
                onClick()
            },
        )

        ClickableText(
            text = option.voteContent,
            modifier = Modifier
                .padding(dim.paddingSize0_5)
                .weight(1f)
                .alignByBaseline(),
            style = if (disabled) style.copy(color = style.color.copy(alpha = ContentAlpha.disabled)) else style,
            onClick = {
                if (disabled) return@ClickableText
                onClick()
            },
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )

        if (disabled) {
            Text(
                text = option.percentage,
                modifier = Modifier
                    .padding(dim.paddingSize0_5)
                    .alignByBaseline(),
                style = style,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
