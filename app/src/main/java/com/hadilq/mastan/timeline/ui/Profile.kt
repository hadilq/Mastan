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
@file:OptIn(ExperimentalMaterialApi::class)

package com.hadilq.mastan.timeline.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import com.hadilq.mastan.theme.PaddingSize0_5
import com.hadilq.mastan.theme.PaddingSize3
import com.hadilq.mastan.theme.ThickSm
import com.hadilq.mastan.timeline.data.Account
import com.hadilq.mastan.R
@Composable
fun AccountChooser(
    onProfileClick: (accountId: String, isCurrent:Boolean) -> Unit = {a,b->},
    onChangeTheme: () -> Unit = {},
    onNewAccount: () -> Unit = {},
    model: AvatarPresenter.AvatarModel
) {
    var expanded by remember { mutableStateOf(false) }

    Row(modifier = Modifier) {

        val url: String? = model.accounts?.firstOrNull()?.avatar
        if (url != null) {
            AvatarImage(url = url, onClick = { expanded = true })
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .wrapContentSize()
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = .9f)
                )
        ) {
            model.accounts?.forEach { account: Account ->
                DropdownMenuItem(onClick = {
                    expanded = false
                    onProfileClick(account.id, account == model.accounts.firstOrNull())
                }, text = {
                    Column {
                        Row {
                            account.let {
                                AvatarImage(url = it.avatar, onClick = {
                                    expanded = false
                                    onProfileClick(account.id, account == model.accounts.firstOrNull())}
                                )
                                val emojis = account.emojis


                                val unformatted = account.displayName
                                val (inlineContentMap, text) = inlineEmojis(
                                    unformatted,
                                    emojis
                                )

                                Text(
                                    modifier = Modifier
                                        .padding(PaddingSize0_5)
                                        .align(Alignment.CenterVertically),
                                    text = text,
                                    inlineContent = inlineContentMap
                                )
                            }
                        }
                    }
                })
            }



            Divider(thickness = ThickSm, color = Color.Gray)

            DropdownMenuItem(onClick = {
                expanded = false
                onChangeTheme()
            }, text = {
                Row {
                    Image(
                        modifier = Modifier.size(PaddingSize3),
                        painter = painterResource(R.drawable.theme),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        "Switch Theme",
                        modifier = Modifier
                            .padding(PaddingSize0_5)
                            .align(Alignment.CenterVertically)
                    )
                }
            })
            DropdownMenuItem(onClick = {
                expanded = false
                onNewAccount()
            }, text = {
                Row {
                    Image(
                        modifier = Modifier.size(PaddingSize3),
                        painter = painterResource(R.drawable.adduser),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                    )
                    Text(
                        "Add User/Server",
                        modifier = Modifier
                            .padding(PaddingSize0_5)
                            .align(Alignment.CenterVertically)
                    )
                }
            })

        }
    }

}
