package com.hadilq.mastan.theme

import androidx.compose.ui.unit.Dp

interface Dimension {
    // Margin Values
    val paddingSizeNone: Dp
    val paddingSize0_5: Dp

    // This is base (100), all other values based on this
    val paddingSize1: Dp
    val paddingSize2: Dp
    val paddingSize2_5: Dp
    val paddingSize3: Dp
    val paddingSize4: Dp
    val paddingSize5: Dp
    val paddingSize6: Dp
    val paddingSize7: Dp
    val paddingSize8: Dp
    val paddingSize9: Dp
    val paddingSize10: Dp
    val paddingSize12: Dp
    val paddingSize14: Dp

    val touchpoint: Dp
    val touchpointMd: Dp
    val touchpointLg: Dp

    // Card related Values
    val cardElevation: Dp
    val cardCornerRadius: Dp

    // Elevation
    val tonalSurfaceElevation: Dp
    val bottomBarElevation: Dp

    // Dividers
    val thickSm: Dp
    val thickMd: Dp
    val thickLg: Dp

    // Panel Size
    val paddingSizePanelHeight: Dp
}
