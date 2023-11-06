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
package com.hadilq.mastan.accounts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.items
import com.hadilq.mastan.theme.LocalMastanThemeUiIo
import com.hadilq.mastan.network.dto.Account
import com.hadilq.mastan.timeline.ui.AvatarImage
import com.hadilq.mastan.ui.util.emojiText

@Composable
fun AccountTab(
    results: List<Account>? = null,
    resultsPaging: LazyPagingItems<Account>? = null,
    goToProfile: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        if (results != null)
            items(results, key = { it.id }) {
                Column {
                    AccountTableContent(goToProfile, it)
                }
                Divider()
            }
        else {
            items(
                items = resultsPaging!!,
                key = { it.id }) {
                if (it != null)
                    Column {
                        AccountTableContent(goToProfile, it)
                    }
                Divider()
            }
        }
    }
}

@Composable
private fun AccountTableContent(
    goToProfile: (String) -> Unit,
    it: Account
) {
    val dim = LocalMastanThemeUiIo.current.dim
    Row(modifier = Modifier
        .clickable { goToProfile(it.id) }
        .padding(dim.paddingSize1)) {
        AvatarImage(dim.paddingSize7, it.avatar, onClick = { goToProfile(it.id) })
        Column(Modifier.padding(start = dim.paddingSize1)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                val text = emojiText(
                    it.displayName,
                    emptyList(),
                    emptyList(),
                    it.emojis,
                    colorScheme,
                    dim,
                )
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.Top),
                    text = text.text,
                    style = MaterialTheme.typography.titleLarge,
                    inlineContent = text.mapping,
                )
                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.secondary,
                    modifier = Modifier
                        .align(Alignment.Top),
                    text = "Followers ${it.followersCount}",
                    style = MaterialTheme.typography.titleMedium,
                )

            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    color = colorScheme.secondary,
                    text = it.username,
                    style = MaterialTheme.typography.titleMedium,
                )

                Text(
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = colorScheme.secondary,
                    modifier = Modifier
                        .align(Alignment.Top),
                    text = "Following ${it.followingCount} ",
                    style = MaterialTheme.typography.titleMedium,
                )

            }
        }
    }
}