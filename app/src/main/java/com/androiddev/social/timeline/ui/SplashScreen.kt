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
package com.androiddev.social.timeline.ui

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import com.androiddev.social.auth.data.AccessTokenRequest
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun SplashScreen(navController: NavHostController) {
    val current: Context = LocalContext.current

    LaunchedEffect(Unit) {
        val accounts: List<AccessTokenRequest> = current.getAccounts()
        val firstAccount = accounts.firstOrNull()
        if (firstAccount == null) {
            navController.navigate("selectServer")
        } else {
            val domain = URLEncoder.encode(firstAccount.domain, StandardCharsets.UTF_8.toString())
            val clientId =
                URLEncoder.encode(firstAccount.clientId, StandardCharsets.UTF_8.toString())
            val clientSecret =
                URLEncoder.encode(firstAccount.clientSecret, StandardCharsets.UTF_8.toString())
            val redirectUri =
                URLEncoder.encode(firstAccount.redirectUri, StandardCharsets.UTF_8.toString())
            val code = URLEncoder.encode(firstAccount.code, StandardCharsets.UTF_8.toString())
            navController.navigate("home/${domain}/${clientId}/${clientSecret}/${redirectUri}/${code}") {
                popUpTo(0)
            }
        }
    }
}