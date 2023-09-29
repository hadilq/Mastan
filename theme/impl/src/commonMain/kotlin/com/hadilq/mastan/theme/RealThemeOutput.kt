package com.hadilq.mastan.theme

import androidx.compose.material3.Typography
import androidx.compose.material3.ColorScheme

class RealThemeOutput : ThemeOutput {
    override val lightColors: ColorScheme by lazy(LazyThreadSafetyMode.NONE) {
        LightColors
    }

    override val darkColors: ColorScheme by lazy(LazyThreadSafetyMode.NONE) {
        DarkColors
    }

    override val dim: Dimension by lazy(LazyThreadSafetyMode.NONE) {
        RealDimension()
    }

    override val type: Typography by lazy(LazyThreadSafetyMode.NONE) {
        Typography
    }
}