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
package com.hadilq.mastan.timeline.ui.model

import android.graphics.Typeface
import android.text.Spanned
import android.text.style.CharacterStyle
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.text.style.URLSpan
import android.text.style.UnderlineSpan
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import androidx.core.text.parseAsHtml
import com.hadilq.mastan.theme.PaddingSize2_5
import com.hadilq.mastan.timeline.data.Attachment
import com.hadilq.mastan.timeline.data.Emoji
import com.hadilq.mastan.timeline.data.FeedType
import com.hadilq.mastan.timeline.data.Mention
import com.hadilq.mastan.timeline.data.Status
import com.hadilq.mastan.timeline.data.Tag
import com.hadilq.mastan.timeline.ui.AvatarImage
import com.hadilq.mastan.ui.util.EmojiText

data class UI(
    val imageUrl: String? = null,
    val displayName: String = "FriendlyMike",
    val userName: String = "FriendlyMike@androiddev.social",
    private val content: String = "",
    val sharingUri: String,
    val replyCount: Int = 0,
    val boostCount: Int = 0,
    val favoriteCount: Int = 0,
    val timePosted: String = "3m",
    val boostedBy: String? = null,
    val directMessage: Boolean = false,
    val self: Status? = null,
    val avatar: String? = null,
    val mentions: List<Mention>,
    val tags: List<Tag>,
    val contentEmojis: List<Emoji>?,
    val accountEmojis: List<Emoji>?,
    val boostedEmojis: List<Emoji>?,
    val boostedAvatar: String?,
    val remoteId: String,
    var replyType: ReplyType? = null,
    val type: FeedType,
    val favorited: Boolean,
    val boosted: Boolean,
    val inReplyTo: String?,
    val accountId: String?,
    val boostedById: String?,
    val contentEmojiText: EmojiText?,
    val boostedEmojiText: EmojiText?,
    val accountEmojiText: EmojiText?,
    val originalId: String,
    val bookmarked: Boolean,
    val attachments: List<Attachment>,
    val card: CardUI?,
    val poll: PollUI?,
    val replyIndention: Int = 0,
)

enum class ReplyType {
    PARENT,
    CHILD
}

data class PollUI(
    val remoteId: String,
    val expiresAt: String,
    val expired: Boolean,
    val multiple: Boolean,
    val votesCount: Int? = null,
    val votersCount: Int? = null,
    val voted: Boolean? = null,
    val content: String?,
    val ownVotes: List<Int>? = null,
    val options: List<PollHashUI>? = null,
    val emojis: List<Emoji>? = null,
)

data class PollHashUI(
    val voteContent: AnnotatedString,
    val percentage: AnnotatedString,
)

data class CardUI(
    val title: AnnotatedString,
    val description: AnnotatedString,
    val url: String,
)

fun String.parseAsMastodonHtml(): Spanned {
    return this.replace("<br> ", "<br>&nbsp;")
        .replace("<br /> ", "<br />&nbsp;")
        .replace("<br/> ", "<br/>&nbsp;")
        .replace("  ", "&nbsp;&nbsp;")
        .parseAsHtml()
        /* Html.fromHtml returns trailing whitespace if the html ends in a </p> tag, which
         * most status contents do, so it should be trimmed. */
        .trimTrailingWhitespace()

}

fun Spanned.trimTrailingWhitespace(): Spanned {
    var i = length
    do {
        i--
    } while (i >= 0 && get(i).isWhitespace())
    return subSequence(0, i + 1) as Spanned
}

fun Spanned.toAnnotatedString(
    primaryColor: Color,
    emojis: List<Emoji>? = listOf(),
    mutableMapOf: MutableMap<String, InlineTextContent>
): AnnotatedString {
    val builder = AnnotatedString.Builder()
    val split = split("((?<=:)|(?=:))".toRegex()).filter { group -> group != "" }
    val newList = mutableListOf<String>()
    val shortCodes = emojis?.map { it.shortcode }?.toSet() ?: emptySet()
    split.forEachIndexed { index, s ->
        if (shortCodes.contains(s) && newList.isNotEmpty()) {
            newList.removeAt(newList.lastIndex)
        }

        if (s != ":" || (newList.isEmpty() || !shortCodes.contains(newList.last()))) {
            newList.add(s)
        }
    }
    newList.forEach { token ->
        val emoji = emojis?.firstOrNull { it.shortcode == token }
        if (emoji != null) {
            builder.appendInlineContent(
                "${emoji.shortcode}",
                ":${token}:"
            )
            mutableMapOf["${emoji.shortcode}"] = InlineTextContent(
                Placeholder(
                    20.sp, 20.sp, PlaceholderVerticalAlign.TextCenter
                ), children = {
                    AvatarImage(PaddingSize2_5, url = emoji.url)
                })
        } else {
            builder.append("${token}")
        }
    }
    val copierContext = CopierContext(primaryColor)
    SpanCopier.values().forEach { copier ->
        val spans = getSpans(0, length, copier.spanClass)
        spans.forEach { span ->
            copier.copySpan(span, getSpanStart(span), getSpanEnd(span), builder, copierContext)
        }
    }

    var toAnnotatedString = builder.toAnnotatedString()

    return toAnnotatedString
}

private data class CopierContext(
    val primaryColor: Color,
)

private enum class SpanCopier {
    URL {
        override val spanClass = URLSpan::class.java
        override fun copySpan(
            span: Any,
            start: Int,
            end: Int,
            destination: AnnotatedString.Builder,
            context: CopierContext
        ) {
            val newStart = if (start > 0) start - 1 else start
            val urlSpan = span as URLSpan
            destination.addStringAnnotation(
                tag = name,
                annotation = urlSpan.url,
                start = newStart,
                end = end,
            )
            destination.addStyle(
                style = SpanStyle(
                    color = context.primaryColor,
                    textDecoration = TextDecoration.Underline
                ),
                start = start,
                end = end,
            )
        }
    },
    FOREGROUND_COLOR {
        override val spanClass = ForegroundColorSpan::class.java
        override fun copySpan(
            span: Any,
            start: Int,
            end: Int,
            destination: AnnotatedString.Builder,
            context: CopierContext
        ) {
            val colorSpan = span as ForegroundColorSpan
            destination.addStyle(
                style = SpanStyle(color = Color(colorSpan.foregroundColor)),
                start = start,
                end = end,
            )
        }
    },
    UNDERLINE {
        override val spanClass = UnderlineSpan::class.java
        override fun copySpan(
            span: Any,
            start: Int,
            end: Int,
            destination: AnnotatedString.Builder,
            context: CopierContext
        ) {
            destination.addStyle(
                style = SpanStyle(textDecoration = TextDecoration.Underline),
                start = start,
                end = end,
            )
        }
    },
    STYLE {
        override val spanClass = StyleSpan::class.java
        override fun copySpan(
            span: Any,
            start: Int,
            end: Int,
            destination: AnnotatedString.Builder,
            context: CopierContext
        ) {
            val styleSpan = span as StyleSpan

            destination.addStyle(
                style = when (styleSpan.style) {
                    Typeface.ITALIC -> SpanStyle(fontStyle = FontStyle.Italic)
                    Typeface.BOLD -> SpanStyle(fontWeight = FontWeight.Bold)
                    Typeface.BOLD_ITALIC -> SpanStyle(
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    )

                    else -> SpanStyle()
                },
                start = start,
                end = end,
            )
        }
    };

    abstract val spanClass: Class<out CharacterStyle>
    abstract fun copySpan(
        span: Any,
        start: Int,
        end: Int,
        destination: AnnotatedString.Builder,
        context: CopierContext
    )
}