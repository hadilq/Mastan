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
package com.hadilq.mastan.loginflow

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.hadilq.mastan.theme.Dimension
import com.hadilq.mastan.theme.LocalMastanThemeUiIo


@Composable
fun SignInUi(
    state: SignInState,
    onEvent: (SignInEvent) -> Unit,
) {
    val localTheme = LocalMastanThemeUiIo.current
    val dim = localTheme.dim
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(dim.paddingSize0_5),
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.Start),
            onClick = { onEvent(BackSignInEvent) }
        ) {
            Icon(
                imageVector = Icons.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = colorScheme.onSurface,
            )
        }
        when (state) {
            is ServerSignInState -> {
                LinearProgressIndicator(
                    Modifier.fillMaxWidth(),
                    color = colorScheme.primary,
                    trackColor = Color.Transparent
                )
            }

            is CreatedApplicationSignInState -> {
                SignInContent(
                    oauthAuthorizeUrl = state.oauthAuthorizeUrl,
                    onCloseClicked = {
                        onEvent(WebViewCancelSignInEvent)
                    },
                    resultUri = {
                        onEvent(ResultUriSignInEvent(it))
                    }
                )

            }

            is OAuthFailedToSignInState -> {
                TryAgain(dim, state.error) { onEvent(OAuthFailedRetrySignInEvent) }
            }

            is CreatedApplicationErrorSignInState -> {
                TryAgain(dim, state.error) { onEvent(CreatedApplicationErrorRetrySignInEvent) }
            }
        }
    }
    var backHandlingEnabled by remember { mutableStateOf(true) }
    BackHandler(backHandlingEnabled) {
        backHandlingEnabled = false
        onEvent(BackSignInEvent)
    }
}

@Composable
private fun ColumnScope.TryAgain(
    dim: Dimension,
    errorMessage: String,
    onEvent: () -> Unit,
) {
    Text(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(dim.paddingSize3),
        color = colorScheme.primary,
        text = errorMessage,
        style = MaterialTheme.typography.bodyLarge,
    )
    IconButton(
        modifier = Modifier
            .align(Alignment.CenterHorizontally)
            .padding(dim.paddingSize3),
        onClick = onEvent
    ) {
        Icon(
            imageVector = Icons.Rounded.Refresh,
            contentDescription = errorMessage,
            tint = colorScheme.error,
        )
    }
}
