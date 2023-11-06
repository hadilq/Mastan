package com.hadilq.mastan.theme

import androidx.compose.runtime.Stable
import kotlinx.serialization.Serializable

@Stable
sealed interface MastanThemeEvent

@Stable
@Serializable
object NoMastanThemeEvent : MastanThemeEvent

@Stable
@Serializable
data class UpdateDynamicColorThemeEvent(val dynamicColor: Boolean) : MastanThemeEvent

@Stable
@Serializable
data class UpdateDarkThemeThemeEvent(val darkTheme: Boolean) : MastanThemeEvent
