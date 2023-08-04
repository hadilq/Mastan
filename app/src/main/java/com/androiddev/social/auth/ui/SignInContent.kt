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
package com.androiddev.social.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.androiddev.social.theme.FireflyTheme

@Composable
fun SignInContent(
    modifier: Modifier = Modifier,
    oauthAuthorizeUrl: String,
    error: String?,
    onErrorFromOAuth: (message: String) -> Unit,
    onCloseClicked: () -> Unit,
    shouldCancelLoadingUrl: (url: String) -> Boolean
) {
    FireflyTheme {
        Box(Modifier.heightIn(min = 1.dp))
        Column(
            modifier = modifier.fillMaxSize().background(Color.Transparent)
        ) {

            if (oauthAuthorizeUrl.isNotEmpty()) {
                SignInWebView(
                    url = oauthAuthorizeUrl,
                    modifier = Modifier.fillMaxSize(),
                    shouldCancelLoadingUrl = shouldCancelLoadingUrl,
                    onWebError = onErrorFromOAuth,
                    onCancel = onCloseClicked,
                )
            }
        }
    }
}
