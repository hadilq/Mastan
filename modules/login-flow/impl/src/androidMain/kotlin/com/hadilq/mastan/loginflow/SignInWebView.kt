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

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.OnNewIntentProvider
import androidx.core.util.Consumer
import com.hadilq.mastan.log.LocalLogUiIo
import java.net.URI
import kotlin.time.Duration.Companion.seconds

@Composable
fun SignInWebView(
    url: String,
    onCancel: () -> Unit,
    resultUri: (url: URI) -> Unit,
) {
    val logger = LocalLogUiIo.current
    val webIntent = webBrowserIntent(
        url = url,
        primaryColor = MaterialTheme.colorScheme.primary,
        secondaryColor =  MaterialTheme.colorScheme.primary.copy(alpha = .5f)
    )

    val handler = remember { Handler(Looper.getMainLooper()) }

    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_CANCELED) {
                // post to a handler to wait for a redirect intent as that should supersede this
                logger.logDebug { "Sign in web view:$result" }
                handler.postDelayed({ onCancel() }, 1.seconds.inWholeMilliseconds)
            }
        }

    OnNewIntent { intent ->
        val redirectUrl = intent?.data?.toString()
        if (redirectUrl != null) {
            logger.logDebug { "Sign in web view on New Intent:$redirectUrl" }
            val uri = URI(redirectUrl)
            val query = uri.query
            if (query.contains("code=") || query.contains("error=")) {
                handler.removeCallbacksAndMessages(null)
            }
            resultUri(uri)
        }
    }

    Box(Modifier.fillMaxSize()) {
        CircularProgressIndicator(
            Modifier
                .align(Alignment.Center)
                .size(84.dp)
        )
    }
    DisposableEffect(url) {
        launcher.launch(webIntent)
        onDispose {
            handler.removeCallbacksAndMessages(null)
        }
    }
}

private fun webBrowserIntent(url: String, primaryColor: Color, secondaryColor: Color): Intent {
    val intent = CustomTabsIntent.Builder()
        .setToolbarColor(primaryColor.toArgb())
        .setSecondaryToolbarColor(secondaryColor.toArgb())
        .build()
        .intent
    intent.data = Uri.parse(url)
    return intent
}

@Composable
private fun OnNewIntent(callback: (Intent?) -> Unit) {
    val context = LocalContext.current
    val newIntentProvider = context as OnNewIntentProvider

    val listener = remember(newIntentProvider) { Consumer<Intent?> { callback(it) } }

    DisposableEffect(listener) {
        newIntentProvider.addOnNewIntentListener(listener)
        onDispose {
            newIntentProvider.removeOnNewIntentListener(listener)
        }
    }
}
