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

import androidx.compose.ui.unit.dp

class RealDimension: Dimension {
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
    override val paddingSizeNone = 0.dp
    override val paddingSize0_5 = 4.dp
    override val paddingSize1 = 8.dp // This is base (100), all other values based on this
    override val paddingSize2 = 16.dp
    override val paddingSize2_5 = 20.dp
    override val paddingSize3 = 24.dp
    override val paddingSize4 = 32.dp
    override val paddingSize5 = 40.dp
    override val paddingSize6 = 48.dp
    override val paddingSize7 = 56.dp
    override val paddingSize8 = 64.dp
    override val paddingSize9 = 72.dp
    override val paddingSize10 = 80.dp
    override val paddingSize12 = 96.dp
    override val paddingSize14 = 112.dp

    override val touchpoint = 48.dp
    override val touchpointMd = 56.dp
    override val touchpointLg = 64.dp

    // Card related Values
    override val cardElevation = 6.dp
    override val cardCornerRadius = 3.dp

    // Elevation
    override val tonalSurfaceElevation = 2.dp
    override val bottomBarElevation = 0.dp

    // Dividers
    override val thickSm = 1.dp
    override val thickMd = 2.dp
    override val thickLg = 3.dp

    // Panel Size
    override val paddingSizePanelHeight = 320.dp
}