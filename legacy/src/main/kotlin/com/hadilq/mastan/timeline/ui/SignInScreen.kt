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

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.hadilq.mastan.AuthOptionalComponent
import com.hadilq.mastan.UserParentComponentProvider
import com.hadilq.mastan.auth.ui.SignInContent
import com.hadilq.mastan.auth.ui.SignInPresenter
import dev.marcellogalhardo.retained.compose.retain
import kotlinx.coroutines.CoroutineScope
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun SignInScreen(
    navController: NavHostController,
    scope: CoroutineScope,
    server: String
) {
    val context = LocalContext.current
    val component = retain(key = server) { NoAuthComponent(context) } as AuthOptionalInjector
    val signInPresenter = component.signInPresenter()
    val accessToken = signInPresenter.model.accessTokenRequest

    LaunchedEffect(server) {
        signInPresenter.start()
    }
    LaunchedEffect(server, accessToken) {
        if (accessToken != null) {
            val domain = URLEncoder.encode(accessToken.domain, StandardCharsets.UTF_8.toString())
            val clientId =
                URLEncoder.encode(accessToken.clientId, StandardCharsets.UTF_8.toString())
            val clientSecret =
                URLEncoder.encode(accessToken.clientSecret, StandardCharsets.UTF_8.toString())
            val redirectUri =
                URLEncoder.encode(accessToken.redirectUri, StandardCharsets.UTF_8.toString())
            val code = URLEncoder.encode(accessToken.code, StandardCharsets.UTF_8.toString())
            navController.navigate("home/${domain}/${clientId}/${clientSecret}/${redirectUri}/${code}") {
                popUpTo(0)
            }
        } else {
            signInPresenter.handle(SignInPresenter.SignIn(server))
        }
    }
    SignInContent(
        oauthAuthorizeUrl = signInPresenter.model.oauthAuthorizeUrl,
        error = signInPresenter.model.error,
        onErrorFromOAuth = {
            //                                TODO("Implement onError")
        },
        onCloseClicked = {

        },
        shouldCancelLoadingUrl = {
            signInPresenter.shouldCancelLoadingUrl(it, scope, server)
        }

    )
}

fun NoAuthComponent(context: Context) =
    ((context.applicationContext as UserParentComponentProvider).component as AuthOptionalComponent.ParentComponent).createAuthOptionalComponent()
