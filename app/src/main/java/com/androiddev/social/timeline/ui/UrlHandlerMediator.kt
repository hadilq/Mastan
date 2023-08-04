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

import android.net.Uri
import android.util.Log
import com.androiddev.social.SkeletonScope
import com.androiddev.social.timeline.data.FeedType
import com.androiddev.social.timeline.ui.model.UI
import com.squareup.anvil.annotations.ContributesBinding
import java.net.URI
import javax.inject.Inject

interface UrlHandlerMediator {
    fun givenUri(
        ui: UI,
        uri: String?,
        isValidUrl: (String) -> Boolean,
        onOpenURI: (URI, FeedType) -> Unit,
        goToTag: (String) -> Unit,
        goToProfile: (String) -> Unit,
        goToConversation: (UI) -> Unit,
    )
}

@ContributesBinding(SkeletonScope::class, boundType = UrlHandlerMediator::class)
class RealUrlHandlerMediator @Inject constructor() : UrlHandlerMediator {

    override fun givenUri(
        ui: UI,
        uri: String?,
        isValidUrl: (String) -> Boolean,
        onOpenURI: (URI, FeedType) -> Unit,
        goToTag: (String) -> Unit,
        goToProfile: (String) -> Unit,
        goToConversation: (UI) -> Unit,
    ) {
        when {
            uri != null && isValidUrl(uri) -> {
                val parsedUri = Uri.parse(uri)
                val fixedUri = URI(
                    parsedUri.scheme?.lowercase(), parsedUri.authority,
                    parsedUri.path, parsedUri.query, parsedUri.fragment
                )
                onOpenURI(fixedUri, ui.type)
                Log.d("Clicked URI", uri)
            }

            uri?.startsWith("###TAG") == true -> {
                goToTag(uri.removePrefix("###TAG"))
            }

            uri != null -> goToProfile(uri)
            ui.replyCount > 0 || ui.inReplyTo != null -> {
                goToConversation(ui)
            }
        }
    }
}
