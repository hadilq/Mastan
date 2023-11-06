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

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hadilq.mastan.theme.LocalMastanThemeUiIo
import com.hadilq.mastan.network.dto.Emoji
import com.hadilq.mastan.ui.util.EmojiText
import com.hadilq.mastan.ui.util.emojiText

@Composable
fun Boosted(
    boostedBy: String?,
    boostedAvatar: String?,
    boostedEmojis: List<Emoji>?,
    drawable: Int?,
    modifier: Modifier = Modifier,
    containerColor: Color = colorScheme.surface,
    onClick: () -> Unit = {}
) {
    val dim = LocalMastanThemeUiIo.current.dim
    boostedBy?.let {
        AssistChip(
            modifier = modifier
                .height(24.dp)
                .wrapContentWidth()
                .padding(horizontal = dim.paddingSize0_5),
            colors = AssistChipDefaults.assistChipColors(
                containerColor = containerColor,
                leadingIconContentColor = colorScheme.secondary, labelColor = colorScheme.primary
            ),
            shape = RoundedCornerShape(25, 25, 25, 25),
            onClick = onClick,
            label = {
                val myColorScheme = colorScheme
                val emojiText by remember(key1 = boostedBy) {
                    mutableStateOf(
                        emojiText(
                            boostedBy,
                            emptyList(),
                            emptyList(),
                            boostedEmojis,
                            myColorScheme,
                            dim,
                        )
                    )
                }
                Text(
                    text = emojiText.text,
                    style = MaterialTheme.typography.labelSmall.copy(color = colorScheme.primary),
                    inlineContent = emojiText.mapping,
                    maxLines = 1
                )
            },
            leadingIcon = {
                AvatarImage(size = dim.paddingSize2, url = boostedAvatar, onClick = onClick)
            },
            trailingIcon = {
                if (drawable != null)
                    Image(
                        modifier = Modifier.height(dim.paddingSize2),
                        painter = painterResource(drawable),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(colorScheme.secondary),
                    )

            }
        )
    }
}

@Composable
fun Boosted(
    boosted: EmojiText?,
    drawable: Int?,
    modifier: Modifier = Modifier,
    containerColor: Color = colorScheme.surface,
    onClick: () -> Unit = {}
) {
    val dim = LocalMastanThemeUiIo.current.dim
    boosted?.let {
        AssistChip(
            modifier = modifier
                .height(24.dp)
                .wrapContentWidth()
                .padding(horizontal = dim.paddingSize0_5),
            colors = AssistChipDefaults.assistChipColors(
                containerColor = containerColor,
                leadingIconContentColor = colorScheme.secondary, labelColor = colorScheme.primary
            ),
            shape = RoundedCornerShape(25, 25, 25, 25),
            onClick = onClick,
            label = {
                colorScheme
                Text(
                    text = boosted.text,
                    style = MaterialTheme.typography.labelSmall.copy(color = colorScheme.primary),
                    inlineContent = boosted.mapping,
                    maxLines = 1
                )
            },
            leadingIcon = {
//                AvatarImage(size = dim.PaddingSize2, url = boostedAvatar, onClick = onClick)
            },
            trailingIcon = {
                if (drawable != null)
                    Image(
                        modifier = Modifier.height(dim.paddingSize2),
                        painter = painterResource(drawable),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(colorScheme.secondary),
                    )

            }
        )
    }
}
