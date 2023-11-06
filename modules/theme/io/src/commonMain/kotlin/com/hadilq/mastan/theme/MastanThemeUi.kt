package com.hadilq.mastan.theme

import androidx.compose.runtime.Composable

typealias MastanThemeUi = @Composable (
    content: @Composable () -> Unit,
) -> Unit
