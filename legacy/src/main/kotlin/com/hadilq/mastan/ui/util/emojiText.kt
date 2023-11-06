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
package com.hadilq.mastan.ui.util

import android.text.Spanned
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.text.AnnotatedString
import com.hadilq.mastan.theme.Dimension
import com.hadilq.mastan.network.dto.Emoji
import com.hadilq.mastan.network.dto.Mention
import com.hadilq.mastan.network.dto.Tag
import com.hadilq.mastan.timeline.data.setClickableText
import com.hadilq.mastan.timeline.ui.empty
import com.hadilq.mastan.timeline.ui.model.parseAsMastodonHtml
import com.hadilq.mastan.timeline.ui.model.toAnnotatedString

fun emojiText(
    content: String,
    mentions: List<Mention>,
    tags: List<Tag>,
    emojis: List<Emoji>?,
    colorScheme: ColorScheme,
    dim: Dimension,
): EmojiText {
    val parseAsMastodonHtml: Spanned = content.parseAsMastodonHtml()
    println(parseAsMastodonHtml)
    val prettyText = setClickableText(
        parseAsMastodonHtml,
        mentions,
        tags,
        empty
    )

    val mapping = mutableMapOf<String, InlineTextContent>()
    val linkColor = colorScheme.primary
    val text= prettyText.toAnnotatedString(
        linkColor,
        emojis,
        mapping,
        dim,
    )
    return EmojiText(mapping, text)
}

data class EmojiText(val mapping: Map<String, InlineTextContent>, val text: AnnotatedString)