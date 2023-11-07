package com.hadilq.mastan.theme

import androidx.compose.runtime.Composable

typealias MastanThemeLogic = @Composable (
    state: MastanThemeState,
    event: MastanThemeEvent,
    onState: (MastanThemeState) -> Unit,
) -> Unit
