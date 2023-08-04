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

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun CustomViewPullRefreshView(
    swipeRefreshState: PullRefreshState,
    refreshTriggerDistance: Dp,
    color: Color = MaterialTheme.colorScheme.primary,
    isRefreshing: Boolean
) {
    Box(
        Modifier
//            .drawWithCache {
//                onDrawBehind {
//                    val distance = refreshTriggerDistance.toPx()
//                    val progress = (swipeRefreshState.progress / distance).coerceIn(0f, 1f)
//                    val brush = Brush.verticalGradient(
//                        0f to color.copy(alpha = 0.45f),
//                        1f to color.copy(alpha = 0f)
//                    )
//                    drawRect(
//                        brush = brush,
//                        alpha = FastOutSlowInEasing.transform(progress)
//                    )
//                }
//            }
            .fillMaxWidth()
            .wrapContentHeight()
    ) {
        if (isRefreshing) {
            LinearProgressIndicator(
                Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent
            )
        } else {
            val trigger = with(LocalDensity.current) { refreshTriggerDistance.toPx() }
            val progress = (swipeRefreshState.progress / trigger).coerceIn(0f, 1f)
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                trackColor = Color.Transparent

            )
        }
    }
}