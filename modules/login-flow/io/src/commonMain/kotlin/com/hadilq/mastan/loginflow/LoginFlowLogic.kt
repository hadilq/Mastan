package com.hadilq.mastan.loginflow

import androidx.compose.runtime.Composable

typealias LoginFlowLogic = @Composable (
    state: LoginFlowState,
    event: LoginFlowEvent,
    onState: (LoginFlowState) -> Unit,
) -> Unit
