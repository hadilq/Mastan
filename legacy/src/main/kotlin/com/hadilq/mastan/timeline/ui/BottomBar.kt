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

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hadilq.mastan.legacy.R
import com.hadilq.mastan.theme.LocalThemeOutput

@Composable
fun BottomBar(
    goToMentions: () -> Unit,
    goToNotifications: () -> Unit,
) {
    val dim = LocalThemeOutput.current.dim
    val size = 24
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier
            .fillMaxWidth()

    ) {
        OutlinedButton(
            contentPadding = PaddingValues(dim.paddingSizeNone, dim.paddingSize1),
            border = BorderStroke(dim.thickSm, Color.Transparent),
            onClick = goToMentions
        ) {
            Image(
                modifier = Modifier.size(size.dp),
                painter = painterResource(R.drawable.at),
                contentDescription = "",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
            )
        }

        //placeholder for spacing
        OutlinedButton(
            border = BorderStroke(dim.thickSm, Color.Transparent),
            onClick = { }
        ) {
        }


        OutlinedButton(
            contentPadding = PaddingValues(dim.paddingSizeNone, dim.paddingSize1),
            border = BorderStroke(dim.thickSm, Color.Transparent),
            onClick = goToNotifications
        ) {
            Image(
                modifier = Modifier
                    .size(28.dp)
                    .rotate(0f),
                painter = painterResource(R.drawable.notification),
                contentDescription = "",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
            )
        }
    }
}
