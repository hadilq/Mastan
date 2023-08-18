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
package com.hadilq.mastan.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.dp

// Use Material 8dp grid
// https://m2.material.io/design/layout/spacing-methods.html#baseline-grid
//
// The name of each token is aligned to orders of magnitude of 8
// 1 = 1   * 8 = 8
// 2 = 2   * 8 = 16
// 2_5 = 2.5 * 8 = 20
// ...
// 14 = 14 * 8 = 112

// Margin Values
val PaddingSizeNone = 0.dp
val PaddingSize0_5 = 4.dp
val PaddingSize1 = 8.dp // This is base (100), all other values based on this
val PaddingSize2 = 16.dp
val PaddingSize2_5 = 20.dp
val PaddingSize3 = 24.dp
val PaddingSize4 = 32.dp
val PaddingSize5 = 40.dp
val PaddingSize6 = 48.dp
val PaddingSize7 = 56.dp
val PaddingSize8 = 64.dp
val PaddingSize9 = 72.dp
val PaddingSize10 = 80.dp
val PaddingSize12 = 96.dp
val PaddingSize14 = 112.dp

val Touchpoint = 48.dp
val TouchpointMd = 56.dp
val TouchpointLg = 64.dp

// Card related Values
val CardElevation = 6.dp
val CardCornerRadius = 3.dp

// Elevation
val TonalSurfaceElevation = 2.dp
val BottomBarElevation = 0.dp

// Dividers
val ThickSm = 1.dp
val ThickMd = 2.dp
val ThickLg = 3.dp

// Panel Size
val PaddingSizePanelHeight = 320.dp

// Commonly used group of padding with less top/bottom padding
val PaddingValuesHoriz2Vert1 = PaddingValues(
    start = PaddingSize2,
    top = PaddingSize1,
    bottom = PaddingSize1,
    end = PaddingSize2
)

// Commonly used group of padding with less side padding
val PaddingValuesHoriz1Vert2 = PaddingValues(
    start = PaddingSize1,
    top = PaddingSize2,
    bottom = PaddingSize2,
    end = PaddingSize1
)