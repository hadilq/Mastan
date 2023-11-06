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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.hadilq.mastan.theme.LocalMastanThemeUiIo
import com.hadilq.mastan.timeline.data.FeedType
import com.hadilq.mastan.timeline.ui.model.CardUI
import java.net.URI

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContentCard(
    card: CardUI,
    feedType: FeedType,
    onOpenURI: (URI, FeedType) -> Unit,
) {
    val dim = LocalMastanThemeUiIo.current.dim
    Surface(
        modifier = Modifier
            .padding(top = dim.paddingSize1)
            .fillMaxWidth(),
        tonalElevation = dim.tonalSurfaceElevation,
        shape = RoundedCornerShape(dim.paddingSize1),
        onClick = {
            onOpenURI(URI(card.url), feedType)
        }
    ) {
        Column(
            modifier = Modifier.padding(dim.paddingSize1),
        ) {
            Text(
                modifier = Modifier.padding(dim.paddingSize0_5),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 16.sp
                ),
                text = card.title
            )
            Text(
                modifier = Modifier.padding(dim.paddingSize0_5),
                style = MaterialTheme.typography.bodySmall.copy(
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 12.sp
                ),
                text = card.description
            )
        }
    }
}
