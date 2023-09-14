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

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalUriHandler
import androidx.navigation.NavController


@Composable
fun OpenHandledUri(
    uriPresenter: UriPresenter,
    navController: NavController,
    code: String,
) {
    val uriHandler = LocalUriHandler.current
    LaunchedEffect(uriPresenter.model.handledUri) {
        Log.d("QQQ", "handledUri: ${uriPresenter.model.handledUri}")
        when (val handledUri = uriPresenter.model.handledUri) {
            is UriPresenter.ConversationHandledUri -> {
                navController.navigate("conversation/$code/${handledUri.statusId}/${handledUri.type}")
                uriPresenter.handle(UriPresenter.Reset)
            }

            is UriPresenter.ProfileHandledUri -> {
                navController.navigate("profile/$code/${handledUri.accountId}")
                uriPresenter.handle(UriPresenter.Reset)
            }

            is UriPresenter.UnknownHandledUri -> {
                uriHandler.openUri(handledUri.uri.toASCIIString())
                uriPresenter.handle(UriPresenter.Reset)
            }

            else -> {}
        }
    }
}