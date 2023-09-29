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

import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.sp
import com.hadilq.mastan.theme.LocalThemeOutput
import com.hadilq.mastan.timeline.data.Emoji
import com.hadilq.mastan.ui.util.EmojiText

@Composable
fun inlineEmojis(
    unformatted: String,
    emojis: List<Emoji>
): EmojiText {
    val dim = LocalThemeOutput.current.dim
    val inlineContentMap: MutableMap<String, InlineTextContent> = mutableMapOf()
    val text = buildAnnotatedString {
        val split = unformatted.split(":").filter { group -> group != "" }
        split.forEach { token ->
            val emoji = emojis.firstOrNull { it.shortcode == token }
            if (emoji != null) {
                appendInlineContent(token, token)
                inlineContentMap[token] = InlineTextContent(
                    Placeholder(
                        20.sp, 20.sp, PlaceholderVerticalAlign.TextCenter
                    ), children = {
                        AvatarImage(dim.paddingSize2_5, url = emoji.url)
                    })
            } else {
                append(token)
            }
        }
    }
    return EmojiText(inlineContentMap, text)
}