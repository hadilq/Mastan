package com.hadilq.mastan.theme

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Stable
@Serializable
data class MastanThemeState(
    val isDarkTheme: Boolean = false,
    val isDynamicColor: Boolean = true,
)
