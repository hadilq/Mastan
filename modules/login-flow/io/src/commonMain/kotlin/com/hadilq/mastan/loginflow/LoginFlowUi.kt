package com.hadilq.mastan.loginflow

import androidx.compose.runtime.Composable

typealias LoginFlowUi = @Composable (
    state: LoginFlowState,
    onEvent: (LoginFlowEvent) -> Unit,
) -> Unit
